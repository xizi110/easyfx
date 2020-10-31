package xyz.yuelai.skin;

import javafx.animation.FillTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Cursor;
import javafx.scene.control.skin.LabeledSkinBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import xyz.yuelai.control.EleSwitch;

/**
 * @author zhong
 * @date 2020-10-30 16:49:00 周五
 */
public class SwitchSkin extends LabeledSkinBase<EleSwitch> {

    private EleSwitch eleSwitch;
    /**
     * 底部容器
     */
    private AnchorPane bottom = new AnchorPane();
    /**
     * 白色圆点
     */
    private StackPane circle = new StackPane();
    /**
     * 变色条
     */
    private Rectangle mask = new Rectangle();

    /**
     * 白色圆点移动动画
     */
    private TranslateTransition translateTransition = new TranslateTransition();
    /**
     * 变色条变色动画
     */
    private FillTransition fillTransition = new FillTransition();
    /**
     * 合并上面两个动画，同时执行
     */
    private ParallelTransition parallelTransition = new ParallelTransition();


    public SwitchSkin(EleSwitch eleSwitch) {
        super(eleSwitch);
        this.eleSwitch = eleSwitch;
        // 用于变色
        mask.widthProperty().bind(bottom.widthProperty());
        mask.heightProperty().bind(bottom.heightProperty());
        mask.arcWidthProperty().bind(mask.heightProperty());
        mask.arcHeightProperty().bind(mask.heightProperty());
        mask.setFill(eleSwitch.isActive() ? eleSwitch.getActiveColor() : eleSwitch.getInActiveColor());
        mask.getStyleClass().add("switch-mask");

        // 小圆点
        circle.translateYProperty().bind(eleSwitch.heightProperty().subtract(circle.heightProperty()).divide(2));
        circle.getStyleClass().add("switch-circle");

        // 容器
        bottom.getChildren().addAll(mask, circle);
        bottom.prefWidthProperty().bind(eleSwitch.widthProperty());
        bottom.prefHeightProperty().bind(eleSwitch.heightProperty());
        bottom.setCursor(Cursor.HAND);

        // 动画
        parallelTransition.getChildren().addAll(translateTransition, fillTransition);
        translateTransition.setInterpolator(Interpolator.EASE_BOTH);
        translateTransition.setNode(circle);
        fillTransition.setInterpolator(Interpolator.EASE_BOTH);
        fillTransition.setShape(mask);

        // 点击事件
        bottom.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> animate());
        updateChildren();
    }

    private void animate() {
        setTranslate();
        setFill();
        parallelTransition.play();
        parallelTransition.setOnFinished(event -> eleSwitch.setActive(eleSwitch.activeProperty().not().get()));
    }

    /**
     * 设置变色动画
     */
    public void setFill() {
        fillTransition.setDuration(Duration.millis(300));
        if (eleSwitch.isActive()) {
            fillTransition.setFromValue(eleSwitch.getActiveColor());
            fillTransition.setToValue(eleSwitch.getInActiveColor());
        } else {
            fillTransition.setFromValue(eleSwitch.getInActiveColor());
            fillTransition.setToValue(eleSwitch.getActiveColor());
        }
    }

    /**
     * 设置移动动画
     */
    private void setTranslate() {
        translateTransition.setDuration(Duration.millis(300));
        if (eleSwitch.isActive()) {
            translateTransition.setFromX(eleSwitch.getWidth() - circle.getWidth() - 2);
            translateTransition.setToX(2);
        } else {
            translateTransition.setFromX(2);
            translateTransition.setToX(eleSwitch.getWidth() - circle.getWidth() - 2);
        }
    }

    @Override
    protected void updateChildren() {
        super.updateChildren();
        if (bottom != null) {
            getChildren().add(bottom);
        }
    }

    @Override
    protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return super.computeMinWidth(height, topInset, rightInset, bottomInset, leftInset) + snapSizeX(bottom.minWidth(-1));
    }

    @Override
    protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return Math.max(super.computeMinHeight(width - bottom.minWidth(-1), topInset, rightInset, bottomInset, leftInset),
                topInset + bottom.minHeight(-1) + bottomInset);
    }

    @Override
    protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return super.computePrefWidth(height, topInset, rightInset, bottomInset, leftInset) + snapSizeX(bottom.prefWidth(-1));
    }

    @Override
    protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return Math.max(super.computePrefHeight(width - bottom.prefWidth(-1), topInset, rightInset, bottomInset, leftInset),
                topInset + bottom.prefHeight(-1) + bottomInset);
    }

    @Override
    protected void layoutChildren(final double x, final double y,
                                  final double w, final double h) {
        final double boxWidth = snapSizeX(bottom.prefWidth(-1));
        final double boxHeight = snapSizeY(bottom.prefHeight(-1));
        bottom.resize(boxWidth, boxHeight);
    }
}
