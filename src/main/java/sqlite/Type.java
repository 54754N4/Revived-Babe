package sqlite;

import sqlite.core.SQLite.SqlComponent;
import sqlite.model.Affinity;

public class Type implements SqlComponent {
	public static final int 
		UNINITIALISED = -1, 
		DEFAULT_SIZE = 255,
		DEFAULT_PRECISION = 2;
	public static final Type
		// Integer
		INT = new Type("INT", Affinity.INTEGER),
		INTEGER = new Type("INTEGER", Affinity.INTEGER),
		TINYINT = new Type("TINYINT", Affinity.INTEGER),
		SMALLINT = new Type("SMALLINT", Affinity.INTEGER),
		MEDIUMINT = new Type("MEDIUMINT", Affinity.INTEGER),
		BIGINT = new Type("BIGINT", Affinity.INTEGER),
		UNSINGED_BIG_INT = new Type("UNSINGED BIG INT", Affinity.INTEGER),
		INT2 = new Type("INT2", Affinity.INTEGER),
		INT8 = new Type("INT8", Affinity.INTEGER),
		// Text
		TEXT = new Type("TEXT", Affinity.TEXT),
		CLOB = new Type("CLOB", Affinity.TEXT),
		// None
		BLOB = new Type("BLOB", Affinity.NONE),
		NONE = new Type("NONE", Affinity.NONE),
		// Real
		REAL = new Type("REAL", Affinity.REAL),
		DOUBLE = new Type("DOUBLE", Affinity.REAL),
		DOUBLE_PRECISION = new Type("DOUBLE PRECISION", Affinity.REAL),
		FLOAT = new Type("FLOAT", Affinity.REAL),
		// Numeric
		NUMERIC = new Type("NUMERIC", Affinity.NUMERIC),
		BOOLEAN = new Type("BOOLEAN", Affinity.NUMERIC),
		DATE = new Type("DATE", Affinity.NUMERIC),
		DATETIME = new Type("DATETIME", Affinity.NUMERIC);
	public static final Type.SizeableType 
		CHAR = new SizeableType("CHAR", Affinity.TEXT, DEFAULT_SIZE),
		VARCHAR = new SizeableType("VARCHAR", Affinity.TEXT, DEFAULT_SIZE),
		VARYING_CHARACTER = new SizeableType("VARYING CHARACTER", Affinity.TEXT, DEFAULT_SIZE),
		NCHAR = new SizeableType("NCHAR", Affinity.TEXT, DEFAULT_SIZE),
		NATIVE_CHARACTER = new SizeableType("NATIVE CHARACTER", Affinity.TEXT, DEFAULT_SIZE),
		NVARCHAR = new SizeableType("NVARCHAR", Affinity.TEXT, DEFAULT_SIZE);
	public static final Type.PrecisionType
		DECIMAL = new PrecisionType("DECIMAL", DEFAULT_SIZE, DEFAULT_PRECISION);
		
	
	protected final String name;
	protected Affinity affinity;
	
	public Type(String name, Affinity affinity) {
		this.name = name;
		this.affinity = affinity;
	}
	
	@Override
	public String asString() {
		return name;
	}
	
	public static class SizeableType extends Type {
		protected int size;
		
		public SizeableType(String name, Affinity type, int size) {
			super(name, type);
			this.size = size;
		}
		
		public Type.SizeableType size(int size) {
			return new SizeableType(name, affinity, size);
		}

		public int getSize() {
			return size;
		}
		
		@Override
		public String asString() {
			return name + "(" + size + ")";
		}
	}
	
	public static class PrecisionType extends Type.SizeableType {
		protected Integer precision;
		
		public PrecisionType(String name, int size, int precision) {
			super(name, Affinity.NUMERIC, size);
			this.precision = precision;
		}
		
		@Override
		public Type.PrecisionType size(int size) {
			return new PrecisionType(name, size, precision);
		}
		
		public Type.PrecisionType precision(int precision) {
			return new PrecisionType(name, size, precision);
		}
		
		public int getPrecision() {
			return precision;
		}
		
		@Override
		public String asString() {
			return name + "(" + size + "," + precision + ")";
		}
	}
}