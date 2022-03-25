package game.tictactoe;

import lib.Emoji;

public enum Type { 
	CROSS(Emoji.CROSS_MARK), CIRCLE(Emoji.HOLLOW_RED_CIRCLE), NONE("_"); 

	public final String string;
	
	private Type(Emoji emoji) {
		string = emoji.toString();
	}
	
	private Type(String string) {
		this.string = string;
	}
	
	@Override
	public String toString() {
		return string;
	}
}