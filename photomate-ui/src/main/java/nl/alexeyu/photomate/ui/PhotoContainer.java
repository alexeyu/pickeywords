package nl.alexeyu.photomate.ui;

import java.awt.BorderLayout;
import java.util.Optional;

import javax.swing.JPanel;

import nl.alexeyu.photomate.api.AbstractPhoto;
import nl.alexeyu.photomate.service.PhotoObserver;

public abstract class PhotoContainer<P extends AbstractPhoto> extends JPanel {
    
    protected final PhotoTable<P> photoTable;
    
    public PhotoContainer(int columnCount) {
        super(new BorderLayout());
        photoTable = createPhotoTable(columnCount);
    }
    
    protected PhotoTable<P> createPhotoTable(int columnCount) {
    	return new PhotoTable<>(columnCount, this);
    }
    
    public void addPhotoObserver(PhotoObserver<? super P> photoObserver) {
        photoTable.addObserver(photoObserver);
    }

    public Optional<P> getSelectedPhoto() {
        return photoTable.getSelectedPhoto();
    }

}
