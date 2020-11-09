package xyz.yuelai.control;


import javafx.scene.Node;
import javafx.scene.control.Labeled;
import javafx.scene.control.Skin;
import xyz.yuelai.skin.EleLabelSkin;

public class EleLabel extends Labeled {

    public EleLabel() {
    }

    public EleLabel(String text) {
        super(text);
    }

    public EleLabel(String text, Node graphic) {
        super(text, graphic);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new EleLabelSkin(this);
    }
}
