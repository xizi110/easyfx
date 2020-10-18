package xyz.yuelai;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.event.EventDispatchChain;
import javafx.event.EventTarget;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import xyz.yuelai.control.Message;
import xyz.yuelai.control.Notification;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * @author zhong
 * @date 2020-09-15 17:48:09 周二
 * <p>
 * 所有 view 的父类。创建 view 对象需要用 {@link #createView(Class)} 方法创建，
 * 否则使用 new ViewImpl() 创建的 view 对象，不能被 View 管理
 */
@SuppressWarnings("all")
public abstract class View implements Initializable, EventTarget {

    /**
     * 当前视图根对象
     */
    private Parent root;

    static boolean ELEMENT_STYLE = false;

    private ObjectProperty<Scene> scene = new SimpleObjectProperty<>();

    /**
     * 构造器之前初始化
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public View() {
        try {
            // Usage of GetResource in new View() may be unsafe if class is extended
            // 在这里就是为了从子类获取资源路径，所以是安全的
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml()));
            loader.setController(this);
            root = loader.load();
            scene.bind(root.sceneProperty());
            scene.addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    if (ELEMENT_STYLE) {
                        newValue.getStylesheets().add(getClass().getResource("/css/element-ui.css").toExternalForm());
                    }
                    newValue.getStylesheets().add(getClass().getResource("/css/icon.css").toExternalForm());

                    newValue.windowProperty().addListener((observable1, oldValue1, newValue1) -> {
                        if (newValue1 != null) {
                            newValue1.setOnHidden(event -> onWindowHidden());
                            newValue1.setOnShowing(event -> onWindowShowing());
                        }
                    });
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Map<String, List<ViewReceiverMethodInfo>> receiverMethods = new HashMap<>();

    /**
     * 异步发送消息给其他激活中的 View ，其他 View 类中被 @Receiver
     * 注解注释的方法且 name 为指定 receiverName 的方法，将被调用
     *
     * @param receiverName 接受者的名字
     * @param data         接收者方法参数
     */
    protected void sendMessageWithAsync(String receiverName, Object... data) {
        CompletableFuture.runAsync(() -> {
            List<ViewReceiverMethodInfo> viewReceiverMethodInfos = receiverMethods.get(receiverName);
            for (ViewReceiverMethodInfo info : viewReceiverMethodInfos) {
                Method method = info.getMethod();
                View view = info.getView();
                if (!(info.isWhenHidden() || view.getWindow().isShowing())) {
                    continue;
                }
                method.setAccessible(true);
                Platform.runLater(() -> {
                    try {
                        method.invoke(view, data);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    /**
     * 获取当前视图的窗体
     *
     * @return 当前视图的窗体
     */
    protected Window getWindow() {
        Scene scene = this.scene.get();
        if (scene == null) {
            return null;
        }
        return scene.getWindow();
    }

    /**
     * 在窗口显示之前调用，子类可以覆盖实现
     */
    protected void onWindowShowing() {
    }

    /**
     * 在窗口关闭之后调用，子类可以覆盖实现
     */
    protected void onWindowHidden() {
    }

    /**
     * 根据view类创建view对象
     *
     * @param viewClass view类
     * @param <T>       view子类类型
     * @return 创建的view
     */
    public static <T extends View> T createView(Class<T> viewClass) {
        if (viewClass == null) {
            return null;
        }

        try {
            Constructor<T> constructor = viewClass.getConstructor();
            T instance = constructor.newInstance();

            // 异步处理 Receiver 注解注释的方法
            CompletableFuture.runAsync(() -> {
                Method[] declaredMethods = viewClass.getDeclaredMethods();
                for (Method declaredMethod : declaredMethods) {
                    declaredMethod.setAccessible(true);
                    Receiver annotation = declaredMethod.getAnnotation(Receiver.class);
                    if (annotation != null) {
                        List<ViewReceiverMethodInfo> viewReceiverMethodInfos = receiverMethods.computeIfAbsent(annotation.name(), k -> new LinkedList<>());
                        ViewReceiverMethodInfo viewReceiverMethodInfo = new ViewReceiverMethodInfo();
                        viewReceiverMethodInfo.setView(instance);
                        viewReceiverMethodInfo.setMethod(declaredMethod);
                        viewReceiverMethodInfo.setReceiverName(annotation.name());
                        viewReceiverMethodInfo.setWhenHidden(annotation.whenHidden());
                        viewReceiverMethodInfos.add(viewReceiverMethodInfo);
                    }
                }
            });

            return instance;
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public EventDispatchChain buildEventDispatchChain(EventDispatchChain tail) {
        throw new UnsupportedOperationException("not implemented");
    }

    /**
     * @return fxml路径
     */
    public abstract String fxml();

    /**
     * @return 视图根对象
     */
    public Parent getRoot() {
        return root;
    }

    /**
     * 创建窗口，暂不显示，用户可以对窗体进行设置
     *
     * @param viewClass view类
     * @return 创建的默认窗体
     */
    protected Stage createWindow(Class<? extends View> viewClass) {
        View view = createView(viewClass);
        Stage stage = new Stage();
        Scene scene = new Scene(view.getRoot());
        stage.setScene(scene);
        return stage;
    }

    /**
     * 弹窗警告
     *
     * @param message 弹窗展示信息
     * @return 接收用户点击按钮类型
     */
    protected Optional<ButtonType> alert(String message, ButtonType... buttonTypes) {
        return alert(message, Alert.AlertType.WARNING, buttonTypes);
    }

    /**
     * 弹窗，指定弹窗类型
     *
     * @param message   弹窗提示信息
     * @param alertType 弹窗类型
     * @return 接收用户点击按钮类型
     */
    protected Optional<ButtonType> alert(String message, Alert.AlertType alertType, ButtonType... buttonTypes) {
        Alert alert = new Alert(alertType);
        alert.initStyle(StageStyle.TRANSPARENT);
        Scene scene = alert.getDialogPane().getScene();
        scene.setFill(null);
        alert.getButtonTypes().addAll(buttonTypes);
        alert.initOwner(getWindow());
        alert.setContentText(message);
        if (ELEMENT_STYLE) {
            String s = getClass().getResource("/css/element-ui.css").toExternalForm();
            alert.getDialogPane().getStylesheets().add(s);
        }
        return alert.showAndWait();
    }

    /**
     * 显示一则默认类型默认延迟的消息
     *
     * @param message 通知信息
     */
    protected void showMessage(String message) {
        showMessage(message, Message.Type.INFO);
    }

    /**
     * 显示一则指定类型默认延迟的消息
     *
     * @param message 通知信息
     */
    protected void showMessage(String message, Message.Type type) {
        showMessage(message, type, Message.DEFAULT_DELAY);
    }

    /**
     * 显示一则默认类型指定延迟的消息
     *
     * @param message 通知信息
     */
    protected void showMessage(String message, long delay) {
        showMessage(message, Message.Type.INFO, delay);
    }

    /**
     * 显示一则指定类型指定延迟的消息
     *
     * @param message 通知信息
     */
    protected void showMessage(String message, Message.Type type, long delay) {
        Parent root = getRoot();
        if (!(root instanceof Pane)) {
            System.err.println("owner 必须是 Pane 或其子类");
            return;
        }
        Message.show((Pane) root, message, type, delay);
    }

    /**
     * 显示一则默认类型的通知， 用户手动关闭
     *
     * @param message 通知信息
     */
    protected void showNotification(String message) {
        showNotification(message, Notification.Type.INFO);
    }

    /**
     * 显示一则指定类型的通知，用户手动关闭
     *
     * @param message 通知信息
     * @param type    通知类型
     */
    protected void showNotification(String message, Notification.Type type) {
        showNotification(message, type, 0);
    }

    /**
     * 显示一则指定类型的通知，自动关闭，默认显示一秒
     *
     * @param message 通知信息
     */
    protected void showNotificationAutoClose(String message) {
        showNotificationAutoClose(message, Notification.Type.INFO);
    }

    protected void showNotificationAutoClose(String message, Notification.Type type) {
        showNotification(message, type, Notification.DEFAULT_DELAY);
    }

    /**
     * 显示一则指定类型的通知，自动关闭，指定显示时间
     *
     * @param message      通知信息
     * @param type         通知类型
     * @param milliseconds 延迟时间 毫秒
     */
    protected void showNotification(String message, Notification.Type type, long milliseconds) {
        Parent root = getRoot();
        if (!(root instanceof Pane)) {
            System.err.println("owner 必须是 Pane 或其子类");
            return;
        }
        Notification.showAutoClose((Pane) root, message, type, milliseconds);
    }

    /**
     * 异步执行任务
     *
     * @param task 任务
     */
    protected void runAsync(Task<?> task) {
        CompletableFuture.runAsync(task);
    }

    /**
     * 被 @Receiver 注解注释的方法信息，用于缓存被注释的方法
     */
    private static class ViewReceiverMethodInfo {

        /**
         * Receiver 方法所属的视图对象
         */
        private View view;
        /**
         * Receiver 方法
         */
        private Method method;
        /**
         * 对应 View 窗体关闭时，是否接受消息
         */
        private boolean whenHidden;
        /**
         * Receiver 名字
         */
        private String receiverName;

        private View getView() {
            return view;
        }

        private void setView(View view) {
            this.view = view;
        }

        private Method getMethod() {
            return method;
        }

        private void setMethod(Method method) {
            this.method = method;
        }

        private boolean isWhenHidden() {
            return whenHidden;
        }

        private void setWhenHidden(boolean whenHidden) {
            this.whenHidden = whenHidden;
        }

        public String getReceiverName() {
            return receiverName;
        }

        private void setReceiverName(String receiverName) {
            this.receiverName = receiverName;
        }
    }
}


