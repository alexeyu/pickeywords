package nl.alexeyu.photomate.api;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.ImageIcon;

import nl.alexeyu.photomate.model.PhotoMetaData;

public interface PhotoApi<S, P extends AbstractPhoto> {
    
    default List<P> createPhotos(Stream<S> sources, PhotoFactory<S, P> photoFactory) {
        List<P> photos = sources
                .map(s -> photoFactory.createPhoto(s))
                .collect(Collectors.toList());
        photos.forEach(photo -> {
            CompletableFuture.supplyAsync(metaDataSupplier(photo))
                .thenAccept(m -> photo.setMetaData(m));
            CompletableFuture.supplyAsync(thumbnailsSupplier(photo))
                .thenAccept(thumbnails -> thumbnails.forEach(
                        t -> photo.addThumbnail(t)));
        });
        return photos;
    }
    
    Supplier<PhotoMetaData> metaDataSupplier(P photo);
    
    Supplier<List<ImageIcon>> thumbnailsSupplier(P photo);
    
}
