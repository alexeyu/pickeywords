package nl.alexeyu.photomate.thumbnail;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import javax.swing.ImageIcon;

import org.junit.Test;

public class FileThumbnailsProviderTest {

    private FileThumbnailsProvider provider = new FileThumbnailsProvider();

    @Test
    public void returnsEmptyListIfThumbnailFileDoesntExist() throws IOException {
        Path file = Files.createTempFile("thumb", "");
        List<ImageIcon> images = provider.apply(file);
        assertEquals(0, images.size());
    }

    @Test
    public void readsImageFromThumbnailFile() throws IOException {
        Path file = Files.createTempFile("thumb", FileThumbnailsProvider.CACHE_SUFFIX);
        String nameWithoutSuffix = file.toString().substring(0, file.toString().indexOf(FileThumbnailsProvider.CACHE_SUFFIX));
        List<ImageIcon> images = provider.apply(Path.of(nameWithoutSuffix));
        assertEquals(1, images.size());
    }
}
