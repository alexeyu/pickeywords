package nl.alexeyu.photomate.api;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Stream;

import javax.swing.ImageIcon;

import nl.alexeyu.photomate.model.PhotoMetaData;
import reactor.core.publisher.Flux;

public interface PhotoApi<S, P extends AbstractPhoto> {
    
    default List<P> createPhotos(Stream<S> sources, PhotoFactory<S, P> photoFactory) {
        return Flux.fromStream(sources)
        		.map(photoFactory::createPhoto)
        		.doOnNext(photo ->  initMetaData(photo))
        		.doOnNext(photo -> initThumbnails(photo))
                .buffer()
                .blockFirst();
    }
    
    default void initMetaData(P photo) {
    	CompletableFuture.supplyAsync(metaDataSupplier(photo))
    		.thenAccept(photo::setMetaData);
    }
    
    default void initThumbnails(P photo) {
    	CompletableFuture.supplyAsync(thumbnailsSupplier(photo))
    		.thenAccept(thumbnails -> thumbnails.forEach(photo::addThumbnail));
    }

    Supplier<? extends PhotoMetaData> metaDataSupplier(P photo);
    
    Supplier<List<ImageIcon>> thumbnailsSupplier(P photo);
    
}
