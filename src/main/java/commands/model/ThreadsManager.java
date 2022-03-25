package commands.model;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

public abstract class ThreadsManager {
	public static final int TOTAL_THREADS = 100, SUCCESS = 0;
	public static final String WINDOWS_NATIVE = "cmd.exe /C", 
			LINUX_NATIVE = "/bin/bash -c";
	public static final ScheduledExecutorService POOL = Executors.newScheduledThreadPool(TOTAL_THREADS, new ThreadNamingFactory());
	
	public static Thread newNativeThread(Runnable runnable) {
		return new Thread(runnable);
	}

	public static Process execute(String...args) throws IOException {
		return new ProcessBuilder()
				.command(wrapNativeInterpreter(args))
				.start();
	}
	
	public static ThreadOutput read(Process process) throws InterruptedException, ExecutionException {
		return new ThreadOutput(process);
	}
	
	public static void kill(boolean now) {
		if (POOL.isShutdown())
			return;
		else if (now)
			POOL.shutdownNow();
		else 
			POOL.shutdown();
	}
	
	/* Convenience methods */
	
	public static String getNativeInterpreter() {
		return (File.separatorChar == '\\') ? WINDOWS_NATIVE : LINUX_NATIVE;	// cause '\' = windows
	}
	
	public static String[] wrapNativeInterpreter(String... cmd) {
		String[] interpreterList = getNativeInterpreter().split(" ");
		String[] c = new String[interpreterList.length + cmd.length];
		System.arraycopy(interpreterList, 0, c, 0, interpreterList.length);
		System.arraycopy(cmd, 0, c, interpreterList.length, cmd.length);
		return c;
	}
	
	/* Nested classes */
	
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
