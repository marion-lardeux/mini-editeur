package editor;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import editor.command.CommandManager;
import editor.controller.EditorController;
import editor.model.EditorEngine;
import editor.view.KeyboardHandler;
import editor.view.SwingView;

public class Main {
    private static final String HELP = "←/→ : curseur  |  Maj+←/→ : sélectionner  |  ⌫ : supprimer  |  Ctrl+C/X/V : copier, couper, coller  |  \n Ctrl+Z/Y : défaire, refaire  |  Ctrl+R : enregistrer  |  Ctrl+P : rejouer";
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::start);
    }

    private static void start() {
        EditorEngine engine = new EditorEngine();
        CommandManager manager = new CommandManager(engine);
        EditorController controller = new EditorController(engine, manager);
        SwingView view = new SwingView(engine);
        engine.addObserver(view);
        KeyboardHandler keyboard = new KeyboardHandler(controller);
        keyboard.install(view.getComponent());

        JLabel help = new JLabel(HELP);
        help.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

        JFrame frame = new JFrame("Mini-éditeur");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new JScrollPane(view.getComponent()), BorderLayout.CENTER);
        frame.add(help, BorderLayout.SOUTH);
        frame.setSize(720, 420);
        frame.setLocationRelativeTo(null);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                view.getComponent().requestFocusInWindow();
            }
        });
        frame.setVisible(true);
    }
}
