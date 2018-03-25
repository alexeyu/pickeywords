package nl.alexeyu.photomate.api;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import javax.inject.Inject;
import javax.inject.Named;
import javax.swing.ImageIcon;

import nl.alexeyu.photomate.model.DefaultPhotoMetaDataBuilder;
import nl.alexeyu.photomate.model.PhotoMetaData;
import nl.alexeyu.photomate.model.PhotoProperty;
import nl.alexeyu.photomate.service.metadata.PhotoMetadataProcessor;
import nl.alexeyu.photomate.service.thumbnail.BufferedImageProvider;
import nl.alexeyu.photomate.service.thumbnail.ThumbnailProvider;

public class LocalPhotoApi<P extends LocalPhoto> implements PhotoApi<Path, P> {
    
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

    @Override
    public Supplier<PhotoMetaData> metaDataSupplier(P photo) {
        return () -> metadataProcessor.read(photo.getPath());
    }

    @Override
    public Supplier<List<ImageIcon>> thumbnailsSupplier(P photo) {
        return () -> getThumbnails(photo);
    }

    private List<ImageIcon> getThumbnails(P photo) {
        var buf = bufferedImageProvider.toBufferedImage(photo.getPath());
        return Arrays.asList(new ImageIcon(thumbnailGenerator.scale(buf)), new ImageIcon(previewGenerator.scale(buf)));
    }

    public void updateProperty(LocalPhoto photo, PhotoProperty property, Object propertyValue) {
        var oldMetaData = photo.metaData();
    	var builder = new DefaultPhotoMetaDataBuilder(oldMetaData);
        var metaData = builder.set(property, propertyValue).build();
        CompletableFuture
        	.runAsync(() -> metadataProcessor.update(photo.getPath(), oldMetaData, metaData))
        	.thenRun(() -> photo.setMetaData(metaData));
    }

}
