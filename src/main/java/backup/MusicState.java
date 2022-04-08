package backup;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

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
import database.DBManager;
import database.Query;
import database.TableManager;
import lib.StringLib;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

public abstract class MusicState {
	private static final String LAST_CHANNEL = "lastVoiceChannel",
			POSITION = "position", VOLUME = "lastVolume", PAUSED = "wasPaused";
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
		restoreVoiceChannel(table, bot, guild);
		restoreVolumeAndPause(table, bot, guild);
		restoreSongPosition(table, bot, guild);
	}
	
	/* Backup/restore: Playlists */
	
	private static void backupPlaylist(TableManager table, CircularDeque queue) {
		if (queue.size() == 0)
			return;
		Map<String, Object> playlist = new HashMap<>();
		// Backup starting from current song and then loop over
		int c = 0, 
			middle = queue.getCurrent();	
		if (middle == CircularDeque.UNINITIALISED)
			middle = 0;
		for (int i=queue.getCurrent(); i<queue.size(); i++) 
			playlist.put(String.valueOf(c++), queue.get(i).getInfo().uri);
		for (int i=0; i<queue.getCurrent(); i++)
			playlist.put(String.valueOf(c++), queue.get(i).getInfo().uri);
		try {
			table.insertOrUpdate(playlist);
		} catch (SQLException e) {
			logger.error("Could not backup playlist", e);
		}
	}
	
	private static void restorePlaylist(TableManager table, MusicBot bot, Guild guild) {
		Map<String, String> all, urls = new TreeMap<>();
		try {
			all = table.selectAll();
			all.entrySet()
				.stream()
				.filter(e -> StringLib.isInteger(e.getKey()))
				.forEachOrdered(e -> urls.put(e.getKey(), e.getValue()));			
			if (urls.size() == 0)
				return;
		} catch (SQLException e) {
			logger.error("Could not retrieve playlist from backup", e);
			return;
		}
		final TrackLoadHandler handler = new TrackLoadHandler(bot.getScheduler(guild))
				.setToggleCount(urls.size()-1);
		final AudioPlayerManager playerManager = bot.getPlayerManager(guild);
		List<Future<Void>> futures = new ArrayList<>();
		for (Entry<String, String> entry : urls.entrySet())
			futures.add(playerManager.loadItemOrdered(playerManager, entry.getValue(), handler));
		try {
			logger.info("Waiting for {} to get first song in guild {}", bot, guild);
			futures.get(0).get(5, TimeUnit.SECONDS);
		} catch (Exception e) {
			logger.error("Couldn't wait for first song to load", e);
		}
	}
	
	/* Backup/restore: Song index + position */
	
	private static void backupSongAndPosition(TableManager table, CircularDeque queue) {
		int current = queue.getCurrent();
		if (current == CircularDeque.UNINITIALISED)
			return;
		try {
			table.insertOrUpdate(POSITION, queue.get(current).getPosition());
		} catch (SQLException e) {
			logger.error("Could not backup current track index + position", e);
		}
	}
	
	private static void restoreSongPosition(TableManager table, MusicBot bot, Guild guild) {
		try {
			bot.play(guild, 0);
			long position = table.retrieveLong(POSITION, -1);
			if (position == -1 || bot.getPlayer(guild).getPlayingTrack() == null)
				return;
			logger.info("Seeking for {} in song for guild {}", bot, guild);
			bot.seekTo(guild, position);
		} catch (Exception e) {
			logger.error("Could not retrieve last song's position", e);
		}
	}
	
	/* Backup/restore: Voice channel */
	
	private static void backupVoiceChannel(TableManager table, MusicBot bot, long guild) {
		AudioManager manager = bot.getManager(guild);
		if (manager == null) 
			return;
		AudioChannel channel = manager.getConnectedChannel();
		if (channel == null)
			return;
		logger.info("Setting last voice channel to {}", channel.getId());
		try {
			table.insertOrUpdate(LAST_CHANNEL, channel.getIdLong());
		} catch (SQLException e) {
			logger.error("Could not backup voice channel", e);
		}
	}
	
	private static void restoreVoiceChannel(TableManager table, MusicBot bot, Guild guild) {
		try {
			long channelID = table.retrieveLong(LAST_CHANNEL, -1);
			if (channelID == -1)
				return;
			VoiceChannel channel = bot.getJDA().getVoiceChannelById(channelID);
			if (channel == null)
				return;
			bot.connect(channel);
		} catch (NumberFormatException | SQLException e) {
			logger.error("Could not retrieve or connect to last channel ID", e);
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
