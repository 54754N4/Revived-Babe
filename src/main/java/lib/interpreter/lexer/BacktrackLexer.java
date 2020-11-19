package lib.interpreter.lexer;

import java.util.Stack;

public abstract class BacktrackLexer<Type extends Enum<Type>> extends Lexer<Type> {
	private Stack<Integer> stack;
	
	public BacktrackLexer(String text) {
		super(text);
		stack = new Stack<>();
	}

	public void save() {
		stack.push(pos);
	}
	
	public void backtrack() {
		pos = stack.pop();
		current = text.charAt(pos);
	}
}