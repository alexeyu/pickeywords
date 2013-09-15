package nl.alexeyu.photomate.service;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import nl.alexeyu.photomate.model.Photo;

import org.apache.commons.io.FileUtils;

public class MovePhotoTask implements PrioritizedTask {
	
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyy-MM-dd");
	
	private final Photo photo;
	
	private final File directory;

	public MovePhotoTask(Photo photo, File directory) {
		this.photo = photo;
		this.directory = directory;
	}

	@Override
	public void run() {
		directory.mkdir();
		String subdirName = DATE_FORMAT.format(photo.getFile().lastModified());
		File subdir = new File(directory, subdirName);
		subdir.mkdir();
		try {
			FileUtils.copyFile(photo.getFile(), new File(subdir, photo.getName()), true);
			photo.getFile().deleteOnExit();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public int getPriority() {
		return 10;
	}

}
