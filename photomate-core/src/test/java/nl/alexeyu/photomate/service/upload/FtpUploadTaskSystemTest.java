package nl.alexeyu.photomate.service.upload;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.eventbus.EventBus;
import com.google.common.io.Resources;

import nl.alexeyu.photomate.api.editable.EditablePhoto;
import nl.alexeyu.photomate.model.PhotoStock;

public class FtpUploadTaskSystemTest {
	
	private final Path targetPhotoPath = Paths.get("/home/ftptest/test.jpg");
	
	private FtpUploadTask task;
	
	@Before
	public void setUp() throws Exception {
		Path sourcePhotoPath = Paths.get(Resources.getResource("test.jpg").toURI());
		EditablePhoto  photo = new EditablePhoto(sourcePhotoPath);
		PhotoStock stock = new PhotoStock("test", "", "localhost", "ftptest", "ftptest");
		PhotoToStock photoToStock = new PhotoToStock(photo, stock);
		task = new FtpUploadTask(photoToStock, 1);
		task.setEventBus(new EventBus());
	}
	
	@After
	public void tearDown() throws Exception {
		Files.delete(targetPhotoPath);
	}

	@Test
	public void uploadSucceeds() throws Exception {
		assertFalse(targetPhotoPath.toFile().exists());
		task.run();
		assertTrue(targetPhotoPath.toFile().exists());
	}

	@Test
	public void overwritesExistingFile() throws Exception {
		Files.createFile(targetPhotoPath);
		assertTrue(targetPhotoPath.toFile().exists());
		task.run();
		assertTrue(targetPhotoPath.toFile().exists());
		assertTrue(targetPhotoPath.toFile().length() > 1000);
	}

}
