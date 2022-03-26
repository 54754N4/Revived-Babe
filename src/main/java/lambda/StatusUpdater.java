package lambda;

@FunctionalInterface
public interface StatusUpdater {
	void println(String message);
}