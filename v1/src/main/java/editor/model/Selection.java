package editor.model;

public class Selection {
    private int start = 0;
    private int end = 0;
    private int anchor = 0;

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public int getAnchor() {
        return anchor;
    }

    public void set(int start, int end) {
        this.start = start;
        this.end = end;
        this.anchor = start;
    }

    public void select(int anchor, int active) {
        this.anchor = anchor;
        this.start = Math.min(anchor, active);
        this.end = Math.max(anchor, active);
    }

    public boolean isEmpty() {
        return start == end;
    }
}
