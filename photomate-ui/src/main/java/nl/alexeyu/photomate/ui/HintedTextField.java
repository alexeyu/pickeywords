package nl.alexeyu.photomate.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import javax.swing.undo.UndoManager;

import com.google.common.base.Strings;

public class HintedTextField extends JPanel {

    private static final Color BG_READ_ONLY = new Color(240, 240, 240);

    private final String propertyName;

    private final JTextComponent textField;

    private Consumer<MouseEvent> mouseEventConsumer;

    private final UndoManager undoManager = new UndoManager();

    public static HintedTextField textArea(String label, String propertyName) {
        return new HintedTextField(label, propertyName, HintedTextField::createTextArea);
    }

    public static HintedTextField textField(String label, String propertyName) {
        return new HintedTextField(label, propertyName, HintedTextField::createTextField);
    }

    private HintedTextField(String label, String propertyName, Supplier<JTextComponent> textFieldFactory) {
        this.propertyName = propertyName;
        setLayout(new BorderLayout());
        textField = textFieldFactory.get();
        textField.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.GRAY),
                        BorderFactory.createEmptyBorder(2, 4, 2, 4)));

        textField.setToolTipText(label);
        add(textField);

        textField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (event.getClickCount() >= 2 && mouseEventConsumer != null) {
                    mouseEventConsumer.accept(event);
                }
            }
        });

        signalChangeOnEnter();
        enableUndo();
    }

    private void signalChangeOnEnter() {
        KeyStroke enterStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        textField.getInputMap().put(enterStroke, enterStroke.toString());
        textField.getActionMap().put(enterStroke.toString(), new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (textField.isEditable()) {
                    firePropertyChanged();
                }
            }
        });
    }

    private void enableUndo() {
        textField.getDocument().addUndoableEditListener(undoManager);

        KeyStroke ctrlZStroke = KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_MASK);
        textField.getInputMap().put(ctrlZStroke, ctrlZStroke.toString());
        textField.getActionMap().put(ctrlZStroke.toString(), new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (undoManager.canUndo()) {
                    undoManager.undo();
                }
            }

        });

    }

    public void reactOnFocus() {
        textField.addFocusListener(new FocusAdapter() {

            @Override
            public void focusLost(FocusEvent e) {
                firePropertyChanged();
            }

        });
    }

    public String getText() {
        return textField.getText();
    }

    public void setText(String text) {
        textField.setText(text);
    }

    public void setEditable(boolean editable) {
        textField.setEditable(editable);
        textField.setBackground(BG_READ_ONLY);
    }

    public void onDoubleClick(Consumer<MouseEvent> eventConsumer) {
        this.mouseEventConsumer = eventConsumer;
    }

    private static JTextArea createTextArea() {
        JTextArea area = new JTextArea(2, 50);
        area.setLineWrap(true);
        suppressTab(area);
        return area;
    }

    private static void suppressTab(JTextArea textArea) {
        KeyStroke tabStroke = KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0);
        textArea.getInputMap().put(tabStroke, tabStroke.toString());
        textArea.getActionMap().put(tabStroke.toString(), new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        });
    }

    private static JTextField createTextField() {
        return new JTextField();
    }

    private void firePropertyChanged() {
        var value = textField.getText();
        if (Strings.nullToEmpty(value).trim().length() > 0) {
            firePropertyChange(propertyName, null, value);
        }
    }

}
