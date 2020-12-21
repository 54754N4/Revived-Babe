package game.tictactoe;

import java.util.function.Consumer;

public final class Game {
	public static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
	public final int size;
	public Type[][] map;
	
	public Game(int size) {
		this.size = size;
		reset();
	}
	
	public void reset() {
		map = new Type[size][size];
		for (int i=0; i<size; ++i)
			for (int j=0; j<size; ++j)
				map[i][j] = Type.NONE;
	}
	
	public int row(String x) {
		return ALPHABET.indexOf(x.toLowerCase());
	}
	
	private String extractPrefix(String coords) {
		return coords.replaceAll("[0-9]+", "");
	}
	
	private int extractPostfix(String coords) {
		return Integer.parseInt(coords.replaceAll("[^0-9]+", ""));
	}
	
	public boolean isValid(String coords) {
		if (!coords.matches("[a-zA-Z]+[0-9]+"))
			return false;
		try {
			Point p = convert(coords);
			return !(p.x == -1 || p.y<0 || p.y>size-1 || map[p.x][p.y] != Type.NONE);
		} catch (Exception e) {
			return false;
		}
	}
	
	public Point convert(String coords) {
		return new Point(row(extractPrefix(coords)), extractPostfix(coords));
	}
	
	public Type get(String coords) {
		Point p = convert(coords);
		return (p.x == -1 || p.y<0 || p.y>size-1) ? null : map[p.x][p.y];
	}
	
	public boolean set(String coords, Type type) {
		Type cell = get(coords);
		if (cell != Type.NONE)
			return false;
		Point p = convert(coords);
		map[p.x][p.y] = type;
		return true;
	}
	
	public boolean checkFull() {
		for (Type[] row : map)
			for (Type type : row)
				if (type == Type.NONE)
					return false;
		return true;
	}
	
	public Type checkWinner() {
		boolean winRow = true,
				winCol = true,
				winDiagonal = true,
				winRDiagonal = true;
		// Check diag + reverse diag simultaneously
		for (int i=0, j=0, ri=size-1; i<size && j<size; i++, j++, ri--) {
			if (winDiagonal && i+1<size && j+1<size)
				winDiagonal = winDiagonal && map[i][j] != Type.NONE && map[i][j] == map[i+1][j+1];
			if (winRDiagonal && i+1<size && ri-1>=0)
				winRDiagonal = winRDiagonal && map[i][ri] != Type.NONE && map[i][ri] == map[i+1][ri-1]; 
		}
		if (winDiagonal)
			return map[0][0];
		else if (winRDiagonal)
			return map[size-1][0];
		// Check row + col simultaneously
		for (int i=0; i<size; i++) {
			for (int j=0; j<size; j++) {
				if (j+1 < size) {
					winRow = winRow && map[i][j] != Type.NONE && map[i][j] == map[i][j+1];
					winCol = winCol && map[j][i] != Type.NONE && map[j][i] == map[j+1][i];
				}
				if (j == size - 2 && winRow)
					return map[i][j];
				if (j == size - 2 && winCol)
					return map[j][i];
			}
			winRow = true;
			winCol = true;
		}
		return Type.NONE;
	}
	
	public Game forEachRow(Consumer<Type[]> consumer) {
		for (Type[] row : map) 
			consumer.accept(row);
		return this;
	}
	
	public Game forEach(Consumer<Type> consumer) {
		return forEach(consumer, null);
	}
	
	public Game forEach(Consumer<Type> consumer, Consumer<Void> rowEndConsumer) {
		for (Type[] row : map) {
			for (Type cell : row)
				consumer.accept(cell);
			if (rowEndConsumer != null)
				rowEndConsumer.accept(null);
		}
		return this;
	}
	
	public static class Point {
		public final int x, y;
		
		public Point(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}
}