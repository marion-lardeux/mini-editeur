package editor.view;

import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import editor.controller.EditorController;

public class KeyboardHandler {
    private final EditorController controller;

    public KeyboardHandler(EditorController controller) {
        this.controller = controller;
    }

    public void install(JComponent component) {
        component.setInputMap(JComponent.WHEN_FOCUSED, new InputMap());
        InputMap inputMap = component.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap actionMap = component.getActionMap();

        bind(inputMap, actionMap, KeyEvent.VK_DELETE, "delete", controller::delete);
        bind(inputMap, actionMap, KeyEvent.VK_RIGHT, "cursorRight", () -> controller.moveCursor(1));
        bind(inputMap, actionMap, KeyEvent.VK_LEFT, "cursorLeft", () -> controller.moveCursor(-1));

        component.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                e.consume();
                char c = e.getKeyChar();
                if (c == '\r') {
                    c = '\n';
                }
                if (c == KeyEvent.CHAR_UNDEFINED) {
                    return;
                }
                if (Character.isISOControl(c) && c != '\n' && c != '\t') {
                    return;
                }
                controller.insert(String.valueOf(c));
            }
        });
    }

    private static void bind(InputMap inputMap, ActionMap actionMap, int keyCode, String name, Runnable action) {
        inputMap.put(KeyStroke.getKeyStroke(keyCode, 0), name);
        actionMap.put(name, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action.run();
            }
        });
    }
}
