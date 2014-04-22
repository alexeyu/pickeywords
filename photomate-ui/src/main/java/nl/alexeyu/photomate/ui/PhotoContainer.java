package nl.alexeyu.photomate.ui;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import nl.alexeyu.photomate.api.AbstractPhoto;
import nl.alexeyu.photomate.service.PhotoObserver;

public abstract class PhotoContainer<P extends AbstractPhoto> extends JPanel {
    
    protected final PhotoTable<P> photoTable;
    
    public PhotoContainer(int columnCount) {
        super(new BorderLayout());
        photoTable = new PhotoTable<>(columnCount, this);
    }
    
    public void addPhotoObserver(PhotoObserver<? super P> photoObserver) {
        photoTable.addObserver(photoObserver);
    }

    public P getSelectedPhoto() {
        return photoTable.getSelectedPhoto();
    }

}
