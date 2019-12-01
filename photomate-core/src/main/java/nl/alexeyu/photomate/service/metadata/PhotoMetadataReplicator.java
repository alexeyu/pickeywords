package nl.alexeyu.photomate.service.metadata;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import com.google.common.base.Strings;

import nl.alexeyu.photomate.api.LocalPhoto;
import nl.alexeyu.photomate.api.LocalPhotoUpdater;
import nl.alexeyu.photomate.api.editable.EditablePhoto;
import nl.alexeyu.photomate.model.Photo;
import nl.alexeyu.photomate.model.PhotoProperty;
import nl.alexeyu.photomate.service.SelectedPhotosProvider;

public final class PhotoMetadataReplicator implements Consumer<Photo> {

    private final SelectedPhotosProvider selectedPhotosProvider;

    private final Function<Photo, Boolean> confirmator;

    private final LocalPhotoUpdater photoUpdater;

    public PhotoMetadataReplicator(SelectedPhotosProvider selectedPhotosProvider,
                                   Function<Photo, Boolean> confirmator,
                                   LocalPhotoUpdater photoUpdater) {
        this.selectedPhotosProvider = selectedPhotosProvider;
        this.confirmator = confirmator;
        this.photoUpdater = photoUpdater;
    }

    public void accept(Photo photo) {
        List<EditablePhoto> selectedPhotos = selectedPhotosProvider.getSelectedPhotos();
        if (!selectedPhotos.isEmpty()) {
            if (confirmator.apply(photo)) {
                selectedPhotos.stream()
                        .filter(target -> !target.equals(photo))
                        .forEach(target -> copyMetadata(target, photo));
            }
            selectedPhotosProvider.clearSelectedPhotos();
        }
    }

    void copyMetadata(LocalPhoto target, Photo source) {
        if (!source.metaData().keywords().isEmpty()) {
            var extendedKeywords = new LinkedHashSet<>(target.metaData().keywords());
            extendedKeywords.addAll(source.metaData().keywords());
            photoUpdater.updateProperty(target, PhotoProperty.KEYWORDS, extendedKeywords);
        }
        if (!Strings.isNullOrEmpty(source.metaData().caption())) {
            photoUpdater.updateProperty(target, PhotoProperty.CAPTION, source.metaData().caption());
        }
        if (!Strings.isNullOrEmpty(source.metaData().description())) {
            photoUpdater.updateProperty(target, PhotoProperty.DESCRIPTION, source.metaData().description());
        }
    }

}
