package nl.alexeyu.photomate.api;

import java.io.File;

public class PhotoFactory {
    
    public LocalPhoto createLocalPhoto(File file, PhotoApi<LocalPhoto> photoApi) {
        return initPhoto(new LocalPhoto(file), photoApi);
    }

    public RemotePhoto createRemotePhoto(String url, String thumbnailUrl, PhotoApi<RemotePhoto> photoApi) {
        return initPhoto(new RemotePhoto(url, thumbnailUrl), photoApi);
    }

    protected <P extends AbstractPhoto> P initPhoto(P photo, PhotoApi<P> photoApi) {
        photoApi.provideMetadata(photo);
        photoApi.provideThumbnail(photo);
        return photo;
    }

}
