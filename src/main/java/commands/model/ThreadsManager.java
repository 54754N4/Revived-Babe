package commands.model;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public abstract class ThreadsManager {
	public static final int TOTAL_THREADS = 100;
	public static final ExecutorService POOL = Executors.newFixedThreadPool(TOTAL_THREADS, new ThreadNamingFactory());
	
	public static Thread newNativeThread(Runnable runnable) {
		return new Thread(runnable);
	}
	
	public static class ThreadNamingFactory implements ThreadFactory {
		private int counter = 0;
		
		@Override
		public Thread newThread(Runnable runnable) {
			Thread t = new Thread(runnable, String.format("%s-%d", runnable.getClass(), ++counter));
			t.setDaemon(true);
			return t;
		}
	}
}
