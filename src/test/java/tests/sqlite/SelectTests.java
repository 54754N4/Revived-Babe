package tests.sqlite;

import static org.junit.jupiter.api.Assertions.assertEquals;

import annotations.FastTest;
import sqlite.Column;
import sqlite.Expression;
import sqlite.SelectCore;

public class SelectTests {
	@FastTest
    public void Sqlite_SelectFromTable_Validates() {
		SelectCore sql = new SelectCore().from("COMPANY");
        assertEquals(sql.asString(), "SELECT * FROM COMPANY");
    }
	
	@FastTest
    public void Sqlite_SelectFromTableWithColumns_Validates() {
		SelectCore sql = new SelectCore()
				.columns(Column.name("sql"), Column.allFrom("sqlite_master"))
				.from("sqlite_master");
        assertEquals(sql.asString(), "SELECT sql, sqlite_master.* FROM sqlite_master");
    }
	
	@FastTest
    public void Sqlite_SelectFromTableWithConditions_Validates() {
		SelectCore sql = new SelectCore()
				.columns(Column.allFrom("sqlite_master"))
				.from("sqlite_master")
				.where(Expression.of("type = 'table' AND tbl_name LIKE %"));
        assertEquals(sql.asString(), "SELECT sqlite_master.* FROM sqlite_master WHERE type = 'table' AND tbl_name LIKE %");
    }
}
