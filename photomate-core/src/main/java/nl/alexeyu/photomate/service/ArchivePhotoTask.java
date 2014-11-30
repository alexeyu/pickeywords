package nl.alexeyu.photomate.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.alexeyu.photomate.api.editable.EditablePhoto;

import com.google.common.io.Files;

public class ArchivePhotoTask implements  Runnable {

	private final Logger logger = LoggerFactory.getLogger("ArchivePhotoTask");
	
	private final EditablePhoto photo;
	
	private final Path directory;

	public ArchivePhotoTask(EditablePhoto photo, Path directory) {
		this.photo = photo;
		this.directory = directory;
	}

	@Override
	public void run() {
		try {
		    File archiveFile = new File(directory.toFile(), photo.getName());
		    Files.createParentDirs(archiveFile);
			Files.copy(photo.getPath().toFile(), archiveFile);
		} catch (IOException ex) {
			logger.error("Error on copying file", ex);
		}
	}

}
