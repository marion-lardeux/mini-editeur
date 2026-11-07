package editor.command;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import editor.model.EditorEngine;
import editor.model.EditorMemento;

public class CommandManager {
    private final EditorEngine engine;
    private final Deque<EditorMemento> undoStack = new ArrayDeque<>();
    private final Deque<EditorMemento> redoStack = new ArrayDeque<>();
    private record Step(Command command, boolean undoable) { }

    private final List<Step> script = new ArrayList<>();
    private boolean recording = false;

    public CommandManager(EditorEngine engine) {
        this.engine = engine;
    }

    public void execute(Command c, boolean undoable) {
        if (undoable) {
            EditorMemento before = engine.save();
            c.execute();
            if (!engine.save().sameText(before)) {
                undoStack.push(before);
                redoStack.clear();
            }
        } else {
            c.execute();
        }
        if (recording) {
            script.add(new Step(c, undoable));
        }
    }

    public void undo() {
        if (undoStack.isEmpty()) {
            return;
        }
        redoStack.push(engine.save());
        engine.restore(undoStack.pop());
    }

    public void redo() {
        if (redoStack.isEmpty()) {
            return;
        }
        undoStack.push(engine.save());
        engine.restore(redoStack.pop());
    }

    public void startRecording() {
        script.clear();
        recording = true;
    }

    public void stopRecording() {
        recording = false;
    }

    public void replay() {
        if (recording) {
            return;
        }
        for (Step step : new ArrayList<>(script)) {
            execute(step.command(), step.undoable());
        }
    }

    public boolean isRecording() { return recording; }
    public boolean canUndo() { return !undoStack.isEmpty(); }
    public boolean canRedo() { return !redoStack.isEmpty(); }
    public int scriptSize() { return script.size(); }
}
