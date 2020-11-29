package json.interpreter;

public enum Type {
	TRUE, FALSE, NULL,
	NUMBER, LONG, DOUBLE, STRING, 
	
	COLON(":"), COMMA(","), 
	OPEN_BRACKET("["), CLOSE_BRACKET("]"),
	OPEN_CURLY("{"), CLOSE_CURLY("}"),
	
	EPSILON, EOF;
	
	private final String string;
	
	public static final Type[] CONSTANTS = new Type[] { TRUE, FALSE, NULL };
	
	private Type(String string) {
		this.string = string;
	}
	
	private Type() {
		string = name();
	}
	
	@Override
	public String toString() {
		return string;
	}
	
	public static boolean isConstant(Type type) {
		for (Type t : CONSTANTS)
			if (t == type)
				return true;
		return false;
	} 
	
	public static Type asConstant(String text) {
		for (Type t : CONSTANTS)
			if (t.toString().equals(text.toUpperCase()))
				return t;
		return null;
	}
}
