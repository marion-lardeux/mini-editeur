package editor.model;

/** Deux positions dans le texte : début et fin. Si début = fin, c'est un simple curseur. */
public class Selection {
    private int start = 0;
    private int end = 0;

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public void set(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public boolean isEmpty() {
        return start == end;
    }
}
