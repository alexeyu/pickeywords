package nl.alexeyu.photomate.service;

import static nl.alexeyu.photomate.model.PhotoProperty.CAPTION;
import static nl.alexeyu.photomate.model.PhotoProperty.DESCRIPTION;
import static nl.alexeyu.photomate.model.PhotoProperty.KEYWORDS;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import nl.alexeyu.photomate.api.LocalPhoto;
import nl.alexeyu.photomate.api.LocalPhotoApi;
import nl.alexeyu.photomate.model.DefaultPhotoMetaDataBuilder;
import nl.alexeyu.photomate.model.PhotoProperty;
import nl.alexeyu.photomate.util.Configuration;

public class EditablePhotoManagerTest {

    private EditablePhotoManager photoManager;

    private LocalPhotoApi photoApi;

    private LocalPhotoApi videoApi;

    private Configuration configuration;

    @Before
    public void init() {
        photoApi = Mockito.mock(LocalPhotoApi.class);
        videoApi = Mockito.mock(LocalPhotoApi.class);
        configuration = Mockito.mock(Configuration.class);
        photoManager = new EditablePhotoManager(photoApi, videoApi, configuration);
    }

    @Test
    public void copiesKeywordsWithoutDuplication() {
        var keywordCaptor = ArgumentCaptor.forClass(Set.class);
        var sourceMetaData = new DefaultPhotoMetaDataBuilder().set(KEYWORDS, List.of("summer", "evening")).build();
        var targetMetaData = new DefaultPhotoMetaDataBuilder().set(KEYWORDS, List.of("landscape", "summer")).build();
        var source = new TestPhoto();
        source.setMetaData(sourceMetaData);
        var target = new TestPhoto();
        target.setMetaData(targetMetaData);
        doNothing().when(photoApi).updateProperty(eq(target), eq(KEYWORDS), keywordCaptor.capture());
        photoManager.copyMetadata(target, source);
        assertEquals(Set.of("summer", "evening", "landscape"), keywordCaptor.getValue());
    }

    @Test
    public void doesntCopyEmptyCaptionAndDescription() {
        var keywordCaptor = ArgumentCaptor.forClass(Set.class);
        var sourceMetaData = new DefaultPhotoMetaDataBuilder().build();
        var targetMetaData = new DefaultPhotoMetaDataBuilder().build();
        var source = new TestPhoto();
        source.setMetaData(sourceMetaData);
        var target = new TestPhoto();
        target.setMetaData(targetMetaData);
        photoManager.copyMetadata(target, source);
        verifyZeroInteractions(photoApi);
    }

    @Test
    public void copiesNonEmptyCaptionAndDescription() {
        var captionCaptor = ArgumentCaptor.forClass(String.class);
        var descriptionCaptor = ArgumentCaptor.forClass(String.class);
        var sourceMetaData = new DefaultPhotoMetaDataBuilder()
                .set(PhotoProperty.CAPTION, "caption")
                .set(PhotoProperty.DESCRIPTION, "description")
                .build();
        var targetMetaData = new DefaultPhotoMetaDataBuilder().build();
        var source = new TestPhoto();
        source.setMetaData(sourceMetaData);
        var target = new TestPhoto();
        target.setMetaData(targetMetaData);
        doNothing().when(photoApi).updateProperty(eq(target), eq(CAPTION), captionCaptor.capture());
        doNothing().when(photoApi).updateProperty(eq(target), eq(DESCRIPTION), descriptionCaptor.capture());
        photoManager.copyMetadata(target, source);
        assertEquals("caption", captionCaptor.getValue());
        assertEquals("description", descriptionCaptor.getValue());
    }

    private static class TestPhoto extends LocalPhoto {

        public TestPhoto() {
            super(Path.of(""));
        }

    }
}
