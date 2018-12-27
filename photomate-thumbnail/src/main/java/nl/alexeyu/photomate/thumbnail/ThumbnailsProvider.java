package nl.alexeyu.photomate.thumbnail;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import javax.swing.ImageIcon;

public final class ThumbnailsProvider implements Function<Path, List<ImageIcon>> {
	
    private final BufferedImageProvider bufferedImageProvider;
	
    private final ThumbnailProvider thumbnailGenerator;

    private final ThumbnailProvider previewGenerator;

	public ThumbnailsProvider(BufferedImageProvider bufferedImageProvider, ThumbnailProvider thumbnailGenerator,
			ThumbnailProvider previewGenerator) {
		this.bufferedImageProvider = bufferedImageProvider;
		this.thumbnailGenerator = thumbnailGenerator;
		this.previewGenerator = previewGenerator;
	}

	@Override
	public List<ImageIcon> apply(Path path) {
		var buf = bufferedImageProvider.toBufferedImage(path);
        return Arrays.asList(new ImageIcon(thumbnailGenerator.scale(buf)), new ImageIcon(previewGenerator.scale(buf)));
	}
	
}
