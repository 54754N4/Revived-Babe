package database;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBManager implements Closeable {
	static final Logger logger = LoggerFactory.getLogger(DBManager.class);
	public static final String BABE_DATABASE = "./database/bot-data.db";
	public static final String DB_URL_ACCESS_FORMAT = "jdbc:sqlite:%s";
	public static DBManager INSTANCE;
	
	static {
		try {
			INSTANCE = new DBManager();
		} catch (SQLException e) {
			logger.error("Could not create db manager for babe", e);
		}
	}
	
	private String database;
	private Connection connection;
	
	private DBManager() throws SQLException {
		this(BABE_DATABASE);
	}
	
	public DBManager(String database) throws SQLException {
		this.database = database;
	}
	
	public Connection getConnection() throws SQLException {
		return connection == null ?
			connection = DriverManager.getConnection(String.format(DB_URL_ACCESS_FORMAT, database)) :
			connection;
	}
	
	public TableManager manage(String table) throws SQLException {
		return new TableManager(getConnection(), database, table);
	}
	
	public TableManager manage(String database, String table) throws SQLException {
		return new TableManager(getConnection(), database, table);
	}
	
	@Override
	public void close() throws IOException {
		try {
			connection.close();
		} catch (SQLException e) {
			logger.error("Could not close "+getClass()+"'s connection.", e);
		}
	}
}