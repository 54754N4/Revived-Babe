package lib;

import java.util.function.Function;

@FunctionalInterface
public interface Pipeline<In, Out> {
	Out apply(In in);
	
	static <In, Out> Pipeline<In, Out> of(Function<In, Out> function) {
		return in -> function.apply(in);
	}
	
	default <T> Pipeline<In, T> then(Function<Out, T> function) {
		return in -> function.apply(apply(in));
	}
}
