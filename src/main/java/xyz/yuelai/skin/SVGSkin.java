package xyz.yuelai.skin;

import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.control.SkinBase;
import xyz.yuelai.control.SVG;

public class SVGSkin extends SkinBase<SVG> {

    private Group group;
    private SVG control;

    public SVGSkin(SVG control) {
        super(control);
        this.control = control;
        initialize();
        resizeSVG();
    }

    private void initialize() {
        group = new Group();
        group.getChildren().addAll(control.getSvgPaths());
        getChildren().add(group);
        // 不需要任何的鼠标事件
        consumeMouseEvents(false);
    }

    /**
     * 使用缩放将 SVG 调整为合适大小
     */
    private void resizeSVG() {
        Bounds boundsInParent = group.getBoundsInParent();
        double width = boundsInParent.getWidth();
        double height = boundsInParent.getHeight();
        if (width > height) {
            group.scaleXProperty().bind(control.sizeProperty().divide(width));
            group.scaleYProperty().bind(group.scaleXProperty());
        } else {
            group.scaleYProperty().bind(control.sizeProperty().divide(height));
            group.scaleXProperty().bind(group.scaleYProperty());
        }
    }

    @Override
    protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return control.getMinWidth();
    }

    @Override
    protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return control.getMinHeight();
    }

    @Override
    protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return control.getPrefWidth();

    }

    @Override
    protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return control.getPrefHeight();
    }
}
