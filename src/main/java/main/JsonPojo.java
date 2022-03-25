package main;

import java.nio.file.Files;
import java.nio.file.Paths;

import json.interpreter.JsonGenerator;

public abstract class JsonPojo {
	public static void main(String[] args) throws Exception {
		String input = "{\r\n"
				+ "    \"abbreviation\": \"GMT\",\r\n"
				+ "    \"client_ip\": \"115.79.140.65\",\r\n"
				+ "    \"datetime\": \"2020-11-23T15:51:31.248095+00:00\",\r\n"
				+ "    \"day_of_week\": 1,\r\n"
				+ "    \"day_of_year\": 328,\r\n"
				+ "    \"dst\": false,\r\n"
				+ "    \"dst_from\": null,\r\n"
				+ "    \"dst_offset\": 0,\r\n"
				+ "    \"dst_until\": null,\r\n"
				+ "    \"raw_offset\": 0,\r\n"
				+ "    \"timezone\": \"Africa/Abidjan\",\r\n"
				+ "    \"unixtime\": 1606146691,\r\n"
				+ "    \"utc_datetime\": \"2020-11-23T15:51:31.248095+00:00\",\r\n"
				+ "    \"utc_offset\": \"+00:00\",\r\n"
				+ "    \"week_number\": 48\r\n"
				+ "}";
		
		// Test nested lists
//		String input = "{\r\n"
//				+ "  \"RawDocsCount\": 3555648,\r\n"
//				+ "  \"RawDocsSearchTime\": 14056,\r\n"
//				+ "  \"ReRankSearchTime\": 1182,\r\n"
//				+ "  \"CacheHit\": false,\r\n"
//				+ "  \"trial\": 1,\r\n"
//				+ "  \"limit\": 9,\r\n"
//				+ "  \"limit_ttl\": 60,\r\n"
//				+ "  \"quota\": 150,\r\n"
//				+ "  \"quota_ttl\": 86400,\r\n"
//				+ "  \"strings\": [\"str\"],\r\n"
//				+ "  \"lists\": [[\"\"]]\r\n"
//				+ "}";
		
		// Test nested objects
//		String input = "{\r\n"
//				+ "  \"time\": 23423.123,\r\n"
//				+ "  \"CacheHit\": false,\r\n"
//				+ "  \"trial\": 1,\r\n"
//				+ "  \"strings\": [\"str\"],\r\n"
//				+ "  \"location\": {\r\n"
//				+ "    \"address\": {\r\n"
//				+ "  	  \"ip\": \"192.175.3.1\",\r\n"
//				+ "  	  \"latitude\": 12443.35,\r\n"
//				+ "  	  \"longitude\": 123.423\r\n"
//				+ "    }\r\n"
//				+ "  }\r\n"
//				+ "}";
		
		// Test list of objects
//		String input = "{\r\n"
//				+ "  \"RawDocsCount\": 3555648,\r\n"
//				+ "  \"RawDocsSearchTime\": 14056,\r\n"
//				+ "  \"ReRankSearchTime\": 1182,\r\n"
//				+ "  \"CacheHit\": false,\r\n"
//				+ "  \"trial\": 1,\r\n"
//				+ "  \"limit\": 9,\r\n"
//				+ "  \"limit_ttl\": 60,\r\n"
//				+ "  \"quota\": 150,\r\n"
//				+ "  \"quota_ttl\": 86400,\r\n"
//				+ "  \"strings\": [\"str\"],\r\n"
//				+ "  \"docs\": [\r\n"
//				+ "    {\r\n"
//				+ "      \"from\": 663.17,\r\n"
//				+ "      \"to\": 665.42,\r\n"
//				+ "      \"anilist_id\": 98444,\r\n"
//				+ "      \"at\": 665.08,\r\n"
//				+ "      \"season\": \"2018-01\",\r\n"
//				+ "      \"anime\": \"搖曳露營\",\r\n"
//				+ "      \"filename\": \"[Ohys-Raws] Yuru Camp - 05 (AT-X 1280x720 x264 AAC).mp4\",\r\n"
//				+ "      \"episode\": 5,\r\n"
//				+ "      \"tokenthumb\": \"bB-8KQuoc6u-1SfzuVnDMw\",\r\n"
//				+ "      \"similarity\": 0.9563952960290518,\r\n"
//				+ "      \"title\": \"ゆるキャン△\",\r\n"
//				+ "      \"title_native\": \"ゆるキャン△\",\r\n"
//				+ "      \"title_chinese\": \"搖曳露營\",\r\n"
//				+ "      \"title_english\": \"Laid-Back Camp\",\r\n"
//				+ "      \"title_romaji\": \"Yuru Camp△\",\r\n"
//				+ "      \"mal_id\": 34798,\r\n"
//				+ "      \"synonyms\": [\"Yurucamp\", \"Yurukyan△\"],\r\n"
//				+ "      \"synonyms_chinese\": [{\"empty\":null}],\r\n"
//				+ "      \"is_adult\": false\r\n"
//				+ "    }\r\n"
//				+ "  ]\r\n"
//				+ "}";

		// Test Lexer
//		JsonLexer lexer = new JsonLexer(input);
//		Token<Type> token;
//		do {
//			token = lexer.getNextToken();
//			System.out.println(token);
//		} while (token.type != Type.EOF);
		
		// Test Parser + Printer
//		JsonParser parser = new JsonParser(lexer);
//		AST tree = parser.parse();
//		System.out.println(tree);
//		JsonPrintInterpreter interpreter = new JsonPrintInterpreter(input);
//		interpreter.interpret();
		
		// Debug input
//		System.out.println(input);
		
		// Measure compilation/interpretation
		long duration = System.currentTimeMillis();
		try {
			JsonGenerator generator = new JsonGenerator("TimeResult"
					+ "", input);
			Files.writeString(Paths.get("out.java"), generator.interpret().toString());
//			System.out.println(sb);
		} finally {
			System.out.println(
					String.format(
							"Executed for : %d ms", 
							System.currentTimeMillis() - duration));
		}
	}
}