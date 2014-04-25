package nl.alexeyu.photomate.ui;

import static nl.alexeyu.photomate.model.PhotoMetaData.CAPTION_PROPERTY;
import static nl.alexeyu.photomate.model.PhotoMetaData.DESCRIPTION_PROPERTY;
import static nl.alexeyu.photomate.ui.UiConstants.BORDER_WIDTH;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import nl.alexeyu.photomate.api.AbstractPhoto;
import nl.alexeyu.photomate.model.Photo;
import nl.alexeyu.photomate.service.PhotoObserver;

public abstract class AbstractPhotoMetaDataPanel<P extends AbstractPhoto> 
    extends JPanel implements PropertyChangeListener, PhotoObserver<P> {
	
    protected HintedTextField captionEditor;
    
    protected HintedTextField descriptionEditor;
    
    protected JList<String> keywordList = new JList<>(new DefaultListModel<String>());
	
	protected P photo;
	
	public AbstractPhotoMetaDataPanel() {
	    super(new BorderLayout(BORDER_WIDTH, BORDER_WIDTH));
	    JPanel editorPanel = new JPanel();
	    editorPanel.setLayout(new BoxLayout(editorPanel, BoxLayout.Y_AXIS));

	    captionEditor = new HintedTextField("Caption", CAPTION_PROPERTY, true);
	    editorPanel.add(captionEditor);
		descriptionEditor = new HintedTextField("Description", DESCRIPTION_PROPERTY, true);
		editorPanel.add(descriptionEditor);

		add(editorPanel, BorderLayout.NORTH);
		add(new JScrollPane(keywordList), BorderLayout.CENTER);
		
		setPreferredSize(UiConstants.PREVIEW_SIZE);
	}
	
	public final void setPhoto(P photo) {
	    if (this.photo != null) {
	        this.photo.removePropertyChangeListener(this);
	    }
	    this.photo = photo;
	    updateComponentsWithPhotoMetaData();
	    if (photo != null) {
	        this.photo.addPropertyChangeListener(this);
	    }
	}

    private void updateComponentsWithPhotoMetaData() {
        captionEditor.setText(isNull(photo) ? "" : photo.getMetaData().getCaption());
        descriptionEditor.setText(isNull(photo) ? "" : photo.getMetaData().getDescription());
        DefaultListModel<String> listModel = new DefaultListModel<>();
        if (!isNull(photo)) {
        	getKeywords().forEach(keyword -> listModel.addElement(keyword));
        }
        keywordList.setModel(listModel);
	}
	
    @Override
    public void propertyChange(PropertyChangeEvent e) {
        if (AbstractPhoto.METADATA_PROPERTY.equals(e.getPropertyName())) {
            updateComponentsWithPhotoMetaData();
        } else {
            firePropertyChange(e.getPropertyName(), null, e.getNewValue());
        }
    }
    
    private Collection<String> getKeywords() {
        return photo.getMetaData().getKeywords();
    }
	
	private boolean isNull(Photo photo) {
	    return photo == null || photo.getMetaData() == null;
	}
	
    @Override
    public void photoSelected(P photo) {
        setPhoto(photo);
    }

}
