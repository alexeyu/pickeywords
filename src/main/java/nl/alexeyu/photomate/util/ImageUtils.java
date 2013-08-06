package nl.alexeyu.photomate.util;

import java.io.File;
import java.net.URL;

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

	public static boolean isJpeg(File file) {
		String fileName = file.getName().toLowerCase();
		return fileName.endsWith("jpg") || fileName.endsWith("jpeg");
	}

	public static File getThumbnailFile(Photo photo) {
		return new File(tempDir + "/" + photo.getName());
	}
	
}
