package sqlite.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.function.Consumer;

import sqlite.SelectCore;

/**
 * 
 * References: 
 * Language syntax diagram 	- https://www.sqlite.org/syntaxdiagrams.html
 * Sqlite syntax tester 	- https://sqliteonline.com/
 */
public class SQLite {
	
	public static interface SqliteStatement extends SqlComponent {
		
	}
	
	public static interface SqlComponent {
		default String asString() {
			throw new SqliteException("Not implemented yet for "+getClass());
		}
		
		default void execute(Connection connection) throws SQLException {
			try (Statement statement = connection.createStatement()) {
				statement.execute(asString());
			}
		}
		
		default ResultSet execute(Connection connection, Consumer<PreparedStatement> preparer) throws SQLException {
			try (PreparedStatement statement = connection.prepareStatement(asString())) {
				preparer.accept(statement);
				return statement.executeQuery();
			}
		}
	}
	
	public static class DBContext {
		public static final String BABE_DATABASE = "./database/bot-data.db";
		public static final String DB_URL_ACCESS_FORMAT = "jdbc:sqlite:%s";
		private final Connection connection;
		
		public DBContext() throws SQLException {
			this(BABE_DATABASE);
		}
		
		public DBContext(String database) throws SQLException {
			connection = DriverManager.getConnection(String.format(DB_URL_ACCESS_FORMAT, database));
		}
	}
	
	// Convenience accessors
	
	public static Create create(String tableName) {
		return new Create(tableName);
	}
	
	public static SelectCore select() {
		return new SelectCore();
	}
	
	/* Statements AST */
	
	public static class Delete implements SqliteStatement {
		// normal delete
		public static class Limited implements SqliteStatement {}
	}
	public static class Drop implements SqliteStatement {
		// by default affects tables
		public static class Index implements SqliteStatement {}
		public static class Trigger implements SqliteStatement {}
		public static class View implements SqliteStatement {}
	}
	public static class Update implements SqliteStatement {
		// normal update row
		public static class Limited implements SqliteStatement {}
	}
	public static class Explain implements SqliteStatement {
		// normal update row
		public static class QueryPlan implements SqliteStatement {}
	}
	public static class Insert implements SqliteStatement {}
	public static class Pragma implements SqliteStatement {}
	public static class Alter implements SqliteStatement {}
	public static class Analyze implements SqliteStatement {}
	public static class Attach implements SqliteStatement {}
	public static class Detach implements SqliteStatement {}
	public static class Begin implements SqliteStatement {}
	public static class Commit implements SqliteStatement {}
	public static class Reindex implements SqliteStatement {}
	public static class Release implements SqliteStatement {}
	public static class Replace implements SqliteStatement {}
	public static class Rollback implements SqliteStatement {}
	public static class Savepoint implements SqliteStatement {}
	public static class Upsert implements SqliteStatement {}
	public static class Vaccum implements SqliteStatement {}
	
	public static class SqliteException extends RuntimeException {
		private static final long serialVersionUID = -1682872125852054640L;

		public SqliteException(String msg) {
			super(msg);
		}
	}
}
