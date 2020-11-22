package commands.name;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lib.StringLib;

public enum Command {
	EXIT("exit", "quit"), 
	TEST("test"),
	UPDATE("update", "upd"),
	CATS("cats"),
	ECHO("echo", "e"),
	HIGHLIGHT("highlight", "hl"),
	PING("ping", "pi"),
	PRAISE("praise"),
	JOIN("join", "j"),
	LEAVE("leave", "le"),
	PLAY("play", "p"),
	PAUSE("pause", "pa", "unpause", "unpa"),
	STOP("stop", "st"),
	SEEK("seek", "se"),
	NEXT("skip", "next", "ne"),
	PREVIOUS("previous", "prev", "pr"),
	CLEAR("clear", "clr", "cl"),
	REPEAT("repeat", "replay", "loop"),
	VOLUME("volume", "vol", "vo", "v"), 
	DEAFEN("deafen", "def", "undeafen", "undef"),
	MUTE("mute", "mu", "unmute", "unmu"),
	INSULT("insult", "swear"),
	CHUCK_NORRIS("chuck", "norris"), 
	FOOD("food", "fo"),
	RECIPE("recipe", "re"), 
	IP("geolocate", "ip"),
	LS("list", "ls"), 
	PRUNE("prune", "delete", "del"), 
	RESTART("restart", "res"),
	GET("get", "g"),
	HELP("help", "h");
	
	public static final Logger logger = LoggerFactory.getLogger(Command.class);
	public final String[] names;
	
	static {
		// Static verification of duplications in command names
		long duration = System.currentTimeMillis();
		Set<String> names = new HashSet<>();
		outside: 
		for (Command command : Command.values()) {
			for (String name : command.names) {
				if (!names.add(name)) {
					logger.error("Duplicated command name "+name, new IllegalArgumentException());
					break outside;
				}
			}
		}
		logger.info("Verification duration: {}", StringLib.millisToTime(System.currentTimeMillis() - duration));
	}
	
	Command(final String...names) {
		this.names = names;
	}
}