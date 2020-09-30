package xyz.yuelai;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.EventDispatchChain;
import javafx.event.EventTarget;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.Window;

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
 *
 * 所有 view 的父类。创建 view 对象需要用 {@link #createView(Class)} 方法创建，
 * 否则使用 new ViewImpl() 创建的 view 对象，不能被 View 管理
 */
@SuppressWarnings("unused")
public abstract class View implements Initializable, EventTarget {

    /**
     * 当前视图根对象
     */
    private Parent root;

    /**
     * 当前视图的窗体，未显示窗体时为null
     */
    private Window window;
    /**
     * 当前窗体场景中包含的 view
     */
    private static Map<Scene, List<View>> sceneViewMap = new HashMap<>();

    /**
     * 当前系统中激活的 view
     */
    private static List<View> views = new LinkedList<>();

    /**
     * 构造器之前初始化
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public View() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml()));
            loader.setController(this);
            root = loader.load();
            root.sceneProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    List<View> views = sceneViewMap.computeIfAbsent(newValue, k -> new LinkedList<>());
                    views.add(this);
                    newValue.windowProperty().addListener((observable1, oldValue1, newValue1) -> {
                        window = newValue1;
                        if (window != null) {
                            window.setOnHidden(event -> {
                                // 窗体隐藏之后，移除对应的 view
                                View.views.removeAll(sceneViewMap.get(window.getScene()));
                                sceneViewMap.remove(window.getScene());
                                onWindowHidden();
                            });
                            window.setOnShowing(event -> onWindowShowing());
                        }
                    });
                }

            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 异步发送消息给其他激活中的 View ，其他 View 类中被 @Receiver 注解注释的方法且 name 为指定 receiverName 的方法，将被调用
     *
     * @param receiverName 接受者的名字
     * @param data         接收者方法参数
     */
    public void sendMessageWithAsync(String receiverName, Object... data) {
        CompletableFuture.runAsync(() -> {
            for (View view : views) {
                Class<? extends View> viewClass = view.getClass();
                Method[] methods = viewClass.getMethods();
                for (Method method : methods) {
                    Receiver annotation = method.getAnnotation(Receiver.class);
                    if (annotation != null && annotation.name().equals(receiverName)) {
                        // 接收者方法运行在JavaFX Application Thread
                        Platform.runLater(() -> {
                            try {
                                method.invoke(view, data);
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        });
                    }
                }
            }
        });
    }

    /**
     * 获取当前视图的窗体
     *
     * @return 当前视图的窗体
     */
    public Window getWindow() {
        return window;
    }

    /**
     * 在窗口显示之前调用，子类可以覆盖实现
     */
    public void onWindowShowing() {
    }

    /**
     * 在窗口关闭之后调用，子类可以覆盖实现
     */
    public void onWindowHidden() {
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
            views.add(instance);
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
    public Stage createWindow(Class<? extends View> viewClass) {
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
    public Optional<ButtonType> alert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        return alert.showAndWait();
    }

    /**
     * 弹窗，指定弹窗类型
     *
     * @param message   弹窗提示信息
     * @param alertType 弹窗类型
     * @return 接收用户点击按钮类型
     */
    public Optional<ButtonType> alert(String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setContentText(message);
        return alert.showAndWait();
    }

    /**
     * 异步执行任务
     *
     * @param task 任务
     */
    public void runAsync(Task<?> task) {
        CompletableFuture.runAsync(task);
    }
}


