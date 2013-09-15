package nl.alexeyu.photomate.app;

import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import nl.alexeyu.photomate.service.PhotoUploader;
import nl.alexeyu.photomate.service.PrioritizedTask;
import nl.alexeyu.photomate.service.keyword.ExifKeywordReader;
import nl.alexeyu.photomate.ui.UploadTable;
import nl.alexeyu.photomate.util.ConfigReader;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

	public @Bean ConfigReader configReader() {
		return new ConfigReader(); 
	}
	
	public @Bean Main app() {
		return new Main(); 
	}

	public @Bean ExecutorService heavyTaskExecutor() {
		int threadCount = Runtime.getRuntime().availableProcessors();
		Comparator comparator = new PrioritizedTask.PrioritizedClassComparator();
        return new ThreadPoolExecutor(threadCount, threadCount,
                0L, TimeUnit.MILLISECONDS,
                new PriorityBlockingQueue<Runnable>(10, comparator));
	}

	public @Bean ExifKeywordReader keywordReader() {
		return new ExifKeywordReader();
	}
	
	public @Bean UploadTable uploadTable() {
		return new UploadTable();
	}

	public @Bean PhotoUploader photoUploader() {
		return new PhotoUploader();
	}

}
