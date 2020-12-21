package lib;

@FunctionalInterface
public interface ComposeableRunnable extends Runnable {
	default <T extends Runnable> ComposeableRunnable compose(T next) {
		return () -> { run(); next.run(); };
	}
}
