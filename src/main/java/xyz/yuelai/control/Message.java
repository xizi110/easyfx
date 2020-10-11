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
public class Message {

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
    private Label message;
    private Pane owner;

    private Message() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Message.fxml"));
        fxmlLoader.setController(this);
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Map<Pane, VBox> messageContainerMap = new HashMap<>();

    public static final long DEFAULT_DELAY = 3000;

    public static void show(Pane owner, String message) {
        show(owner, message, Type.INFO, DEFAULT_DELAY);
    }

    public static void show(Pane owner, String message, long delay) {
        show(owner, message, Type.INFO, delay);
    }

    public static void show(Pane owner, String message, Type type) {
        show(owner, message, type, DEFAULT_DELAY);
    }
    public static void show(Pane owner, String message, Type type, long delay) {
        Message instance = new Message();
        instance.owner = owner;
        instance.message.setText(message);
        if (type == null){
            type = Type.INFO;
        }
        instance.root.getStyleClass().add("message-" + type.name().toLowerCase());
        VBox container = messageContainerMap.computeIfAbsent(owner, pane -> {
            VBox vBox = new VBox();
            vBox.setManaged(false);
            vBox.setSpacing(15);
            vBox.setLayoutY(15);
            vBox.layoutXProperty().bind(owner.widthProperty().subtract(instance.root.widthProperty()).divide(2));
            owner.getChildren().add(vBox);
            return vBox;
        });
        container.getChildren().add(instance.root);

        if (delay <= 0){
            delay = DEFAULT_DELAY;
        }

        instance.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> container.getChildren().remove(instance.root));
            }
        }, delay);
    }

    private static Timer timer;

    private void schedule(TimerTask task, long milliseconds) {
        if (timer == null) {
            timer = new Timer(true);
        }
        timer.schedule(task, milliseconds);
    }
}
