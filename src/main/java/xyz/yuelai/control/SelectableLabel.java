package xyz.yuelai.control;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Cursor;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import xyz.yuelai.skin.SelectableLabelSkin;

public class SelectableLabel extends Control {


    public SelectableLabel() {
        this("");
    }

    public SelectableLabel(String text) {
        setCursor(Cursor.TEXT);
        setText(text);
    }

    public final StringProperty textProperty() {
        if (text == null) {
            text = new SimpleStringProperty(this, "text", "");
        }
        return text;
    }

    private StringProperty text;

    public final void setText(String value) {
        textProperty().setValue(value);
    }

    public final String getText() {
        return text == null ? "" : text.getValue();
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new SelectableLabelSkin(this);
    }
}
