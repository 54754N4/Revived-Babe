package sqlite;

import sqlite.core.SQLite.SqliteStatement;

public class CompoundOperator implements SqliteStatement {
	private boolean union = false, all = false, // all applies to union only
			intersect = false, except = false;
	
}
