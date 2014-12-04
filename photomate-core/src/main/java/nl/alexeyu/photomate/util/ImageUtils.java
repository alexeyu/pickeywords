package nl.alexeyu.photomate.util;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.ImageIcon;

import nl.alexeyu.photomate.model.Photo;

public class ImageUtils {
	
	private final static String tempDir;

	static {
		tempDir = System.getProperty("java.io.tmpdir");
	}
	
	public static ImageIcon getImage(String name) {
		URL url = ImageUtils.class.getResource("/img/" + name);
		return new ImageIcon(url);
	}

	public static boolean isJpeg(Path path) {
		String fileName = path.getFileName().toString().toLowerCase();
		return fileName.endsWith("jpg") || fileName.endsWith("jpeg");
	}

	public static Path getThumbnailFile(Photo photo) {
		return Paths.get(tempDir, photo.name());
	}
	
}
