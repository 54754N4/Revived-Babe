package commands.name;

public enum Command {
	EXIT("exit", "quit"), 
	TEST("test"),
	UPDATE("update", "upd"),
	CATS("cats"),
	ECHO("echo", "e"),
	HIGHLIGHT("highlight", "hl"),
	PING("ping", "pi"),
	PRAISE("praise", "pr"),
	JOIN("join", "j"),
	LEAVE("leave", "le"),
	PLAY("play", "p"), 
	VOLUME("volume", "vol", "vo", "v"), 
	PAUSE("pause", "pa", "unpause", "un"),
	REPEAT("repeat", "replay", "loop")
	;
	
	public final String[] names;
	
	Command(final String...names) {
		this.names = names;
	}
}
