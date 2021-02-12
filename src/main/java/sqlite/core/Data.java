package sqlite.core;

import sqlite.Column;
import sqlite.Expression;
import sqlite.SelectCore;

public class Data {
	
	public static void main(String[] args) {
//		Create create = new Create("Company")
//				.ifNotExists()
//				.column("Id", Type.INT, Column.primaryKey().autoincrement(), Column.notNull())
//				.column("Name", Type.TEXT, Column.notNull())
//				.column("Age", Type.INT, Column.notNull())
//				.column("Address", Type.CHAR.size(50))
//				.column("Salary", Type.REAL);
//		System.out.println(create.asString());
		SelectCore select = new SelectCore()
				.columns(Column.name("column_1"))
				.from("table_1")
				.where(Expression.of(new SelectCore()
						.columns(Column.name("column_1"))
						.from("table_2")
						.asString()));
		System.out.println(select.asString());
	}
}
