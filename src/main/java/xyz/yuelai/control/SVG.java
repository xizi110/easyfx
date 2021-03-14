package xyz.yuelai.control;

import javafx.beans.DefaultProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.WritableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.StyleableProperty;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.shape.SVGPath;
import xyz.yuelai.skin.SVGSkin;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@DefaultProperty("svgPaths")
public class SVG extends Control {


    /**
     * 默认的 SVG 大小
     */
    private static final double DEFAULT_SIZE = 12.0;

    /**
     * SVG 大小，正方形
     */
    private DoubleProperty size;

    public double getSize() {
        return sizeProperty().get();
    }

    public DoubleProperty sizeProperty() {
        if (size == null) {
            size = new SimpleDoubleProperty() {
                @Override
                public Object getBean() {
                    return SVG.this;
                }

                @Override
                public String getName() {
                    return "size";
                }

                @Override
                protected void invalidated() {
                    double v = get();
                    if (v > 0) {
                        setPrefSize(v, v);
                    }
                }
            };
        }
        return size;
    }

    public void setSize(double size) {
        this.sizeProperty().set(size);
    }


    public SVG() {
        this(DEFAULT_SIZE, Collections.emptyList());
    }

    /**
     * 使用默认大小构造 SVG
     *
     * @param svgPaths SVGPath 列表
     */
    public SVG(SVGPath... svgPaths) {
        this(DEFAULT_SIZE, svgPaths);
    }

    /**
     * 指定 SVG 大小的构造器
     *
     * @param size     svg 大小
     * @param svgPaths SVGPath列表
     */
    public SVG(double size, SVGPath... svgPaths) {
        this(size, Arrays.asList(svgPaths));
    }

    /**
     * 使用默认大小构造 SVG
     *
     * @param svgPaths SVGPath列表
     */
    public SVG(List<SVGPath> svgPaths) {
        this(DEFAULT_SIZE, svgPaths);
    }

    /**
     * 构造器
     *
     * @param size     svg 大小
     * @param svgPaths SVGPath列表
     */
    public SVG(double size, List<SVGPath> svgPaths) {
        setSize(size);
        this.svgPaths.setAll(svgPaths);
        ((StyleableProperty<Boolean>)(WritableValue<Boolean>)focusTraversableProperty()).applyStyle(null, Boolean.FALSE);
    }

    private ObservableList<SVGPath> svgPaths = FXCollections.observableArrayList();

    public ObservableList<SVGPath> getSvgPaths() {
        return svgPaths;
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new SVGSkin(this);
    }

}
