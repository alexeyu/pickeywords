package nl.alexeyu.photomate.app;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import nl.alexeyu.photomate.service.PhotoUploader;
import nl.alexeyu.photomate.service.keyword.ExifKeywordReader;
import nl.alexeyu.photomate.service.keyword.KeywordReader;
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
		return Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() / 2);
	}

	public @Bean ExecutorService lightTaskExecutor() {
		return Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
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
