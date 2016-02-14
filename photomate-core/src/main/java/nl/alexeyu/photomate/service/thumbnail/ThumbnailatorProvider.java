package nl.alexeyu.photomate.service.thumbnail;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import net.coobird.thumbnailator.Thumbnails;

public class ThumbnailatorProvider implements ThumbnailProvider {

	private final Dimension thumnailSize;
	
	public ThumbnailatorProvider(Dimension thumnailSize) {
		this.thumnailSize = thumnailSize;
	}

	@Override
	public Image scale(BufferedImage source) {
		try {
			return Thumbnails.of(source)
					.size(thumnailSize.width, thumnailSize.height)
					.outputQuality(0.6)
					.asBufferedImage();
		} catch (IOException e) {
			throw new IllegalStateException();
		}
	}

}
