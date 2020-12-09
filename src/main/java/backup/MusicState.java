package backup;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

public class MusicState {
	private static final String LAST_CHANNEL = "lastVoiceChannel",
			CURRENT = "current", POSITION = "position";
	
	private static final Logger logger = LoggerFactory.getLogger(MusicState.class);
	
	public static void backup(MusicBot bot) throws SQLException {
		for (Entry<Long, MusicController> entry : bot.getControllers().entrySet())
			handle(bot, entry.getKey(), entry.getValue().getScheduler());
	}
	
	private static void handle(MusicBot bot, long guild, TrackScheduler scheduler) {
		TableManager table = null;
		try {
			table = DBManager.INSTANCE.manage("Backup"+getName(bot)+guild);
			Map<String, Object> playlist = new HashMap<>();
			CircularDeque queue = scheduler.getQueue(); 
			queue.forEachIndexed((i, track) -> 
				playlist.put(i+"", track.getInfo().uri));
			if (playlist.size() == 0)
				return;
			clear(bot, guild);
			table.insertOrUpdate(playlist);
			AudioManager manager = bot.getManager(guild);
			if (manager == null) 
				return;
			VoiceChannel channel = manager.getConnectedChannel();
			if (channel == null)
				return;
			logger.info("Setting last voice channel to {}", channel.getId());
			table.insertOrUpdate(LAST_CHANNEL, channel.getIdLong());
			int current = queue.getCurrent();
			if (current == CircularDeque.UNINITIALISED)
				return;
			long position = queue.get(current).getPosition();
			table.insertOrUpdate(CURRENT, current);
			table.insertOrUpdate(POSITION, position);
		} catch (SQLException e) {
			logger.error("Backup: Could not connect to database", e);
		}
	}
	
	public static void clear(MusicBot bot, long id) {
		try {
			Query.clearTable("Backup"+getName(bot)+id);
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
			logger.error("Restore: Could not connect to database", e);
		}
	}
	
	private static void restore(MusicBot bot, String idLong, TableManager table) {
		try {
			Map<String, String> urls = table.selectAll();
			if (urls.size() == 0)
				return;
			logger.info("Restoring tracks for guild : {}", idLong);
			final long id = Long.parseLong(idLong);
			Guild guild = bot.getJDA().getGuildById(id);
			logger.info("Guild is {}", guild);
			if (guild == null) 
				return;
			bot.setupAudio(id);
			final TrackLoadHandler handler = new TrackLoadHandler(bot.getScheduler(id));
			urls.forEach((name, value) -> bot.getPlayerManager(id).loadItem(value, handler));
			long channelID = table.retrieveLong(LAST_CHANNEL, -1);
			if (channelID == -1)
				return;
			VoiceChannel channel = bot.getJDA().getVoiceChannelById(channelID);
			if (channel == null)
				return;
			bot.connectTo(channel);
			int current = table.retrieveInt(CURRENT, -1);
			if (current == -1) 
				return;
			bot.play(guild, current);
			long position = table.retrieveLong(POSITION, -1);
			if (position == -1)
				return;
			bot.waitForTrack(guild)
				.seekTo(guild,  position);
		} catch (Exception e) {
			logger.error("Restore: Couldn't restore tracks for guild "+idLong, e);
		}
	}
	
	private static String getName(UserBot bot) {
		return bot.getClass().getSimpleName();
	}
}
