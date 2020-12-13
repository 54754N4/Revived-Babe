package game.tictactoe;

import java.util.function.Consumer;

public final class Game {
	public static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
	public final int size;
	public final Type[][] map;
	
	public Game(int size) {
		this.size = size;
		map = new Type[size][size];
		for (int i=0; i<size; ++i)
			for (int j=0; j<size; ++j)
				map[i][j] = Type.NONE;
	}
	
	public int row(String x) {
		return ALPHABET.indexOf(x);
	}
	
	private String extractPrefix(String coords) {
		return coords.replaceAll("[0-9]+", "");
	}
	
	private int extractPostfix(String coords) {
		return Integer.parseInt(coords.replaceAll("[^0-9]+", ""));
	}
	
	public boolean isValid(String coords) {
		return coords.matches("[a-zA-Z]+[0-9]+");
	}
	
	public boolean set(String coords, Type type) {
		int x = row(extractPrefix(coords)), y = extractPostfix(coords);
		if (x == -1 || y<0 || y>size-1)
			return false;
		map[x][y] = type;
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
}