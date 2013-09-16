package nl.alexeyu.photomate.app;

import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import nl.alexeyu.photomate.service.UploadPhotoListener;
import nl.alexeyu.photomate.service.WeighedTask;
import nl.alexeyu.photomate.service.keyword.ExifKeywordReader;
import nl.alexeyu.photomate.service.keyword.KeywordReader;
import nl.alexeyu.photomate.ui.UploadTable;

import com.google.inject.AbstractModule;

public class AppModule extends AbstractModule {
	
	@Override
	protected void configure() {
		bind(ExecutorService.class).to(PhotoExecutorService.class);
		bind(KeywordReader.class).to(ExifKeywordReader.class);
		bind(UploadPhotoListener.class).to(UploadTable.class);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private static class PhotoExecutorService extends ThreadPoolExecutor {
		
		private static final int THREAD_COUNT = Runtime.getRuntime().availableProcessors();
		
		private static final Comparator COMPARATOR = new WeighedTaskComparator();
		
		public PhotoExecutorService() {
			super(THREAD_COUNT, THREAD_COUNT,
					0L, TimeUnit.MILLISECONDS,
					new PriorityBlockingQueue<Runnable>(10, COMPARATOR));
		}

	}

	public static class WeighedTaskComparator implements Comparator<WeighedTask> {

		@Override
		public int compare(WeighedTask t1, WeighedTask t2) {
			return t2.getWeight().ordinal() - t1.getWeight().ordinal();
		}

	}

}
