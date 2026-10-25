package editor.model;

import java.util.ArrayList;
import java.util.List;

public class EditorEngine implements EditorReader {
    private final Buffer buffer = new Buffer();
    private final Selection selection = new Selection();
    private final Clipboard clipboard = new Clipboard();
    private final List<EditorObserver> observers = new ArrayList<>();

    public void copy() {
        if (selection.isEmpty()) {
            return;
        }
        clipboard.set(buffer.substring(selection.getStart(), selection.getEnd()));
    }

    public void cut() {
        if (selection.isEmpty()) {
            return;
        }
        copy();
        int start = selection.getStart();
        buffer.delete(start, selection.getEnd());
        selection.set(start, start);
        notifyObservers();
    }

    public void paste() {
        if (clipboard.isEmpty()) {
            return;
        }
        replaceSelection(clipboard.get());
    }

    public void insert(String s) {
        if (s == null || (s.isEmpty() && selection.isEmpty())) {
            return;
        }
        replaceSelection(s);
    }

    public void delete() {
        int start = selection.getStart();
        int end = selection.getEnd();
        if (selection.isEmpty()) {
            if (start >= buffer.length()) {
                return;
            }
            end = start + 1;
        }
        buffer.delete(start, end);
        selection.set(start, start);
        notifyObservers();
    }

    public void moveCursor(int delta) {
        int pos;
        if (selection.isEmpty()) {
            pos = clamp(selection.getStart() + delta, 0, buffer.length());
        } else {
            pos = (delta < 0) ? selection.getStart() : selection.getEnd();
        }
        selection.set(pos, pos);
        notifyObservers();
    }

    public void extendSelection(int delta) {
        int anchor = selection.getAnchor();
        int active = (selection.getStart() == anchor) ? selection.getEnd() : selection.getStart();
        int newActive = clamp(active + delta, 0, buffer.length());
        selection.select(anchor, newActive);
        notifyObservers();
    }

    public void addObserver(EditorObserver o) {
        observers.add(o);
    }

    private void notifyObservers() {
        for (EditorObserver o : new ArrayList<>(observers)) {
            o.update();
        }
    }

    @Override
    public String getText() {
        return buffer.getText();
    }

    @Override
    public int getSelStart() {
        return selection.getStart();
    }

    @Override
    public int getSelEnd() {
        return selection.getEnd();
    }

    private void replaceSelection(String s) {
        int start = selection.getStart();
        buffer.delete(start, selection.getEnd());
        buffer.insert(start, s);
        int pos = start + s.length();
        selection.set(pos, pos);
        notifyObservers();
    }

    private static int clamp(int i, int min, int max) {
        return Math.max(min, Math.min(max, i));
    }
}
