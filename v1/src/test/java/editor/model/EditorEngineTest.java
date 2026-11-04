package editor.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Le moteur se teste sans aucune interface graphique (intérêt du MVC). */
class EditorEngineTest {
    private final EditorEngine e = new EditorEngine();

    @Test
    void insertPlacesCursorAfterText() {
        e.insert("abc");
        assertEquals("abc", e.getText());
        assertEquals(3, e.getSelStart());
        assertEquals(3, e.getSelEnd());
    }

    @Test
    void insertReplacesSelection() {
        e.insert("abcd");
        e.moveCursor(-3);
        e.extendSelection(2);
        e.insert("X");
        assertEquals("aXd", e.getText());
        assertEquals(2, e.getSelStart());
    }

    @Test
    void cursorIsClampedToText() {
        e.insert("ab");
        e.moveCursor(10);
        assertEquals(2, e.getSelStart());
        e.moveCursor(-10);
        assertEquals(0, e.getSelStart());
    }

    @Test
    void extendSelectionBothDirectionsAndShrink() {
        e.insert("abcd");
        e.extendSelection(-2);
        assertEquals(2, e.getSelStart());
        assertEquals(4, e.getSelEnd());
        e.extendSelection(1);
        assertEquals(3, e.getSelStart());
        e.extendSelection(10);
        assertEquals(4, e.getSelStart());
        assertEquals(4, e.getSelEnd());
    }

    @Test
    void backspaceDeletesCharBeforeCursor() {
        e.insert("abc");
        e.backspace();
        assertEquals("ab", e.getText());
        assertEquals(2, e.getSelStart());
    }

    @Test
    void backspaceDeletesSelection() {
        e.insert("abcd");
        e.extendSelection(-2);
        e.backspace();
        assertEquals("ab", e.getText());
        assertEquals(2, e.getSelStart());
        assertEquals(2, e.getSelEnd());
    }

    @Test
    void backspaceAtStartOrOnEmptyBufferDoesNothing() {
        e.backspace();
        assertEquals("", e.getText());
        e.insert("a");
        e.moveCursor(-1);
        e.backspace();
        assertEquals("a", e.getText());
    }

    @Test
    void copyThenPasteReplacesSelectionAndMovesCursorAfter() {
        e.insert("hello");
        e.moveCursor(-5);
        e.extendSelection(5);
        e.copy();
        e.moveCursor(1);
        e.paste();
        assertEquals("hellohello", e.getText());
        assertEquals(10, e.getSelStart());
        assertEquals(10, e.getSelEnd());
    }

    @Test
    void cutRemovesSelectionAndFillsClipboard() {
        e.insert("abcd");
        e.extendSelection(-2);
        e.cut();
        assertEquals("ab", e.getText());
        e.paste();
        assertEquals("abcd", e.getText());
    }

    @Test
    void copyAndCutWithEmptySelectionLeaveClipboardUntouched() {
        e.insert("abc");
        e.extendSelection(-3);
        e.copy();
        e.moveCursor(1);
        e.insert("Z");
        e.copy();
        e.cut();
        e.paste();
        assertEquals("abcZabc", e.getText());
    }

    @Test
    void pasteWithEmptyClipboardDoesNothing() {
        e.insert("abc");
        e.paste();
        assertEquals("abc", e.getText());
    }

    @Test
    void observersAreNotifiedOnChange() {
        int[] calls = {0};
        e.addObserver(() -> calls[0]++);
        e.insert("a");
        e.moveCursor(-1);
        assertEquals(2, calls[0]);
    }
}
