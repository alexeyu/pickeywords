package nl.alexeyu.photomate.api;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;
import javax.swing.ImageIcon;

import nl.alexeyu.photomate.model.DefaultPhotoMetaData;
import nl.alexeyu.photomate.model.PhotoMetaData;
import nl.alexeyu.photomate.model.PhotoProperty;
import nl.alexeyu.photomate.service.metadata.PhotoMetadataProcessor;
import nl.alexeyu.photomate.service.thumbnail.BufferedImageProvider;
import nl.alexeyu.photomate.service.thumbnail.ThumbnailProvider;
import nl.alexeyu.photomate.util.ImageUtils;

public class LocalPhotoApi<P extends LocalPhoto> implements PhotoApi<P> {
    
    @Inject
    private PhotoMetadataProcessor metadataProcessor;
    
    @Inject 
    private BufferedImageProvider bufferedImageProvider;
    
    @Inject 
    @Named("thumbnail")
    private ThumbnailProvider thumbnailGenerator;
    
    @Inject 
    @Named("preview")
    private ThumbnailProvider previewGenerator;
    
    public List<P> createPhotos(Path dir, LocalPhotoFactory<P> photoFactory) {
    	try {
	    	List<P> photos = Files.list(dir)
	    			.filter(path -> ImageUtils.isJpeg(path))
	    			.map(path -> photoFactory.createPhoto(path))
	    			.collect(Collectors.toList());
	    	photos.forEach(photo -> init(photo));
	    	return photos;
    	} catch (IOException ex) {
    		throw new IllegalStateException(ex);
    	}
    }

    @Override
    public void provideThumbnail(P photo) {
    	CompletableFuture.runAsync(() -> provideThumbnails(photo));
    }
    
    private void provideThumbnails(P photo) {
    	BufferedImage buf = bufferedImageProvider.toBufferedImage(photo.getPath());
    	for (int i = 0; i < photo.getThumbnailCount(); i++) {
    		photo.addThumbnail(new ImageIcon(getGenerator(i).getThumbnail(buf)));
    	}
    }
    
    private ThumbnailProvider getGenerator(int index) {
    	switch (index) {
		case 0: return thumbnailGenerator;
		case 1: return previewGenerator;
		default: throw new IllegalArgumentException("This type of thumbnails is not supported");
		}
    }

    @Override
    public void provideMetadata(LocalPhoto photo) {
    	CompletableFuture.supplyAsync(() -> metadataProcessor.read(photo.getPath())).thenAccept(m -> photo.setMetaData(m));
    }

    public void updateProperty(LocalPhoto photo, String propertyName, Object propertyValue) {
        Map<PhotoProperty, Object> newProps = new HashMap<>();
        for (PhotoProperty pp : PhotoProperty.values()) {
        	newProps.put(pp, photo.getMetaData().getProperty(pp));
        }
        newProps.put(PhotoProperty.of(propertyName), propertyValue);
        PhotoMetaData metaData = new DefaultPhotoMetaData(newProps);
        CompletableFuture
        	.runAsync(() -> metadataProcessor.update(photo.getPath(), photo.getMetaData(), metaData))
        	.thenRun(() -> photo.setMetaData(metaData));
    }

}
