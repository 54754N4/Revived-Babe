package sqlite;

import sqlite.core.SQLite;
import sqlite.core.SQLite.SqlComponent;

public class Ordering implements SqlComponent {
	// https://www.sqlite.org/syntaxdiagrams.html#ordering-term
	private Expression expr;
	private String collation = "";
	private boolean asc = false, desc = false, 
			nullsFirst = false, nullsLast = false;
	
	public Ordering(Expression expr) {
		this.expr = expr;
	}
	
	public static Ordering expression(Expression expr) {
		return new Ordering(expr);
	}
	
	public Ordering collate(String name) {
		collation = name;
		return this;
	}
	
	public Ordering ascending() {
		asc = true;
		desc = false;
		return this;
	}
	
	public Ordering descending() {
		desc = true;
		asc = false;
		return this;
	}
	
	public Ordering nullsFirst() {
		nullsFirst = true;
		nullsLast = false;
		return this;
	}
	
	public Ordering nullsLast() {
		nullsLast = true;
		nullsFirst = false;
		return this;
	}
	
	@Override
	public String asString() {
		return expr.asString() + " " 
				+ (collation.equals("") ?  "" : "COLLATE "+collation+" ")
				+ (asc ? "ASC " : desc ? "DESC " : "")
				+ (nullsFirst ? "NULLS FIRST " : nullsLast ? "NULLS LAST " : "");
	}
}