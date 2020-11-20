package json.interpreter;

import java.util.Map.Entry;

import json.interpreter.AST.JsonArray;
import json.interpreter.AST.JsonObject;
import lib.interpreter.parser.ParsingException;

public class JsonPrintInterpreter implements Visitor {
	private JsonParser parser;
	private AST ast;
	private int depth = 0;
	
	public JsonPrintInterpreter(String input) throws ParsingException {
		parser = new JsonParser(new JsonLexer(input));
		ast = parser.parse();
	}
	
	public void interpret() throws Exception {
		visit(ast);
	}
	
	@Override
	public void visit(JsonObject object) {
		String key;
		Object value;
		System.out.print(multiply("\t", depth)+"{\n");
		String indent = multiply("\t", ++depth), end = ",\n";
		for (Entry<String, Object> entry : object.dict.entrySet()) {
			key = entry.getKey();
			value = entry.getValue();
			if (value == null) {
				System.out.print(indent+key+": "+"(null) "+value+end);
			} else if (Boolean.class.isInstance(value)) {
				System.out.print(indent+key+": "+"(Bool) "+value+end);
			} else if (Integer.class.isInstance(value)) {
				System.out.print(indent+key+": "+"(Integer) "+value+end);
			} else if (Double.class.isInstance(value)) {
				System.out.print(indent+key+": "+"(Double) "+value+end);
			} else if (String.class.isInstance(value)) {
				System.out.print(indent+key+": "+"(String) "+value+end);
			} else if (JsonArray.class.isInstance(value)) {
				System.out.print(indent+key+": "+"(Array) \n");
				visit(JsonArray.class.cast(value));
				System.out.print(end);
			} else if (JsonObject.class.isInstance(value)) {
				System.out.print(indent+key+": "+"(Object) \n");
				depth++;
				visit(JsonObject.class.cast(value));
				depth--;
				System.out.print(end);
			}
		}
		System.out.print(multiply("\t", --depth)+"}");
	}

	@Override
	public void visit(JsonArray array) {
		Object o;
		System.out.print(multiply("\t", depth)+"[\n");
		String end, indent = multiply("\t", ++depth);
		for (int i=0; i<array.list.size(); i++) {
			o = array.list.get(i);
			end = ((i == array.list.size() - 1) ? "": ",") + "\n";
			if (o == null) {
				System.out.print(indent+"(null) "+o+end);
			} else if (Boolean.class.isInstance(o)) {
				System.out.print(indent+"(Bool) "+o+end);
			} else if (Integer.class.isInstance(o)) {
				System.out.print(indent+"(Integer) "+o+end);
			} else if (Double.class.isInstance(o)) {
				System.out.print(indent+"(Double) "+o+end);
			} else if (String.class.isInstance(o)) {
				System.out.print(indent+"(String) "+o+end);
			} else if (JsonArray.class.isInstance(o)) {
				System.out.print(indent+"(Array) \n");
				visit(JsonArray.class.cast(o));
				System.out.print(end);
			} else if (JsonObject.class.isInstance(o)) {
				System.out.print(indent+"(Object) \n");
				visit(JsonObject.class.cast(o));
				System.out.print(end);
			}
		}
		System.out.print(multiply("\t", --depth)+"]");
	}
	
	/* Helpers */
	
	public static String multiply(String start, long count) {
		StringBuilder sb = new StringBuilder();
		while (count-- > 0) sb.append(start);
		return sb.toString();
	}
}
