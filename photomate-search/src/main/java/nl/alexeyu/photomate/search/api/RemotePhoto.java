package nl.alexeyu.photomate.search.api;

import nl.alexeyu.photomate.api.AbstractPhoto;

public final class RemotePhoto extends AbstractPhoto {
    
    private final String url;
    
    private final String thumbnailUrl;
    
    public RemotePhoto(String url, String thumbnailUrl) {
        this.url = url;
        this.thumbnailUrl = thumbnailUrl;
    }

    @Override
    public String name() {
        return url;
    }

    public String thumbnailUrl() {
        return thumbnailUrl;
    }

    public String photoUrl() {
        return url;
    }

}
