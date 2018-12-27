package nl.alexeyu.photomate.thumbnail;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.imgscalr.Scalr;

public final class ImgscalrThumbnailProvider implements ThumbnailProvider {

	private static final Logger logger = LogManager.getLogger();

	private final Dimension thumnailSize;

    public ImgscalrThumbnailProvider(Dimension previewSize) {
        this.thumnailSize = previewSize;
    }

    @Override
    public Image scale(BufferedImage source) {
    	logger.debug("{} scales {}", this, source);
        boolean portrait = source.getHeight() > source.getWidth();
        Scalr.Mode fitMode = portrait ? Scalr.Mode.FIT_TO_HEIGHT : Scalr.Mode.FIT_TO_WIDTH;
        try {
        	return Scalr.resize(source, Scalr.Method.SPEED, fitMode, thumnailSize.width, thumnailSize.height);
        } catch (Exception ex) {
        	logger.catching(ex);
        	return new BufferedImage(thumnailSize.width, thumnailSize.height, 
        			BufferedImage.TYPE_INT_RGB);
        }
    }

}
