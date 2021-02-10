package backup;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

public abstract class MusicState {
	private static final String LAST_CHANNEL = "lastVoiceChannel",
			CURRENT = "current", POSITION = "position", 
			VOLUME = "lastVolume", PAUSED = "wasPaused";
	private static final Logger logger = LoggerFactory.getLogger(MusicState.class);
	
	private static String getName(UserBot bot) {
		return bot.getClass().getSimpleName();
	}
	
	public static void backup(MusicBot bot) {
		for (Entry<Long, MusicController> entry : bot.getControllers().entrySet())
			backup(bot, entry.getKey(), entry.getValue().getScheduler());
	}
	
	public static void clear(MusicBot bot, long id) {
		try {
			DBManager.INSTANCE
				.manage("Backup"+getName(bot)+id)
				.clear();
		} catch (SQLException e) {
			logger.error("Could not clear table", e);
		}
	}
	
	public static void restore(UserBot bot) {
		if (MusicBot.class.isInstance(bot)) {
			logger.info("Restoring tracks for {}", getName(bot));
			restore(MusicBot.class.cast(bot));
		}
	}
	
	private static void restore(MusicBot bot) {
		try {
			List<String> tables = Query.getGuildTables("Backup"+getName(bot)+"%");
			logger.info("Matched guild tables : {}", Arrays.toString(tables.toArray()));
			int size = ("Backup"+getName(bot)).length();
			for (String name : tables) 
				restore(bot, name.substring(size), DBManager.INSTANCE.manage(name));
		} catch (SQLException e) {
			logger.error("Restore: Could not restore from database", e);
		}
	}
	
	private static void backup(MusicBot bot, long guild, TrackScheduler scheduler) {
		TableManager table = null;
		try {
			table = DBManager.INSTANCE.manage("Backup"+getName(bot)+guild);
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
		restorePlaylist(table, bot, guildID);
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
	
	private static void restorePlaylist(TableManager table, MusicBot bot, long guild) {
		Map<String, String> urls;
		try {
			urls = table.selectAll();
			if (urls.size() == 0)
				return;
		} catch (SQLException e) {
			logger.error("Could not retrieve playlist from backup", e);
			return;
		}
		final TrackLoadHandler handler = new TrackLoadHandler(bot.getScheduler(guild));
		final AudioPlayerManager playerManager = bot.getPlayerManager(guild);
		urls.forEach((name, value) -> playerManager.loadItemOrdered(playerManager, value, handler));
	}
	
	/* Backup/restore: Song index + position */
	
	private static void backupSongAndPosition(TableManager table, CircularDeque queue) {
		int current = queue.getCurrent();
		if (current == CircularDeque.UNINITIALISED)
			return;
		try {
			table.insertOrUpdate(CURRENT, current);
			table.insertOrUpdate(POSITION, queue.get(current).getPosition());
		} catch (SQLException e) {
			logger.error("Could not backup current track index + position", e);
		}
	}
	
	private static void restoreSongAndPosition(TableManager table, MusicBot bot, Guild guild) {
		try {
			int current = table.retrieveInt(CURRENT, -1);
			if (current == -1) 
				return;
			bot.play(guild, current);
			bot.getScheduler(guild)
				.getQueue()
				.setCurrent(current);
		} catch (NumberFormatException | SQLException e) {
			logger.error("Could not retrieve last song index", e);
			return;
		}
		try {
			long position = table.retrieveLong(POSITION, -1);
			if (position == -1)
				return;
			bot.waitForTrack(guild)
				.seekTo(guild,  position);
		} catch (NumberFormatException | SQLException | InterruptedException e) {
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
			bot.connectTo(channel);
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
