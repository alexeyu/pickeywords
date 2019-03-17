package nl.alexeyu.photomate.thumbnail;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.BiConsumer;

import javax.swing.ImageIcon;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CachingThumbnailsProviderTest {
    
    private FileThumbnailsProvider fileThumbnailsProvider;
    private ScalingThumbnailsProvider scalingThumbnailsProvider;
    private BiConsumer<Path, ImageIcon> imageSaver;
    private CachingThumbnailsProvider provider;

    @Before
    @SuppressWarnings("unchecked")
    public void init() {
        fileThumbnailsProvider = Mockito.mock(FileThumbnailsProvider.class);
        scalingThumbnailsProvider = Mockito.mock(ScalingThumbnailsProvider.class);
        imageSaver = Mockito.mock(BiConsumer.class);
        provider = new CachingThumbnailsProvider(fileThumbnailsProvider, scalingThumbnailsProvider, imageSaver);
    }
    
    @Test
    public void usesCachedThumbnailsIfPossible() throws IOException {
        when(fileThumbnailsProvider.apply(Mockito.any(Path.class))).thenReturn(List.of(new ImageIcon()));
        var images = provider.apply(Files.createTempFile("img", ""));
        assertEquals(1, images.size());
        verifyZeroInteractions(scalingThumbnailsProvider, imageSaver);
    }

    @Test
    public void scalesImageAndWritesThumbnailToCache() throws IOException {
        var image = new ImageIcon();
        when(fileThumbnailsProvider.apply(Mockito.any(Path.class))).thenReturn(List.of());
        when(scalingThumbnailsProvider.apply(Mockito.any(Path.class))).thenReturn(List.of(image));
        var imageFile = Files.createTempFile("img", "");
        Mockito.doAnswer(i -> null).when(imageSaver).accept(imageFile, image);
        var images = provider.apply(imageFile);
        assertEquals(1, images.size());
    }

}
