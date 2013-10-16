package nl.alexeyu.photomate.app;

import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import nl.alexeyu.photomate.api.PhotoStockApi;
import nl.alexeyu.photomate.api.ShutterPhotoStockApi;
import nl.alexeyu.photomate.service.ThumbnailProvider;
import nl.alexeyu.photomate.service.UploadPhotoListener;
import nl.alexeyu.photomate.service.WeighedTask;
import nl.alexeyu.photomate.service.keyword.ExifKeywordProcessor;
import nl.alexeyu.photomate.service.keyword.KeywordProcessor;
import nl.alexeyu.photomate.service.keyword.KeywordReader;
import nl.alexeyu.photomate.service.thumbnail.ImgscalrThumbnailProvider;
import nl.alexeyu.photomate.ui.UploadTable;

import com.google.inject.AbstractModule;

public class AppModule extends AbstractModule {
	
	@Override
	protected void configure() {
		bind(ExecutorService.class).to(PhotoExecutorService.class);
		bind(KeywordReader.class).to(ExifKeywordProcessor.class);
	    bind(KeywordProcessor.class).to(ExifKeywordProcessor.class);
		bind(UploadPhotoListener.class).to(UploadTable.class);
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
		    if (t1 instanceof WeighedTask) {
		        if (t2 instanceof WeighedTask) {
		            return - ((WeighedTask) t2).getWeight().ordinal() + ((WeighedTask) t1).getWeight().ordinal();
		        } else {
		            return -1;
		        }
		    } else {
		        return 1;
		    }
		}

	}

}
