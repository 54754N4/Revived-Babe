package database;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBManager implements Closeable {
	private static final Logger logger = LoggerFactory.getLogger(DBManager.class);
	public static final String BABE_DATABASE = "bot-data.db";
	public static final String DB_URL_ACCESS_FORMAT = "jdbc:sqlite:./database/%s";
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
	
	public List<String> getGuildTables(String pattern) throws SQLException {
		String sql = "SELECT name FROM sqlite_master WHERE type = 'table' AND name NOT LIKE 'sqlite_%' AND name LIKE ?;";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1,  pattern);
			List<String> results = new ArrayList<>();
			try (ResultSet rs = statement.executeQuery()) {
				while (rs.next())
					results.add(rs.getString(1));
				return results;
			}
		}
	}
	
	public static void createNewDatabase(String fileName) throws SQLException {
	    String url = "jdbc:sqlite:" + fileName;
        try (
        	Connection connection = DriverManager.getConnection(url);
        	Statement statement = connection.createStatement()
        ) {
        	statement.execute("PRAGMA auto_vacuum = FULL");
            DatabaseMetaData meta = connection.getMetaData();
            logger.info("The driver name is " + meta.getDriverName());
            logger.info("New database {} has been created.", fileName);
        }
    }
	
	public static void main(String[] args) throws SQLException, IOException {
		DBManager dbmgr = DBManager.INSTANCE;
		TableManager mgr = dbmgr.manage("Config");
		mgr.insertOrUpdate(new String[] {"DEFAULT_VOLUME", "DEFAULT_REPEAT_QUEUE", "DEFAULT_REPEAT_SONG"}, new Object[] {50, true, false});
		System.out.println(mgr.selectAll());
		dbmgr.close();
	}
}