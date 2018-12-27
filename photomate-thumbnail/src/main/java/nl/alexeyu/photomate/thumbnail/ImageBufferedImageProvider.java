package nl.alexeyu.photomate.thumbnail;

import java.awt.image.BufferedImage;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ImageBufferedImageProvider implements BufferedImageProvider {

	private static final Logger logger = LogManager.getLogger();

    @Override
	public BufferedImage toBufferedImage(Path photoFile) {
        try {
        	logger.debug("Reading {}", photoFile);
            return ImageIO.read(photoFile.toFile());
        } catch (Exception ex) {
        	logger.catching(ex);
        	return new BufferedImage(0, 0, BufferedImage.TYPE_INT_RGB);
        }
    }

}
