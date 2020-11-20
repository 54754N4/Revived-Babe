package json.interpreter;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import json.interpreter.AST.JsonArray;
import json.interpreter.AST.JsonObject;


/*
 * TO-DO: 	builder (useless probably)
 * DONE:	Serializable, constructors, getters, setters, toString, 
 */
public class JsonGenerator implements Visitor {
	private JsonParser parser;
	private AST ast;
	
	private int depth = 0, count = 0;
	private boolean preprocess = true;
	private Stack<Context> context;
	private String name;
	private final StringBuilder sb;
//	private BufferedWriter writer;	
	
	public JsonGenerator(String name, String input) throws Exception {
		this.name = name;
		parser = new JsonParser(new JsonLexer(input));
		ast = parser.parse();
		context = new Stack<>();
		sb = new StringBuilder();
	}
	
	public void interpret() throws Exception {
		sb.append(Constants.IMPORT+"\n\n");
		visit(ast);
		handleContext();
		System.out.println(sb);
	}
	
	@Override
	public void visit(JsonObject object) throws Exception {
		sb.append(multiply("\t", depth++) + String.format("public static final class %s implements Serializable {%n", classify(name)));
		Map<String, String> attributes = writeAttributes(object);
		writeConstructor(classify(name), attributes);
		writeMethods(attributes);
		writeToString(attributes);
		writeBuilder(attributes);
		sb.append(multiply("\t", --depth)+"}\n\n");
	}

	@Override
	public void visit(JsonArray array) {	
		// deal with arrays
	}
	
	/* Helpers */
	
	private Map<String, String> writeAttributes(JsonObject object) throws Exception {
		Map<String, String> map = new HashMap<>();
		String indent = multiply("\t", depth);
		BiConsumer<String,String> printer = (type, identifier) -> sb.append(String.format("%spublic %s %s;%n", indent, type, identifier));
		String key, type = "";
		Object value;
		for (Entry<String, Object> entry : object.dict.entrySet()) {
			key = entry.getKey();
			value = entry.getValue();
			if (value == null) 
				type = "Object";
			else if (Boolean.class.isInstance(value))
				type = "bool";
			else if (Integer.class.isInstance(value))
				type = "int";
			else if (Double.class.isInstance(value))
				type = "double";
			else if (String.class.isInstance(value))
				type = "String";
			else if (JsonArray.class.isInstance(value)) {
				if (preprocess) 
					context.push(new ArrayContext(key, JsonArray.class.cast(value)));
				type = String.format("PLACEHOLDER%d[]", count++);
			} else if (JsonObject.class.isInstance(value)) {
				if (preprocess)
					context.push(new ObjectContext(key, JsonObject.class.cast(value)));
				type = classify(key);
			}
			printer.accept(type, key);
			map.put(key, type);
		} 
		return map;
	}
	
	private void writeConstructor(String name, Map<String, String> attributes) {
		String indent = multiply("\t", depth++);
		Consumer<String> printer = str -> sb.append(str);
		// Declare constructor
		printer.accept(String.format("%n%spublic %s(", indent, name));
		attributes.forEach((key, value) -> printer.accept(String.format("%s %s, ", value, key)));
		// Delete last comma + space
		deleteLastChars(2);
		printer.accept(") {\n");
		// Transfer parameters to attributes
		String inner = multiply("\t", depth);
		attributes.forEach((key, value) -> printer.accept(String.format("%2$sthis.%1$s = %1$s;%n", key, inner)));
		printer.accept(indent + "}\n\n");
		depth--;
	}
	
	private void writeMethods(Map<String, String> attributes) {
		String indent = multiply("\t", depth),
				inner = multiply("\t", depth+1);
		attributes.forEach((key, value) -> {
			String name = classify(key), 
				getter = (key.equals("bool") ? "is": "get") + name,
				setter = "set" + name;
			sb.append(String.format("%spublic %s %s() {%n%sreturn %s;%n%s}%n%n", indent, value, getter, inner, key, indent));
			sb.append(String.format("%spublic %s %s(%s %s) {%n%sthis.%s = %s;%n%s}%n%n", indent, value, setter, value, key, inner, key, key, indent));
		});
	}
	
	private void writeToString(Map<String, String> attributes) {
		String indent = multiply("\t", depth),
				inner = multiply("\t", depth+1);
		Consumer<String> printer = str -> sb.append(str);
		printer.accept(String.format("%s@Override%n%spublic String toString() {%n%sreturn getClass() + \" {\\n\"", indent, indent, inner));
		attributes.forEach((key, value) -> printer.accept(String.format(" + \"%s: \" + %s + \", \"", key, removeArray(key))));
		printer.accept(String.format("+ \"\\n}\";%n%s}%n%n", indent));
	}
	
	private void writeBuilder(Map<String, String> attributes) {
		String indent = multiply("\t", depth++),
				inner = multiply("\t", depth);
		sb.append(String.format("%s// Insert builder here%n%n", indent));
		// TO DO
		depth--;
	}
	
	private String removeArray(String str) {
		return str.endsWith("[]") ? str.substring(0, str.length()-2) : str;
	}
	
	private void handleContext() throws Exception {
		// Start postprocessing child classes and arrays
		preprocess = false;
		String oldName;
		ObjectContext obj;
		ArrayContext arr;
		for (Context c : context) {
			oldName = name;
			if (ObjectContext.class.isInstance(c)) {
				obj = ObjectContext.class.cast(c);
				name = obj.name;
				visit(obj.object);
				
			} else if (ArrayContext.class.isInstance(c)) {
				arr = ArrayContext.class.cast(c);
				name = arr.name;
				visit(arr.array);
			}
			name = oldName;
		}
	}
	
	private void deleteLastChars(int count) {
		while (count-- > 0) sb.deleteCharAt(sb.length()-1);
	}
	
	private static String classify(String str) {
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}
	
	private static String multiply(String start, long count) {
		StringBuilder sb = new StringBuilder();
		while (count-- > 0) sb.append(start);
		return sb.toString();
	}
	
	private static final class Constants {
		public static final String IMPORT = "import java.io.Serializable;";
	}
	
	public static class Context {}
	
	public static final class ObjectContext extends Context {
		public String name;
		public JsonObject object;
		
		public ObjectContext(String name, JsonObject object) {
			this.name = name;
			this.object = object;
		}
	}
	
	public static final class ArrayContext extends Context {
		public String name;
		public JsonArray array;
		
		public ArrayContext(String name, JsonArray array) {
			this.name = name;
			this.array = array;
		}
	}
}
