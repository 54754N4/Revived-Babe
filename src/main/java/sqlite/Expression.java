package sqlite;

import sqlite.core.SQLite.SqlComponent;

public class Expression implements SqlComponent {
	// https://www.sqlite.org/syntax/expr.html
	private String string;
	
	public Expression(String string) {
		this.string = string;
	}
	
	public static Expression of(String string) {
		return new Expression(string);
	}
	
	@Override
	public String asString() {
		return string;
	}
}