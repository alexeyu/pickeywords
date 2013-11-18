package nl.alexeyu.photomate.app;

import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import nl.alexeyu.photomate.api.PhotoStockApi;
import nl.alexeyu.photomate.api.shutterstock.ShutterPhotoStockApi;
import nl.alexeyu.photomate.service.PrioritizedTask;
import nl.alexeyu.photomate.service.metadata.ExifPhotoMetadataProcessor;
import nl.alexeyu.photomate.service.metadata.PhotoMetadataProcessor;
import nl.alexeyu.photomate.service.metadata.PhotoMetadataReader;
import nl.alexeyu.photomate.service.thumbnail.ImgscalrThumbnailProvider;
import nl.alexeyu.photomate.service.thumbnail.ThumbnailProvider;

import com.google.inject.AbstractModule;

public class AppModule extends AbstractModule {
	
	@Override
	protected void configure() {
		bind(ExecutorService.class).to(PhotoExecutorService.class);
		bind(PhotoMetadataReader.class).to(ExifPhotoMetadataProcessor.class);
	    bind(PhotoMetadataProcessor.class).to(ExifPhotoMetadataProcessor.class);
		bind(PhotoStockApi.class).to(ShutterPhotoStockApi.class);
		bind(ThumbnailProvider.class).to(ImgscalrThumbnailProvider.class);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private static class PhotoExecutorService extends ThreadPoolExecutor {
		
		private static final int THREAD_COUNT = Runtime.getRuntime().availableProcessors();
		
		private static final Comparator COMPARATOR = new WeighedTaskComparator();
		
		public PhotoExecutorService() {
			super(THREAD_COUNT, THREAD_COUNT,
					0L, TimeUnit.MILLISECONDS,
					new PriorityBlockingQueue(50, COMPARATOR));
		}

	}

	public static class WeighedTaskComparator implements Comparator<Object> {

		@Override
		public int compare(Object t1, Object t2) {
		    if (t1 instanceof PrioritizedTask) {
		        if (t2 instanceof PrioritizedTask) {
		            PrioritizedTask p1 = (PrioritizedTask) t1;
		            PrioritizedTask p2 = (PrioritizedTask) t2;
		            return p1.getPriority().ordinal() - p2.getPriority().ordinal();
		        } else {
		            return -1;
		        }
		    } else {
		        return 1;
		    }
		}

	}

}
