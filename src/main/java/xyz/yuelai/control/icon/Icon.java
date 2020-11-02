package xyz.yuelai.control.icon;

import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.css.converter.StringConverter;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
@SuppressWarnings("unused")
public class Icon extends Text {

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        return STYLEABLES;
    }

    private StyleableObjectProperty<String> iconCode;

    private void setIconCode(String value) {
        iconCodeProperty().set(value);
    }

    private String getIconCode() {
        return iconCode == null ? "" : iconCode.get();
    }

    private StyleableObjectProperty<String> iconCodeProperty() {
        if (iconCode == null) {
            iconCode = new StyleableObjectProperty<>("") {

                @Override
                public CssMetaData<Icon, String> getCssMetaData() {
                    return ICON_CODE;
                }

                @Override
                public Object getBean() {
                    return Icon.this;
                }

                @Override
                public String getName() {
                    return "iconCode";
                }

                @Override
                protected void invalidated() {
                    String uCode = get();
                    setText(uCode);
                }
            };
        }
        return iconCode;
    }

    private static final CssMetaData<Icon, String> ICON_CODE =
            new CssMetaData<>("-fx-icon-code",
                    StringConverter.getInstance(), "") {

                @Override
                public boolean isSettable(Icon n) {
                    return n.iconCode == null || !n.iconCode.isBound();
                }

                @Override
                public StyleableProperty<String> getStyleableProperty(Icon n) {
                    return n.iconCodeProperty();
                }
            };
    private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

    static {
        final List<CssMetaData<? extends Styleable, ?>> styleables =
                new ArrayList<>(Text.getClassCssMetaData());
        Collections.addAll(styleables,
                ICON_CODE
        );
        STYLEABLES = Collections.unmodifiableList(styleables);
    }
}
