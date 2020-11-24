package bot;

import bot.model.Bot;
import bot.model.MusicBot;

public class SlaveBot extends MusicBot {
	private String[] prefixes;
	private int number;
	
	public SlaveBot() throws NoMoreSlavesException {
		super(Bot.Slaves.pop());
		if (bot == null)
			throw new NoMoreSlavesException("No more slaves to launch!");
		number = bot.ordinal()-2;	// although 3 before, start numbering from 1
		prefixes = new String[] {
				number+".", 
				"hey slave "+number, 
				"@Slave "+number
		};
	}

	@Override
	public String[] getPrefixes() {
		return prefixes;
	}

	@Override
	public String prefixHelp() {
		return "Give me a command."
				+"My fast prefix is \""+prefixes[0]
				+"\",\nand slow prefix is \""+prefixes[1]
				+"\",\nor just mention me at the start of a message.";
	}

	public static class NoMoreSlavesException extends Exception {
		private static final long serialVersionUID = -9089133642585266972L;

		public NoMoreSlavesException(String msg) {
			super(msg);
		}
	}
}
