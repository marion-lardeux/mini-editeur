package editor.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import editor.controller.EditorController;
import editor.model.EditorEngine;

class CommandManagerTest {
    private final EditorEngine engine = new EditorEngine();
    private final CommandManager manager = new CommandManager(engine);
    private final EditorController c = new EditorController(engine, manager);

    private void type(String s) {
        for (char ch : s.toCharArray()) {
            c.insert(String.valueOf(ch));
        }
    }

    @Test
    void undoRedoCutAndPaste() {
        type("hello");
        c.moveCursor(-5);
        c.extendSelection(5);
        c.copy();
        c.moveCursor(1);
        c.paste();
        c.extendSelection(-5);
        c.cut();
        assertEquals("hello", engine.getText());

        c.undo();
        assertEquals("hellohello", engine.getText());
        assertEquals(5, engine.getSelStart());
        assertEquals(10, engine.getSelEnd());
        c.undo();
        assertEquals("hello", engine.getText());
        c.redo();
        assertEquals("hellohello", engine.getText());
        c.redo();
        assertEquals("hello", engine.getText());
    }

    @Test
    void undoGoesBackToTheVeryBeginning() {
        type("abc");
        c.backspace();
        while (manager.canUndo()) {
            c.undo();
        }
        assertEquals("", engine.getText());
        while (manager.canRedo()) {
            c.redo();
        }
        assertEquals("ab", engine.getText());
    }

    @Test
    void newActionClearsRedoStack() {
        type("ab");
        c.undo();
        assertTrue(manager.canRedo());
        type("Z");
        assertFalse(manager.canRedo());
    }

    @Test
    void noOpCommandIsNotAddedToHistory() {
        c.backspace();
        assertFalse(manager.canUndo());
    }

    @Test
    void cursorMovesAndCopyAreNotUndoable() {
        type("ab");
        c.undo();
        c.undo();
        c.moveCursor(1);
        c.extendSelection(1);
        c.copy();
        assertFalse(manager.canUndo());
    }

    @Test
    void undoDoesNotTouchClipboard() {
        type("ab");
        c.extendSelection(-2);
        c.cut();
        c.undo();
        assertEquals("ab", engine.getText());
        c.moveCursor(1);
        c.paste();
        assertEquals("abab", engine.getText());
    }

    @Test
    void recordAndReplayScript() {
        type("ab");
        c.toggleRecording();
        c.extendSelection(-2);
        c.copy();
        c.moveCursor(1);
        c.paste();
        c.toggleRecording();
        assertEquals(4, manager.scriptSize());
        assertEquals("abab", engine.getText());

        c.replay();
        assertEquals("ababab", engine.getText());
        c.undo();
        assertEquals("abab", engine.getText());
    }

    @Test
    void replayIsIgnoredWhileRecording() {
        type("a");
        c.toggleRecording();
        c.replay();
        assertEquals(0, manager.scriptSize());
        assertEquals("a", engine.getText());
    }

    @Test
    void replayedActionsAreNotRecordedAgain() {
        c.toggleRecording();
        type("x");
        c.toggleRecording();
        c.replay();
        c.replay();
        assertEquals(1, manager.scriptSize());
        assertEquals("xxx", engine.getText());
    }

    @Test
    void engineObserversAreNotifiedOnUndo() {
        int[] calls = {0};
        engine.addObserver(() -> calls[0]++);
        type("a");
        int before = calls[0];
        c.undo();
        assertTrue(calls[0] > before);
    }
}
