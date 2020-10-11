package xyz.yuelai.control;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author zhong
 * @date 2020-09-30 13:46:43 周三
 */
public class Notification {

    public enum Type {
        /**
         * 信息展示
         */
        INFO,
        /**
         * 警告用户
         */
        WARNING,
        /**
         * 提示用户错误
         */
        ERROR,
        /**
         * 告知用户成功
         */
        SUCCESS
    }

    private HBox root;
    @FXML
    private Label iconClose;
    @FXML
    private Label title;
    @FXML
    private Label content;
    private Pane owner;

    private Notification() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Notification.fxml"));
        fxmlLoader.setController(this);
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Map<Pane, VBox> notificationContainerMap = new HashMap<>();

    public static final long DEFAULT_DELAY = 4500;

    public static void show(Pane owner, String message) {
        showAutoClose(owner, message, Type.INFO, 0);
    }

    public static void show(Pane owner, String message, Type type) {
        showAutoClose(owner, message, type, 0);
    }

    public static void showAutoClose(Pane owner, String message, Type type) {
        showAutoClose(owner, message, type, DEFAULT_DELAY);
    }

    /**
     * 显示通知
     *
     * @param owner   通知显示的面板
     * @param message 通知消息
     * @param type    通知类型
     * @param delay   通知停留时间，毫秒，为 0 则不会自动关闭
     */
    public static void showAutoClose(Pane owner, String message, Type type, long delay) {
        Notification instance = new Notification();
        instance.owner = owner;
        instance.content.setText(message);
        if (type == null){
            type = Type.INFO;
            instance.title.setText("信息");
        }
        switch (type){
            case INFO:
                instance.title.setText("信息");
                break;
            case WARNING:
                instance.title.setText("警告");
                break;
            case ERROR:
                instance.title.setText("错误");
                break;
            default:
                instance.title.setText("成功");
                break;
        }
        instance.root.getStyleClass().add("notification-" + type.name().toLowerCase());
        VBox container = notificationContainerMap.computeIfAbsent(owner, pane -> {
            VBox vBox = new VBox();
            vBox.setManaged(false);
            vBox.setSpacing(15);
            vBox.setLayoutY(15);
            vBox.layoutXProperty().bind(owner.widthProperty().subtract(instance.root.widthProperty()).subtract(17));
            owner.getChildren().add(vBox);
            return vBox;
        });
        container.getChildren().add(instance.root);

        if (delay < 0) {
            delay = DEFAULT_DELAY;
        }
        // 延时 0ms 则不会自动关闭
        if (delay != 0){
            instance.schedule(new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> container.getChildren().remove(instance.root));
                }
            }, delay);
        }

        instance.iconClose.setOnMouseClicked(event -> container.getChildren().remove(instance.root));
    }

    private static Timer timer;

    private void schedule(TimerTask task, long milliseconds) {
        if (timer == null) {
            timer = new Timer(true);
        }
        timer.schedule(task, milliseconds);
    }
}
