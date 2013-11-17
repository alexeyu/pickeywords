package nl.alexeyu.photomate.api;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import nl.alexeyu.photomate.util.ImageUtils;

public class PhotoFactory {

    public <P extends LocalPhoto> List<P> createLocalPhotos(File dir, PhotoApi<LocalPhoto> photoApi, Class<P> clazz) {
        List<P> photos = new ArrayList<>();
        for (File file : dir.listFiles()) {
            if (ImageUtils.isJpeg(file)) {
                photos.add(createLocalPhoto(file, photoApi, clazz));
            }
        }
        return photos;
    }

    public <P extends LocalPhoto> P createLocalPhoto(File file, PhotoApi<LocalPhoto> photoApi, Class<P> clazz) {
        try {
            Constructor<P> c = clazz.getConstructor(File.class);
            P photo = c.newInstance(file);
            photoApi.provideMetadata(photo);
            photoApi.provideThumbnail(photo);
            return photo;
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public RemotePhoto createRemotePhoto(String url, String thumbnailUrl, PhotoApi<RemotePhoto> photoApi) {
        RemotePhoto photo = new RemotePhoto(url, thumbnailUrl);
        photoApi.provideMetadata(photo);
        photoApi.provideThumbnail(photo);
        return photo;
    }

}
