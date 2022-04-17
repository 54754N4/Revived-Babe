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
	HELP("help", "h"), 
	SPEAK("speak", "say", "tts"), 
	TIME("time", "t"), 
	START("start", "s"), 
	KILL("kill", "k"), 
	Log("log", "lo"), 
	RATES("rates", "ra"), 
	TRACE("trace", "what"), 
	TEST_FSM("fsm"),
	TIC_TAC_TOE("tic tac toe", "tictactoe", "ttt"), 
	COMMAND_LINE("command", "cmdline", "cmd"), 
	JAVASCRIPT("javascript", "js"), 
	POLL("poll", "po"), 
	SCREENSHOT("screenshot", "sc"), 
	LYRICS("lyrics", "lr"), 
	REMINDER("reminder", "remind", "rem"),
	DISCONNECT("disconnect", "dc"), 
	REMOVE("remove", "rm"), 
	SERVER("server", "srv", "sv"),
	ENCODE("encode", "enc"), 
	SHUFFLE("shuffle", "sh"), 
	SYNONYMS("synonyms", "syn"), 
	QR_CODE("qr"), 
	PLAYLIST("playlist", "pl"),
	EVAL("eval", "ev");
	
	public static final Logger logger = LoggerFactory.getLogger(Command.class);
	public final String[] names;
	
	static {
		// Static verification of duplications in command names
		Set<String> names = new HashSet<>();
		boolean fail = false;
		long duration = System.currentTimeMillis();
		outside: 
		for (Command command : Command.values()) {
			for (String name : command.names) {
				if (!names.add(name)) {
					System.out.println("Duplicated command name "+name);
					fail = true;
					break outside;
				}
			}
		}
		logger.info("------------------------------------------------------------");
		logger.info(
			"Command name duplicates verification: {}. (duration: {})", 
			fail ? "FAILED" : "PASS", 
			StringLib.millisToTime(System.currentTimeMillis() - duration));
		logger.info("------------------------------------------------------------");
	}
	
	private Command(final String...names) {
		this.names = names;
	}
	
	public String[] getNames() {
		return names;
	}
	
	public static void main(String[] args) {}	// just for static name duplication check
}