package nl.alexeyu.photomate.service;

import java.util.List;

import javax.swing.Icon;

import nl.alexeyu.photomate.api.PhotoStockApi;
import nl.alexeyu.photomate.model.StockPhotoDescription;

import com.google.inject.Inject;

public class PhotoSearcher {

    @Inject
    private PhotoStockApi photoStockApi;
    
    public List<StockPhotoDescription> search(String keyword) {
        return photoStockApi.search(keyword);
    }
    
    public Icon getIcon(StockPhotoDescription photo) {
        return photoStockApi.getImage(photo);
    }

}
