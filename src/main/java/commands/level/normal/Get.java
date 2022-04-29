package commands.level.normal;

import audio.CircularDeque;
import bot.hierarchy.UserBot;
import commands.hierarchy.DiscordCommand;
import commands.model.ThreadsManager;
import commands.name.Command;
import json.YTMP3AudioInfo;
import json.YTMP3StatusInfo;
import lib.StringLib;
import lib.YoutubeToMP3;
import lib.YoutubeToMP3.StatusListener;
import net.dv8tion.jda.api.entities.Message;

public class Get extends DiscordCommand {

	public Get(UserBot bot, Message message) {
		super(bot, message, Command.GET.names);
	}

	@Override
	public String helpMessage() {
		return helpBuilder("<index>", 
				"-m or --mp3\tdownloads as mp3 (youtube urls only)",
				"Give me index of song to get url from.");
	}

	@Override
	protected void execute(String input) throws Exception {
		if (input.equals("")) {
			println("You didn't give me any input");
			return;
		}
		if (!StringLib.isInteger(input)) {
			println("You need to give me an integer.");
			return;
		}
		CircularDeque queue = getMusicBot().getPlaylist(getGuild());
		if (queue.size() == 0) {
			println("No songs in queue.");
			return;
		}
		int index = Integer.parseInt(input);
		if (index < 0 && index >= queue.size()) {
			println("Index cannot be outside the range [0,%d]", queue.size());
			return;
		}
		String url = queue.get(index).getInfo().uri;
		if (hasArgs("-m", "--mp3")) {
			handleMp3(url);
			return;
		}
		if (url.startsWith(StringLib.MUSIC_PATH))
			url = StringLib.obfuscateMusicFolder(url);
		println(url);
	}

	private void handleMp3(String input) {
		final Message message = getChannel().sendMessage("Loading..").complete();
		YoutubeToMP3 converter = new YoutubeToMP3(input, new StatusListener() {

			@Override
			public void onRequest(YTMP3AudioInfo info) {
				message.editMessage("Requesting track details..").queue();
			}

			@Override
			public void onConvert(YTMP3StatusInfo info) {
				message.editMessage("Started conversion process..").queue();
			}

			@Override
			public void onCheck(YTMP3StatusInfo info) {
				message.editMessage("Checking for end of conversion..").queue();
			}

			@Override
			public void onFinished(String url) {
				message.editMessage(String.format("MP3 track available at : %s", url)).queue();
			}

			@Override
			public void onError(Throwable t) {
				message.editMessage("Error: "+t.getMessage()).queue();
			}
			
		});
		ThreadsManager.POOL.submit(converter);
	}
}
