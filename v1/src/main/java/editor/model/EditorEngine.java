package editor.model;
import java.util.ArrayList;
import java.util.List;

public class EditorEngine implements EditorReader {
    private final Buffer buffer = new Buffer();
    private final Selection selection = new Selection();
    private final List<EditorObserver> observers = new ArrayList<>();

    public void insert(String s) {
        if (s == null || s.isEmpty()) {
            return;
        }
        int pos = selection.getStart();
        buffer.insert(pos, s);
        int newPos = pos + s.length();
        selection.set(newPos, newPos);
        notifyObservers();
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
        int pos = clamp(selection.getStart() + delta, 0, buffer.length());
        selection.set(pos, pos);
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

    private static int clamp(int i, int min, int max) {
        return Math.max(min, Math.min(max, i));
    }
}
