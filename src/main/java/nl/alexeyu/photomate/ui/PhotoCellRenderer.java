package nl.alexeyu.photomate.ui;

import static nl.alexeyu.photomate.ui.UiConstants.EMPTY_BORDER;
import static nl.alexeyu.photomate.ui.UiConstants.LINE_BORDER;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import nl.alexeyu.photomate.api.EditablePhoto;
import nl.alexeyu.photomate.model.Photo;
import nl.alexeyu.photomate.model.PhotoMetaData;
import nl.alexeyu.photomate.util.ImageUtils;

public class PhotoCellRenderer extends DefaultTableCellRenderer {
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, 
            boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel label = new JLabel();
        Photo photo = (Photo) value;
        if (photo == null) {
            return label;
        }
        ImageIcon thumbnail = photo.getThumbnail();
        if (thumbnail == null) {
            label.setText("Loading...");
        } else {
            label.setIcon(thumbnail);
        }
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(isSelected ? LINE_BORDER : EMPTY_BORDER);
        if (photo instanceof EditablePhoto) {
            panel.add(createTitle((EditablePhoto) photo), BorderLayout.NORTH);
        }
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }

    private JComponent createTitle(EditablePhoto photo) {
        String title = photo.getName();
        PhotoMetaData metadata = photo.getMetaData();
        if (metadata != null) {
            title += " [" + metadata.getKeywords().size() + "]";
        }
        JLabel nameLabel = new JLabel(title);
        if (!photo.isReadyToUpload()) {
            nameLabel.setIcon(ImageUtils.getImage("error.png"));
        }
        nameLabel.setForeground(Color.GRAY);
        return nameLabel;
    }
}
