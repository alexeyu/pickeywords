package nl.alexeyu.photomate.ui;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import nl.alexeyu.photomate.api.AbstractPhoto;
import nl.alexeyu.photomate.api.LocalPhoto;
import nl.alexeyu.photomate.model.Photo;

public class PhotoView extends JPanel implements PropertyChangeListener, PhotoObserver {
    
    private static final int CELL_HEIGHT = Photo.THUMBNAIL_SIZE.height + 20;
    
    private PhotoTable<LocalPhoto> photoTable;
	
	private JLabel photoPreview = new JLabel();
	
	public PhotoView() {
	    super(new BorderLayout());
	    photoTable = new PhotoTable(1, this);
	    photoTable.setRowHeight(CELL_HEIGHT);
	    photoTable.setPreferredScrollableViewportSize(AbstractPhoto.THUMBNAIL_SIZE);
	    
        photoPreview.setPreferredSize(Photo.PREVIEW_SIZE);
	}
	
	@Override
    public void photoSelected(Photo photo) {
        initPreview();
        firePropertyChange("photo", null, photoTable.getSelectedPhoto());
    }

	private void initPreview() {
	    LocalPhoto photo = photoTable.getSelectedPhoto();
	    photoPreview.setIcon(photo == null ? null : photo.getPreview());
	}
	
    public void setPhotos(List<LocalPhoto> photos) {
	    for (LocalPhoto photo : photos) {
	        photo.addPropertyChangeListener(this);
	    }
	    photoTable.setPhotos(photos);
	    if (photos.size() > 0) {
	        photoTable.getSelectionModel().setLeadSelectionIndex(0);
	    }
	}
	
	public JComponent getPreview() {
	    return photoPreview;
	}

	@Override
    public void propertyChange(PropertyChangeEvent e) {
	    photoTable.repaint();
	    initPreview();
    }

}
