package editor.controller;

import editor.model.EditorEngine;

public class EditorController {
    private final EditorEngine engine;

    public EditorController(EditorEngine engine) {
        this.engine = engine;
    }

    public void copy() {
        engine.copy();
    }

    public void cut() {
        engine.cut();
    }

    public void paste() {
        engine.paste();
    }

    public void insert(String s) {
        engine.insert(s);
    }

    public void delete() {
        engine.delete();
    }

    public void moveCursor(int delta) {
        engine.moveCursor(delta);
    }

    public void extendSelection(int delta) {
        engine.extendSelection(delta);
    }
}
