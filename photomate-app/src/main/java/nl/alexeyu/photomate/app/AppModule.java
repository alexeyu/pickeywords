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
import nl.alexeyu.photomate.util.ConfigReader;
import nl.alexeyu.photomate.util.DefaultCmdExecutor;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class AppModule extends AbstractModule {
	
	@Override
	protected void configure() {
		ExifPhotoMetadataProcessor metadataProcessor = new ExifPhotoMetadataProcessor(new DefaultCmdExecutor("exiftool"));
		bind(PhotoMetadataReader.class).toInstance(metadataProcessor);
	    bind(PhotoMetadataProcessor.class).toInstance(metadataProcessor);
		bind(PhotoStockApi.class).to(ShutterPhotoStockApi.class);
		bind(ConfigReader.class).toInstance(ConfigReader.createDefault());

		bind(ThumbnailProvider.class).annotatedWith(Names.named("thumbnail")).toInstance(
				new ImgscalrThumbnailProvider(THUMBNAIL_SIZE));
		bind(ThumbnailProvider.class).annotatedWith(Names.named("preview")).toInstance(
				new ImgscalrThumbnailProvider(PREVIEW_SIZE));
	}

}
