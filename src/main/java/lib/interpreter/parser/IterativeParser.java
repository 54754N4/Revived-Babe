package lib.interpreter.parser;

import lib.interpreter.lexer.Lexer;
import lib.interpreter.lexer.Token;

public abstract class IterativeParser<Type extends Enum<Type>, Tree> implements Parser<Tree> {
	protected Lexer<Type> lexer;
	protected Token<Type> current;
	
	public IterativeParser(Lexer<Type> lexer) throws ParsingException {
		this.lexer = lexer;
		current = lexer.getNextToken();
	}
	
	protected boolean is(Type type) {
		return current.type == type;
	}
	
	protected boolean isNot(Type type) {
		return !is(type);
	}
	
	
	protected boolean is(Type[] types) {
		for (Type type : types) if (is(type)) return true;
		return false;
	}
	
	protected void consume(Type type) throws ParsingException {
		if (current.type == type) current = lexer.getNextToken();
		else lexer.error(String.format("Expected token of type: (%s,%s) ", type.name(), type.toString()));
	}
	
	public Tree error() throws ParsingException {
		lexer.error();	// throws error with line and pos
		return null;
	}
	
	public Tree error(String msg) throws ParsingException {
		lexer.error(msg);
		return null;
	}
}
