package lambda;

@FunctionalInterface
public interface IndexedConsumer<E> {
	void accept(int i, E item);
}