package nl.alexeyu.photomate.ui;

import java.awt.*;
import java.util.Optional;
import java.util.function.Consumer;

import javax.swing.*;

import nl.alexeyu.photomate.api.AbstractPhoto;
import nl.alexeyu.photomate.model.Photo;
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

    public void setHighlightedPhotoConsumer(Consumer<Photo> consumer) {
        photoTable.setHighlightedPhotoConsumer(consumer);
    }
    
    public void addPhotoObserver(PhotoObserver<? super P> photoObserver) {
        photoTable.addObserver(photoObserver);
    }

    public Optional<P> getActivePhoto() {
        return photoTable.getActivePhoto();
    }

}
