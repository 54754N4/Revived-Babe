package json.interpreter;

import json.interpreter.AST.JsonArray;
import json.interpreter.AST.JsonObject;
import json.interpreter.AST.Member;
import lib.interpreter.lexer.Token;
import lib.interpreter.parser.IterativeParser;
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
public class JsonParser extends IterativeParser<Type, AST> {
	
	public JsonParser(JsonLexer lexer) throws ParsingException {
		super(lexer);
	}

	@Override
	public AST parse() throws ParsingException {
		return object();
	}

	// object 		:= OPEN_CURLY members CLOSE_CURLY
	private AST object() throws ParsingException {
		JsonObject ast = new JsonObject();
		consume(Type.OPEN_CURLY);
		ast = members(ast);
		consume(Type.CLOSE_CURLY);
		return ast;
	}
	
	// members		:= EPSILON | string COLON value [COMMA members]+
	private JsonObject members(JsonObject json) throws ParsingException {
		if (is(Type.EPSILON)) {
			consume(Type.EPSILON);
			return json;
		}
		Token<Type> name = current;
		consume(Type.STRING);
		consume(Type.COLON);
		Object value = value();
		json.add(new Member(name.value, value));
		while (is(Type.COMMA)) {
			consume(Type.COMMA);
			members(json);
		}
		return json;
	}

	// value		:= constant | number | double | string | array | objects
	private Object value() throws ParsingException {
		Token<Type> curr = current;
		if (Type.isConstant(current.type)) {
			consume(current.type);
			switch (curr.type) {
				case FALSE: return false;
				case NULL: return null;
				case TRUE: return true;
				default:
					return error(String.format("Invalid constant : %s", current.toString()));
			}
		} else if (is(Type.NUMBER)) {
			consume(Type.NUMBER);
			return Integer.parseInt(curr.value);
		} else if (is(Type.DOUBLE)) {
			consume(Type.DOUBLE);
			return Double.parseDouble(curr.value);
		} else if (is(Type.STRING)) {	
			consume(Type.STRING);
			return curr.value;
		} else if (is(Type.EPSILON)) {
			consume(Type.EPSILON);
			return curr.value;
		} else if (is(Type.OPEN_BRACKET)) return array(new JsonArray());
		else if (is(Type.CLOSE_BRACKET)) return "";	// By default an empty array is made of strings 
		else if (is(Type.OPEN_CURLY)) return object();
		return error(String.format("Invalid type value : %s", current.toString()));
	}

	// array		:= OPEN_BRACKET value [COMMA value]+ CLOSE_BRACKET
	private Object array(JsonArray jsonArray) throws ParsingException {
		consume(Type.OPEN_BRACKET);
		jsonArray.list.add(value());
		while (is(Type.COMMA) && isNot(Type.CLOSE_BRACKET)) {
			consume(Type.COMMA);
			jsonArray.list.add(value());
		}
		consume(Type.CLOSE_BRACKET);
		return jsonArray;
	}
}