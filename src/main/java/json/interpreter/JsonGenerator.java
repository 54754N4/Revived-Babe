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
	// Formats to create name placeholder for anonymous objects/arrays 
	private static final String OBJECT_NAME_FORMAT = "OBJECT_PLACEHOLDER%d";
	private static final String ARRAY_NAME_FORMAT = "ARRAY_PLACEHOLDER%d[]";
	
	private JsonParser parser;
	private AST ast;
	private List<String> created;
	private Stack<Context> context;		// child objects/arrays
	private String name;				// current object name
	private StringBuilder sb;			// generated output
	
	private int depth, arrCount, objCount;	// tabulation depth + placeholders count
	private boolean first;
	
	public JsonGenerator(String name, String input) throws Exception {
		this.name = name;
		parser = new JsonParser(new JsonLexer(input));
		ast = parser.parse();
		context = new Stack<>();
		created = new ArrayList<>();
		sb = new StringBuilder();
		depth = 0;
		arrCount = 0;
		objCount = 0;
		first = true;
	}
	
	public StringBuilder interpret() throws Exception {
		sb.append(Constants.IMPORT+"\n\n");
		visit(ast);
		postProcessChildren();
		System.out.println(String.format("Found %d object, %d array", objCount, arrCount));
		System.out.println(context.size());
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
		if (created.contains(name))
			return;
		objCount++;
		String prepend = "";
		if (first) {	
			first = false;
			prepend = "public ";
		}
		sb.append(multiply("\t", depth++) + String.format("%sfinal class %s implements Serializable {%n", prepend, classify(name)));
		sb.append(multiply("\t", depth) + "private static final long serialVersionUID = 1L;\n");
		Map<String, String> attributes = scanAttributes(object);
		writeAttributes(attributes);
		writeConstructor(classify(name), attributes);
		writeGetters(attributes);
		writeSetters(attributes, "");	// make return type void
		writeToString(attributes);
		writeBuilder(attributes);
		sb.append(multiply("\t", --depth)+"}\n\n");
		created.add(name);
	}

	// Needs to figure out array type by looking at first element
	@Override
	public void visit(JsonArray array) throws Exception {	 
		String oldName, type = "";
		Object value;
		value = array.list.get(0);	// in json array parsing, only first value is parsed
		if (value == null) 
			type = "Object";
		else if (Boolean.class.isInstance(value))
			type = "boolean";
		else if (Integer.class.isInstance(value))
			type = "int";
		else if (Double.class.isInstance(value))
			type = "double";
		else if (String.class.isInstance(value))
			type = "String";
		else if (JsonArray.class.isInstance(value))
			type = String.format(ARRAY_NAME_FORMAT, arrCount-1);
		else if (JsonObject.class.isInstance(value)) {
			oldName = name;
			name = type = String.format(OBJECT_NAME_FORMAT, objCount++);	// object in array == unnamed => insert placeholder
			visit(JsonObject.class.cast(value));
			name = oldName;
		}
		replaceAll(String.format(ARRAY_NAME_FORMAT, arrCount-1), type+"[]");
	}
	
	/* Helpers */
	
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
			else if (Double.class.isInstance(value))
				type = "double";
			else if (String.class.isInstance(value))
				type = "String";
			else if (JsonArray.class.isInstance(value)) {
				context.push(new ArrayContext(key, JsonArray.class.cast(value)));
				type = String.format(ARRAY_NAME_FORMAT, arrCount++);
			} else if (JsonObject.class.isInstance(value)) {
				context.push(new ObjectContext(key, JsonObject.class.cast(value)));
				type = classify(key);
			}
			map.put(key, type);
		}
		return map;
	}
	
	private void writeAttributes(Map<String, String> dict) {
		String indent = multiply("\t", depth);
		for (Entry<String, String> entry : dict.entrySet())
			sb.append(String.format("%spublic %s %s;%n", indent, entry.getValue(), entry.getKey()));
		sb.append("\n");
	}
	
	private void writeConstructor(String name, Map<String, String> attributes) {
		String indent = multiply("\t", depth++);
		sb.append(String.format("%spublic %s(", indent, name));
		attributes.forEach((key, value) -> sb.append(String.format("%s %s, ", value, key)));
		deleteLastChars(2).append(") {\n");			// delete last comma + space 
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
		sb.deleteCharAt(sb.length()-1);
		sb.append(String.format(");%n%s}%n", indent));
		depth--;
	}
	
	/* Start post-processing child classes and arrays
	 * that had been replaced with placeholders. */
	private void postProcessChildren() throws Exception {
		String oldName;
		ObjectContext obj;
		ArrayContext arr;
		List<Context> visitables = new ArrayList<>();
		do {
			visitables.addAll(context);
			context.clear();
			for (Context c : visitables) {
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
			visitables.clear();
		} while (context.size() != 0);
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
