package nl.alexeyu.photomate.util;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import javax.swing.ImageIcon;

public class ImageUtils {

    public static ImageIcon getImage(String name) {
        var url = ImageUtils.class.getResource("/img/" + name);
        return new ImageIcon(url);
    }

    public static Stream<Path> getJpegImages(Path dir) {
        try {
            return Files.list(dir)
                    .filter(path -> isJpeg(path));
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public static boolean isJpeg(Path path) {
        var fileName = path.getFileName()
                .toString()
                .toLowerCase();
        return fileName.endsWith(".jpg") || fileName.endsWith(".jpeg");
    }

}
