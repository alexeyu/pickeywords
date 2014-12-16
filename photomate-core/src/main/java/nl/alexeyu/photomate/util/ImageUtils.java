package nl.alexeyu.photomate.util;

import java.net.URL;
import java.nio.file.Path;

import javax.swing.ImageIcon;

public class ImageUtils {
	
	public static ImageIcon getImage(String name) {
		URL url = ImageUtils.class.getResource("/img/" + name);
		return new ImageIcon(url);
	}

	public static boolean isJpeg(Path path) {
		String fileName = path.getFileName().toString().toLowerCase();
		return fileName.endsWith("jpg") || fileName.endsWith("jpeg");
	}

}
