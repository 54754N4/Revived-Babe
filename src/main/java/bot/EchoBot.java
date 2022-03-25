package bot;

import javax.annotation.Nullable;

import audio.handlers.EchoHandler;
import bot.hierarchy.Bot;
import bot.hierarchy.MusicBot;
import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.audio.factory.IAudioSendFactory;

public class EchoBot extends MusicBot {
	private static final String[] prefixes = new String[] { "e.", "hey echo ", "@Echo" }; 
	private EchoHandler handler;
	
	public EchoBot() {
		super(Bot.ECHO);
		handler = new EchoHandler();
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
	
	@Override
	public @Nullable IAudioSendFactory getAudioSendFactory() {
		return null;	// don't use JDA-NAS
	}
	
	@Override
	public AudioSendHandler getAudioSendHandler() {
		return handler;
	}
	
	@Override
	public AudioReceiveHandler getAudioReceiveHandler() {
		return handler;
	}

}
