package nl.alexeyu.photomate.ui;

import static nl.alexeyu.photomate.model.PhotoMetaData.CAPTION_PROPERTY;
import static nl.alexeyu.photomate.model.PhotoMetaData.DESCRIPTION_PROPERTY;
import static nl.alexeyu.photomate.model.PhotoMetaData.KEYWORDS_PROPERTY;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nl.alexeyu.photomate.api.AbstractPhoto;
import nl.alexeyu.photomate.api.LocalPhoto;
import nl.alexeyu.photomate.model.PhotoMetaData;

import org.apache.commons.collections.ListUtils;

public class LocalPhotoMetaDataPanel extends AbstractPhotoMetaDataPanel<LocalPhoto> {
    
    private HintedTextField keywordToAddField;
	
	public LocalPhotoMetaDataPanel() {
	    captionEditor.addPropertyChangeListener(CAPTION_PROPERTY, this);
		descriptionEditor.addPropertyChangeListener(DESCRIPTION_PROPERTY, this);

		keywordList.addKeyListener(new KeyAdapter() {
            
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_DELETE) {
                    List<String> keywords = keywordList.getSelectedValuesList();
                    if (keywords.size() > 0) {
                        List<String> reducedKeywords = ListUtils.removeAll(photo.getMetaData().getKeywords(), keywords);
                        firePropertyChange(KEYWORDS_PROPERTY, photo.getMetaData().getKeywords(), reducedKeywords);
                    }
                }
            }
		});
		keywordList.addPropertyChangeListener(KEYWORDS_PROPERTY, this);
		
		keywordToAddField = new HintedTextField("Keyword to add", "newKeyword");
		add(keywordToAddField, BorderLayout.SOUTH);
		keywordToAddField.addPropertyChangeListener(this);
	}
	
    @Override
    public void propertyChange(PropertyChangeEvent e) {
        if ("newKeyword".equals(e.getPropertyName())) {
            List<String> newKeywords = new ArrayList<>(photo.getMetaData().getKeywords());
            newKeywords.add(e.getNewValue().toString());
            firePropertyChange(PhotoMetaData.KEYWORDS_PROPERTY, null, newKeywords);
            keywordToAddField.setText("");
        } else {
            super.propertyChange(e);
        }
    }

}
