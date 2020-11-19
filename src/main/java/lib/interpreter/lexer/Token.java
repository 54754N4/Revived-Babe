package lib.interpreter.lexer;

public class Token<T extends Enum<T>> {
	public final T type;
	public final String value;
	
	public Token(T type, Object value) {
		this.type = type;
		this.value = value.toString();
	}
	
	public Token(T type) {
		this(type, type.toString());
	}
	
	@Override
	public String toString() {
		return String.format("(%s, %s)", type.name(), value);
	}
	
	public static <T extends Enum<T>> Token<T> of(T type, Object value) {
		return new Token<>(type, value);
	}
	
	public static <T extends Enum<T>> Token<T> of(T type) {
		return new Token<>(type);
	}
}