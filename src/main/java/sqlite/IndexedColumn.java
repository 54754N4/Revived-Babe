package sqlite;

import sqlite.core.SQLite.SqlComponent;

public class IndexedColumn implements SqlComponent {
	private String columnName, collationName;
	private boolean asc = false, desc = false;
	private Expression expr;
	
	public IndexedColumn(String columnName) {
		this.columnName = columnName;
	}
	
	public IndexedColumn(Expression expr) {
		this.expr = expr;
	}
	
	public static IndexedColumn[] of(String...columnNames) {
		IndexedColumn[] cols = new IndexedColumn[columnNames.length];
		for (int i=0; i<cols.length; i++)
			cols[i] = new IndexedColumn(columnNames[i]); 
		return cols;
	}
	
	public IndexedColumn ascending() {
		asc = true;
		desc = false;
		return this;
	}
	
	public IndexedColumn descending() {
		desc = true;
		asc = false;
		return this;
	}
	
	public IndexedColumn collate(String collationName) {
		this.collationName = collationName;
		return this;
	}
	
	@Override
	public String asString() {
		if (columnName == null && expr == null)
			throw new IllegalArgumentException("You have to set either column name or expression");
		StringBuilder sb = new StringBuilder();
		sb.append((columnName != null ? columnName : expr.asString()) + " ");
		if (collationName != null)
			sb.append("COLLATE "+collationName+" ");
		sb.append(asc ? "ASC" : desc ? "DESC" : "");
		return sb.toString();
	}
}