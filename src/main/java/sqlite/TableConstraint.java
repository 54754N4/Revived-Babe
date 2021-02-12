package sqlite;

import java.util.ArrayList;
import java.util.List;

import sqlite.model.Conflict;
import sqlite.model.TransactionAction;

/* Table constraints */
public class TableConstraint extends Constraint {	// https://www.sqlite.org/syntaxdiagrams.html#table-constraint
	
	public static class PrimaryKeyOrUnique extends TableConstraint {
		private boolean primaryKey = true;
		private Conflict conflict = Conflict.NONE;
		private List<IndexedColumn> columns = new ArrayList<>();
		
		public PrimaryKeyOrUnique columns(IndexedColumn...columns) {
			for (IndexedColumn column : columns)
				this.columns.add(column);
			return this;
		}
		
		public PrimaryKeyOrUnique primaryKey() {
			primaryKey = true;
			return this;
		}
		
		public PrimaryKeyOrUnique unique() {
			primaryKey = false;
			return this;
		}
		
		@Override
		public String asString() {
			StringBuilder sb = new StringBuilder(getName());
			sb.append(primaryKey ? "PRIMARY KEY " : "UNIQUE ");
			sb.append("(");
			if (columns.size() != 0) {
				columns.forEach(col -> sb.append(col.asString()+", "));
				sb.deleteCharAt(sb.length()-1).deleteCharAt(sb.length()-1);
			}
			sb.append(")\n");
			sb.append(conflict.toString());
			return sb.toString();
		}
	}
	
	public static class Check extends TableConstraint {
		private Expression expr;
		
		public Check(Expression expr) {
			this.expr = expr;
		}
		
		@Override
		public String asString() {
			return getName() + "CHECK (" + expr.asString() + ")";
		}
	}
	
	public static class ForeignKey extends TableConstraint {
		private List<String> columns;
		private ColumnConstraint.ForeignKey foreignKey; // FK clause
		
		public ForeignKey(String foreignTable) {
			columns = new ArrayList<>();
			foreignKey = new ColumnConstraint.ForeignKey(foreignTable);
		}
		
		public ForeignKey columns(String...columns) {
			for (String column : columns)
				this.columns.add(column);
			return this;
		}
		
		public ForeignKey references(String...columns) {
			foreignKey.references(columns);
			return this;
		}
		
		public ForeignKey onDelete(TransactionAction action) {
			foreignKey.onDelete(action);
			return this;
		}
		
		public ForeignKey onUpdate(TransactionAction action) {
			foreignKey.onUpdate(action);
			return this;
		}
		
		public ForeignKey match(String name) {
			foreignKey.match(name);
			return this;
		}
		
		public ForeignKey not() {
			foreignKey.not();
			return this;
		} 
		
		public ForeignKey deferrable() {
			foreignKey.deferrable();
			return this;
		}
		
		public ForeignKey initiallyDeferred() {
			foreignKey.initiallyDeferred();
			return this;
		}
		
		public ForeignKey initiallyImmediate() {
			foreignKey.initiallyImmediate();
			return this;
		}
		
		@Override
		public String asString() {
			StringBuilder sb = new StringBuilder(getName());
			sb.append("FOREIGN KEY (");
			if (columns.size() == 0)
				throw new IllegalArgumentException("You should specify at least 1 column as foreign key in this table");
			columns.forEach(key -> sb.append(key+", "));
			sb.deleteCharAt(sb.length()-1)		// remove last space
				.deleteCharAt(sb.length()-1)	// remove last comma
				.append(") ")
				.append(foreignKey.asString());	// now append column's foreign-key clause
			return sb.toString().trim();
		}
	}
}