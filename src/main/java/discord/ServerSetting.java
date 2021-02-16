package discord;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import lib.StringLib;
import net.dv8tion.jda.api.managers.GuildManager;

public class ServerSetting<T extends Enum<T>>  extends ServerRestAction {
	private Predicate<T> excluded;
	private final Supplier<T[]> retrieve;
	private final Consumer<T> set;
	private final Function<T, ? extends String> accessor; 
	
	public ServerSetting(Class<T> type, Consumer<T> set, GuildManager manager) {
		this(type, set, manager, t -> true);
	}
	
	public ServerSetting(Class<T> type, Consumer<T> set, GuildManager manager, Predicate<T> excluded) {
		super(manager);
		this.retrieve = () -> type.getEnumConstants();
		this.accessor = t -> t.toString();
		this.set = set;
		this.excluded = excluded;
	}
	
	public @Nullable ServerSetting<T> select(String match) {
		List<T> elements = retrieve();
		if (StringLib.isInteger(match)) 
			set(elements.get(Integer.parseInt(match)));
		else {
			Optional<T> element = elements.stream()
					.filter(t -> StringLib.matchSimplified(accessor.apply(t), match))
					.findFirst();
			if (element.isPresent()) 
				set(element.get());
		}
		return this;
	}
	
	public T set(T element) {
		set.accept(element);
		return element;
	}
	
	public List<T> retrieve() {
		return Arrays.stream(retrieve.get())
				.filter(excluded.negate())
				.collect(Collectors.toList());
	}
}