package editor.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Façade du modèle. À cette étape elle ne sait qu'insérer du texte, exposer son état en
 * lecture seule (EditorReader) et prévenir ses observateurs.
 */
public class EditorEngine implements EditorReader {
    private final Buffer buffer = new Buffer();
    private final Selection selection = new Selection();
    private final List<EditorObserver> observers = new ArrayList<>();

    /** Insère le texte à la position du curseur. */
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
}
