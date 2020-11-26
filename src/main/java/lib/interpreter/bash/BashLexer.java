package lib.interpreter.bash;

import lib.interpreter.lexer.Lexer;
import lib.interpreter.lexer.Token;
import lib.interpreter.parser.ParsingException;

public class BashLexer extends Lexer<Type> {

	public BashLexer(String text) {
		super(text);
	}
	
	private Token<Type> number() throws ParsingException {
		StringBuilder result = new StringBuilder();
		boolean foundDot = false;
		while ((isDigit() || isDot()) && notFinished()) {
			if (isDot() && foundDot)
				error("Wtf is this; why is there 2 dots in your number lol.");
			else if (isDot())
				foundDot = true;
			result.append(current);
			advance();
		}
		return Token.of(Type.NUMBER, Integer.parseInt(result.toString()));
	}
	
	private Token<Type> word() throws ParsingException {
		StringBuilder sb = new StringBuilder();
		while (isLetter()) {
			sb.append(current);
			advance();
		}
		String result = sb.toString();
		Type type = Type.from(result);
		if (type == null) type = Type.WORD;
		return Token.of(type, result);
	}
	
	private Token<Type> string() throws ParsingException {
		StringBuilder sb = new StringBuilder();
		char target = current;
		advance();	// consume opening quote
		while (isNot(target) && notFinished()) {
			sb.append(current);
			advance();
		}
		if (isFinished())
			return error(String.format("Close your damn strings, unmatched open quote : %c", target));
		advance();	// consume closing quote
		String result = sb.toString();
		return result.equals("") ? 
				Token.of(Type.EPSILON) : 
				Token.of(Type.WORD, result);
	}
	
	@Override
	public Token<Type> getNextToken() throws ParsingException {
		while (notFinished()) {
			if (isSpace()) skipWhiteSpace();
			else if (isDigit()) return number();
			else if (isLetter()) return word();
			else if (isSingleQuote() || isDoubleQuote()) return string();	// TODO
			else switch (current) {
				case '\n': advance(); return Token.of(Type.NEWLINE);
				case '=': advance(); return Token.of(Type.EQUALS);
				case '{': advance(); return Token.of(Type.CURLY_START);
				case '}': advance(); return Token.of(Type.CURLY_END);
				case '-': advance(); return Token.of(Type.HYPHEN);
				case '(': 
					advance(); 
					if (is('(')) {
						advance();
						return Token.of(Type.ARITH_START);
					}
					return Token.of(Type.PAREN_START);
				case ')': 
					advance(); 
					if (is(')')) {
						advance();
						return Token.of(Type.ARITH_END);
					}
					return Token.of(Type.PAREN_END);
				case '|': 
					advance();
					if (is('|')) {
						advance();
						return Token.of(Type.OR_OR);
					}
					return Token.of(Type.PIPE);
				case '[': 
					advance();
					if (is('[')) {
						advance();
						return Token.of(Type.COND_START);
					}
					return Token.of(Type.TEST_START);
				case ']': 
					advance();
					if (is(']')) {
						advance();
						return Token.of(Type.COND_END);
					}
					return Token.of(Type.TEST_END);
				case '>':
					advance();
					if (is('>')) {
						advance();
						return Token.of(Type.GREATER_GREATER);
					}
					return Token.of(Type.GREATER);
				case ';':
					advance();
					if (is(';')) {
						advance();
						if (is('&')) {
							advance();
							return Token.of(Type.SEMI_SEMI_AND);
						}
						return Token.of(Type.SEMI_SEMI);
					}
					if (is('&')) {
						advance();
						return Token.of(Type.SEMI_AND);
					}
					return Token.of(Type.SEMICOLON);
				case '&':
					advance();
					if (is('>')) {
						advance();
						if (is('>')) {
							advance();
							return Token.of(Type.AND_GREATER_GREATER);
						}
						return Token.of(Type.AND_GREATER);
					}
					if (is('&')) {
						advance();
						return Token.of(Type.AND_AND);
					}
					return Token.of(Type.AND);
				case '<':
					advance();
					if (is('<')) {
						advance();
						if (is('<')) {
							advance();
							return Token.of(Type.LESS_LESS_LESS);
						} 
						if (is('-')) {
							advance();
							return Token.of(Type.LESS_LESS_MINUS);
						}
						return Token.of(Type.LESS_LESS);
					}
					if (is('&')) {
						advance();
						return Token.of(Type.LESS_AND);
					}
					if (is('>')) {
						advance();
						return Token.of(Type.LESS_GREATER);
					}
					return Token.of(Type.LESS);
				default: error();
			}
		}
		return Token.of(Type.EOF);
	}

}
