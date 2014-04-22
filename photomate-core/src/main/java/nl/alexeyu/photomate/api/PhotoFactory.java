package nl.alexeyu.photomate.api;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

import nl.alexeyu.photomate.util.ImageUtils;

public class PhotoFactory {

    public <P extends LocalPhoto> List<P> createLocalPhotos(File dir, PhotoApi<LocalPhoto> photoApi, Class<P> clazz) {
    	try {
	    	return Files.list(dir.toPath())
	    			.filter((path) -> ImageUtils.isJpeg(path.toFile()))
	    			.map(path -> createLocalPhoto(path.toFile(), photoApi, clazz))
	    			.collect(Collectors.toList());
    	} catch (IOException ex) {
    		throw new IllegalStateException(ex);
    	}
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
