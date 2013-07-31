package nl.alexeyu.photomate.util;

import java.io.File;
import java.net.URL;

import javax.swing.ImageIcon;

public class ImageUtils {
	
	public static ImageIcon getImage(String name) {
		URL url = ImageUtils.class.getResource("/img/" + name);
		return new ImageIcon(url);
	}

	public static boolean isJpeg(File file) {
		String fileName = file.getName().toLowerCase();
		return fileName.endsWith("jpg") || fileName.endsWith("jpeg");
	}

}
