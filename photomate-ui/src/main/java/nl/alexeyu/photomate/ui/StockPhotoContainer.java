package nl.alexeyu.photomate.ui;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import nl.alexeyu.photomate.api.PhotoStockApi;
import nl.alexeyu.photomate.api.RemotePhoto;

import com.google.inject.Inject;

public class StockPhotoContainer extends PhotoContainer<RemotePhoto> implements PropertyChangeListener {
    
    private static final int COLUMN_COUNT = 4;
    
    private static final String KEYWORD_SEARCH = "keyword_search";

    private HintedTextField keywordsToSearch;

    @Inject
    private PhotoStockApi photoStockApi;
    
    public StockPhotoContainer() {
        super(COLUMN_COUNT);
        setBorder(UiConstants.EMPTY_BORDER);
        keywordsToSearch = new HintedTextField("Search by", KEYWORD_SEARCH, false);
        keywordsToSearch.addPropertyChangeListener(this);
        add(keywordsToSearch, BorderLayout.NORTH);
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent e) {
        if (e.getPropertyName().equals(KEYWORD_SEARCH)) {
            List<RemotePhoto> photos = photoStockApi.search(e.getNewValue().toString());
            photoTable.setPhotos(photos);
        }
    }

}
