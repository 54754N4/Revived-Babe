package backup;

import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import database.DBManager;
import database.TableManager;
import net.dv8tion.jda.api.entities.MessageChannel;

public abstract class Reminders {
	private static final Logger logger = LoggerFactory.getLogger(Reminders.class);
	private static Map<LocalDateTime, Reminder> reminders = new ConcurrentHashMap<>();
	
	public static TableManager getTable() throws SQLException {
		return DBManager.INSTANCE.manage("Reminders");
	}
	
	public static void add(LocalDateTime date, Reminder reminder, MessageChannel channel) {
		reminders.put(date, reminder);
		load(date, reminder, channel);
	}
	
	/* Backup and restore handling */
	 
	public static void restoreAll(final MessageChannel channel) {
		Map<LocalDateTime, Reminder> restored = restore();
		restored.forEach((date, reminder) -> load(date, reminder, channel));
		reminders = restored;
	}
	
	private static Map<LocalDateTime, Reminder> restore() {
		Map<LocalDateTime, Reminder> deserialized = new ConcurrentHashMap<>();
		Map<String, String> vals;
		try {
			vals = getTable().selectAll();	// retrieve from DB
		} catch (Exception e) {
			logger.error("Could not restore reminders", e);
			return deserialized;
		}
		for (Entry<String, String> entry : vals.entrySet()) {
			logger.info("Restoring ("+entry.getKey()+", "+entry.getValue()+")");
			deserialized.put(LocalDateTime.parse(entry.getKey()), new Reminder(entry.getValue()));
		}
		return deserialized;
	}
	
	private static void load(final LocalDateTime date, Reminder reminder, MessageChannel channel) {
		long after = ChronoUnit.SECONDS.between(Instant.now(), date.atZone(ZoneId.systemDefault()).toInstant());
		logger.info("Loading reminder "+date+" with ETA in sec : "+after);
		if (after < 0)
			reminders.remove(date);	// if concurrent modification exception then use iterator.remove()
		else 
			channel.sendMessage(reminder.message)
				.queueAfter(after, TimeUnit.SECONDS, msg -> reminders.remove(date));	// remove after execution
	}
	
	public static void backup() {
		String[] keys = reminders.keySet()
				.stream()
				.map(key -> key.toString())
				.collect(Collectors.toList())
				.toArray(new String[0]);
		Object[] values = reminders.values().toArray(new Reminder[0]);
		try {
			getTable().reset().insertOrUpdate(keys, values);
		} catch (Exception e) {
			logger.error("Could not backup reminders", e);
		}
	}
	
	public static class Reminder {
		public final String message;
		
		public Reminder(String message) {
			this.message = message;
		}
		
		@Override
		public String toString() {
			return message;
		}
	}
}
