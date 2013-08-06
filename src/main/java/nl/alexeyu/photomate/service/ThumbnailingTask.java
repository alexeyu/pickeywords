package nl.alexeyu.photomate.service;

import static java.awt.RenderingHints.*;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import nl.alexeyu.photomate.model.Photo;
import nl.alexeyu.photomate.ui.Constants;
import nl.alexeyu.photomate.util.ImageUtils;

public class ThumbnailingTask implements Runnable {
	
	private final Logger logger = Logger.getLogger("photomate.ThumbnailingTask");

	private static final GraphicsConfiguration GCONFIG = GraphicsEnvironment
			.getLocalGraphicsEnvironment()
			.getDefaultScreenDevice()
			.getDefaultConfiguration();
	
	private final Photo photo;
	
	private final UpdateListener<Photo> observer;
	
	public ThumbnailingTask(Photo photo, UpdateListener<Photo> observer) {
		this.photo = photo;
		this.observer = observer;
	}

	public void run() {
		try {
			long time = System.currentTimeMillis();
			BufferedImage source = ImageIO.read(photo.getFile());
			int w = Constants.THUMBNAIL_SIZE.width;
			int h = Constants.THUMBNAIL_SIZE.height;
			BufferedImage image = GCONFIG.createCompatibleImage(w, h);
			Graphics2D g2d = image.createGraphics();
			double xScale = (double) w / source.getWidth();
			double yScale = (double) h / source.getHeight();
			AffineTransform at = AffineTransform.getScaleInstance(xScale, yScale);
			g2d.setRenderingHint(KEY_INTERPOLATION, VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			g2d.drawRenderedImage(source, at);
			photo.setThumbnail(image);
			observer.onUpdate(photo);
			System.out.println(">> " + (System.currentTimeMillis() - time));
			g2d.dispose();
			File thumbnailFile = ImageUtils.getThumbnailFile(photo);
			thumbnailFile.deleteOnExit();
			ImageIO.write(image, "jpg", thumbnailFile);
		} catch (IOException ex) {
			logger.log(Level.SEVERE, "Cannot make thumbnail", ex);
		}
	}
	
}
