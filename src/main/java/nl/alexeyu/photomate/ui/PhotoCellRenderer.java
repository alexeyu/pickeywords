package nl.alexeyu.photomate.ui;

import static nl.alexeyu.photomate.ui.Constants.EMPTY_BORDER;
import static nl.alexeyu.photomate.ui.Constants.LINE_BORDER;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import nl.alexeyu.photomate.model.Photo;
import nl.alexeyu.photomate.util.ImageUtils;

public class PhotoCellRenderer extends DefaultTableCellRenderer {
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, 
            boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel label = new JLabel();
        if (value instanceof Photo) {
            ImageIcon icon = ((Photo) value).getThumbnail();
            label.setIcon(icon == null ? ImageUtils.getImage("queue.png") : icon);
            label.setBorder(hasFocus ? LINE_BORDER : EMPTY_BORDER);
        }
        return label;
    }
    
}
