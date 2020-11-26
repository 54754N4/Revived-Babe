package lib.interpreter.bash;

public enum Type {
	WORD, COPROC("coproc"), FUNCTION("function"),
	LOCAL("local"), REDIR_WORD, NUMBER, 
	HYPHEN("-"),
	
	// branching
	IF("if"), THEN("then"), ELIF("elif"), 
	ELSE("else"), FI("fi"),
	
	SELECT("select"), DONE("done"), DO("do"),
	FOR("for"), UNTIL("until"), WHILE("while"),
	
	// redirection
	AND_GREATER_GREATER("&>>"), AND_GREATER("&>"),
	LESS_AND("<&"), GREATER_AND(">&"), 
	LESS_LESS_LESS("<<<"), LESS_LESS_MINUS("<<-"),
	LESS_LESS("<<"), LESS_GREATER("<>"),
	GREATER_BAR(">|"), GREATER_GREATER(">>"),
	LESS("<"), GREATER(">"),
	
	// case ops
	CASE("case"), ESAC("esac"), IN("in"),
	SEMI_SEMI_AND(";;&"), SEMI_AND(";&"),
	SEMI_SEMI(";;"),
	
	// dual ops
	PAREN_START("("), PAREN_END(")"),
	ARITH_START("(("), ARITH_END("))"),
	COND_START("[["), COND_END("]]"),
	TEST_START("["), TEST_END("]"),
	CURLY_START("{"), CURLY_END("}"),
	
	// binary operators
	PIPE("|"), SEMICOLON(";"), AND("&"),
	OR_OR("||"), AND_AND("&&"), EQUALS("="),  
	
	// terminators
	NEWLINE,
	EOF,
	EPSILON;
	
	private final String string;
	
	private Type(String string) {
		this.string = string;
	}
	
	private Type() {
		string = name();
	}
	
	@Override
	public String toString() {
		return string;
	}
	
	public static Type from(String name) {
		switch (name.toLowerCase()) {
			case "coproc": return COPROC;
			case "function": return FUNCTION;
			case "local": return LOCAL;
			case "if": return IF;
			case "then": return THEN;
			case "elif": return ELIF;
			case "else": return ELSE;
			case "fi": return FI;
			case "select": return SELECT;
			case "do": return DO;
			case "done": return DONE;
			case "for": return FOR;
			case "until": return UNTIL;
			case "while": return WHILE;
			case "case": return CASE;
			case "esac": return ESAC;
			case "in": return IN;
			default: return null;	// shouldn't happen
		}
	}
}
