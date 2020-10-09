package xyz.yuelai.control;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

/**
 * @author zhong
 * @date 2020-09-30 13:46:43 周三
 */
public class Notification extends HBox {

    public enum NotificationType {
        /**
         * 信息展示
         */
        DEFAULT,
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

    private Label title;
    private Label closeIcon;
    private Label content;
    private NotificationType type;

    public Notification(String message) {
        this(message, NotificationType.DEFAULT);
    }

    public Notification(String message, NotificationType type) {

        this.type = type;
        switch (type) {
            case INFO: {
                title = new Label("消息");
                break;
            }
            case SUCCESS: {
                title = new Label("成功");
                break;
            }
            case WARNING: {
                title = new Label("警告");
                break;
            }
            case ERROR: {
                title = new Label("错误");
                break;
            }
            default: {
                title = new Label("提示");
                break;
            }
        }
        content = new Label(message);

        initStyle();
        initLayout();

    }

    /**
     * 初始化样式
     */
    private void initStyle() {
        SVGPath svgPath = new SVGPath();
        svgPath.setContent("M764 215.008L512 467.008 260 215.008q-10.016-8.992-22.496-8.992t-22.016 9.504-9.504 22.016 8" +
                ".992 22.496l252 252-252 252q-12.992 12.992-8.512 31.008t22.016 22.496 31.488-8.512l252-252 252 252q10.0" +
                "16 8.992 22.496 8.992t22.016-9.504 9.504-22.016-8.992-22.496L556.992 512l252-252q12.992-12.992 8.512-31" +
                ".008t-22.496-22.496-31.008 8.512z");
        Region region = new Region();
        region.setShape(svgPath);
        region.setBackground(new Background(new BackgroundFill(Color.valueOf("#979aa0"), CornerRadii.EMPTY, Insets.EMPTY)));

        closeIcon = new Label(null, region);
        closeIcon.setMinSize(0, 0);
        closeIcon.setPrefSize(10, 10);

        closeIcon.hoverProperty().addListener((observableValue, oldVal, newVal) -> {
            if (newVal){
                closeIcon.getGraphic().setStyle("-fx-background-color: #000");
            }else {
                closeIcon.getGraphic().setStyle("-fx-background-color: #979aa0");
            }
        });

        closeIcon.setOnMouseClicked(mouseEvent -> {
            if (runnable != null){
                runnable.run();
            }
        });

        getStylesheets().add(getClass().getResource("/css/notification.css").toExternalForm());
        getStyleClass().add("notification");
        title.getStyleClass().add("title");
        closeIcon.getStyleClass().add("close-icon");
        content.getStyleClass().add("content");

        setEffect(new DropShadow(12, 0, 2, Color.color(0, 0, 0, 0.1)));
    }

    /**
     * 初始化控件布局
     */
    private void initLayout() {
        VBox vBox = new VBox();
        HBox.setHgrow(vBox, Priority.ALWAYS);

        HBox hBox = new HBox();
        title.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(title, Priority.ALWAYS);
        hBox.setAlignment(Pos.CENTER);
        hBox.getChildren().addAll(title, closeIcon);

        HBox hBox2 = new HBox();
        content.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(content, Priority.ALWAYS);
        hBox2.getChildren().add(content);

        vBox.getChildren().addAll(hBox, hBox2);
        HBox.setMargin(vBox, new Insets(0, 8, 0, 13));

        if (type != NotificationType.DEFAULT) {
            Label label = new Label(null, getIcon(type));
            getChildren().add(label);
        }

        getChildren().add(vBox);

    }

    /**
     * 根据 type 创建图标
     *
     * @param type 图表类型
     * @return
     */
    private Region getIcon(NotificationType type) {
        SVGPath svgPath = new SVGPath();
        Region region = new Region();
        region.setShape(svgPath);
        region.setPrefSize(22, 22);
        String content;
        switch (type) {
            case INFO: {
                content = "M512 64q190.016 4.992 316.512 131.488T960 512q-4.992 190.016-131.488 316.512T512 960q-190.016-" +
                        "4.992-316.512-131.488T64 512q4.992-190.016 131.488-316.512T512 64z m67.008 275.008q26.016 0 43.0" +
                        "08-15.488t16.992-41.504-16.992-41.504-42.496-15.488-42.496 15.488-16.992 41.504 16.992 41.504 42" +
                        ".016 15.488z m12 360q0-6.016 0.992-16t0-19.008l-52.992 60.992q-8 8.992-16.512 14.016t-14.496 3.0" +
                        "08q-8.992-4-8-14.016l88-276.992q4.992-28-8.992-48t-44.992-24q-35.008 0.992-76.512 29.504t-72.512" +
                        " 72.512v15.008q-0.992 10.016 0 19.008l52.992-60.992q8-8.992 16.512-14.016t13.504-3.008q10.016 4." +
                        "992 7.008 16l-87.008 276q-7.008 24.992 7.008 44.512t48.992 26.496q50.016-0.992 84-28.992t63.008-72z";
                region.setBackground(new Background(new BackgroundFill(Color.valueOf("#333333"), CornerRadii.EMPTY, Insets.EMPTY)));
                break;
            }
            case SUCCESS: {
                content = "M512 64q190.016 4.992 316.512 131.488T960 512q-4.992 190.016-131.488 316.512T512 960q-190.016" +
                        "-4.992-316.512-131.488T64 512q4.992-190.016 131.488-316.512T512 64z m-56 536l-99.008-99.008q-12" +
                        "-11.008-27.488-11.008t-27.008 11.488-11.488 26.496 11.008 27.008l127.008 127.008q11.008 11.008 " +
                        "27.008 11.008t27.008-11.008l263.008-263.008q15.008-15.008 9.504-36.512t-27.008-27.008-36.512 9.504z";
                region.setBackground(new Background(new BackgroundFill(Color.valueOf("#67c23a"), CornerRadii.EMPTY, Insets.EMPTY)));

                break;
            }
            case WARNING: {
                content = "M512 64q190.016 4.992 316.512 131.488T960 512q-4.992 190.016-131.488 316.512T512 960q-190.016" +
                        "-4.992-316.512-131.488T64 512q4.992-190.016 131.488-316.512T512 64z m0 192q-26.016 0-43.008 19." +
                        "008T453.984 320l23.008 256q2.016 14.016 11.488 22.496t23.488 8.512 23.488-8.512 11.488-22.496l2" +
                        "3.008-256q2.016-26.016-15.008-44.992T511.936 256z m0 512q22.016-0.992 36.512-15.008t14.496-36-1" +
                        "4.496-36.512T512 665.984t-36.512 14.496-14.496 36.512 14.496 36T512 768z";
                region.setBackground(new Background(new BackgroundFill(Color.valueOf("#e9ad52"), CornerRadii.EMPTY, Insets.EMPTY)));

                break;
            }
            case ERROR: {
                content = "M512 64q190.016 4.992 316.512 131.488T960 512q-4.992 190.016-131.488 316.512T512 " +
                        "960q-190.016-4.992-316.512-131.488T64 512q4.992-190.016 131.488-316.512T512 64z m0 394.016l-" +
                        "104-104q-12-12-27.488-12t-27.008 11.488-11.488 27.008 12 27.488l104 104-104 104q-12 12-12 27.4" +
                        "88t11.488 27.008 27.008 11.488 27.488-12l104-104 104 104q16 15.008 36.992 9.504t26.496-26.496-9" +
                        ".504-36.992L565.984 512l104-104q12-12 12-27.488t-11.488-27.008-27.008-11.488-27.488 12z";
                region.setBackground(new Background(new BackgroundFill(Color.valueOf("#f56c6c"), CornerRadii.EMPTY, Insets.EMPTY)));
                break;
            }
            default: {
                content = "";
            }
        }
        svgPath.setContent(content);
        return region;
    }

    private Runnable runnable;
    public void onClose(Runnable runnable) {
        this.runnable = runnable;
    }
}
