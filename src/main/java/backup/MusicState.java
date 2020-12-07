package backup;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import audio.TrackScheduler;
import audio.track.handlers.TrackLoadHandler;
import bot.model.MusicBot;
import database.DBManager;
import database.TableManager;
import net.dv8tion.jda.api.entities.Guild;

public class MusicState {
	private static final Logger logger = LoggerFactory.getLogger(MusicState.class);
	
	public static void backup(MusicBot bot) throws SQLException {
		for (Entry<Long, TrackScheduler> entry : bot.getSchedulers().entrySet())
			handle(bot, entry.getKey(), entry.getValue());
	}
	
	private static void handle(MusicBot bot, long guild, TrackScheduler scheduler) {
		TableManager table = null;
		try {
			table = DBManager.INSTANCE.manage("Backup"+bot.getClass()+guild);
			Map<String, Object> playlist = new HashMap<>();
			scheduler.getQueue().forEachIndexed((i, track) -> 
				playlist.put("track"+i, track.getInfo().uri));
			table.insert(playlist);
		} catch (SQLException e) {
			logger.error("Backup: Could not connect to database", e);
		}
	}
	
	public static void restore(MusicBot bot) {
		try {
			List<String> tables = DBManager.INSTANCE.getGuildTables("Backup"+bot.getClass()+"%");
			int size = ("Backup"+bot.getClass()).length();
			for (String name : tables) 
				restore(bot, name.substring(size), DBManager.INSTANCE.manage(name).selectAll());	// if crash then id is wrong
		} catch (SQLException e) {
			logger.error("Restore: Could not connect to database", e);
		}
	}
	
	private static void restore(MusicBot bot, String idLong, Map<String, String> urls) {
		long id = Long.parseLong(idLong);
		Guild guild = bot.getJDA().getGuildById(id);
		if (guild == null)
			return;
		TrackLoadHandler handler = new TrackLoadHandler(bot.setupAudio(id).getScheduler(id));
		urls.forEach((name, value) -> bot.getPlayerManager(id).loadItem(value, handler));
	}
}
