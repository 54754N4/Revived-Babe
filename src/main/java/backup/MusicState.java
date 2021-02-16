package backup;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.OptionalInt;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

import audio.CircularDeque;
import audio.MusicController;
import audio.TrackScheduler;
import audio.track.handlers.TrackLoadHandler;
import bot.hierarchy.MusicBot;
import bot.hierarchy.UserBot;
import commands.model.ThreadSleep;
import database.DBManager;
import database.Query;
import database.TableManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

public abstract class MusicState {
	private static final String LAST_CHANNEL = "lastVoiceChannel",
			CURRENT = "current", POSITION = "position", 
			VOLUME = "lastVolume", PAUSED = "wasPaused";
	private static final Logger logger = LoggerFactory.getLogger(MusicState.class);
	
	public static void backup(MusicBot bot) {
		for (Entry<Long, MusicController> entry : bot.getControllers().entrySet())
			backup(bot, entry.getKey(), entry.getValue().getScheduler());
	}
	
	public static void clear(MusicBot bot, long id) {
		try {
			DBManager.INSTANCE
				.manage("Backup"+bot.getBotName()+id)
				.clear();
		} catch (SQLException e) {
			logger.error("Could not clear table", e);
		}
	}
	
	public static void restore(UserBot bot) {
		if (MusicBot.class.isInstance(bot)) {
			logger.info("Restoring tracks for {}", bot.getBotName());
			restore(MusicBot.class.cast(bot));
		}
	}
	
	private static void restore(MusicBot bot) {
		try {
			List<String> tables = Query.getGuildTables("Backup"+bot.getBotName()+"%");
			logger.info("Matched guild tables : {}", Arrays.toString(tables.toArray()));
			int size = ("Backup"+bot.getBotName()).length();
			for (String name : tables) 
				restore(bot, name.substring(size), DBManager.INSTANCE.manage(name));
		} catch (SQLException e) {
			logger.error("Restore: Could not restore from database", e);
		}
	}
	
	private static void backup(MusicBot bot, long guild, TrackScheduler scheduler) {
		TableManager table = null;
		try {
			table = DBManager.INSTANCE.manage("Backup"+bot.getBotName()+guild);
			table.reset();
		} catch (SQLException e) {
			logger.error("Backup: Could not connect and clear database for guild "+guild+" with bot "+bot, e);
		}
		CircularDeque queue = scheduler.getQueue();
		backupPlaylist(table, queue);
		backupVoiceChannel(table, bot, guild);
		backupVolumeAndPause(table, bot, guild);
		backupSongAndPosition(table, queue);
	}
	
	private static void restore(MusicBot bot, String idLong, TableManager table) {
		final long guildID = Long.parseLong(idLong);
		Guild guild = bot.getJDA().getGuildById(guildID);
		logger.info("Guild is {}", guild);
		if (guild == null) 
			return;
		bot.setupAudio(guildID);
		restorePlaylist(table, bot, guild);
		restoreVoiceChannel(table, bot);
		restoreVolumeAndPause(table, bot, guild);
		restoreSongAndPosition(table, bot, guild);
	}
	
	/* Backup/restore: Playlists */
	
	private static void backupPlaylist(TableManager table, CircularDeque queue) {
		if (queue.size() == 0)
			return;
		Map<String, Object> playlist = new HashMap<>();
		queue.forEachIndexed((i, track) -> playlist.put(i+"", track.getInfo().uri));
		try {
			table.insertOrUpdate(playlist);
		} catch (SQLException e) {
			logger.error("Could not backup playlist", e);
		}
	}
	
	private static int restorePlaylist(TableManager table, MusicBot bot, Guild guild) {
		Map<String, String> urls;
		try {
			urls = table.selectAll();
			if (urls.size() == 0)
				return 0;
		} catch (SQLException e) {
			logger.error("Could not retrieve playlist from backup", e);
			return 0;
		}
		final TrackLoadHandler handler = new TrackLoadHandler(bot.getScheduler(guild));
		final AudioPlayerManager playerManager = bot.getPlayerManager(guild);
		List<Future<Void>> loaders = new ArrayList<>();
		urls.forEach((name, value) -> loaders.add(playerManager.loadItemOrdered(playerManager, value, handler)));
		try {
			ThreadSleep.waitFor(loaders).call();
		} catch (Exception e) {
			logger.error("Could not wait for tracks", e);
		}
		return urls.size();
	}
	
	/* Backup/restore: Song index + position */
	
	private static void backupSongAndPosition(TableManager table, CircularDeque queue) {
		int current = queue.getCurrent();
		if (current == CircularDeque.UNINITIALISED)
			return;
		try {
			table.insertOrUpdate(CURRENT, queue.get(current).getInfo().title);
			table.insertOrUpdate(POSITION, queue.get(current).getPosition());
		} catch (SQLException e) {
			logger.error("Could not backup current track index + position", e);
		}
	}
	
	private static void restoreSongAndPosition(TableManager table, MusicBot bot, Guild guild) {
		try {
			String current = table.retrieve(CURRENT, "");
			if (current.equals("")) 
				return;
			CircularDeque queue = bot.getScheduler(guild).getQueue();
			OptionalInt index = IntStream.range(0, queue.size())
				.filter(i -> queue.get(i).getInfo().title.equals(current))
				.findFirst();
			if (index.isPresent())
				bot.play(guild, index.getAsInt());
		} catch (NumberFormatException | SQLException e) {
			logger.error("Could not retrieve last song index", e);
			return;
		}
		try {
			long position = table.retrieveLong(POSITION, -1);
			if (position == -1)
				return;
			bot.seekTo(guild,  position);
		} catch (Exception e) {
			logger.error("Could not retrieve last song's position", e);
		}
	}
	
	/* Backup/restore: Voice channel */
	
	private static void backupVoiceChannel(TableManager table, MusicBot bot, long guild) {
		AudioManager manager = bot.getManager(guild);
		if (manager == null) 
			return;
		VoiceChannel channel = manager.getConnectedChannel();
		if (channel == null)
			return;
		logger.info("Setting last voice channel to {}", channel.getId());
		try {
			table.insertOrUpdate(LAST_CHANNEL, channel.getIdLong());
		} catch (SQLException e) {
			logger.error("Could not backup voice channel", e);
		}
	}
	
	private static void restoreVoiceChannel(TableManager table, MusicBot bot) {
		try {
			long channelID = table.retrieveLong(LAST_CHANNEL, -1);
			if (channelID == -1)
				return;
			VoiceChannel channel = bot.getJDA().getVoiceChannelById(channelID);
			if (channel == null)
				return;
			bot.connect(channel);
		} catch (NumberFormatException | SQLException e) {
			logger.error("Could not retrieve last channel ID", e);
		}
	}
	
	/* Backup/restore: Volume + Pause */
	
	private static void backupVolumeAndPause(TableManager table, MusicBot bot, long guild) {
		AudioPlayer player = bot.getPlayer(guild);
		try {
			table.insertOrUpdate(VOLUME, player.getVolume());
			table.insertOrUpdate(PAUSED, player.isPaused());
		} catch (SQLException e) {
			logger.error("Could not backup volume and pause state", e);
		}
	}
	
	private static void restoreVolumeAndPause(TableManager table, MusicBot bot, Guild guild) {
		try {
			int volume = table.retrieveInt(VOLUME, -1);
			if (volume == -1)
				return;
			bot.setVolume(guild, volume);
		} catch (NumberFormatException | SQLException e) {
			logger.error("Could not retrieve last volume", e);
		}
		try {
			boolean paused = table.retrieveBool(PAUSED, false);
			if (paused)
				bot.pause(guild);
		} catch (NumberFormatException | SQLException e) {
			logger.error("Could not retrieve last pause state", e);
		}
	}
}
