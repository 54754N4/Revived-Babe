package lib.interpreter.parser;

public interface Parser<T> {
	public T parse() throws ParsingException;
}