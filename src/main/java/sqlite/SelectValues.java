package sqlite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import sqlite.core.SQLite.SqlComponent;

public class SelectValues implements SqlComponent {
	private List<Expression> values = new ArrayList<>();
	
	public SelectValues values(Expression...values) {
		this.values.addAll(Arrays.asList(values));
		return this;
	}
}
