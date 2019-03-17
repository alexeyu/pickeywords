package nl.alexeyu.photomate.thumbnail;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

public class ScalingThumbnailsProvider implements ThumbnailsProvider {

    private final BufferedImageProvider bufferedImageProvider;

    private final ThumbnailProvider thumbnailGenerator;

    private final ThumbnailProvider previewGenerator;

    public ScalingThumbnailsProvider(BufferedImageProvider bufferedImageProvider, ThumbnailProvider thumbnailGenerator) {
        this(bufferedImageProvider, thumbnailGenerator, null);
    }

    public ScalingThumbnailsProvider(BufferedImageProvider bufferedImageProvider, ThumbnailProvider thumbnailGenerator,
            ThumbnailProvider previewGenerator) {
        this.bufferedImageProvider = bufferedImageProvider;
        this.thumbnailGenerator = thumbnailGenerator;
        this.previewGenerator = previewGenerator;
    }

    @Override
    public List<ImageIcon> apply(Path path) {
        var buf = bufferedImageProvider.toBufferedImage(path);
        List<ImageIcon> thumbnailList = new ArrayList<>(2);
        thumbnailList.add(new ImageIcon(thumbnailGenerator.scale(buf)));
        if (previewGenerator != null) {
            thumbnailList.add(new ImageIcon(previewGenerator.scale(buf)));
        }
        return thumbnailList;
    }

}
