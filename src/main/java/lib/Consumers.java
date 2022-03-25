package lib;

import java.util.function.Consumer;

public abstract class Consumers {
	public static final <T> Consumer<T> ignore() {
		return (a) -> {};
	}
	
	public static final <T> void ignore(T element) {
		return;
	}
}
