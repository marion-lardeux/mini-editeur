package editor.model;

public class Buffer {
    private final StringBuilder content = new StringBuilder();

    public void insert(int pos, String s) {
        content.insert(pos, s);
    }

    public void delete(int start, int end) {
        content.delete(start, end);
    }

    public String substring(int start, int end) {
        return content.substring(start, end);
    }

    public int length() {
        return content.length();
    }

    public String getText() {
        return content.toString();
    }
}
