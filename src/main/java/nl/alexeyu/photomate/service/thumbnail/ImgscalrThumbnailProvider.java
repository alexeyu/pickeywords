package nl.alexeyu.photomate.service.thumbnail;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import nl.alexeyu.photomate.ui.UiConstants;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.imgscalr.Scalr;

public class ImgscalrThumbnailProvider implements ThumbnailProvider {

	@Override
    public Pair<Image, Image> getThumbnails(File photoFile, boolean generatePreview) {
        try {
            BufferedImage source = ImageIO.read(photoFile);
            Image thumbnail = Scalr.resize(source, 
                    Scalr.Method.SPEED, 
                    Scalr.Mode.FIT_TO_WIDTH, 
                    UiConstants.THUMBNAIL_SIZE.width, 
                    UiConstants.THUMBNAIL_SIZE.height);
            Image preview = generatePreview 
                    ? Scalr.resize(source, 
                        Scalr.Method.SPEED, 
                        Scalr.Mode.FIT_TO_WIDTH, 
                        UiConstants.PREVIEW_SIZE.width, 
                        UiConstants.PREVIEW_SIZE.height)
                    : null;
            return new ImmutablePair<Image, Image>(thumbnail, preview);
        } catch (IOException ex) {
            throw new IllegalStateException("Could not read image " + photoFile, ex);
        }
	}

	
}
