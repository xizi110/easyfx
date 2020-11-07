package xyz.yuelai.skin;

import com.sun.javafx.tk.FontLoader;
import com.sun.javafx.tk.FontMetrics;
import com.sun.javafx.tk.Toolkit;
import javafx.collections.ObservableList;
import javafx.scene.control.SkinBase;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;
import xyz.yuelai.control.EleLabel;

import java.util.Arrays;
import java.util.Comparator;

public class EleLabelSkin extends SkinBase<EleLabel> {

    private EleLabel eleLabel;

    private TextArea textArea;

    public EleLabelSkin(EleLabel eleLabel) {
        super(eleLabel);
        this.eleLabel = eleLabel;

        String text = eleLabel.getText();

        textArea = new TextArea(text);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setFont(eleLabel.getFont());

        getChildren().add(textArea);

    }

    private FontMetrics fontMetrics;

    private float stringWidth(Font font, String str) {
        if (fontMetrics == null) {
            FontLoader fontLoader = Toolkit.getToolkit().getFontLoader();
            fontMetrics = fontLoader.getFontMetrics(font);
        }
        char[] chars = str.toCharArray();
        float width = 0;
        for (char c : chars) {
            width += fontMetrics.getCharWidth(c);
        }
        return width;
    }

    @Override
    protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {

        String s = Arrays.stream(textArea.getText().split("\n")).max(Comparator.comparingInt(String::length)).get();

        textArea.setPrefWidth(stringWidth(textArea.getFont(), s));

        ObservableList<CharSequence> paragraphs = textArea.getParagraphs();
        int lineNum = paragraphs.size();
        for (CharSequence paragraph : paragraphs) {
            float stringWidth = stringWidth(eleLabel.getFont(), paragraph.toString());
            long rowCount = (long) Math.ceil(stringWidth / textArea.getPrefWidth());
            if (rowCount > 1) {
                lineNum += rowCount - 1;
            }
        }
        textArea.setPrefRowCount(lineNum);
        super.layoutChildren(contentX, contentY, contentWidth, contentHeight);
    }
}
