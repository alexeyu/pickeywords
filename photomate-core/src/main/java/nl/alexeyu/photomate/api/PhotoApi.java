package nl.alexeyu.photomate.api;

import nl.alexeyu.photomate.model.Photo;

public interface PhotoApi<P extends Photo> {
    
    void provideThumbnail(P photo);
    
    void provideMetadata(P photo);

}