package nl.alexeyu.photomate.ui;

import static nl.alexeyu.photomate.ui.UiConstants.PREVIEW_SIZE;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;

import nl.alexeyu.photomate.api.EditablePhoto;
import nl.alexeyu.photomate.api.LocalPhoto;
import nl.alexeyu.photomate.service.PhotoObserver;

public class EditablePhotoSource extends PhotoSource<EditablePhoto> implements PropertyChangeListener, PhotoObserver<EditablePhoto> {
    
	private JLabel photoPreview = new JLabel();
	
	public EditablePhotoSource() {
	    super(1);
	    photoTable.addObserver(this);
        photoPreview.setPreferredSize(PREVIEW_SIZE);
	}
	
	private void initPreview() {
	    EditablePhoto photo = photoTable.getSelectedPhoto();
	    photoPreview.setIcon(photo == null ? null : photo.getPreview());
	}
	
    public void setPhotos(List<EditablePhoto> photos) {
	    for (LocalPhoto photo : photos) {
	        photo.addPropertyChangeListener(this);
	    }
	    photoTable.setPhotos(photos);
	    if (photos.size() > 0) {
	        photoTable.getSelectionModel().setSelectionInterval(0, 0);
	    }
	}
	
	public JComponent getPreview() {
	    return photoPreview;
	}
	
	@Override
    public void propertyChange(PropertyChangeEvent e) {
	    initPreview();
    }

    @Override
    public void photoSelected(EditablePhoto photo) {
        initPreview();
    }

}
