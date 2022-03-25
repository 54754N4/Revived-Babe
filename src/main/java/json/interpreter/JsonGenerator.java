package json.interpreter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;
import java.util.regex.Pattern;

import json.interpreter.AST.JsonArray;
import json.interpreter.AST.JsonObject;

public class JsonGenerator implements Visitor {
	// Interpreter attributes
	private JsonParser parser;
	private AST ast;
	
	private static final String ARRAY_NAME_FORMAT = "ARRAY_PLACEHOLDER%d[]";
	
	private int depth, arrCount, objCount;	// current tabulation depth + occurences
	private boolean first, debug;			// first == public but not static class
	private StringBuilder sb;				// generated output
	
	private List<ObjectContext> objects;
	private Stack<ArrayContext> arrays;		// child objects/arrays
	private ArrayContext arrayContext;		// current array visited
	private String name;					// current object name
	
	public JsonGenerator(String responseName, String input) throws Exception {
		this.name = responseName;
		parser = new JsonParser(new JsonLexer(input));
		ast = parser.parse();
		sb = new StringBuilder();
		objects = new ArrayList<>();
		arrays = new Stack<>();
		depth = 0;
		arrCount = 0;
		objCount = 0;
		first = true;
		debug = false;
	}
	
	public StringBuilder interpret() throws Exception {
		sb.append(Constants.IMPORT+"\n\n");
		visit(ast);
		System.out.println(String.format("Found %d object, %d array", objCount, arrCount));
		return sb;
	}
	
	/* Since we write all to 1 file, only first class
	 * can be public; so we keep track of it. All classes
	 * are also final, since most json responses don't use
	 * inheritance. 
	 * Note: SUID defaults to 1 since there's no
	 * backward-compatibility to care about. */
	@Override
	public void visit(JsonObject object) throws Exception {
		objCount++;
		if (debug) System.out.println("Inside object : "+name);
		String prepend = "public static ";
		if (first) {	
			first = false;
			prepend = "public ";
		}
		sb.append("\n"+multiply("\t", depth++) + String.format("%sfinal class %s implements Serializable {%n", prepend, classify(name)));
		sb.append(multiply("\t", depth) + "private static final long serialVersionUID = 1L;\n");
		Map<String, String> attributes = scanAttributes(object);
		writeAttributes(attributes);
		writeConstructor(attributes);
		writeGetters(attributes);
		writeSetters(attributes);	// return void by default
		writeToString(attributes);
		writeBuilder(attributes);
		postProcessChildren();
		sb.append(multiply("\t", --depth)+"}\n\n");
	}

	private Map<String, String> scanAttributes(JsonObject object) {
		Map<String, String> map = new HashMap<>();
		String key, type = "";
		Object value;
		for (Entry<String, Object> entry : object.dict.entrySet()) {
			key = entry.getKey();
			value = entry.getValue();
			if (value == null) 
				type = "Object";
			else if (Boolean.class.isInstance(value))
				type = "boolean";
			else if (Integer.class.isInstance(value))
				type = "int";
			else if (Long.class.isInstance(value))
				type = "long";
			else if (Double.class.isInstance(value))
				type = "double";
			else if (String.class.isInstance(value))
				type = "String";
			else if (JsonArray.class.isInstance(value)) {
				type = String.format(ARRAY_NAME_FORMAT, arrCount);
				arrays.push(new ArrayContext(key, type, JsonArray.class.cast(value)));
				if (debug) System.out.println("Setting placeholder array : "+type+" for name : "+key);
				arrCount++;
			} else if (JsonObject.class.isInstance(value)) {
				type = classify(key);
				objects.add(new ObjectContext(key, type, JsonObject.class.cast(value)));
				if (debug) System.out.println("Setting placeholder object : "+type+" for name : "+key);
			}
			map.put(key, type);
		}
		return map;
	}
	
	// Needs to figure out array type by looking at first element
	@Override
	public void visit(JsonArray array) throws Exception {
		if (debug) System.out.println("Dealing with array : "+arrayContext.name);
		String type = "";
		Object value;
		value = array.list.get(0);	// in json array parsing, only first value is parsed
		if (value == null) 
			type = "Object";
		else if (Boolean.class.isInstance(value))
			type = "boolean";
		else if (Integer.class.isInstance(value))
			type = "int";
		else if (Long.class.isInstance(value))
			type = "long";
		else if (Double.class.isInstance(value))
			type = "double";
		else if (String.class.isInstance(value))
			type = "String";
		else if (JsonArray.class.isInstance(value)) {
			visit(JsonArray.class.cast(value));	// nested array so visit child array to know type
			type = arrayContext.name+"[]";		
		} else if (JsonObject.class.isInstance(value)) {
			type = removeS(classify(arrayContext.name));	// remove ending s if plurar since it's object of array
			objects.add(new ObjectContext(type, arrayContext.placeholder, JsonObject.class.cast(value)));
		}
		arrayContext.name = type;
	}
	
	/* Helpers */
	
	/* Start post-processing child classes and arrays
	 * that had been replaced with placeholders. */
	private void postProcessChildren() throws Exception {
		String oldName;
		ArrayContext arrayContext;	
		while (!arrays.isEmpty()) {
			arrayContext =  arrays.pop();
			this.arrayContext = arrayContext;
			visit(arrayContext.element);
			String arrayName = arrayContext.placeholder,
					with = arrayContext.name+"[]";
			if (debug) System.out.println("Renaming array : "+arrayName+" with : "+with);
			replaceAll(arrayName, with);
		}
		do {
			// Copy objects array to allow visit to concurrently push to objects list
			List<ObjectContext> copies = new ArrayList<>(objects);
			for (ObjectContext objectContext : copies) {
				oldName = name;
				name = objectContext.name;
				objects.remove(objectContext);	// remove BEFORE visiting
				visit(objectContext.element);
				name = oldName;
			}
		} while (objects.size() != 0);
	}
	
	private void writeAttributes(Map<String, String> dict) {
		String indent = multiply("\t", depth);
		for (Entry<String, String> entry : dict.entrySet())
			sb.append(String.format("%spublic %s %s;%n", indent, entry.getValue(), entry.getKey()));
		sb.append("\n");
	}
	
	private void writeConstructor(Map<String, String> attributes) {
		name = classify(name);
		String indent = multiply("\t", depth++);
		sb.append(String.format("%spublic %s(", indent, name));
		attributes.forEach((key, value) -> sb.append(String.format("%s %s, ", value, key)));
		if (attributes.size() != 0) // delete last comma + space
			deleteLastChars(2);
		sb.append(") {\n");
		// Transfer parameters to attributes
		String inner = multiply("\t", depth);
		attributes.forEach((key, value) -> sb.append(String.format("%2$sthis.%1$s = %1$s;%n", key, inner)));
		sb.append(indent + "}\n\n");
		depth--;
	}
	
	private void writeGetters(Map<String, String> attributes) {
		String indent = multiply("\t", depth),
				inner = multiply("\t", depth+1);
		attributes.forEach((key, value) -> {
			String name = classify(key), 
				getter = (key.equals("bool") ? "is": "get") + name;
			sb.append(String.format("%spublic %s %s() {%n%sreturn %s;%n%s}%n%n", indent, value, getter, inner, key, indent));
		});
	}
	
	private void writeSetters(Map<String, String> attributes) {
		writeSetters(attributes, "");
	}
	
	private void writeSetters(Map<String, String> attributes, String returnType) {
		String indent = multiply("\t", depth),
				inner = multiply("\t", depth+1);
		final String outType = returnType.equals("") ? "void" : returnType;
		final String outValue = returnType.equals("") ? "" : "\n"+inner+"return this;";
		attributes.forEach((key, value) -> {
			String name = classify(key), 
				setter = "set" + name;
			sb.append(String.format("%spublic %s %s(%s %s) {%n%sthis.%s = %s;%s%n%s}%n%n", indent, outType, setter, value, key, inner, key, key, outValue, indent));
		});
	}
	
	private void writeToString(Map<String, String> attributes) {
		String indent = multiply("\t", depth),
				inner = multiply("\t", depth+1);
		sb.append(String.format("%s@Override%n%spublic String toString() {%n%sreturn new StringBuilder()%n%s.append(getClass().getName()).append(\"{\\n\")\n", indent, indent, inner, inner+"\t"));
		attributes.forEach((key, value) -> sb.append(String.format("%s\t.append(\"%s: \").append(%s+\",\\n\")\n", inner, key, removeArray(key))));
		sb.append(String.format("%s.append(\"\\n}\").toString();%n%s}%n%n", inner+"\t", indent));
	}
	
	private void writeBuilder(Map<String, String> attributes) {
		String indent = multiply("\t", depth++);
		sb.append(String.format("%spublic static class Builder {%n", indent));
		writeAttributes(attributes);
		writeSetters(attributes, "Builder");
		writeBuild(attributes);
		sb.append(String.format("%s}\n", indent));
		depth--;
	}
	
	private void writeBuild(Map<String, String> attributes) {
		String indent = multiply("\t", depth++),
				inner = multiply("\t", depth);
		sb.append(String.format("%spublic %s build() {%n%sreturn new %s(", indent, classify(name), inner, classify(name)));
		attributes.forEach((key, value) -> sb.append(String.format("%s,", key)));
		if (attributes.size() != 0)
			sb.deleteCharAt(sb.length()-1);
		sb.append(String.format(");%n%s}%n", indent));
		depth--;
	}
	
	// Replaces all placeholder names with actual type found or an object placeholder
	public void replaceAll(String regex, String with) {
		sb = new StringBuilder(sb.toString().replaceAll(Pattern.quote(regex), with));
	}
	
	private String removeArray(String str) {
		return str.endsWith("[]") ? str.substring(0, str.length()-2) : str;
	}
	
	private StringBuilder deleteLastChars(int count) {
		while (count-- > 0) sb.deleteCharAt(sb.length()-1);
		return sb;
	}
	
	private static String classify(String str) {
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}
	
	private static String removeS(String str) {
		return str.endsWith("s") ? str.substring(0, str.length()-1) : str;
	}
	
	private static String multiply(String start, long count) {
		StringBuilder sb = new StringBuilder();
		while (count-- > 0) sb.append(start);
		return sb.toString();
	}
	
	private static final class Constants {
		public static final String IMPORT = "import java.io.Serializable;";
	}
	
	private static abstract class Context<T extends AST> {
		public String name, placeholder;
		public T element;
		
		public Context(String name, String placeholder, T element) {
			this.name = name;
			this.element = element;
			this.placeholder = placeholder;
		}
	}
	
	private static final class ArrayContext extends Context<JsonArray> {
		public ArrayContext(String name, String placeholder, JsonArray e) {
			super(name, placeholder, e);
		}
	}
	
	private static final class ObjectContext extends Context<JsonObject>{
		public ObjectContext(String name, String placeholder, JsonObject e) {
			super(name, placeholder, e);
		}
	}
}
