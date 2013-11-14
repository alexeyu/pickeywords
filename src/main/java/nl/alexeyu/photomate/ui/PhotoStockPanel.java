package nl.alexeyu.photomate.ui;

import static nl.alexeyu.photomate.ui.UiConstants.BORDER_WIDTH;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.JPanel;

import nl.alexeyu.photomate.api.AbstractPhoto;
import nl.alexeyu.photomate.api.RemotePhoto;
import nl.alexeyu.photomate.api.shutterstock.ShutterPhotoStockApi;
import nl.alexeyu.photomate.model.Photo;

import com.google.inject.Inject;

public class PhotoStockPanel extends JPanel implements PropertyChangeListener, PhotoObserver {
    
    private static final String KEYWORD_SEARCH = "keyword_search";

    private static final int COLUMN_COUNT = 4;

    private HintedTextField keywordsToSearch;

    private PhotoTable<RemotePhoto> photoTable;
    
    @Inject
    private ShutterPhotoStockApi photoStockApi;
    
    public PhotoStockPanel() {
        super(new BorderLayout(BORDER_WIDTH, BORDER_WIDTH));
        setBorder(UiConstants.EMPTY_BORDER);
        keywordsToSearch = new HintedTextField("Search by:", KEYWORD_SEARCH);
        keywordsToSearch.addPropertyChangeListener(this);

        photoTable = new PhotoTable<>(COLUMN_COUNT, this);
        photoTable.setRowHeight(135);

        add(keywordsToSearch, BorderLayout.NORTH);
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent e) {
        if (e.getPropertyName().equals(AbstractPhoto.THUMBNAIL_PROPERTY)) {
            photoTable.repaint();
        } else if (e.getPropertyName().equals(KEYWORD_SEARCH)) {
            List<RemotePhoto> photos = photoStockApi.search(e.getNewValue().toString());
            for (RemotePhoto photo : photos) {
                photo.addPropertyChangeListener(PhotoStockPanel.this);
            }
            photoTable.setPhotos(photos);
        }
    }

    
    @Override
    public void photoSelected(Photo photo) {
        firePropertyChange("photo", null, photo);
    }

}
