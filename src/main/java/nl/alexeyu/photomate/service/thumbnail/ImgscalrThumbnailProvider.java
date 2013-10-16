package nl.alexeyu.photomate.service.thumbnail;

import static nl.alexeyu.photomate.ui.Constants.THUMBNAIL_SIZE;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import nl.alexeyu.photomate.service.ThumbnailProvider;

import org.imgscalr.Scalr;

public class ImgscalrThumbnailProvider implements ThumbnailProvider {

	@Override
    public Image getThumbnail(String photoPath) throws Exception {
        BufferedImage source = ImageIO.read(new File(photoPath));
        return Scalr.resize(source, 
                Scalr.Method.SPEED, 
                Scalr.Mode.FIT_TO_WIDTH, 
                THUMBNAIL_SIZE.width, 
                THUMBNAIL_SIZE.height);
	}

	
}
