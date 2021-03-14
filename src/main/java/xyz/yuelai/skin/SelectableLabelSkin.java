package xyz.yuelai.skin;

import com.sun.javafx.util.Utils;
import javafx.geometry.Point2D;
import javafx.scene.control.SkinBase;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.scene.text.HitInfo;
import javafx.scene.text.Text;
import xyz.yuelai.control.SelectableLabel;

import java.text.BreakIterator;

public class SelectableLabelSkin extends SkinBase<SelectableLabel> {

    private Path selectedBg;
    private SelectableLabel control;
    private Text text;


    private HitInfo selectStart;
    private HitInfo selectEnd;
    private BreakIterator wordIterator;


    public SelectableLabelSkin(SelectableLabel control) {
        super(control);
        // init control
        this.control = control;
        text = new Text(control.getText());
        text.setSelectionFill(Color.WHEAT);

        selectedBg = new Path();
        selectedBg.setStroke(null);

        selectedBg.setFill(Color.valueOf("#308dfc"));

        // init event
        control.setOnMousePressed(event -> {
            selectStart = getIndex(event.getX(), event.getY());
            clearSelected();
        });

        text.setOnMouseDragged(event -> {
            selectEnd = getIndex(event.getX(), event.getY());
            int start;
            int end;
            // 从后往前选择
            if (selectEnd.getCharIndex() < selectStart.getCharIndex()) {
                start = selectEnd.isLeading() ? selectEnd.getCharIndex() : selectEnd.getCharIndex() + 1;
                end = selectStart.isLeading() ? selectStart.getCharIndex() : selectStart.getCharIndex() + 1;
            } else {
                // 从前往后选择
                start = selectEnd.isLeading() ? selectStart.getCharIndex() : selectStart.getCharIndex();
                end = selectEnd.isLeading() ? selectEnd.getCharIndex() : selectEnd.getCharIndex() + 1;
            }
            selectText(start, end);
        });

        text.setOnMouseClicked(event -> {
            int clickCount = event.getClickCount();
            if (clickCount == 2) {
                previousWord(getIndex(event.getX(), event.getY()).getCharIndex());
            } else if (clickCount == 3) {
                selectText(0, text.getText().length());
            }
        });

        control.setOnKeyPressed(event -> {
            if (event.isControlDown() && (event.getCode() == KeyCode.C)) {
                if (selectText != null && selectText.length() > 0) {
                    final ClipboardContent content = new ClipboardContent();
                    content.putString(selectText);
                    Clipboard.getSystemClipboard().setContent(content);
                }
            }
        });
        getChildren().addAll(selectedBg, text);
    }

    private void previousWord(int index) {
        final int textLength = text.getText().length();
        final String text = this.text.getText();
        if (textLength <= 0) {
            return;
        }

        if (wordIterator == null) {
            wordIterator = BreakIterator.getWordInstance();
        }
        wordIterator.setText(text);

        int pos1 = wordIterator.preceding(Utils.clamp(0, index, textLength));
        int pos2 = wordIterator.following(Utils.clamp(0, index, textLength));
        selectText(pos1, pos2);
    }

    private String selectText;

    private void selectText(int begin, int end) {
        text.setSelectionStart(begin);
        text.setSelectionEnd(end);
        selectedBg.layoutXProperty().bind(text.layoutXProperty());
        selectedBg.layoutYProperty().bind(text.layoutYProperty());
        selectedBg.getElements().setAll(text.getSelectionShape());
        selectText = text.getText().substring(begin, Math.min(end, text.getText().length()));
    }

    private void clearSelected() {
        text.setSelectionStart(0);
        text.setSelectionEnd(0);
        selectedBg.getElements().clear();
    }

    private HitInfo getIndex(double x, double y) {
        Point2D p = new Point2D(x - text.getLayoutX(), y - text.getLayoutY());
        return text.hitTest(p);
    }
}
