package database;

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

public abstract class Query {

	public static void createNewDatabase(String fileName) throws SQLException {
	    String url = "jdbc:sqlite:" + fileName;
	    try (
	    	Connection connection = DriverManager.getConnection(url);
	    	Statement statement = connection.createStatement()
	    ) {
	    	statement.execute("PRAGMA auto_vacuum = FULL;");
	        DatabaseMetaData meta = connection.getMetaData();
	        DBManager.logger.info("The driver name is " + meta.getDriverName());
	        DBManager.logger.info("New database {} has been created.", fileName);
	    }
	}
	
	public static List<String> getGuildTables(String pattern) throws SQLException {
		String sql = "SELECT name FROM sqlite_master WHERE type = 'table' AND name NOT LIKE 'sqlite_%' AND name LIKE ?;";
		try (PreparedStatement statement = DBManager.INSTANCE.getConnection().prepareStatement(sql)) {
			statement.setString(1,  pattern);
			List<String> results = new ArrayList<>();
			try (ResultSet rs = statement.executeQuery()) {
				while (rs.next())
					results.add(rs.getString(1));
				return results;
			}
		}
	}
	
	public static void main(String[] args) throws SQLException, IOException {
		DBManager dbmgr = DBManager.INSTANCE;
		TableManager mgr = dbmgr.manage("Config");
//		mgr.insertOrUpdate(new String[] {"DEFAULT_VOLUME", "DEFAULT_REPEAT_QUEUE", "DEFAULT_REPEAT_SONG"}, new Object[] {50, true, false});
		System.out.println(mgr.selectAll());
		dbmgr.close();
	}
}
