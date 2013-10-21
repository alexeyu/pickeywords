package nl.alexeyu.photomate.model;

import java.io.File;

import nl.alexeyu.photomate.api.PhotoApi;

public class PhotoFactory {
    
    public LocalPhoto createLocalPhoto(File file, PhotoApi<LocalPhoto> photoApi) {
        return initPhoto(new LocalPhoto(file), photoApi);
    }

    public RemotePhoto createRemotePhoto(String url, String thumbnailUrl, PhotoApi<RemotePhoto> photoApi) {
        return initPhoto(new RemotePhoto(url, thumbnailUrl), photoApi);
    }

    protected <P extends AbstractPhoto> P initPhoto(P photo, PhotoApi<P> photoApi) {
        photoApi.provideKeywords(photo, photo.getKeywordsResultProcessor());
        photoApi.provideThumbnail(photo, photo.getThumbnailProcessor());
        return photo;
    }

}
