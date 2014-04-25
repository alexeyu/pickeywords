package nl.alexeyu.photomate.service;

import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({"unchecked", "rawtypes"})
public class PhotoExecutorService extends ThreadPoolExecutor {

	private static final Logger logger = LoggerFactory.getLogger("Executor");
	
	private static final int THREAD_COUNT = Runtime.getRuntime().availableProcessors();
	
	private static final Comparator<Object> COMPARATOR = new WeighedTaskComparator();
	
	public PhotoExecutorService() {
		super(THREAD_COUNT / 2, 
			  THREAD_COUNT,
			  0L, TimeUnit.MILLISECONDS,
			  new PriorityBlockingQueue(50, COMPARATOR),
			  new RejectedExecutionHandler() {
					@Override
					public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
						logger.error("Rejected: " + r);
					}
			  });
	}

	static class WeighedTaskComparator implements Comparator<Object> {
		
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
