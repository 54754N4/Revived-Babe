package tests.sqlite;
import static org.junit.jupiter.api.Assertions.assertEquals;

import annotations.FastTest;
import sqlite.TableColumn;
import sqlite.ColumnConstraint.NotNull;
import sqlite.ColumnConstraint.PrimaryKey;
import sqlite.Table;
import sqlite.Type;
import sqlite.core.Create;
import sqlite.core.SQLite;
import sqlite.core.StatementList;

public class CreateTests {
	
	@FastTest
    public void Sqlite_CreateTableColumnAndConstraint_Validates() {
		Create sql = new Create("Contacts")
				.ifNotExists()
				.column("Id", Type.INTEGER, TableColumn.primaryKey().ascending())
				.column("FirstName", Type.TEXT, TableColumn.notNull())
				.column("LastName", Type.TEXT, TableColumn.notNull())
				.column("Email", Type.TEXT, TableColumn.notNull(), TableColumn.unique())
				.column("Phone", Type.TEXT, TableColumn.notNull(), TableColumn.unique());
        assertEquals(
    		sql.asString(),	
    		"CREATE TABLE IF NOT EXISTS Contacts (\n"
    		+ "Id INTEGER PRIMARY KEY ASC,\n"
    		+ "FirstName TEXT NOT NULL,\n"
    		+ "LastName TEXT NOT NULL,\n"
    		+ "Email TEXT NOT NULL UNIQUE,\n"
    		+ "Phone TEXT NOT NULL UNIQUE\n"
    		+ ")"
        );
    }
	
	@FastTest
	public void Sqlite_CreateTableColumnMultipleConstraints_Validates() {
		Create sql = new Create("Company")
				.ifNotExists()
				.column("Id", Type.INT, TableColumn.primaryKey().autoincrement(), TableColumn.notNull())
				.column("Name", Type.TEXT, TableColumn.notNull())
				.column("Age", Type.INT, TableColumn.notNull())
				.column("Address", Type.CHAR.size(50))
				.column("Salary", Type.REAL);
		assertEquals(
			sql.asString(),
			"CREATE TABLE IF NOT EXISTS Company (\n"
			+ "Id INT PRIMARY KEY AUTOINCREMENT NOT NULL,\n"
			+ "Name TEXT NOT NULL,\n"
			+ "Age INT NOT NULL,\n"
			+ "Address CHAR(50),\n"
			+ "Salary REAL\n"
			+ ")");
	}
	
	@FastTest
	public void Sqlite_CreateTableWithClasses_Validates() {
		Create sql = new Create("Company")
				.ifNotExists()
				.column(new TableColumn("Id", Type.INT, new PrimaryKey().autoincrement(), new NotNull()))
				.column(new TableColumn("Name", Type.TEXT, new NotNull()))
				.column(new TableColumn("Age", Type.INT, new NotNull()))
				.column(new TableColumn("Address", Type.CHAR.size(50)))
				.column(new TableColumn("Salary", Type.REAL));
		assertEquals(
			sql.asString(),
			"CREATE TABLE IF NOT EXISTS Company (\n"
			+ "Id INT PRIMARY KEY AUTOINCREMENT NOT NULL,\n"
			+ "Name TEXT NOT NULL,\n" 
			+ "Age INT NOT NULL,\n"
			+ "Address CHAR(50),\n"
			+ "Salary REAL\n"
			+ ")");
	}
	
	@FastTest
	public void Sqlite_CreateTableWithTableConstraints_Validates() {
		Create sql = new Create("Company")
				.ifNotExists()
				.column(new TableColumn("Id", Type.INT, new PrimaryKey().autoincrement(), new NotNull()))
				.column(new TableColumn("Name", Type.TEXT, new NotNull()))
				.column(new TableColumn("Age", Type.INT, new NotNull()))
				.column(new TableColumn("Address", Type.CHAR.size(50)))
				.column(new TableColumn("Salary", Type.REAL))
				.column(new TableColumn("DepartmentId", Type.INTEGER))
				.constraints(Table.foreignKey("Departments").columns("DepartmentId").references("DepartmentId"));
		assertEquals(
			sql.asString(),
			"CREATE TABLE IF NOT EXISTS Company (\n"
			+ "Id INT PRIMARY KEY AUTOINCREMENT NOT NULL,\n"
			+ "Name TEXT NOT NULL,\n"
			+ "Age INT NOT NULL,\n"
			+ "Address CHAR(50),\n"
			+ "Salary REAL,\n"
			+ "DepartmentId INTEGER,\n"
			+ "FOREIGN KEY (DepartmentId) REFERENCES Departments (DepartmentId)\n"
			+ ")");
	}
	
	@FastTest
	public void Sqlite_CreateForeignKeysRelationship_Validates() {
		StatementList list = new StatementList()
				.add(SQLite.create("[Departments]")
						.column("[DepartmentId]", Type.INTEGER, TableColumn.notNull(), TableColumn.primaryKey().autoincrement())
						.column("[DepartmentName]", Type.NVARCHAR.size(50), TableColumn.notNull()))
				.add(SQLite.create("[Students]")
						.column("[StudentId]", Type.INTEGER, TableColumn.primaryKey().autoincrement(), TableColumn.notNull())
						.column("[StudentName]", Type.NVARCHAR.size(50), TableColumn.notNull())
						.column("[DepartmentId]", Type.INTEGER, TableColumn.notNull())
						.column("[DateOfBirth]", Type.DATE, TableColumn.notNull())
						.constraints(Table.foreignKey("Departments")
								.columns("DepartmentId")
								.references("DepartmentId")));
		assertEquals(
			list.asString(),
			"CREATE TABLE [Departments] (\n"
			+ "[DepartmentId] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n"
			+ "[DepartmentName] NVARCHAR(50) NOT NULL\n"
			+ ");\n"
			+ "CREATE TABLE [Students] (\n"
			+ "[StudentId] INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n"
			+ "[StudentName] NVARCHAR(50) NOT NULL,\n"
			+ "[DepartmentId] INTEGER NOT NULL,\n"
			+ "[DateOfBirth] DATE NOT NULL,\n"
			+ "FOREIGN KEY (DepartmentId) REFERENCES Departments (DepartmentId)\n"
			+ ");\n");
	}
}
