package nl.alexeyu.photomate.app;

import static nl.alexeyu.photomate.ui.UiConstants.PREVIEW_SIZE;
import static nl.alexeyu.photomate.ui.UiConstants.THUMBNAIL_SIZE;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

import nl.alexeyu.photomate.api.LocalPhotoApi;
import nl.alexeyu.photomate.api.PhotoFileCleaner;
import nl.alexeyu.photomate.api.archive.ArchivePhoto;
import nl.alexeyu.photomate.api.editable.EditablePhoto;
import nl.alexeyu.photomate.search.api.PhotoStockApi;
import nl.alexeyu.photomate.search.shutterstock.ShutterPhotoStockApi;
import nl.alexeyu.photomate.service.metadata.ExifPhotoMetadataProcessor;
import nl.alexeyu.photomate.service.metadata.PhotoMetadataProcessor;
import nl.alexeyu.photomate.service.metadata.PhotoMetadataReader;
import nl.alexeyu.photomate.thumbnail.ImageBufferedImageProvider;
import nl.alexeyu.photomate.thumbnail.ImgscalrThumbnailProvider;
import nl.alexeyu.photomate.thumbnail.ThumbnailProvider;
import nl.alexeyu.photomate.thumbnail.ThumbnailsProvider;
import nl.alexeyu.photomate.thumbnail.VideoBufferedImageProvider;
import nl.alexeyu.photomate.util.ConfigReader;
import nl.alexeyu.photomate.util.DefaultCmdExecutor;

public class AppModule extends AbstractModule {

    @Override
    protected void configure() {
        ExifPhotoMetadataProcessor metadataProcessor = new ExifPhotoMetadataProcessor(
                new DefaultCmdExecutor("exiftool"), new PhotoFileCleaner("_original"));
        bind(PhotoMetadataReader.class).toInstance(metadataProcessor);
        bind(PhotoMetadataProcessor.class).toInstance(metadataProcessor);
        bind(PhotoStockApi.class).to(ShutterPhotoStockApi.class);
        bind(ConfigReader.class).toInstance(ConfigReader.createDefault());

        ThumbnailProvider thumbnailProvider = new ImgscalrThumbnailProvider(THUMBNAIL_SIZE);
        ThumbnailProvider previewProvider = new ImgscalrThumbnailProvider(PREVIEW_SIZE);
        
        LocalPhotoApi<EditablePhoto> localPhotoApi = new LocalPhotoApi<>(metadataProcessor, 
    			new ThumbnailsProvider(new ImageBufferedImageProvider(), thumbnailProvider, previewProvider));
        bind(new TypeLiteral<LocalPhotoApi<EditablePhoto>>() {}).annotatedWith(Names.named("photoApi")).toInstance(localPhotoApi);

        LocalPhotoApi<EditablePhoto> localVideoApi = new LocalPhotoApi<>(metadataProcessor, 
    			new ThumbnailsProvider(new VideoBufferedImageProvider(), thumbnailProvider, previewProvider));
        bind(new TypeLiteral<LocalPhotoApi<EditablePhoto>>() {}).annotatedWith(Names.named("videoApi")).toInstance(localVideoApi);

        LocalPhotoApi<ArchivePhoto> archivePhotoApi = new LocalPhotoApi<>(metadataProcessor, 
    			new ThumbnailsProvider(new ImageBufferedImageProvider(), thumbnailProvider, previewProvider));
        bind(new TypeLiteral<LocalPhotoApi<ArchivePhoto>>() {}).annotatedWith(Names.named("archiveApi")).toInstance(archivePhotoApi);

        bind(EventBus.class).toInstance(new EventBus());
    }

}
