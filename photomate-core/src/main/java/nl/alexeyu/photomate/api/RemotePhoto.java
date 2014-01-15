package nl.alexeyu.photomate.api;

import nl.alexeyu.photomate.model.PhotoMetaData;


public class RemotePhoto extends AbstractPhoto {
    
    private final String url;
    
    private final String thumbnailUrl;
    
    RemotePhoto(String url, String thumbnailUrl) {
        this.url = url;
        this.thumbnailUrl = thumbnailUrl;
    }

    @Override
    public String getName() {
        return url;
    }

    @Override
    public void setMetaData(PhotoMetaData metaData) {
        if (!this.metaData.compareAndSet(null, metaData)) {
            throw new IllegalStateException("Attempt to set template 2nd time");
        }
        firePropertyChanged(METADATA_PROPERTY, null, metaData);
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String getUrl() {
        return url;
    }

}
