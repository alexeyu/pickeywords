package nl.alexeyu.photomate.service.thumbnail;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;

import com.google.common.io.Files;

public class ImgscalrThumbnailProvider implements ThumbnailProvider {
	
	private final Dimension previewSize;
	private final Dimension thumbnailSize;
	
	public ImgscalrThumbnailProvider(Dimension thumbnailSize, Dimension previewSize) {
		this.previewSize = previewSize;
		this.thumbnailSize = thumbnailSize;
	}
	
	@Override
    public Thumbnails getThumbnails(Path photoFile, boolean generatePreview) {
        try {
        	byte[] content = Files.toByteArray(photoFile.toFile());
        	InputStream is = new ByteArrayInputStream(content);
        	BufferedImage source;
        	synchronized (this) {
        		source = ImageIO.read(is);
        	}
            Image thumbnail = scale(source, thumbnailSize);
            Image preview = generatePreview ? scale(source, previewSize) : null;
            return new Thumbnails(thumbnail, preview);
        } catch (IOException ex) {
            throw new IllegalStateException("Could not read image " + photoFile, ex);
        }
	}
	
	private Image scale(BufferedImage source, Dimension size) {
    	boolean portrait = source.getHeight() > source.getWidth();
    	Scalr.Mode fitMode = portrait ? Scalr.Mode.FIT_TO_HEIGHT : Scalr.Mode.FIT_TO_WIDTH; 
		return Scalr.resize(source, 
				Scalr.Method.SPEED, 
                fitMode, 
                size.width, 
                size.height);
	}

	
}
