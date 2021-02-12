package sqlite;

import java.io.File;

import sqlite.core.SQLite.SqlComponent;
import sqlite.model.Literal;

public class LiteralValue implements SqlComponent {	// https://www.sqlite.org/syntax/literal-value.html
	private Literal literal;
	private String value;
	private File file;
	
	public LiteralValue(File file) {
		this.literal = Literal.BLOB;
		this.file = file;
	}
	
	public LiteralValue(double numeric) {
		this(Literal.NUMERIC, Double.toString(numeric));
	}
	
	public LiteralValue(String string) {
		this(Literal.STRING, string);
	}
	
	public LiteralValue(Literal literal, String value) {
		this.literal = literal;
		this.value = value;
	}
	
	public LiteralValue(Literal literal) {
		this(literal, literal.toString());
	}
}