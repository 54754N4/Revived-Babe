package lib.interpreter.parser;


import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import lib.interpreter.lexer.BacktrackLexer;
import lib.interpreter.lexer.Token;

public abstract class BacktrackParser<Type extends Enum<Type>, Tree> implements Parser<Tree> {
	private BacktrackLexer<Type> lexer;
	private List<Token<Type>> tokens;
	private Stack<Integer> stack;	// to backtrack at different levels
	private int current;			// index in tokens list
	
	public BacktrackParser(BacktrackLexer<Type> lexer) throws ParsingException {
		this.lexer = lexer;
		current = 0;
		stack = new Stack<>();
		tokens = new ArrayList<>();
		Token<Type> token;
		Type eof = finalToken();
		while ((token = lexer.getNextToken()).type != eof)
			tokens.add(token);
		tokens.add(new Token<>(eof));
	}
	
	protected abstract Type finalToken();
	
	protected boolean is(Type[] types) {
		if (current >= tokens.size())
			return false;
		for (Type type : types)
			if (is(type))
				return true;
		return false;
	}
	
	protected void consume(Type type) throws ParsingException {
		if (is(type)) 
			current++;
		else 
			lexer.error("Expected type :"+type.name()+" "+type);
	}
	
	protected Token<Type> current() {
		return tokens.get(current);
	}
	
	protected void save() {
		stack.push(current);
	}
	
	protected void backtrack() {
		current = stack.pop();
	}
	
	protected boolean is(Type type) {
		return current().type == type;
	}
	
	public Tree error() throws ParsingException {
		lexer.error();
		return null;
	}
	
	public Tree error(String msg) throws ParsingException {
		lexer.error(msg);
		return null;
	}
}