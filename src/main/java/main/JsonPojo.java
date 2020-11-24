package main;

import json.interpreter.JsonGenerator;

public abstract class JsonPojo {
	public static void main(String[] args) throws Exception {
		String input = "{\r\n"
				+ "  \"base\": \"EUR\",\r\n"
				+ "  \"date\": \"2020-11-16\",\r\n"
				+ "  \"val\": null,\r\n"
				+ "  \"vali\": 1,\r\n"
				+ "  \"vald\": 1.0,\r\n"
				+ "  \"valb\": false,\r\n"
				+ "  \"arr\": [{\"el\":\"value\"}, 1, false, true, null],\r\n"
				+ "  \"rates\": {\r\n"
				+ "    \"GBP\": 0.89819,\r\n"
				+ "    \"HKD\": 9.1725\r\n"
				+ "  }\r\n"
				+ "}";
		
//		String input = "{\r\n"
//				+ "    \"abbreviation\": \"GMT\",\r\n"
//				+ "    \"client_ip\": \"115.79.140.65\",\r\n"
//				+ "    \"datetime\": \"2020-11-23T15:51:31.248095+00:00\",\r\n"
//				+ "    \"day_of_week\": 1,\r\n"
//				+ "    \"day_of_year\": 328,\r\n"
//				+ "    \"dst\": false,\r\n"
//				+ "    \"dst_from\": null,\r\n"
//				+ "    \"dst_offset\": 0,\r\n"
//				+ "    \"dst_until\": null,\r\n"
//				+ "    \"raw_offset\": 0,\r\n"
//				+ "    \"timezone\": \"Africa/Abidjan\",\r\n"
//				+ "    \"unixtime\": 1606146691,\r\n"
//				+ "    \"utc_datetime\": \"2020-11-23T15:51:31.248095+00:00\",\r\n"
//				+ "    \"utc_offset\": \"+00:00\",\r\n"
//				+ "    \"week_number\": 48\r\n"
//				+ "}";

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
		
//		System.out.println(input);
		
		JsonGenerator generator = new JsonGenerator("ExchangeRate", input);
		StringBuilder sb = generator.interpret();
		System.out.println(sb);
	}
}


