package bot;

import audio.handlers.EchoHandler;
import bot.model.Bot;
import bot.model.MusicBot;
import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.audio.AudioSendHandler;

public class EchoBot extends MusicBot {
	private EchoHandler handler;
	
	public EchoBot() {
		super(Bot.ECHO);
		handler = new EchoHandler();
	}

	@Override
	public String[] getPrefixes() {
		return new String[] {""};
	}

	@Override
	public String prefixHelp() {
		return "This bot has no commands yet.";
	}
	
	@Override
	protected AudioSendHandler getAudioSendHandler() {
		return handler;
	}
	
	@Override
	protected AudioReceiveHandler getAudioReceiveHandler() {
		return handler;
	}

}
