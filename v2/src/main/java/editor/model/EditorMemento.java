package editor.model;

public final class EditorMemento {
    private final String text;
    private final int start;
    private final int end;
    private final int anchor;

    EditorMemento(String text, int start, int end, int anchor) {
        this.text = text;
        this.start = start;
        this.end = end;
        this.anchor = anchor;
    }

    String text() { return text; }
    int start() { return start; }
    int end() { return end; }
    int anchor() { return anchor; }

    public boolean sameText(EditorMemento other) {
        return text.equals(other.text);
    }
}
