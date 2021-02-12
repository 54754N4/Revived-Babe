package sqlite;

import sqlite.core.SQLite.SqlComponent;

public abstract class Constraint implements SqlComponent {
	protected String name;
	
	public Constraint name(String name) {
		this.name = name;
		return this;
	}
	
	protected String getName() {
		return name != null ? "CONSTRAINT "+name+" ": "";
	}
	
	@Override
	public String asString() {
		return getName();
	}
}