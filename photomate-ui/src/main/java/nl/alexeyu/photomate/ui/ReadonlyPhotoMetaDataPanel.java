package nl.alexeyu.photomate.ui;

import static nl.alexeyu.photomate.model.PhotoProperty.CAPTION;
import static nl.alexeyu.photomate.model.PhotoProperty.DESCRIPTION;

import java.awt.dnd.DropTarget;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import nl.alexeyu.photomate.api.AbstractPhoto;
import nl.alexeyu.photomate.model.PhotoProperty;

public class ReadonlyPhotoMetaDataPanel extends AbstractPhotoMetaDataPanel<AbstractPhoto> {

    public ReadonlyPhotoMetaDataPanel(DropTarget dropTarget) {
        captionEditor.setEditable(false);
        captionEditor.onDoubleClick(event -> {
            if (photo != null) {
                firePropertyChange(CAPTION.propertyName(), null, captionEditor.getText());
            }});
        descriptionEditor.setEditable(false);
        descriptionEditor.onDoubleClick(event -> {
            if (photo != null) {
                firePropertyChange(DESCRIPTION.propertyName(), null, descriptionEditor.getText());
            }});

        keywordList.setDragEnabled(true);
        keywordList.setDropTarget(dropTarget);
        keywordList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (photo != null && e.getClickCount() >= 2) {
                    firePropertyChange(PhotoProperty.KEYWORDS.propertyName(), null, keywordList.getSelectedValuesList());
                }
            }
        });

    }

}
