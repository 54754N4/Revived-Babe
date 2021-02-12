package sqlite.core;

import java.util.ArrayList;
import java.util.List;

import sqlite.core.SQLite.SqliteStatement;

public class StatementList implements SqliteStatement {
	private List<SqliteStatement> statements = new ArrayList<>();
	
	public StatementList add(SqliteStatement statement) {
		statements.add(statement);
		return this;
	}
	
	@Override
	public String asString() {
		StringBuilder sb = new StringBuilder();
		for (SqliteStatement statement : statements) 
			sb.append(statement.asString()).append(";\n");
		return sb.toString();
	}
}