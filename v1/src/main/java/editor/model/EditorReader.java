package editor.model;

/** Accès en lecture seule à l'état de l'éditeur, utilisé par les vues. */
public interface EditorReader {
    String getText();

    int getSelStart();

    int getSelEnd();
}
