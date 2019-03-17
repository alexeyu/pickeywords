package nl.alexeyu.photomate.thumbnail;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.function.BiConsumer;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ImageSaver implements BiConsumer<Path, ImageIcon> {
    
    private static final Logger logger = LogManager.getLogger();

    @Override
    public void accept(Path path, ImageIcon imageIcon) {
        BufferedImage image = (BufferedImage) imageIcon.getImage();
        try {
            ImageIO.write(image, "png", thumbnailFile(path));
        } catch (IOException ex) {
            logger.error("Could not cache thumbnail for {}", path);
            logger.catching(ex);
        }
    }
    
    private File thumbnailFile(Path path) {
        return new File(path.toAbsolutePath().toString() + FileThumbnailsProvider.CACHE_SUFFIX);
    }

}
