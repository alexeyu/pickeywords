package nl.alexeyu.photomate.ui;

import java.awt.BorderLayout;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import javax.swing.JPanel;

import nl.alexeyu.photomate.api.AbstractPhoto;
import nl.alexeyu.photomate.service.PhotoObserver;

public abstract class PhotoContainer<P extends AbstractPhoto> extends JPanel {
    
    protected final PhotoTable<P> photoTable;
    
    public PhotoContainer(int columnCount) {
        super(new BorderLayout());
        photoTable = createPhotoTable(columnCount);
    }
    
    private PhotoTable<P> createPhotoTable(int columnCount) {
    	return new PhotoTable<>(columnCount, this);
    }

    public void setHighlightedPhotoConsumer(Consumer<P> consumer) {
        photoTable.setHighlightedPhotoConsumer(consumer);
    }
    
    public void addPhotoObserver(PhotoObserver<? super P> photoObserver) {
        photoTable.addObserver(photoObserver);
    }

    public Optional<P> getActivePhoto() {
        return photoTable.getActivePhoto();
    }

    public List<P> getSelectedPhotos() {
        return photoTable.getSelectedPhotos();
    }

}
