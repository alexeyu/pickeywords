package nl.alexeyu.photomate.api.editable;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.swing.ImageIcon;

import nl.alexeyu.photomate.model.DefaultPhotoMetaDataBuilder;
import nl.alexeyu.photomate.model.PhotoMetaData;
import nl.alexeyu.photomate.model.PhotoProperty;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class EditablePhotoTest {
    
    private EditablePhoto photo;
    
    private PhotoMetaData metaData;
    
    private ImageIcon thumbnail;
    
    private ImageIcon preview;
    
    @Before
    public void setUp() {
        Path photoPath = Mockito.mock(Path.class);
        File photoFile = Mockito.mock(File.class);
        Mockito.when(photoPath.toFile()).thenReturn(photoFile); 
        photo = new EditablePhoto(photoPath);
        
        thumbnail = Mockito.mock(ImageIcon.class);
        preview = Mockito.mock(ImageIcon.class);
        metaData = new DefaultPhotoMetaDataBuilder()
            .set(PhotoProperty.CREATOR, "Genius")
            .build();
    }
    
    @Test
    public void emptyPhoto() {
        assertFalse(photo.thumbnail().isPresent());
        assertFalse(photo.preview().isPresent());
        assertFalse(photo.metaData().isPresent());
    }

    @Test
    public void thumbnails() {
        photo.addThumbnail(thumbnail);
        photo.addThumbnail(preview);
        assertSame(thumbnail, photo.thumbnail().get());
        assertSame(preview, photo.preview().get());
    }

    @Test
    public void metaData() {
        photo.setMetaData(metaData);
        assertSame(metaData, photo.metaData().get());
    }
    
    @Test
    public void readyToUpload() {
        assertFalse(photo.isReadyToUpload());
        
        photo.addThumbnail(thumbnail);
        assertFalse("Cannot upload - metadata is not set", photo.isReadyToUpload());
        
        photo.setMetaData(metaData);
        assertFalse("Cannot upload - no caption", photo.isReadyToUpload());
        
        PhotoMetaData metaDataWithCaption = new DefaultPhotoMetaDataBuilder(metaData).
                set(PhotoProperty.CAPTION, "Masterpeace").build();
        photo.setMetaData(metaDataWithCaption);
        assertFalse("Cannot upload - no description", photo.isReadyToUpload());
        
        PhotoMetaData metaDataWithDescription = new DefaultPhotoMetaDataBuilder(metaDataWithCaption).
                set(PhotoProperty.DESCRIPTION, "No need").build();
        photo.setMetaData(metaDataWithDescription);
        assertFalse("Cannot upload - no keywords", photo.isReadyToUpload());
        
        List<String> keywords = IntStream.rangeClosed(0, 51)
                .mapToObj(i -> String.valueOf(i))
                .collect(Collectors.toList());
        PhotoMetaData metaDataWithTooManyKeywords = new DefaultPhotoMetaDataBuilder(metaDataWithDescription)
            .set(PhotoProperty.KEYWORDS, keywords).build();
        photo.setMetaData(metaDataWithTooManyKeywords);
        assertFalse("Cannot upload - too many keywords", photo.isReadyToUpload());
        
        PhotoMetaData metaDataWithKeywords = new DefaultPhotoMetaDataBuilder(metaDataWithDescription).
                set(PhotoProperty.KEYWORDS, Arrays.asList("summer", "smoke")).build();
        photo.setMetaData(metaDataWithKeywords);
        assertTrue(photo.isReadyToUpload());
    }

}
