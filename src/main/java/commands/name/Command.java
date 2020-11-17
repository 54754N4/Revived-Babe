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
	PAUSE("pause", "pa", "unpause", "unpa"),
	STOP("stop", "st"),
	NEXT("skip", "next", "ne"),
	PREVIOUS("previous", "prev", "pr"),
	CLEAR("clear", "clr", "cl"),
	REPEAT("repeat", "replay", "loop"),
	VOLUME("volume", "vol", "vo", "v"), 
	DEAFEN("deafen", "def", "undeafen", "undef"),
	MUTE("mute", "mu", "unmute", "unmu"),
	;
	
	public final String[] names;
	
	Command(final String...names) {
		this.names = names;
	}
}