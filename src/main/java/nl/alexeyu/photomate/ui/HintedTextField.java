package nl.alexeyu.photomate.ui;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BoxLayout;
import javax.swing.JTextField;

import org.apache.commons.lang3.StringUtils;

public class HintedTextField extends JTextField {

    private final String propertyName;

    private String oldValue;
    
    private boolean saveOnExit = false;

    public HintedTextField(String label, String propertyName) {
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

        addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(FocusEvent e) {
                oldValue = getText();
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (saveOnExit) {
                    firePropertyChanged();
                }
            }
            
        });
    }

    public void setSaveOnExit(boolean saveOnExit) {
        this.saveOnExit = saveOnExit;
    }

    private void firePropertyChanged() {
        String value = getText();
        if (StringUtils.isNotBlank(value) && !value.trim().equals(oldValue)) {
            HintedTextField.this.firePropertyChange(propertyName, null, value);
            oldValue = value;
        }
    }
    
}
