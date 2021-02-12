package sqlite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sqlite.core.SQLite.SqlComponent;

/* Maps to select-core specifically in syntax diagram. 
 * https://www.sqlite.org/syntax/select-stmt.html
 */
public class SelectCore implements SqlComponent {
	private boolean distinct = false,
			all = false;
	private List<Column> columns = new ArrayList<>();
	private List<String> from = new ArrayList<>();
	private Expression where, having;
	private List<Expression> groupBy = new ArrayList<>();
	private Map<String, String> windows = new HashMap<>();
	private List<Ordering> ordering = new ArrayList<>();
	private Limit limit;
	
	public SelectCore() {
		columns.add(Column.all());
	}
	
	public SelectCore distinct() {
		distinct = true;
		all = false;
		return this;
	}
	
	public SelectCore all() {
		all = true;
		distinct = false;
		return this;
	}
	
	public SelectCore columns(Column...columns) {
		this.columns.clear();
		this.columns.addAll(Arrays.asList(columns));
		return this;
	}
	
	public SelectCore from(String table) {
		from.add(table);
		return this;
	}
	
	public SelectCore where(Expression expr) {
		where = expr;
		return this;
	}
	
	public SelectCore groupBy(Expression...exprs) {
		groupBy.addAll(Arrays.asList(exprs));
		return this;
	}
	
	public SelectCore having(Expression expr) {
		if (groupBy.size() == 0)
			throw new IllegalArgumentException("Need to call groupBy first.");
		having = expr;
		return this;
	}
	
	public SelectCore addWindow(String name, String definition) {
		windows.put(name, definition);
		return this;
	}
	
	public SelectCore orderBy(Ordering...ordering) {
		this.ordering.addAll(Arrays.asList(ordering));
		return this;
	}
	
	public SelectCore limit(Limit limit) {
		this.limit = limit;
		return this;
	}
	
	@Override
	public String asString() {
		StringBuilder sb = new StringBuilder("SELECT ");
		if (distinct) sb.append("DISTINCT ");
		else if (all) sb.append("ALL ");
		if (columns.size() == 0) 
			throw new IllegalStateException("You didn't specify columns to select.");
		for (Column column : columns)
			sb.append(column.asString().trim() + ", ");
		sb.deleteCharAt(sb.length()-1) 		// remove last space
			.deleteCharAt(sb.length()-1)	// remove last comma
			.append(" ");
		if (from.size() != 0) {
			sb.append("FROM ");
			for (String name : from) 
				sb.append(name + ",");
			sb.deleteCharAt(sb.length()-1)
				.append(" ");
		}
		if (where != null) 
			sb.append("WHERE " + where.asString() + " ");
		if (groupBy.size() != 0) {
			sb.append("GROUP BY ");
			for (Expression e : groupBy) 
				sb.append(e.asString() + ",");
			sb.append(" ");
			if (having != null) 
				sb.append("HAVING " + having.asString() + " ");
		}
		if (windows.size() != 0) {
			sb.append("WINDOW ");
			windows.forEach((name, definition) -> sb.append(name + " AS " + definition + ","));
			sb.deleteCharAt(sb.length()-1).append(" ");
		}
		if (ordering.size() != 0) {
			sb.append("ORDER BY ");
			ordering.forEach(ordering -> sb.append(ordering.asString() + ","));
			sb.deleteCharAt(sb.length()-1).append(" ");
		}
		if (limit != null)
			sb.append(limit.asString()+" ");
		return sb.toString().trim();
	}
}