package nl.alexeyu.photomate.api;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;
import javax.swing.ImageIcon;

import nl.alexeyu.photomate.model.DefaultPhotoMetaDataBuilder;
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
    	photo.addThumbnail(new ImageIcon(thumbnailGenerator.getThumbnail(buf)));
    	photo.addThumbnail(new ImageIcon(previewGenerator.getThumbnail(buf)));
    }
    
    @Override
    public void provideMetadata(LocalPhoto photo) {
    	CompletableFuture
    		.supplyAsync(() -> metadataProcessor.read(photo.getPath()))
    		.thenAccept(m -> photo.setMetaData(m));
    }

    public void updateProperty(LocalPhoto photo, String propertyName, Object propertyValue) {
    	DefaultPhotoMetaDataBuilder builder = new DefaultPhotoMetaDataBuilder(photo.metaData.get());
        PhotoMetaData metaData = builder.set(PhotoProperty.of(propertyName), propertyValue).build();
        CompletableFuture
        	.runAsync(() -> metadataProcessor.update(photo.getPath(), photo.metaData().get(), metaData))
        	.thenRun(() -> photo.setMetaData(metaData));
    }

}
