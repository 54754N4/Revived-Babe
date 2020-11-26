package main;

import lib.interpreter.bash.BashLexer;
import lib.interpreter.bash.Type;
import lib.interpreter.lexer.Token;

public abstract class BashTest {
	public static void main(String[] args) throws Exception {
		String input = "if [ \"a\" = \"3\" ]\n"
				+ "then\n"
				+ "do\n"
				+ "\techo hello world\n"
				+ "fi";
		
		BashLexer lexer = new BashLexer(input);
		Token<Type> token;
		do {
			token = lexer.getNextToken();
			System.out.println(token);
		} while (token.type != Type.EOF);
		
//		JsonParser parser = new JsonParser(lexer);
//		AST tree = parser.parse();
//		System.out.println(tree);
//		JsonPrintInterpreter interpreter = new JsonPrintInterpreter(input);
//		interpreter.interpret();
		
//		System.out.println(input);
		
//		JsonGenerator generator = new JsonGenerator("ExchangeRate", input);
//		StringBuilder sb = generator.interpret();
//		System.out.println(sb);
	}
}


