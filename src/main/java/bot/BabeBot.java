package bot;

import bot.model.Bot;
import bot.model.MusicBot;
import lib.Emoji;
import lib.messages.ReactionsTracker;

public class BabeBot extends MusicBot {
	public static String FAST_PREFIX = "..", BOT_PREFIX = "hey babe ";
	
	public BabeBot() {
		super(Bot.BABE);
		exitOnKill = true;
	}

	@Override
	public String[] getPrefixes() {
		return new String[]{ BOT_PREFIX, FAST_PREFIX, "@Babe" };
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
		ReactionsTracker.INSTANCE.stopTracking();
		super.preKill(now);
	}
}
