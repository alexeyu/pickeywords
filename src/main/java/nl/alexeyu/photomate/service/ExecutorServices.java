package nl.alexeyu.photomate.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorServices {

	private static ExecutorService heavyTaskExecutor = Executors.newFixedThreadPool(
			Runtime.getRuntime().availableProcessors());

	private static ExecutorService lightTaskExecutor = Executors.newFixedThreadPool(
			Runtime.getRuntime().availableProcessors());
	
	public static ExecutorService getHeavyTasksExecutor() {
		return heavyTaskExecutor;
	}

	public static ExecutorService getLightTasksExecutor() {
		return lightTaskExecutor;
	}

}
