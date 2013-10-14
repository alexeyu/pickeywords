package nl.alexeyu.photomate.ui;

import static nl.alexeyu.photomate.ui.Constants.EMPTY_BORDER;
import static nl.alexeyu.photomate.ui.Constants.LINE_BORDER;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import nl.alexeyu.photomate.model.Photo;

public class PhotoCellRenderer extends DefaultTableCellRenderer {
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, 
            boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel label = new JLabel();
        if (value instanceof Photo) {
            label.setIcon(((Photo) value).getThumbnail());
            label.setBorder(hasFocus ? LINE_BORDER : EMPTY_BORDER);
        }
        return label;
    }
    
}
