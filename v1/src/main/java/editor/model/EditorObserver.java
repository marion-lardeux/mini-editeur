package editor.model;

/** Contrat à respecter pour être prévenu d'un changement du modèle (patron Observer). */
public interface EditorObserver {
    void update();
}
