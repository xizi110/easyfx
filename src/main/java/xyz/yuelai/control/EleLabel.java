package xyz.yuelai.control;

import javafx.scene.control.Labeled;
import javafx.scene.control.Skin;
import xyz.yuelai.skin.EleLabelSkin;

/**
 * @author zhong
 * @date 2020-11-09 10:07:40 周一
 */
public class EleLabel extends Labeled {

    public EleLabel() {
    }

    public EleLabel(String s) {
        setText(s);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new EleLabelSkin(this);
    }
}
