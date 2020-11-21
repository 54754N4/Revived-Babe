package json.interpreter;

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

public abstract class JsonPojo {
	public static Class<?> from(String input) {
		return null;
	}
	
	public static void main(String[] args) throws Exception {
		String input = "{\r\n"
				+ "  \"base\": \"EUR\",\r\n"
				+ "  \"date\": \"2020-11-16\",\r\n"
				+ "  \"val\": null,\r\n"
				+ "  \"vali\": 1,\r\n"
				+ "  \"vald\": 1.0,\r\n"
				+ "  \"valb\": false\r\n,"
				+ "  \"arr\": [1, true, false, null, {\"el\":\"value\"}],\r\n"
				+ "  \"rates\": {\r\n"
				+ "    \"GBP\": 0.89819,\r\n"
				+ "    \"HKD\": 9.1725\r\n"
				+ "  }\r\n"
				+ "}";
//		JsonLexer lexer = new JsonLexer(input);
//		Token<Type> token;
//		do {
//			token = lexer.getNextToken();
//			System.out.println(token);
//		} while (token.type != Type.EOF);
		
//		JsonParser parser = new JsonParser(lexer);
//		AST tree = parser.parse();
//		System.out.println(tree);
//		JsonPrintInterpreter interpreter = new JsonPrintInterpreter(input);
//		interpreter.interpret();
		String name = "ExchangeRate"; 
		JsonGenerator generator = new JsonGenerator(name, input);
		generator.interpret();
	}
}
