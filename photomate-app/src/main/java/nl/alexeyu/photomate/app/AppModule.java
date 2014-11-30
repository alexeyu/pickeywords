package nl.alexeyu.photomate.app;

import static nl.alexeyu.photomate.ui.UiConstants.PREVIEW_SIZE;
import static nl.alexeyu.photomate.ui.UiConstants.THUMBNAIL_SIZE;
import nl.alexeyu.photomate.api.PhotoStockApi;
import nl.alexeyu.photomate.api.shutterstock.ShutterPhotoStockApi;
import nl.alexeyu.photomate.service.metadata.ExifPhotoMetadataProcessor;
import nl.alexeyu.photomate.service.metadata.PhotoMetadataProcessor;
import nl.alexeyu.photomate.service.metadata.PhotoMetadataReader;
import nl.alexeyu.photomate.service.thumbnail.ImgscalrThumbnailProvider;
import nl.alexeyu.photomate.service.thumbnail.ThumbnailProvider;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class AppModule extends AbstractModule {
	
	@Override
	protected void configure() {
		bind(PhotoMetadataReader.class).to(ExifPhotoMetadataProcessor.class);
	    bind(PhotoMetadataProcessor.class).to(ExifPhotoMetadataProcessor.class);
		bind(PhotoStockApi.class).to(ShutterPhotoStockApi.class);
		bind(ThumbnailProvider.class).annotatedWith(Names.named("thumbnail")).toInstance(
				new ImgscalrThumbnailProvider(THUMBNAIL_SIZE));
		bind(ThumbnailProvider.class).annotatedWith(Names.named("preview")).toInstance(
				new ImgscalrThumbnailProvider(PREVIEW_SIZE));
	}

}
