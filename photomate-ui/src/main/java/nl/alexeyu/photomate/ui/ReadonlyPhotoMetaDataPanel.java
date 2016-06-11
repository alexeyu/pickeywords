package nl.alexeyu.photomate.ui;

import static nl.alexeyu.photomate.model.PhotoProperty.CAPTION;
import static nl.alexeyu.photomate.model.PhotoProperty.DESCRIPTION;

import java.awt.dnd.DropTarget;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

import javax.swing.DefaultListModel;

import nl.alexeyu.photomate.api.AbstractPhoto;
import nl.alexeyu.photomate.model.PhotoProperty;

public class ReadonlyPhotoMetaDataPanel extends AbstractPhotoMetaDataPanel<AbstractPhoto> {

    public ReadonlyPhotoMetaDataPanel(DropTarget dropTarget) {
        captionEditor.setEditable(false);
        captionEditor.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (photo.isPresent() && e.getClickCount() >= 2) {
                    firePropertyChange(CAPTION.propertyName(), null, captionEditor.getText());
                }
            }
        });
        descriptionEditor.setEditable(false);
        descriptionEditor.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (photo.isPresent() && e.getClickCount() >= 2) {
                    firePropertyChange(DESCRIPTION.propertyName(), null, descriptionEditor.getText());
                }
            }
        });

        keywordList.setDragEnabled(true);
        keywordList.setDropTarget(dropTarget);
        keywordList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (photo.isPresent() && e.getClickCount() >= 2) {
                    DefaultListModel<String> model = (DefaultListModel<String>) keywordList.getModel();
                    String[] values = new String[model.size()];
                    model.copyInto(values);
                    firePropertyChange(PhotoProperty.KEYWORDS.propertyName(), null, Arrays.asList(values));
                }
            }
        });

    }

}
