package xyz.yuelai.control;

import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import xyz.yuelai.skin.EleLabelSkin;

public class EleLabel extends Label {

    public EleLabel() {
        this(null);
    }

    public EleLabel(String text) {
        super(text);
        getStyleClass().add("ele-label");
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new EleLabelSkin(this);
    }
}
