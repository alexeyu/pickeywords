package nl.alexeyu.photomate.thumbnail;

import java.awt.Image;
import java.awt.image.BufferedImage;

public interface ThumbnailProvider {

	Image scale(BufferedImage source);

}
