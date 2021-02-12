package sqlite;

import sqlite.core.SQLite.SqlComponent;
import sqlite.model.TransactionAction;

public class OnTransaction implements SqlComponent {
	private boolean delete;	// or update
	private TransactionAction action;
	
	public OnTransaction() {}
	
	public OnTransaction(boolean delete, TransactionAction action) {
		this.delete = delete;
		this.action = action;
	}
	
	@Override
	public String asString() {
		return (delete ? "ON DELETE " : "ON UPDATE ")
			+ action.name().replaceAll("_", " ");
	}
}