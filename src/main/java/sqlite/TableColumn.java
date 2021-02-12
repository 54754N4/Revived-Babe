package sqlite;

import java.util.ArrayList;
import java.util.List;

import sqlite.ColumnConstraint.Check;
import sqlite.ColumnConstraint.Default;
import sqlite.ColumnConstraint.NotNull;
import sqlite.ColumnConstraint.PrimaryKey;
import sqlite.ColumnConstraint.Unique;
import sqlite.core.SQLite.SqlComponent;

public class TableColumn implements SqlComponent {	// https://www.sqlite.org/syntaxdiagrams.html#column-def
	private String name;
	private Type type;
	private List<ColumnConstraint> constraints;
	
	public TableColumn(String name, ColumnConstraint...constraints) {
		this(name, null, constraints);
	}
	
	public TableColumn(String name, Type type, ColumnConstraint...constraints) {
		this.name = name;
		this.type = type;
		this.constraints = new ArrayList<>();
		add(constraints);
	}
	
	public TableColumn add(ColumnConstraint...constraints) {
		for (ColumnConstraint constraint : constraints)
			this.constraints.add(constraint);
		return this;
	}
	
	@Override
	public String asString() {
		StringBuilder sb = new StringBuilder(name+" ");
		sb.append(type != null ? type.asString() + " " : "");
		if (constraints.size() != 0) {
			constraints.forEach(constraint -> sb.append(constraint.asString()+" "));
			sb.deleteCharAt(sb.length()-1);
		}
		return sb.toString().trim();
	}
	
	/* Column constraints */
	
	public static PrimaryKey primaryKey() {
		return new PrimaryKey();
	}
	
	public static NotNull notNull() {
		return new NotNull();
	}
	
	public static Unique unique() {
		return new Unique();
	}
	
	public static Check check(Expression expr) {
		return new Check(expr);
	}
	
	public static Default defaultExpression(Expression expr) {
		return new Default(expr);
	}
	
	public static Default defaultLiteral(LiteralValue literal) {
		return new Default(literal);
	}
	
	public static Default defaultSigned(int signed) {
		return new Default(signed);
	}
}