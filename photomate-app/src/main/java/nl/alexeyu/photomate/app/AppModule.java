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
import nl.alexeyu.photomate.service.metadata.ChangedPhotoKeywordsProvider;
import nl.alexeyu.photomate.service.metadata.ChangedVideoKeywordsProvider;
import nl.alexeyu.photomate.service.metadata.ExifMetadataProcessor;
import nl.alexeyu.photomate.service.metadata.PhotoMetadataProcessor;
import nl.alexeyu.photomate.service.metadata.PhotoMetadataReader;
import nl.alexeyu.photomate.thumbnail.CachingThumbnailsProvider;
import nl.alexeyu.photomate.thumbnail.FileThumbnailsProvider;
import nl.alexeyu.photomate.thumbnail.ImgscalrThumbnailProvider;
import nl.alexeyu.photomate.thumbnail.PhotoBufferedImageProvider;
import nl.alexeyu.photomate.thumbnail.ScalingThumbnailsProvider;
import nl.alexeyu.photomate.thumbnail.ThumbnailsProvider;
import nl.alexeyu.photomate.thumbnail.VideoBufferedImageProvider;
import nl.alexeyu.photomate.util.Configuration;
import nl.alexeyu.photomate.util.DefaultCmdExecutor;

public class AppModule extends AbstractModule {

    @Override
    protected void configure() {
        var exifExecutor = new DefaultCmdExecutor("exiftool");
        var tmpFileCleaner = new PhotoFileCleaner("_original");
        var photoMetadataProcessor = new ExifMetadataProcessor(exifExecutor,
                new ChangedPhotoKeywordsProvider(), tmpFileCleaner);
        bind(PhotoMetadataReader.class).toInstance(photoMetadataProcessor);
        bind(PhotoMetadataProcessor.class).toInstance(photoMetadataProcessor);
        bind(PhotoStockApi.class).to(ShutterPhotoStockApi.class);
        bind(Configuration.class).toInstance(Configuration.createDefault());

        var thumbnailProvider = new ImgscalrThumbnailProvider(THUMBNAIL_SIZE);
        var previewProvider = new ImgscalrThumbnailProvider(PREVIEW_SIZE);

        var photoBufferedImageProvider = new PhotoBufferedImageProvider();
        LocalPhotoApi<EditablePhoto> localPhotoApi = new LocalPhotoApi<>(photoMetadataProcessor, 
                new ScalingThumbnailsProvider(photoBufferedImageProvider, thumbnailProvider, previewProvider));
        bind(new TypeLiteral<LocalPhotoApi<EditablePhoto>>() {}).annotatedWith(Names.named("photoApi"))
            .toInstance(localPhotoApi);

        var videoMetadataProcessor = new ExifMetadataProcessor(
                exifExecutor, new ChangedVideoKeywordsProvider(), tmpFileCleaner);
        LocalPhotoApi<EditablePhoto> localVideoApi = new LocalPhotoApi<>(videoMetadataProcessor, 
                new ScalingThumbnailsProvider(new VideoBufferedImageProvider(), thumbnailProvider, previewProvider));
        bind(new TypeLiteral<LocalPhotoApi<EditablePhoto>>() {}).annotatedWith(Names.named("videoApi"))
            .toInstance(localVideoApi);

        ThumbnailsProvider archiveThumbnailsProvider = new CachingThumbnailsProvider(
                new FileThumbnailsProvider(), 
                new ScalingThumbnailsProvider(photoBufferedImageProvider, thumbnailProvider));
        LocalPhotoApi<ArchivePhoto> archivePhotoApi = new LocalPhotoApi<>(photoMetadataProcessor,  archiveThumbnailsProvider);
        bind(new TypeLiteral<LocalPhotoApi<ArchivePhoto>>() {}).annotatedWith(Names.named("archiveApi"))
            .toInstance(archivePhotoApi);

        bind(EventBus.class).toInstance(new EventBus());
    }

}
