package lambda;

@FunctionalInterface
public interface ThrowableRunnable {
	void run() throws Exception;
}