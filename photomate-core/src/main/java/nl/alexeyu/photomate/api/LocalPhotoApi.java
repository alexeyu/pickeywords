package nl.alexeyu.photomate.api;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import javax.swing.ImageIcon;

import nl.alexeyu.photomate.model.DefaultPhotoMetaDataBuilder;
import nl.alexeyu.photomate.model.PhotoMetaData;
import nl.alexeyu.photomate.model.PhotoProperty;
import nl.alexeyu.photomate.service.metadata.PhotoMetadataProcessor;
import nl.alexeyu.photomate.thumbnail.ThumbnailsProvider;

public class LocalPhotoApi<P extends LocalPhoto> implements PhotoApi<Path, P>, LocalPhotoUpdater {

    private final PhotoMetadataProcessor metadataProcessor;

    private final ThumbnailsProvider thumbnailsProvider;

    public LocalPhotoApi(PhotoMetadataProcessor metadataProcessor, ThumbnailsProvider thumbnailsProvider) {
        this.metadataProcessor = metadataProcessor;
        this.thumbnailsProvider = thumbnailsProvider;
    }

    @Override
    public Supplier<PhotoMetaData> metaDataSupplier(P photo) {
        return () -> metadataProcessor.read(photo.getPath());
    }

    @Override
    public Supplier<List<ImageIcon>> thumbnailsSupplier(P photo) {
        return () -> thumbnailsProvider.apply(photo.getPath());
    }

    @Override
    public void updateProperty(LocalPhoto photo, PhotoProperty property, Object propertyValue) {
        var oldMetaData = photo.metaData();
        var metaData = new DefaultPhotoMetaDataBuilder(oldMetaData).set(property, propertyValue).build();
        CompletableFuture.runAsync(() -> metadataProcessor.update(photo.getPath(), oldMetaData, metaData))
                .thenRunAsync(() -> photo.setMetaData(metadataProcessor.read(photo.getPath())));
    }

}
