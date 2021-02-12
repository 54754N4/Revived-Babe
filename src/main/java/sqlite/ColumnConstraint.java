package sqlite;

import java.util.ArrayList;
import java.util.List;

import sqlite.model.Conflict;
import sqlite.model.TransactionAction;

public class ColumnConstraint extends Constraint {	// https://www.sqlite.org/syntaxdiagrams.html#column-constraint
	
	public static class PrimaryKey extends ColumnConstraint {
		private boolean asc = false, desc = false, autoincrement = false;
		private Conflict conflict = Conflict.NONE;
		
		public PrimaryKey ascending() {
			asc = true;
			desc = false;
			return this;
		}
		
		public PrimaryKey descending() {
			asc = false;
			desc = true;
			return this;
		}
		
		public PrimaryKey autoincrement() {
			autoincrement = true;
			return this;
		}
		
		public PrimaryKey onConflict(Conflict conflict) {
			this.conflict = conflict;
			return this;
		}
		
		@Override
		public String asString() {
			return getName() +
					"PRIMARY KEY " + (asc ? "ASC " : desc ? "DESC ": "")
					+ conflict.toString() 
					+ (autoincrement ? "AUTOINCREMENT" : "");
		}
	}
	
	public static class NotNull extends ColumnConstraint {
		private Conflict conflict = Conflict.NONE;
		
		public NotNull onConflict(Conflict conflict) {
			this.conflict = conflict;
			return this;
		}
		
		@Override
		public String asString() {
			return getName() + "NOT NULL" + (conflict != Conflict.NONE ? " " + conflict.toString() : "");
		}
	}
	
	public static class Unique extends ColumnConstraint {
		private Conflict conflict = Conflict.NONE;
		
		public Unique onConflict(Conflict conflict) {
			this.conflict = conflict;
			return this;
		}
		
		@Override
		public String asString() {
			return getName() + "UNIQUE " + conflict.toString();
		}
	}
	
	public static class Check extends ColumnConstraint {
		private Expression expr;
		
		public Check(Expression expr) {
			this.expr = expr;
		}
		
		@Override
		public String asString() {
			return getName() + "CHECK (" + expr.asString() + ")";
		}
	}
	
	public static class Default extends ColumnConstraint {
		private Expression expr;
		private LiteralValue literal;
		private int signed;
		
		public Default(Expression expr) {
			this.expr = expr;
		}
		
		public Default(LiteralValue literal) {
			this.literal = literal;
		}
		
		public Default(int signed) {
			this.signed = signed;
		}
		
		@Override
		public String asString() {
			if (expr != null)
				return getName() + "DEFAULT (" + expr.asString() + ")";
			else if (literal != null)
				return getName() + "DEFAULT " + literal.asString();
			return getName() + "DEFAULT " + signed;
		}
	}
	
	public static class Collate extends ColumnConstraint {
		private String collationName;
		
		public Collate(String collationName) {
			this.collationName = collationName;
		}
		
		@Override
		public String asString() {
			return getName() + "COLLATE " + collationName;
		}
	}
	
	// https://www.sqlite.org/syntax/foreign-key-clause.html
	public static class ForeignKey extends ColumnConstraint { // https://www.sqlite.org/syntaxdiagrams.html#foreign-key-clause
		private boolean not = false, 
				deferrable = false, 
				initiallyDeferred = false,
				initiallyImmediate = false;
		private String foreignTable;
		private List<String> columns = new ArrayList<>();
		private List<OnTransaction> transactionsHandlers = new ArrayList<>();
		
		public ForeignKey(String foreignTable) {
			this.foreignTable = foreignTable;
		}
		
		public List<String> getColumns() {
			return columns;
		}
		
		public ForeignKey references(String...columns) {
			for (String column : columns)
				this.columns.add(column);
			return this;
		}
		
		public ForeignKey onDelete(TransactionAction action) {
			transactionsHandlers.add(new OnTransaction(true, action));
			return this;
		}
		
		public ForeignKey onUpdate(TransactionAction action) {
			transactionsHandlers.add(new OnTransaction(false, action));
			return this;
		}
		
		public ForeignKey match(String name) {
			transactionsHandlers.add(new Match(name));
			return this;
		}
		
		public ForeignKey not() {
			not = true;
			return this;
		} 
		
		public ForeignKey deferrable() {
			deferrable = true;
			return this;
		}
		
		public ForeignKey initiallyDeferred() {
			initiallyDeferred = true;
			initiallyImmediate = false;
			return this;
		}
		
		public ForeignKey initiallyImmediate() {
			initiallyImmediate = true;
			initiallyDeferred = false;
			return this;
		}
		
		@Override
		public String asString() {
			StringBuilder sb = new StringBuilder();
			sb.append("REFERENCES ").append(foreignTable+" ");
			if (columns.size() != 0) {
				sb.append("(");
				columns.forEach(col -> sb.append(col+","));
				sb.deleteCharAt(sb.length()-1);	// remove last extra comma
				sb.append(")");
			} else 
				throw new IllegalArgumentException("Please specify which columns are referenced");
			transactionsHandlers.forEach(transaction -> sb.append(transaction.asString()+"\n"));
			if (deferrable) {
				if (not)
					sb.append("NOT ");
				sb.append("DEFERRABLE ");
				if (initiallyDeferred)
					sb.append("INITIALLY DEFERRED");
				if (initiallyImmediate)
					sb.append("INITIALLY IMMEDIATE");
			}
			return getName() + sb.toString();
		}
	}
	
	public static class As extends ColumnConstraint {
		private boolean generatedAlways = false, 
				stored = false, 
				virtual = false;
		private Expression expr;
		
		public As(Expression expr) {
			this.expr = expr;
		}
		
		public As generatedAlways() {
			generatedAlways = true;
			return this;
		}
		
		public As stored() {
			stored = true;
			virtual = false;
			return this;
		}
		
		public As virtual() {
			virtual = true;
			stored = false;
			return this;
		}
		
		@Override
		public String asString() {
			return getName() +
				(generatedAlways ? "GENERATED ALWAYS " : "")
				+ "AS (" + expr.toString() + ")"
				+ (stored ? "STORED" : "")
				+ (virtual ? "VIRTUAL" : "");
		}
	}
}