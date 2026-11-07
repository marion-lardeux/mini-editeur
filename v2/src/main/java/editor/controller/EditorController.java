package editor.controller;

import editor.command.CommandManager;
import editor.model.EditorEngine;

public class EditorController {
    private final EditorEngine engine;
    private final CommandManager manager;

    public EditorController(EditorEngine engine, CommandManager manager) {
        this.engine = engine;
        this.manager = manager;
    }

    public void copy() { manager.execute(engine::copy, false); }
    public void cut() { manager.execute(engine::cut, true); }
    public void paste() { manager.execute(engine::paste, true); }
    public void insert(String s) { manager.execute(() -> engine.insert(s), true); }
    public void backspace() { manager.execute(engine::backspace, true); }
    public void moveCursor(int delta) { manager.execute(() -> engine.moveCursor(delta), false); }
    public void extendSelection(int delta) { manager.execute(() -> engine.extendSelection(delta), false); }

    public void undo() { manager.undo(); }
    public void redo() { manager.redo(); }
    public void toggleRecording() {
        if (manager.isRecording()) {
            manager.stopRecording();
        } else {
            manager.startRecording();
        }
    }
    public void replay() { manager.replay(); }
}
