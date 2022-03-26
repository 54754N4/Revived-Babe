package lambda;

@FunctionalInterface
public interface Executable {
	void invoke() throws Exception;
}