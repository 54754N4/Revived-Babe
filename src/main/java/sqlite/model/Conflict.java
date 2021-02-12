package sqlite.model;

public enum Conflict {
	NONE, ROLLBACK, ABORT, FAIL, IGNORE, REPLACE;
	
	@Override
	public String toString() {
		return this == NONE ? "" : "ON CONFLICT "+name();
	}
}