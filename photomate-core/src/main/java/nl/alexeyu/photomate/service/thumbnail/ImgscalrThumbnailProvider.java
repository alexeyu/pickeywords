package nl.alexeyu.photomate.service.thumbnail;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.imgscalr.Scalr;

public class ImgscalrThumbnailProvider implements ThumbnailProvider {
	
	private final Dimension previewSize;
	private final Dimension thumbnailSize;
	
	public ImgscalrThumbnailProvider(Dimension thumbnailSize, Dimension previewSize) {
		this.previewSize = previewSize;
		this.thumbnailSize = thumbnailSize;
	}
	
	@Override
    public Pair<Image, Image> getThumbnails(File photoFile, boolean generatePreview) {
        try {
        	byte[] content = FileUtils.readFileToByteArray(photoFile);
        	InputStream is = new ByteArrayInputStream(content);
        	BufferedImage source;
        	synchronized (this) {
        		source = ImageIO.read(is);
        	}
        	boolean portrait = source.getHeight() > source.getWidth();
        	Scalr.Mode fitMode = portrait ? Scalr.Mode.FIT_TO_HEIGHT : Scalr.Mode.FIT_TO_WIDTH; 
            Image thumbnail = Scalr.resize(source, 
                    Scalr.Method.SPEED, 
                    fitMode, 
                    thumbnailSize.width, 
                    thumbnailSize.height);
            Image preview = generatePreview 
                    ? Scalr.resize(source, 
                        Scalr.Method.SPEED, 
                        fitMode, 
                        previewSize.width, 
                        previewSize.height)
                    : null;
            return new ImmutablePair<Image, Image>(thumbnail, preview);
        } catch (IOException ex) {
            throw new IllegalStateException("Could not read image " + photoFile, ex);
        }
	}

	
}
