package nl.alexeyu.photomate.service.thumbnail;

import java.awt.Image;
import java.awt.image.BufferedImage;

public interface ThumbnailProvider {

	Image getThumbnail(BufferedImage source);

}
