package json.interpreter;

import json.interpreter.AST.JsonArray;
import json.interpreter.AST.JsonObject;

public interface Visitor {
	void visit(JsonObject object) throws Exception;
	void visit(JsonArray array) throws Exception;
	default void visit(AST ast) throws Exception {			// visit dispatcher
		if (JsonObject.class.isInstance(ast))
			visit(JsonObject.class.cast(ast));
		else if (JsonArray.class.isInstance(ast))
			visit(JsonArray.class.cast(ast));
		else
			throw new IllegalStateException(String.format("No visit method defined for %s", ast));
	}
}
