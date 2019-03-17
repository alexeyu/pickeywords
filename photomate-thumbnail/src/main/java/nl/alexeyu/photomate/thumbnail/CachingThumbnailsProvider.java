package nl.alexeyu.photomate.thumbnail;

import java.nio.file.Path;
import java.util.List;
import java.util.function.BiConsumer;

import javax.swing.ImageIcon;

public class CachingThumbnailsProvider implements ThumbnailsProvider {

    private final FileThumbnailsProvider fileThumbnailsProvider;
    private final ScalingThumbnailsProvider scalingThumbnailsProvider;
    private final BiConsumer<Path, ImageIcon> imageSaver; 

    public CachingThumbnailsProvider(FileThumbnailsProvider fileThumbnailsProvider,
            ScalingThumbnailsProvider scalingThumbnailsProvider) {
        this(fileThumbnailsProvider, scalingThumbnailsProvider, new ImageSaver());
    }

    public CachingThumbnailsProvider(FileThumbnailsProvider fileThumbnailsProvider,
            ScalingThumbnailsProvider scalingThumbnailsProvider,
            BiConsumer<Path, ImageIcon> imageSaver) {
        this.fileThumbnailsProvider = fileThumbnailsProvider;
        this.scalingThumbnailsProvider = scalingThumbnailsProvider;
        this.imageSaver = imageSaver;
    }

    @Override
    public List<ImageIcon> apply(Path path) {
        List<ImageIcon> cachedThumbnails = fileThumbnailsProvider.apply(path);
        if (!cachedThumbnails.isEmpty()) {
            return cachedThumbnails;
        }
        List<ImageIcon> scaledThumbnails = scalingThumbnailsProvider.apply(path);
        imageSaver.accept(path, scaledThumbnails.get(0));
        return scaledThumbnails;
    }

}
