package nl.alexeyu.photomate.api.archive;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.nio.file.Path;

import org.junit.Test;

public class ArchivePhotoTest {
    
    @Test
    public void delete() {
        Path photoPath = mock(Path.class);
        ArchivePhoto photo = new ArchivePhoto(photoPath);
        assertFalse(photo.isDeleted());
        photo.delete();
        assertTrue(photo.isDeleted());
    }

}
