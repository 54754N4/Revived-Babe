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
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TableManager implements Closeable {
	private static final Logger logger = LoggerFactory.getLogger(TableManager.class);
	public static final String BABE_DATABASE = "bot-data.db";
	public static final String DB_URL_ACCESS_FORMAT = "jdbc:sqlite:.\\database\\%s";
	
	private final String table;
	private final Connection connection;

	public static void main(String[] args) throws SQLException, IOException {
		TableManager mgr = new TableManager("Config");
		mgr.insertOrUpdate(new String[] {"DEFAULT_VOLUME", "DEFAULT_REPEAT_QUEUE", "DEFAULT_REPEAT_SONG"}, new Object[] {50, true, false});
		System.out.println(mgr.selectAll());
		mgr.close();
	}
	
	protected TableManager(String table) throws SQLException {
		this(BABE_DATABASE, table);
	}
	
	protected TableManager(String database, String table) throws SQLException {
		this.table = table;
		connection = DriverManager.getConnection(String.format(DB_URL_ACCESS_FORMAT, database));
		try { createTableIfNotExists(); }
		catch (SQLException databaseNonExistant) {
			try { // and now try again after creating database
				createNewDatabase(database);
				createTableIfNotExists(); 	
			} catch (SQLException e) { 
				logger.error("Could not create new database: "+database+" and table: "+table, e); 
			}
		}
	}
	
	@Override
	public void close() throws IOException {
		try {
			connection.close();
		} catch (SQLException e) {
			logger.error("Could not close "+getClass()+"'s connection.", e);
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
            logger.info("New database '"+fileName+"' has been created.");
        }
    }
	
	public TableManager createTableIfNotExists() throws SQLException {
		String sql = String.format("CREATE TABLE IF NOT EXISTS %s (key TEXT UNIQUE, value TEXT)", table);
		try (Statement statement = connection.createStatement()) { statement.execute(sql); }
		return this;
	}

	public Map<String, String> selectAll() throws SQLException {
		return select("");
	} 
	
	public Map<String, String> select(Object condition) throws SQLException {
		String sql = "SELECT key, value FROM "+table;
		if (condition != "") 
			sql += " WHERE key LIKE ?;";
		Map<String, String> pairs = new HashMap<>();
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			if (condition != "") 
				statement.setString(1, condition.toString());
			ResultSet rs = statement.executeQuery();
			while (rs.next())
				pairs.put(rs.getString("key"), rs.getString("value"));
			rs.close();
		}
		return pairs;
	}
	
	public TableManager insert(String key, Object value) throws SQLException {
		return insert(new String[] {key}, new Object[] {value});
	}
	
	public TableManager insert(String[] keys, Object[] values) throws SQLException {
		StringBuilder sb = new StringBuilder().append("INSERT INTO "+table+" (key,value) VALUES ");
		for (int i=0; i<keys.length; i++) 
			sb.append("(?,?),");
		String sql = sb.deleteCharAt(sb.length()-1).append(";").toString();
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			for (int i=0, count=1; i<keys.length; i++, count+=2) {
				statement.setString(count, keys[i]);
				statement.setString(count+1, values[i].toString());
			}
			statement.executeUpdate();
		}
		return this;
	}
	
	public TableManager insertOrUpdate(String key, Object value) throws SQLException {
		return insertOrUpdate(new String[] {key}, new Object[] {value});
	}
	
	public TableManager insertOrUpdate(String[] keys, Object[] values) throws SQLException {
		try { 
			insert(keys, values); 
		} catch (SQLException keyAlreadyExists) {
			logger.info("Couldn't insert, updating instead.");
			update(keys, values); 
		}
		return this;
	}
	
	public TableManager update(String key, Object value) throws SQLException {
		return update(new String[] {key}, new Object[] {value});
	}
	
	public TableManager update(String[] keys, Object[] values) throws SQLException {
		String sql =  "UPDATE "+table+" SET value = ? WHERE key = ?;";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			for (int i=0; i<keys.length; i++) {
				statement.setString(1,  values[i].toString());
				statement.setString(2, keys[i]);
				statement.addBatch();
			}
			statement.executeBatch(); 
		}
		return this;
	}
	
	public int count() throws SQLException {
		String sql = "SELECT Count(*) FROM "+table+";";
		try (
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery(sql);
		) {
			return rs.getInt(1);
		}
	}
	
	// Retrieves a type, and if it doesnt exist, inserts a default value
	public <T> String retrieve(String key, T onError) throws SQLException {
		String value = select(key).get(key); 
		if (value == null) {
			value = onError.toString();
			insert(key, value);
		}
		return value;
	}
	
	public int retrieveInt(String key, int onError) throws NumberFormatException, SQLException {
		return Integer.parseInt(retrieve(key, onError));
	}
	
	public long retrieveLong(String key, long onError) throws NumberFormatException, SQLException {
		return Long.parseLong(retrieve(key, onError));
	}
	
	public boolean retrieveBool(String key, boolean onError) throws SQLException {
		return Boolean.parseBoolean(retrieve(key, onError));
	}
	
	public long delete(String pattern) throws SQLException {
		long before = count();
		String sql = "DELETE from "+table+" WHERE key LIKE ?;";
		try (PreparedStatement statement = connection.prepareStatement(sql)) { 
			statement.setString(1,  pattern);
			statement.executeUpdate();
			return before - count();
		}
	}
	
	public TableManager drop() throws SQLException {
		String sql = "DROP TABLE "+table;
		try (Statement statement = connection.createStatement()) { statement.execute(sql); }
		return this;
	}
	
	public TableManager vacuum() throws SQLException {
		String sql = "VACUUM";
		try (Statement statement = connection.createStatement()) { statement.execute(sql); }
		return this;
	}
	
	public TableManager reset() throws SQLException {
		drop();
		createTableIfNotExists();
		return this;
	}
}