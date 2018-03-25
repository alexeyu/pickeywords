package nl.alexeyu.photomate.ui;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BoxLayout;
import javax.swing.JTextField;

import com.google.common.base.Strings;

public class HintedTextField extends JTextField {

    private final String propertyName;

    public HintedTextField(String label, String propertyName, boolean reactOnFocus) {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.propertyName = propertyName;
        setToolTipText(label);
        addKeyListener(new KeyAdapter() {

            @Override
            public void keyTyped(KeyEvent e) {
                if (isEditable() && e.getKeyChar() == KeyEvent.VK_ENTER) {
                    firePropertyChanged();
                }
            }
        });

        if (reactOnFocus) {
            addFocusListener(new FocusAdapter() {

                @Override
                public void focusLost(FocusEvent e) {
                    firePropertyChanged();
                }

            });
        }
    }

    private void firePropertyChanged() {
        var value = getText();
        if (Strings.nullToEmpty(value).trim().length() > 0) {
            firePropertyChange(propertyName, null, value);
        }
    }

}
