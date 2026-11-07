package editor.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

import editor.model.EditorObserver;
import editor.model.EditorReader;

public class SwingView implements EditorObserver {
    private static final Highlighter.HighlightPainter SELECTION_PAINTER = new DefaultHighlighter.DefaultHighlightPainter(new Color(0x9EC9FF));
    private final EditorReader reader;
    private final EditorPane pane = new EditorPane();

    public SwingView(EditorReader reader) {
        this.reader = reader;
        pane.setEditable(false);
        pane.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16));
        pane.setMargin(new Insets(8, 8, 8, 8));
        pane.setFocusTraversalKeysEnabled(false);
        render();
    }

    @Override
    public void update() {
        render();
    }

    public JComponent getComponent() {
        return pane;
    }

    private void render() {
        String text = reader.getText();
        int start = reader.getSelStart();
        int end = reader.getSelEnd();

        pane.setText(text);
        Highlighter highlighter = pane.getHighlighter();
        highlighter.removeAllHighlights();
        if (start < end) {
            try {
                highlighter.addHighlight(start, end, SELECTION_PAINTER);
            } catch (BadLocationException e) {
                throw new IllegalStateException("Sélection hors du texte : " + start + "-" + end, e);
            }
            pane.setCursorPosition(-1);
        } else {
            pane.setCursorPosition(start);
        }
        pane.setCaretPosition(end);
    }

    private static class EditorPane extends JTextPane {
        private int cursorPosition = -1;
        void setCursorPosition(int position) {
            this.cursorPosition = position;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (cursorPosition < 0) {
                return;
            }
            try {
                Rectangle2D r = modelToView2D(cursorPosition);
                if (r != null) {
                    g.setColor(getForeground());
                    g.fillRect((int) r.getX(), (int) r.getY(), 2, (int) r.getHeight());
                }
            } catch (BadLocationException e) {
            }
        }
    }
}
