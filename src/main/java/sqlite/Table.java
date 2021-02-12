package sqlite;

import sqlite.TableConstraint.Check;
import sqlite.TableConstraint.ForeignKey;
import sqlite.TableConstraint.PrimaryKeyOrUnique;

public class Table {
	
	public static PrimaryKeyOrUnique primaryKey() {
		return new PrimaryKeyOrUnique();
	}
	
	public static PrimaryKeyOrUnique unique() {
		return new PrimaryKeyOrUnique().unique();
	}
	
	public static Check check(Expression expr) {
		return new Check(expr);
	}
	
	public static ForeignKey foreignKey(String foreignTable) {
		return new ForeignKey(foreignTable);
	}
}