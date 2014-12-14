package nl.alexeyu.photomate.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import nl.alexeyu.photomate.api.editable.EditablePhoto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArchivePhotoTask implements Runnable {

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
			Files.createDirectories(directory);
		    Path archiveFile = directory.resolve(photo.name());
		    Files.copy(photo.getPath(), archiveFile);
		} catch (IOException ex) {
			logger.error("Error on copying file", ex);
		}
	}

}
