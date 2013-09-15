package nl.alexeyu.photomate.service;

import java.util.Comparator;

public interface PrioritizedTask extends Runnable {

	int getPriority();

	public static class PrioritizedClassComparator implements Comparator<PrioritizedTask> {

		@Override
		public int compare(PrioritizedTask t1, PrioritizedTask t2) {
			return t1.getPriority() - t2.getPriority();
		}

	}
}
