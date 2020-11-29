package lib.interpreter.parser;

public class ParsingException extends Exception {
	private static final long serialVersionUID = 6880635872675668536L;
	public final int line, pos;
	public final char c;

	public ParsingException(int line, int pos, char c, String msg) {
		super(String.format("[line,col]=[%d,%d] Unexpected syntax at: %c%n%s", line, pos, c, msg));
		this.line = line;
		this.pos = pos;
		this.c = c;
	}
	
	public ParsingException(int line, int pos, char c) {
		this(line, pos, c, "");
	}
}