package nl.alexeyu.photomate.service.thumbnail;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

import javax.imageio.ImageIO;

public final class BufferedImageProvider {

    public BufferedImage toBufferedImage(Path photoFile) {
        try {
        	return ImageIO.read(photoFile.toFile());
        } catch (IOException ex) {
            throw new IllegalStateException("Could not read image " + photoFile, ex);
        }
	}

}
