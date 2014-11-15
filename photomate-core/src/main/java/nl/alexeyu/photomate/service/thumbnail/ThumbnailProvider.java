package nl.alexeyu.photomate.service.thumbnail;

import java.nio.file.Path;

public interface ThumbnailProvider {
    
    Thumbnails getThumbnails(Path photoPath, boolean generatePreview);

}
