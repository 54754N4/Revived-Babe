package lambda;

@FunctionalInterface
public interface ThrowableSupplier<T> {
	T get() throws Exception;
}
