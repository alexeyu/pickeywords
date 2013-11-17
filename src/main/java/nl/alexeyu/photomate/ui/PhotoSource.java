package nl.alexeyu.photomate.ui;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;

import nl.alexeyu.photomate.api.AbstractPhoto;

public abstract class PhotoSource<P extends AbstractPhoto> {
    
    protected final PhotoTable<P> photoTable;
    
    protected final JPanel panel;
    
    public PhotoSource(int columnCount) {
        panel = new JPanel(new BorderLayout());
        photoTable = new PhotoTable<>(columnCount, panel);
    }
    
    public void addPhotoObserver(PhotoObserver<? super P> photoObserver) {
        photoTable.addObserver(photoObserver);
    }

    public JComponent getComponent() {
        return panel;
    }
    
    public P getSelectedPhoto() {
        return photoTable.getSelectedPhoto();
    }

}
