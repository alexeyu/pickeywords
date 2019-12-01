package nl.alexeyu.photomate.service.metadata;

import static nl.alexeyu.photomate.model.PhotoProperty.CAPTION;
import static nl.alexeyu.photomate.model.PhotoProperty.DESCRIPTION;
import static nl.alexeyu.photomate.model.PhotoProperty.KEYWORDS;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import nl.alexeyu.photomate.api.LocalPhoto;
import nl.alexeyu.photomate.api.LocalPhotoApi;
import nl.alexeyu.photomate.api.LocalPhotoUpdater;
import nl.alexeyu.photomate.api.editable.EditablePhoto;
import nl.alexeyu.photomate.model.DefaultPhotoMetaDataBuilder;
import nl.alexeyu.photomate.model.PhotoProperty;
import nl.alexeyu.photomate.service.EditablePhotoManager;
import nl.alexeyu.photomate.service.SelectedPhotosProvider;
import nl.alexeyu.photomate.util.Configuration;

public class PhotoMetadataReplicatorTest {

    private PhotoMetadataReplicator replicator;

    private LocalPhotoUpdater updater;

    private SelectedPhotosProvider selectedPhotosProvider;

    @Before
    public void init() {
        updater = Mockito.mock(LocalPhotoUpdater.class);
        selectedPhotosProvider = Mockito.mock(SelectedPhotosProvider.class);
    }

    @Test
    public void copiesKeywords() {
        replicator = new PhotoMetadataReplicator(selectedPhotosProvider, p -> true, updater);
        var keywordCaptor = ArgumentCaptor.forClass(Set.class);
        var sourceMetaData = new DefaultPhotoMetaDataBuilder().set(KEYWORDS, List.of("landscape", "summer")).build();
        var targetMetaData = new DefaultPhotoMetaDataBuilder().set(KEYWORDS, List.of()).build();
        var source = new TestPhoto();
        source.setMetaData(sourceMetaData);
        var target = new TestPhoto();
        target.setMetaData(targetMetaData);
        doNothing().when(updater).updateProperty(eq(target), eq(KEYWORDS), keywordCaptor.capture());
        replicator.copyMetadata(target, source);
        assertEquals(Set.of("landscape", "summer"), keywordCaptor.getValue());
    }


    @Test
    public void copiesKeywordsWithoutDuplication() {
        replicator = new PhotoMetadataReplicator(selectedPhotosProvider, p -> true, updater);
        var keywordCaptor = ArgumentCaptor.forClass(Set.class);
        var sourceMetaData = new DefaultPhotoMetaDataBuilder().set(KEYWORDS, List.of("summer", "evening")).build();
        var targetMetaData = new DefaultPhotoMetaDataBuilder().set(KEYWORDS, List.of("landscape", "summer")).build();
        var source = new TestPhoto();
        source.setMetaData(sourceMetaData);
        var target = new TestPhoto();
        target.setMetaData(targetMetaData);
        doNothing().when(updater).updateProperty(eq(target), eq(KEYWORDS), keywordCaptor.capture());
        replicator.copyMetadata(target, source);
        assertEquals(Set.of("summer", "evening", "landscape"), keywordCaptor.getValue());
    }

    @Test
    public void doesntCopyEmptyCaptionAndDescription() {
        replicator = new PhotoMetadataReplicator(selectedPhotosProvider, p -> true, updater);
        var sourceMetaData = new DefaultPhotoMetaDataBuilder().build();
        var targetMetaData = new DefaultPhotoMetaDataBuilder().build();
        var source = new TestPhoto();
        source.setMetaData(sourceMetaData);
        var target = new TestPhoto();
        target.setMetaData(targetMetaData);
        replicator.copyMetadata(target, source);
        verifyZeroInteractions(updater);
    }

    @Test
    public void copiesNonEmptyCaptionAndDescription() {
        replicator = new PhotoMetadataReplicator(selectedPhotosProvider, p -> true, updater);
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
        doNothing().when(updater).updateProperty(eq(target), eq(CAPTION), captionCaptor.capture());
        doNothing().when(updater).updateProperty(eq(target), eq(DESCRIPTION), descriptionCaptor.capture());
        replicator.copyMetadata(target, source);
        assertEquals("caption", captionCaptor.getValue());
        assertEquals("description", descriptionCaptor.getValue());
    }

    @Test
    public void doesntCopyWhenNoSelectedPhotos() {
        var confirmator = Mockito.mock(Function.class);
        replicator = new PhotoMetadataReplicator(selectedPhotosProvider, confirmator, updater);
        when(selectedPhotosProvider.getSelectedPhotos()).thenReturn(List.of());
        replicator.accept(new TestPhoto());
        verifyZeroInteractions(confirmator, updater);
    }

    @Test
    public void doesntCopyWhenNoConfirmation() {
        var photo = new EditablePhoto(Path.of("file"));
        var confirmator = Mockito.mock(Function.class);
        when(confirmator.apply(photo)).thenReturn(false);
        replicator = new PhotoMetadataReplicator(selectedPhotosProvider, confirmator, updater);
        when(selectedPhotosProvider.getSelectedPhotos()).thenReturn(List.of(photo));
        replicator.accept(photo);
        verify(confirmator).apply(photo);
        verifyZeroInteractions(updater);
    }

    @Test
    public void doesntCopyWhenSelectedPhotoIsTheSameAsSource() {
        var photo = new EditablePhoto(Path.of("file"));
        var confirmator = Mockito.mock(Function.class);
        when(confirmator.apply(photo)).thenReturn(true);
        replicator = new PhotoMetadataReplicator(selectedPhotosProvider, confirmator, updater);
        when(selectedPhotosProvider.getSelectedPhotos()).thenReturn(List.of(photo));
        doNothing().when(selectedPhotosProvider).clearSelectedPhotos();
        replicator.accept(photo);
        verify(confirmator).apply(photo);
        verify(selectedPhotosProvider).clearSelectedPhotos();
        verifyZeroInteractions(updater);
    }

    private static class TestPhoto extends LocalPhoto {

        public TestPhoto() {
            super(Path.of(""));
        }

    }
}
