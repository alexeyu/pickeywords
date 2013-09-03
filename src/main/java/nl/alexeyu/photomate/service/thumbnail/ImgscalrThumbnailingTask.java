package nl.alexeyu.photomate.service.thumbnail;

import static nl.alexeyu.photomate.ui.Constants.THUMBNAIL_SIZE;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import nl.alexeyu.photomate.model.Photo;
import nl.alexeyu.photomate.service.UpdateListener;

import org.imgscalr.Scalr;

public class ImgscalrThumbnailingTask extends AbstractThumbnailingTask {
	
	public ImgscalrThumbnailingTask(Photo photo, UpdateListener<Photo> observer) {
		super(photo, observer);
	}

	@Override
	protected Image scale() throws IOException {
		BufferedImage source = ImageIO.read(photo.getFile());
		return Scalr.resize(source, 
				Scalr.Method.SPEED, 
				Scalr.Mode.FIT_TO_WIDTH, 
				THUMBNAIL_SIZE.width, 
				THUMBNAIL_SIZE.height);
	}

	
}
