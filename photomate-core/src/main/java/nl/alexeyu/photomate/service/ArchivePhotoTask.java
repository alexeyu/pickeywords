package nl.alexeyu.photomate.service;

import java.io.File;
import java.io.IOException;

import nl.alexeyu.photomate.api.EditablePhoto;

import org.apache.commons.io.FileUtils;

public class ArchivePhotoTask implements PrioritizedTask, Runnable {
	
	private final EditablePhoto photo;
	
	private final File directory;

	public ArchivePhotoTask(EditablePhoto photo, File directory) {
		this.photo = photo;
		this.directory = directory;
	}

	@Override
	public void run() {
		directory.mkdir();
		try {
		    File file = photo.getFile();
			FileUtils.copyFile(file, new File(directory, file.getName()), true);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public TaskPriority getPriority() {
		return TaskPriority.HIGH;
	}

}
