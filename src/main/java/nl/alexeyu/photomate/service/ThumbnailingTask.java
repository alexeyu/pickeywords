package nl.alexeyu.photomate.service;

import static java.awt.RenderingHints.KEY_INTERPOLATION;
import static java.awt.RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;

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

public class ThumbnailingTask implements Runnable {
	
	private final Logger logger = Logger.getLogger("photomate.ThumbnailingTask");

	private static final GraphicsConfiguration GCONFIG = GraphicsEnvironment
			.getLocalGraphicsEnvironment()
			.getDefaultScreenDevice()
			.getDefaultConfiguration();
	
	private final Photo photo;
	
	private final UpdateListener<Photo> observer;
	
	private final String tempDir;
	
	public ThumbnailingTask(Photo photo, UpdateListener<Photo> observer) {
		this.photo = photo;
		this.observer = observer;
		this.tempDir = System.getProperty("java.io.tmpdir");
	}

	public void run() {
		try {
			long time = System.currentTimeMillis();
			BufferedImage source = ImageIO.read(photo.getFile());
			int w = Constants.THUMBNAIL_SIZE.width;
			int h = Constants.THUMBNAIL_SIZE.height;
			BufferedImage bi = GCONFIG.createCompatibleImage(w, h);
			Graphics2D g2d = bi.createGraphics();
			double xScale = (double) w / source.getWidth();
			double yScale = (double) h / source.getHeight();
			AffineTransform at = AffineTransform.getScaleInstance(xScale, yScale);
			g2d.setRenderingHint(KEY_INTERPOLATION, VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			g2d.drawRenderedImage(source, at);
			photo.setThumbnail(bi);
			observer.onUpdate(photo);
			logger.log(Level.FINE, ">> " + (System.currentTimeMillis() - time));
			g2d.dispose();
			File thumbnailFile = new File(tempDir + "/" + photo.getName());
			thumbnailFile.deleteOnExit();
			ImageIO.write(bi, "jpg", thumbnailFile);
		} catch (IOException ex) {
			logger.log(Level.SEVERE, "Cannot make thumbnail", ex);
		}
	}
	
}
