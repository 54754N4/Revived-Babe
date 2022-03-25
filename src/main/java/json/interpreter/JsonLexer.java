package json.interpreter;

import java.util.Arrays;

import lib.interpreter.lexer.Lexer;
import lib.interpreter.lexer.Token;
import lib.interpreter.parser.ParsingException;

/* Grammar: 
expression 	:= object
object 		:= OPEN_CURLY members CLOSE_CURLY
array		:= OPEN_BRACKET value [COMMA value]+ CLOSE_BRACKET
members		:= EPSILON | string COLON value [COMMA members]+
value		:= constant | number | double | string | array | object
string		:= QUOTE value QUOTE
double		:= number DOT digits
number		:= [-] digits
digits 		:= [0-9]+ 
constant	:= true | false | null
*/
public class JsonLexer extends Lexer<Type> {

	public JsonLexer(String text) {
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
		String num = result.toString();
		long l;
		return foundDot ? 
				Token.of(Type.DOUBLE, Double.parseDouble(num)) : 
				num.length() > 9 && (l = Long.parseLong(num)) > Integer.MAX_VALUE ?
					Token.of(Type.LONG, l) :
					Token.of(Type.NUMBER, Integer.parseInt(num));
	}
	
	private Token<Type> string() throws ParsingException {
		StringBuilder sb = new StringBuilder();
		char target = current;
		advance();	// consume opening single or double quote
		while (notFinished() && 
				(isNot(target) || (is(target) && isEscaped()))) {
			sb.append(current);
			advance();
		}
		if (isFinished())
			return error(String.format("Close your damn strings, unmatched open quote : %c", target));
		advance();	// consume closing single or double quote
		String result = sb.toString();
		return result.equals("") ? 
				Token.of(Type.EPSILON) : 
				Token.of(Type.STRING, result);
	}
	
	private Token<Type> constant() throws ParsingException {
		StringBuilder sb = new StringBuilder();
		while (isLetter()) {
			sb.append(current);
			advance();
		}
		String result = sb.toString();
		Type type = Type.asConstant(result);
		return type != null ?
				Token.of(type, result) : 
				error(String.format("Only valid constants are %s, you gave : %s", Arrays.toString(Type.CONSTANTS), result));
	}

	@Override
	public Token<Type> getNextToken() throws ParsingException {
		while (notFinished()) {
			if (isSpace()) skipWhiteSpace();
			else if (isTab()) skipTab();
			else if (isCRLF()) skipCRLF();
			else if (isLetter()) return constant();
			else if ((isSingleQuote() || isDoubleQuote()) && notEscaped()) return string();
			else if (isDigit())	return number();
			else switch (current) {
				case ':': advance(); return Token.of(Type.COLON);
				case ',': advance(); return Token.of(Type.COMMA);
				case '[': advance(); return Token.of(Type.OPEN_BRACKET);
				case ']': advance(); return Token.of(Type.CLOSE_BRACKET);
				case '{': advance(); return Token.of(Type.OPEN_CURLY);
				case '}': advance(); return Token.of(Type.CLOSE_CURLY);
				default: return error("Unexpted character : "+current);
			}
		} return Token.of(Type.EOF);
	}
}
