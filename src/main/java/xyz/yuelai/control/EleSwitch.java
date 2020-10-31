package xyz.yuelai.control;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Labeled;
import javafx.scene.control.Skin;
import javafx.scene.paint.Color;
import xyz.yuelai.skin.SwitchSkin;

/**
 * @author zhong
 * @date 2020-10-30 16:46:21 周五
 */
public class EleSwitch extends Labeled {

    public EleSwitch() {
        this(false);
    }

    public EleSwitch(boolean active) {
        activeProperty().set(active);
        setPrefSize(40, 20);
        getStyleClass().add(DEFAULT_STYLE_CLASS);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new SwitchSkin(this);
    }

    /**
     * 是否处于激活状态，白色圆点在右边代表激活，在左边代表未激活，默认未激活状态
     */
    private BooleanProperty active;

    public boolean isActive() {
        return active != null && active.get();
    }

    public BooleanProperty activeProperty() {
        if (active == null) {
            active = new SimpleBooleanProperty() {
                @Override
                public Object getBean() {
                    return EleSwitch.this;
                }

                @Override
                public String getName() {
                    return "active";
                }
            };
        }
        return active;
    }

    public void setActive(boolean active) {
        this.activeProperty().set(active);
    }

    /**
     * 激活时的变色条颜色
     */
    private ObjectProperty<Color> activeColor;

    public ObjectProperty<Color> activeColorProperty() {
        if (activeColor == null) {
            activeColor = new SimpleObjectProperty<>(DEFAULT_ACTIVE_COLOR) {
                @Override
                public Object getBean() {
                    return EleSwitch.this;
                }

                @Override
                public String getName() {
                    return "activeColor";
                }
            };
        }
        return activeColor;
    }

    public Color getActiveColor() {
        return activeColorProperty().get();
    }

    public void setActiveColor(Color activeColor) {
        this.activeColorProperty().set(activeColor);
    }

    /**
     * 未激活时变色条颜色
     */
    private ObjectProperty<Color> inactiveColor;

    public ObjectProperty<Color> inactiveColorProperty() {
        if (inactiveColor == null) {
            inactiveColor = new SimpleObjectProperty<>(DEFAULT_INACTIVE_COLOR) {
                @Override
                public Object getBean() {
                    return EleSwitch.this;
                }

                @Override
                public String getName() {
                    return "inactiveColor";
                }
            };
        }
        return inactiveColor;
    }

    public Color getInActiveColor() {
        return inactiveColorProperty().get();
    }

    public void setInActiveColor(Color inactiveColor) {
        this.inactiveColorProperty().set(inactiveColor);
    }

    private static final String DEFAULT_STYLE_CLASS = "switch";
    private static final Color DEFAULT_ACTIVE_COLOR = Color.valueOf("#13ce66");
    private static final Color DEFAULT_INACTIVE_COLOR = Color.valueOf("#ff4949");

}
