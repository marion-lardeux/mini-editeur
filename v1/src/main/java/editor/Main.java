package editor;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import editor.model.EditorEngine;
import editor.view.SwingView;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::start);
    }

    private static void start() {
        EditorEngine engine = new EditorEngine();
        SwingView view = new SwingView(engine);
        engine.addObserver(view);

        engine.insert("Test (affichage par le main): Hello World");

        JLabel help = new JLabel("Étape 1 : affichage seul, pas encore de saisie au clavier");
        help.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

        JFrame frame = new JFrame("Mini-éditeur");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new JScrollPane(view.getComponent()), BorderLayout.CENTER);
        frame.add(help, BorderLayout.SOUTH);
        frame.setSize(720, 420);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
