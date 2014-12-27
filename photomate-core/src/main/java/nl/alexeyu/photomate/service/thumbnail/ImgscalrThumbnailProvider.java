package nl.alexeyu.photomate.service.thumbnail;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;

import org.imgscalr.Scalr;

public final class ImgscalrThumbnailProvider implements ThumbnailProvider {
	
	private final Dimension thumnailSize;
	
	public ImgscalrThumbnailProvider(Dimension previewSize) {
		this.thumnailSize = previewSize;
	}
	
	@Override
	public Image scale(BufferedImage source) {
    	boolean portrait = source.getHeight() > source.getWidth();
    	Scalr.Mode fitMode = portrait ? Scalr.Mode.FIT_TO_HEIGHT : Scalr.Mode.FIT_TO_WIDTH;
		return Scalr.resize(source, 
				Scalr.Method.SPEED, 
                fitMode, 
                thumnailSize.width, 
                thumnailSize.height);
	}

}
