package nl.alexeyu.photomate.ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

import javax.swing.DefaultListModel;

import nl.alexeyu.photomate.api.AbstractPhoto;
import nl.alexeyu.photomate.model.PhotoMetaData;

public class ReadonlyPhotoMetaDataPanel extends AbstractPhotoMetaDataPanel<AbstractPhoto> {
    
	public ReadonlyPhotoMetaDataPanel() {
	    captionEditor.setEditable(false);
	    captionEditor.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (photo != null && e.getClickCount() >= 2) {
                    firePropertyChange(PhotoMetaData.CAPTION_PROPERTY, null, captionEditor.getText());
                }
            }
        });
	    descriptionEditor.setEditable(false);
	    descriptionEditor.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (photo != null && e.getClickCount() >= 2) {
                    firePropertyChange(PhotoMetaData.DESCRIPTION_PROPERTY, null, descriptionEditor.getText());
                }
            }
	    });

	    keywordList.setEnabled(false);
        keywordList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (photo != null && e.getClickCount() >= 2) {
                    DefaultListModel<String> model = (DefaultListModel<String>) keywordList.getModel();
                    String[] values = new String[model.size()];
                    model.copyInto(values);
                    firePropertyChange(PhotoMetaData.KEYWORDS_PROPERTY, null, Arrays.asList(values));
                }
            }
        });
	    
	}
	
}
