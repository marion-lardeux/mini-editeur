package editor.view;

import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
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
        int shortcut = shortcutMask();

        bind(inputMap, actionMap, KeyEvent.VK_C, shortcut, "copy", controller::copy);
        bind(inputMap, actionMap, KeyEvent.VK_X, shortcut, "cut", controller::cut);
        bind(inputMap, actionMap, KeyEvent.VK_V, shortcut, "paste", controller::paste);
        bind(inputMap, actionMap, KeyEvent.VK_DELETE, 0, "delete", controller::delete);
        bind(inputMap, actionMap, KeyEvent.VK_RIGHT, 0, "cursorRight", () -> controller.moveCursor(1));
        bind(inputMap, actionMap, KeyEvent.VK_LEFT, 0, "cursorLeft", () -> controller.moveCursor(-1));
        bind(inputMap, actionMap, KeyEvent.VK_RIGHT, InputEvent.SHIFT_DOWN_MASK, "extendRight", () -> controller.extendSelection(1));
        bind(inputMap, actionMap, KeyEvent.VK_LEFT, InputEvent.SHIFT_DOWN_MASK, "extendLeft", () -> controller.extendSelection(-1));

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

    static int shortcutMask() {
        try {
            return Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
        } catch (HeadlessException e) {
            return InputEvent.CTRL_DOWN_MASK;
        }
    }

    private static void bind(InputMap inputMap, ActionMap actionMap, int keyCode, int modifiers, String name, Runnable action) {
        inputMap.put(KeyStroke.getKeyStroke(keyCode, modifiers), name);
        actionMap.put(name, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action.run();
            }
        });
    }
}
