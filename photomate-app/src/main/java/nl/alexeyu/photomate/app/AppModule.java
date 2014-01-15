package nl.alexeyu.photomate.app;

import static nl.alexeyu.photomate.ui.UiConstants.PREVIEW_SIZE;
import static nl.alexeyu.photomate.ui.UiConstants.THUMBNAIL_SIZE;

import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;

public class AppModule extends AbstractModule {
	
	@Override
	protected void configure() {
		bind(ExecutorService.class).to(PhotoExecutorService.class);
		bind(PhotoMetadataReader.class).to(ExifPhotoMetadataProcessor.class);
	    bind(PhotoMetadataProcessor.class).to(ExifPhotoMetadataProcessor.class);
		bind(PhotoStockApi.class).to(ShutterPhotoStockApi.class);
		bind(ThumbnailProvider.class).toInstance(new ImgscalrThumbnailProvider(THUMBNAIL_SIZE, PREVIEW_SIZE));
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private static class PhotoExecutorService extends ThreadPoolExecutor {
		
		private static final Logger logger = LoggerFactory.getLogger("Executor");
		
		private static final int THREAD_COUNT = 4;
		
		private static final Comparator COMPARATOR = new WeighedTaskComparator();
		
		public PhotoExecutorService() {
			super(THREAD_COUNT, THREAD_COUNT,
					0L, TimeUnit.MILLISECONDS,
					new PriorityBlockingQueue(50, COMPARATOR),
					new RejectedExecutionHandler() {
						
						@Override
						public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
							logger.error("Rejected: " + r);
						}
					});
		}

	}

	public static class WeighedTaskComparator implements Comparator<Object> {
		
		private static final Logger logger = LoggerFactory.getLogger("Comparator");

		@Override
		public int compare(Object t1, Object t2) {
			int result;
		    if (t1 instanceof PrioritizedTask) {
		        if (t2 instanceof PrioritizedTask) {
		            PrioritizedTask p1 = (PrioritizedTask) t1;
		            PrioritizedTask p2 = (PrioritizedTask) t2;
		            result = p2.getPriority().ordinal() - p1.getPriority().ordinal();
		        } else {
		        	result = 1;		        	
		        }
		    } else {
		        result = -1;
		    }
		    logger.debug(t1 + (result > 0 ? ">" : result < 0 ? "<" : "=") + t2);
		    return result;
		}

	}

}
