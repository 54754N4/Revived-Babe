package sqlite;

import sqlite.core.SQLite.SqlComponent;

public class Column implements SqlComponent {
	
	public static ExpressionColumn name(String expression) {
		return new ExpressionColumn(expression);
	}
	
	public static AllColumn all() {
		return new AllColumn();
	}
	
	public static AllReferencedColumn allFrom(String table) {
		return new AllReferencedColumn(table);
	}
	
	public static class ExpressionColumn extends Column {
		private Expression expr;
		private boolean as = false;
		private String alias = "";
		
		public ExpressionColumn(String expr) {
			this.expr = new Expression(expr);
		}
		
		public Column.ExpressionColumn alias(String alias) {
			this.alias = alias;
			return this;
		}
		
		public Column.ExpressionColumn as(String alias) {
			as = true;
			return alias(alias);
		}
		
		@Override
		public String asString() {
			return expr.asString() + (as ? " AS " : " ") + alias + " ";
		}
	}
	
	public static class AllColumn extends Column {
		@Override
		public String asString() {
			return "* ";
		}
	}
	
	public static class AllReferencedColumn extends Column {
		private String reference;
		
		public AllReferencedColumn(String table) {
			reference = table;
		}
		
		@Override
		public String asString() {
			return reference + ".* ";
		}
	}
}