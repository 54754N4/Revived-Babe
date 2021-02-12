package sqlite.core;

import java.util.ArrayList;
import java.util.List;

import sqlite.TableColumn;
import sqlite.ColumnConstraint;
import sqlite.SelectCore;
import sqlite.TableConstraint;
import sqlite.Type;
import sqlite.core.SQLite.SqliteStatement;

public class Create implements SqliteStatement {
	private String tableName;
	private boolean temp = false, 
			temporary = false, 
			ifNotExists = false, 
			withoutRowID = false;
	private SelectCore select;
	private List<TableColumn> columns = new ArrayList<>();
	private List<TableConstraint> constraints = new ArrayList<>();
	
	public Create(String tableName) {
		this.tableName = tableName;
	}
	
	public Create temp() {
		temp = true;
		return this;
	}
	
	public Create temporary() {
		temporary = true;
		return this;
	}
	
	public Create ifNotExists() {
		ifNotExists = true;
		return this;
	}
	
	public Create withoutRowID() {
		withoutRowID = true;
		return this;
	}
	
	public Create as(SelectCore select) {
		this.select = select;
		return this;
	}
	
	public Create column(String name, Type type, ColumnConstraint...constraints) {
		columns.add(new TableColumn(name, type, constraints));
		return this;
	}
	
	public Create column(TableColumn column) {
		columns.add(column);
		return this;
	}

	public Create constraints(TableConstraint...constraints) {
		for (TableConstraint constraint : constraints)
			this.constraints.add(constraint);
		return this;
	}

	@Override
	public String asString() {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE ");
		if (temp)
			sb.append("TEMP ");
		else if (temporary)
			sb.append("TEMPORARY ");
		sb.append("TABLE ");
		if (ifNotExists)
			sb.append("IF NOT EXISTS ");
		sb.append(tableName+" ");
		if (select != null) 
			return sb.append("AS ")
					.append(select.asString()+" ")
					.toString();
		sb.append("(\n");
		if (columns.size() != 0) {
			columns.stream()
				.map(TableColumn::asString)
				.forEach(col -> sb.append(col).append(",\n"));
			sb.deleteCharAt(sb.length()-1)		// remove newline
				.deleteCharAt(sb.length()-1); 	// remove comma
		}
		if (constraints.size() != 0)
			constraints.stream()
				.map(TableConstraint::asString)
				.forEach(constraint -> sb.append(",\n").append(constraint));
		sb.append("\n)");
		if (withoutRowID)
			sb.append("WITHOUT ROWID");
		return sb.toString();
	}
	
	public static class Index implements SqliteStatement {}
	public static class Trigger implements SqliteStatement {}
	public static class View implements SqliteStatement {}
	public static class VirtualTable implements SqliteStatement {}
}