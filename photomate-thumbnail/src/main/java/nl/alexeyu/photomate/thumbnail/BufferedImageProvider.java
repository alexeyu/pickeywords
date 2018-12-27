package nl.alexeyu.photomate.thumbnail;

import java.awt.image.BufferedImage;
import java.nio.file.Path;

public interface BufferedImageProvider {

	BufferedImage toBufferedImage(Path photoFile);

}