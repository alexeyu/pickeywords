package nl.alexeyu.photomate.api.archive;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.nio.file.Path;

import nl.alexeyu.photomate.api.PhotoFileProcessor;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

public class ArchivePhotoTest {
    
    @Test
    public void delete() {
        Path photoPath = mock(Path.class);
        PhotoFileProcessor photoCleaner = mock(PhotoFileProcessor.class);
        ArchivePhoto photo = new ArchivePhoto(photoPath, photoCleaner);
        assertFalse(photo.isDeleted());
        photo.delete();
        ArgumentCaptor<Path> pathCaptor = ArgumentCaptor.forClass(Path.class);
        verify(photoCleaner).process(pathCaptor.capture());
        assertTrue(photo.isDeleted());
        assertSame(photoPath, pathCaptor.getValue());
    }

}
