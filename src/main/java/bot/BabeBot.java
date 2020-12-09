package bot;

import bot.hierarchy.Bot;
import bot.hierarchy.MusicBot;
import commands.model.TypingWatchdog;
import lib.Emoji;

public class BabeBot extends MusicBot {
	public static String FAST_PREFIX = "..", BOT_PREFIX = "hey babe ";
	private static String[] prefixes = new String[]{ BOT_PREFIX, FAST_PREFIX, "@Babe" }; 
	
	public BabeBot() {
		super(Bot.BABE);
		exitOnKill = true;
	}

	@Override
	public String[] getPrefixes() {
		return prefixes;
	}
	
	@Override
	public String prefixHelp() {
		return "Hey, give me a command babe."
			+"My fast prefix is \""+FAST_PREFIX
			+"\",\nand slow prefix is \""+BOT_PREFIX
			+"\",\nor just mention me at the start of a message ~~ "+Emoji.HEART_DECORATION;
	}

	@Override 
	protected void preKill(boolean now) throws Exception {
		getReactionsTracker().stopTracking();
		TypingWatchdog.kill();
		Bot.Slaves.killSlaves(now);
		super.preKill(now);
	}
}
