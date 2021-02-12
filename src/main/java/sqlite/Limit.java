package sqlite;

import sqlite.core.SQLite.SqlComponent;

public class Limit implements SqlComponent {
	private Expression expr, offset, comma;
	
	public Limit(Expression expr) {
		this.expr = expr;
	}
	
	public static Limit of(Expression expr) {
		return new Limit(expr);
	}
	
	public Limit offset(Expression expr) {
		offset = expr;
		comma = null;
		return this;
	}
	
	public Limit comma(Expression expr) {
		comma = expr;
		offset = null;
		return this;
	}
	
	@Override
	public String asString() {
		return "LIMIT " + expr.asString() + " " 
				+ (offset != null ? "OFFSET " + offset + " " : "")  
				+ (comma != null ? ", " + comma + " " : "");
	}
}
