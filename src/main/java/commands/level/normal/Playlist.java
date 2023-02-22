package commands.level.normal;

import audio.TrackScheduler;
import bot.hierarchy.UserBot;
import commands.hierarchy.DiscordCommand;
import commands.name.Command;
import net.dv8tion.jda.api.entities.Message;

public class Playlist extends DiscordCommand {
	public static final String NOTES_EMOJI = new String(Character.toChars(0x1F3B6));

	public Playlist(UserBot bot, Message message) {
		super(bot, message, Command.PLAYLIST.names);
	}

	@Override
	public String helpMessage() {
		return helpBuilder("<playlist_name>",
				"-l or --list\tlists all playlists",
				"-c or --create\tcreats a playlist",
				"-r or --remove\tremoves a playlist",
				"-cl or --clear\tremoves all playlist except default",
				"Manages playlists and by default swaps between them");
	}

	@Override
	protected void execute(String input) throws Exception {
		if (!fromMusicBot()) {
			println("This command only works for music bots.");
			return;
		}
		TrackScheduler scheduler = getMusicBot().getScheduler(getGuild());
		if (hasArgs("-l", "--list"))
			printItems(
				scheduler.getPlaylistNames().toArray(),
				scheduler.getCurrentPlaylist()::equals,
				playlist -> NOTES_EMOJI + playlist + NOTES_EMOJI);
		else if (hasArgs("-cl", "--clear")) {
			scheduler.clearPlaylists();
			println("Cleared all except default playlist");
		} else if (input.equals(""))
			println("Please give a playlist name");
		else if (hasArgs("-c", "--create")) {
			if (scheduler.hasPlaylist(input))
				println("Playlist %s already exists", inline(input));
			else {
				scheduler.createPlaylist(input);
				println("Created new playlist %s", inline(input));
			}
		} else if (hasArgs("-r", "--remove")) {
			if (scheduler.hasPlaylist(input)) {
				scheduler.removePlaylist(input);
				println("Removed playlist %s", inline(input));
			} else
				println("Playlist %s doesn't exist", inline(input));
		} else {
			if (scheduler.hasPlaylist(input)) {
				scheduler.swapPlaylist(input);
				println("Swapped to playlist %s", inline(input));
			} else 
				println("Playlist %s doesn't exist", inline(input));
		}
	}
}
