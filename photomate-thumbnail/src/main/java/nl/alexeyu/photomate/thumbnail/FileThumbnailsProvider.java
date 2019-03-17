package nl.alexeyu.photomate.thumbnail;

import java.nio.file.Path;
import java.util.List;

import javax.swing.ImageIcon;

public class FileThumbnailsProvider implements ThumbnailsProvider {

    public static final String CACHE_SUFFIX = ".thumbnail";

    @Override
    public List<ImageIcon> apply(Path path) {
        Path cachedFile = Path.of(path.toString() + CACHE_SUFFIX);
        if (cachedFile.toFile().exists()) {
            return List.of(new ImageIcon(cachedFile.toString()));
        }
        return List.of();
    }

}
