package nl.alexeyu.photomate.service.thumbnail;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.coobird.thumbnailator.Thumbnails;

public class ThumbnailatorProvider implements ThumbnailProvider {
	
	private static final Logger logger = LogManager.getLogger();

    private final Dimension thumnailSize;

    public ThumbnailatorProvider(Dimension thumnailSize) {
        this.thumnailSize = thumnailSize;
    }

    @Override
    public Image scale(BufferedImage source) {
        try {
        	logger.debug("{} scales {}", this, source);
            return Thumbnails.of(source)
                    .size(thumnailSize.width, thumnailSize.height)
                    .outputQuality(0.6)
                    .asBufferedImage();
        } catch (Exception ex) {
        	logger.catching(ex);
        	return new BufferedImage(thumnailSize.width, thumnailSize.height, 
        			BufferedImage.TYPE_INT_RGB);
        }
    }

}
