package lib.interpreter.parser;


import java.util.ArrayList;
import java.util.List;

import lib.interpreter.lexer.Lexer;
import lib.interpreter.lexer.Token;

public abstract class LookaheadParser<Type extends Enum<Type>, Tree> extends IterativeParser<Type, Tree> {
	private List<Token<Type>> deque;
	
	public LookaheadParser(Lexer<Type> lexer, int lookahead) throws ParsingException {
		super(lexer);
		this.deque = new ArrayList<>();
		deque.add(current);	// retrieved by super.contstructor
		while (lookahead-->0) deque.add(lexer.getNextToken());	// add as much as lookahead requires
	}

	protected Type peek(int i) { // 'i' cannot be bigger than lookahead, throws IndexOutOfBoundsException
		return deque.get(i).type;
	}
	
	@Override
	protected void consume(Type type) throws ParsingException {
		super.consume(popFirst().type);	// verify correct type to consume
		// Fix current deque mapping
		deque.add(current);
		current = peekFirst();
	}
	
	private Token<Type> peekFirst() {
		return deque.get(0);
	}

	private Token<Type> popFirst() {
		return deque.remove(0);
	}
}