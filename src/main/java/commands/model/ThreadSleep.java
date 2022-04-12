package commands.model;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.function.Supplier;

import commands.hierarchy.Command;

public class ThreadSleep {
	public static final long MAX_CHECK_RATE = 60000, MIN_CHECK_RATE = 200; // only between [200ms, 1min]
	
	public static long checkRateOf(long duration) {
		long checkRate = duration/10%MAX_CHECK_RATE;	// default duration = 10*checkRate
		if (checkRate < MIN_CHECK_RATE)
			checkRate = MIN_CHECK_RATE;
		return checkRate;
	}
	
	public static Callable<Void> nonBlocking(final long duration, Command command) {
		return nonBlocking(duration, command::isFinished);
	}
	
	public static Callable<Void> nonBlocking(final long duration, final Supplier<Boolean> abortCondition) {
		final long checkRate = checkRateOf(duration);
		return () -> {
			long start = System.currentTimeMillis();
			while (!abortCondition.get() && System.currentTimeMillis() - start < duration) 
				Thread.sleep(checkRate);
			return null;	// since return type is Void
		};
	}
	
	public static Callable<Void> blocking(final long duration) {
		return () -> {
			Thread.sleep(duration);
			return null;
		};
	}
	
	public static Callable<Void> blocking(final Supplier<Boolean> abortCondition) {
		return () -> {
			while (!abortCondition.get())
				Thread.sleep(MIN_CHECK_RATE);
			return null;
		};
	}
	
	// Convenience methods
	
	public static <T> Callable<Void> waitFor(final List<Future<T>> futures) {
		return ThreadSleep.blocking(() -> futures.stream().allMatch(Future::isDone));
	}
}
