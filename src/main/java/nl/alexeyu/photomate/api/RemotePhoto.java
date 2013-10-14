package nl.alexeyu.photomate.api;

import java.util.List;

import javax.swing.ImageIcon;

import nl.alexeyu.photomate.model.Photo;

public class RemotePhoto implements Photo {
    
    private final String url;
    
    private final String thumbnailUrl;
    
    private final PhotoStockApi photoStockApi;
    
    private ImageIcon thumbnail;
    
    private List<String> keywords;

    public RemotePhoto(String url, String thumbnailUrl, PhotoStockApi photoStockApi) {
        this.url = url;
        this.thumbnailUrl = thumbnailUrl;
        this.photoStockApi = photoStockApi;
    }

    @Override
    public String getName() {
        return url;
    }

    @Override
    public ImageIcon getThumbnail() {
        if (thumbnail == null) {
            thumbnail = photoStockApi.getImage(thumbnailUrl);
        }
        return thumbnail;
    }

    @Override
    public List<String> getKeywords() {
        if (keywords == null) {
            keywords = photoStockApi.getKeywords(url);
        }
        return keywords;
    }

}
