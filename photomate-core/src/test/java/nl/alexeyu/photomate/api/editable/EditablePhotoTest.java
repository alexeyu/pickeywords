package nl.alexeyu.photomate.api.editable;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.swing.ImageIcon;

import org.junit.Before;
import org.junit.Test;

import nl.alexeyu.photomate.model.DefaultPhotoMetaDataBuilder;
import nl.alexeyu.photomate.model.PhotoMetaData;
import nl.alexeyu.photomate.model.PhotoProperty;

public class EditablePhotoTest {
    
    private EditablePhoto photo;
    
    private PhotoMetaData metaDataWithCreator;
    
    private ImageIcon thumbnail;
    
    private ImageIcon preview;
    
    @Before
    public void setUp() {
        photo = new EditablePhoto(Paths.get("."));
        
        thumbnail = new ImageIcon(new byte[] {});
        preview = new ImageIcon(new byte[] {});
        metaDataWithCreator = new DefaultPhotoMetaDataBuilder()
            .set(PhotoProperty.CREATOR, "Genius")
            .build();
    }
    
    @Test
    public void emptyPhoto() {
        assertNull(photo.thumbnail().getImage());
        assertNull(photo.preview().getImage());
        assertTrue(photo.metaData().isEmpty());
    }

    @Test
    public void thumbnails() {
        photo.addThumbnail(thumbnail);
        photo.addThumbnail(preview);
        assertSame(thumbnail, photo.thumbnail());
        assertSame(preview, photo.preview());
    }

    @Test
    public void metaData() {
        photo.setMetaData(metaDataWithCreator);
        assertSame(metaDataWithCreator, photo.metaData());
    }
    
    @Test
    public void cannotUploadEmptyPhoto() {
        assertFalse(photo.isReadyToUpload());
    }

    @Test
    public void cannotUploadPhotoWithThumbailOnly() {
        photo.addThumbnail(thumbnail);
        assertFalse("Cannot upload - metadata is not set", photo.isReadyToUpload());
    }

    @Test
    public void notReadyToUploadPhotoWithoutCaption() {
        photo.addThumbnail(thumbnail);        
        photo.setMetaData(metaDataWithCreator);
        assertFalse("Cannot upload - no caption", photo.isReadyToUpload());
    }

    @Test
    public void cannotUploadWithoutDescription() {
        photo.addThumbnail(thumbnail);
        PhotoMetaData metaDataWithCaption = new DefaultPhotoMetaDataBuilder(metaDataWithCreator).
                set(PhotoProperty.CAPTION, "Masterpeace").build();
        photo.setMetaData(metaDataWithCaption);
        assertFalse("Cannot upload - no description", photo.isReadyToUpload());
    }

    @Test
    public void cannotUploadWithoutKeywords() {
        photo.addThumbnail(thumbnail);
        PhotoMetaData metaDataWithDescription = new DefaultPhotoMetaDataBuilder(metaDataWithCreator)
                .set(PhotoProperty.CAPTION, "Masterpeace")
                .set(PhotoProperty.DESCRIPTION, "No need")
                .build();
        photo.setMetaData(metaDataWithDescription);
        assertFalse("Cannot upload - no keywords", photo.isReadyToUpload());
    }

    @Test
    public void cannotUploadWithTooManyKeywords() {
        photo.addThumbnail(thumbnail);
        PhotoMetaData metaDataWithDescription = new DefaultPhotoMetaDataBuilder(metaDataWithCreator)
                .set(PhotoProperty.CAPTION, "Masterpeace")
                .set(PhotoProperty.DESCRIPTION, "No need")
                .build();
        List<String> keywords = IntStream.rangeClosed(0, 51)
                .mapToObj(i -> String.valueOf(i))
                .collect(Collectors.toList());
        PhotoMetaData metaDataWithTooManyKeywords = new DefaultPhotoMetaDataBuilder(metaDataWithDescription)
            .set(PhotoProperty.KEYWORDS, keywords).build();
        photo.setMetaData(metaDataWithTooManyKeywords);
        assertFalse("Cannot upload - too many keywords", photo.isReadyToUpload());
    }

    @Test
    public void readyToUpload() {
        photo.addThumbnail(thumbnail);
        PhotoMetaData metaDataWithKeywords = new DefaultPhotoMetaDataBuilder()
        		.set(PhotoProperty.CAPTION, "Caption")
        		.set(PhotoProperty.DESCRIPTION, "Description")
        		.set(PhotoProperty.KEYWORDS, asList("summer", "smoke")).build();
        photo.setMetaData(metaDataWithKeywords);
        assertTrue(photo.isReadyToUpload());
    }

}
