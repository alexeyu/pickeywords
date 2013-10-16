package nl.alexeyu.photomate.model;

import nl.alexeyu.photomate.api.PhotoApi;


public class RemotePhoto extends AbstractPhoto {
    
    private final String url;
    
    private final String thumbnailUrl;
    
    public RemotePhoto(String url, String thumbnailUrl, PhotoApi photoApi) {
        super(photoApi);
        this.url = url;
        this.thumbnailUrl = thumbnailUrl;
        getThumbnail();
    }

    @Override
    public String getName() {
        return url;
    }

    @Override
    protected String getThumbnailUrl() {
        return thumbnailUrl;
    }

    @Override
    protected String getUrl() {
        return url;
    }

}
