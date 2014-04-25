package nl.alexeyu.photomate.ui;

import static java.util.Arrays.asList;
import static nl.alexeyu.photomate.model.PhotoMetaData.CAPTION_PROPERTY;
import static nl.alexeyu.photomate.model.PhotoMetaData.DESCRIPTION_PROPERTY;
import static nl.alexeyu.photomate.model.PhotoMetaData.KEYWORDS_PROPERTY;

import java.awt.BorderLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import nl.alexeyu.photomate.api.EditablePhoto;
import nl.alexeyu.photomate.model.PhotoMetaData;

import org.apache.commons.collections.ListUtils;

public class EditablePhotoMetaDataPanel extends AbstractPhotoMetaDataPanel<EditablePhoto> {
	
    private static final String NEW_KEYWORD_PROPERTY = "newKeyword";

    private HintedTextField keywordToAddField;
	
	public EditablePhotoMetaDataPanel() {
	    captionEditor.addPropertyChangeListener(CAPTION_PROPERTY, this);
		descriptionEditor.addPropertyChangeListener(DESCRIPTION_PROPERTY, this);

		keywordList.addKeyListener(new KeyAdapter() {
            
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_DELETE) {
                    removeKeywords(keywordList.getSelectedValuesList());
                }
            }
		});
		keywordList.addPropertyChangeListener(KEYWORDS_PROPERTY, this);
		
		keywordToAddField = new HintedTextField("Keyword to add", NEW_KEYWORD_PROPERTY, false);
		add(keywordToAddField, BorderLayout.SOUTH);
		keywordToAddField.addPropertyChangeListener(this);
	}
	
    @Override
    public void propertyChange(PropertyChangeEvent e) {
        if (NEW_KEYWORD_PROPERTY.equals(e.getPropertyName())) {
        	addKeywords(asList(e.getNewValue().toString()));
            keywordToAddField.setText("");
        } else {
            super.propertyChange(e);
        }
    }
    
    @SuppressWarnings("unchecked")
    private void removeKeywords(List<String> keywords) {
        if (keywords.size() > 0) {
            List<String> reducedKeywords = ListUtils.removeAll(photo.getMetaData().getKeywords(), keywords);
            firePropertyChange(KEYWORDS_PROPERTY, photo.getMetaData().getKeywords(), reducedKeywords);
        }
    }
    
    private void addKeywords(List<String> keywords) {
    	Collection<String> extendedKeywords = new LinkedHashSet<>(photo.getMetaData().getKeywords());
    	extendedKeywords.addAll(keywords);
        firePropertyChange(PhotoMetaData.KEYWORDS_PROPERTY, photo.getMetaData().getKeywords(), extendedKeywords);
    }

    public DropTarget getDropTarget() {
    	return new DropTarget(keywordList, 
			new DropTargetAdapter() {
				
				@Override
				public void drop(DropTargetDropEvent dtde) {
					try {
						DataFlavor dataFlavor = new DataFlavor("text/plain; class=java.lang.String");
						String draggedValue = dtde.getTransferable().getTransferData(dataFlavor).toString();
						String[] keywords = draggedValue.split(System.getProperty("line.separator"));
						addKeywords(asList(keywords));
					} catch (Exception ex) {
						throw new IllegalStateException(ex);
					}
				}
				
			});
    }


}
