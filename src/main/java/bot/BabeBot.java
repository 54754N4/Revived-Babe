package bot;

import backup.Reminders;
import backup.SpellingCorrector;
import bot.hierarchy.Bot;
import bot.hierarchy.MusicBot;
import commands.model.Invoker;
import commands.model.ThreadsManager;
import commands.model.TypingWatchdog;
import lib.Emoji;
import lib.scrape.Browser;

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
			+"\nMy fast prefix is \""+FAST_PREFIX
			+"\",\nand slow prefix is \""+BOT_PREFIX
			+"\",\nor just mention me at the start of a message ~~ "+Emoji.HEART_DECORATION;
	}

	@Override 
	protected void preKill(boolean now) throws Exception {
		safeRunChain(
				getReactionsTracker()::stopTracking,
				() -> ThreadsManager.kill(now),
				TypingWatchdog::kill,
				Reminders::backup,
				() -> SpellingCorrector.serialize(Invoker.getCorrector()),
				() -> Bot.killAll(now),
				() -> Browser.getInstance().kill());
		super.preKill(now);
	}
}
