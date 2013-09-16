package nl.alexeyu.photomate.app;

import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import nl.alexeyu.photomate.service.WeighedTask;
import nl.alexeyu.photomate.service.UploadPhotoListener;
import nl.alexeyu.photomate.service.keyword.ExifKeywordReader;
import nl.alexeyu.photomate.service.keyword.KeywordReader;
import nl.alexeyu.photomate.ui.UploadTable;
import nl.alexeyu.photomate.util.ConfigReader;

import com.google.inject.AbstractModule;

public class AppModule extends AbstractModule {
	
	@Override
	protected void configure() {
		bind(ExecutorService.class).to(PhotoExecutorService.class);
		bind(KeywordReader.class).to(ExifKeywordReader.class);
		bind(ConfigReader.class);
		bind(Main.class);
		bind(UploadPhotoListener.class).to(UploadTable.class);
	}

	private static class PhotoExecutorService extends ThreadPoolExecutor {
		
		private static final int THREAD_COUNT = Runtime.getRuntime().availableProcessors();
		
		private static final Comparator COMPARATOR = new PrioritizedClassComparator();
		
		public PhotoExecutorService() {
			super(THREAD_COUNT, THREAD_COUNT,
					0L, TimeUnit.MILLISECONDS,
					new PriorityBlockingQueue<Runnable>(10, COMPARATOR));
		}

	}

	public static class PrioritizedClassComparator implements Comparator<WeighedTask> {

		@Override
		public int compare(WeighedTask t1, WeighedTask t2) {
			return t2.getWeight().ordinal() - t1.getWeight().ordinal();
		}

	}

}
