package editor.model;

public class Clipboard {
    private String content = "";

    public void set(String s) {
        this.content = (s == null) ? "" : s;
    }

    public String get() {
        return content;
    }

    public boolean isEmpty() {
        return content.isEmpty();
    }
}
