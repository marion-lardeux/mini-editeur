package editor.model;

/** Zone de travail : contient le texte. */
public class Buffer {
    private final StringBuilder content = new StringBuilder();

    public void insert(int pos, String s) {
        content.insert(pos, s);
    }

    public int length() {
        return content.length();
    }

    public String getText() {
        return content.toString();
    }
}
