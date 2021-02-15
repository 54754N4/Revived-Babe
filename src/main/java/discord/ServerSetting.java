package discord;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import lib.StringLib;

public class ServerSetting<T extends Enum<T>> {
	private final Supplier<T[]> retrieve;
	private final Consumer<T> set;
	private final Function<T, ? extends String> accessor; 
	
	public ServerSetting(Class<T> type, Consumer<T> set) {
		this.retrieve = () -> type.getEnumConstants();
		this.accessor = t -> t.toString();
		this.set = set;
	}
	
	public @Nullable T select(String match) {
		Optional<T> element = Arrays.stream(retrieve())
			.filter(t -> StringLib.matchSimplified(accessor.apply(t), match))	// use accessor to keep Stream<T>
			.findFirst();
		return element.isPresent() ? set(element.get()) : null;
	}
	
	public T set(T element) {
		set.accept(element);
		return element;
	}
	
	public T[] retrieve() {
		return retrieve.get();
	}
}