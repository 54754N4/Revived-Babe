package backup;

import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import database.DBManager;
import database.TableManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

public abstract class Reminders {
	private static final Logger logger = LoggerFactory.getLogger(Reminders.class);
	private static final List<Reminder> reminders = new ArrayList<>();
	private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(100);
	private static final Lock lock = new ReentrantLock();
	
	private static TableManager getTable() throws SQLException {
		return DBManager.INSTANCE.manage("Reminders");
	}
	
	public static void add(Reminder reminder) {
		if (reminder.channel == null) {
			logger.error("Couldn't retrieve reminder's channel", new IllegalArgumentException(reminder.toString()));
			return;
		}
		long after = reminder.untilExecution();
		if (after > 0) {
			try {
				lock.lock();
				reminders.add(reminder);
			} finally {
				lock.unlock();
			}
			executor.schedule(reminder, after, TimeUnit.SECONDS);
		} else 
			executor.execute(reminder);
	}
	
	public static final void clear() {
		reminders.clear();
	}
	
	public static final Collection<Reminder> getReminders() {
		return reminders.stream().collect(Collectors.toUnmodifiableList());
	}
	
	/* Backup and restore handling */
	 
	public static void restoreAll(final JDA jda) {	// called on first execution
		logger.info("Restoring reminders");
		Map<String, String> vals;
		try {
			vals = getTable().selectAll();	// retrieve from DB
		} catch (Exception e) {
			logger.error("Could not restore reminders", e);
			return;
		}
		vals.values()
			.stream()
			.map(rem -> Reminder.deserialize(rem, jda))
			.forEach(Reminders::add);
	}
	
	public static void backup() {
		executor.shutdownNow();
		Map<String, Object> map = new HashMap<>();
		int i=0;
		for (Reminder reminder : reminders)
			map.put(String.valueOf(i++), reminder.serialize());
		try {
			logger.info("Backing up reminders");
			getTable().reset().insertOrUpdate(map);
		} catch (Exception e) {
			logger.error("Could not backup reminders", e);
		}
	}
	
	public static class Reminder implements Runnable {
		public static final String SEPARATOR = "..::||::..";	// unique serparator that won't be in text
		private final String message;
		private final LocalDateTime date;
		private final MessageChannel channel;
		
		private Reminder(String message, LocalDateTime date, MessageChannel channel) {
			this.message = message;
			this.date = date;
			this.channel = channel;
		}
		
		public long untilExecution() {
			return ChronoUnit.SECONDS.between(Instant.now(), date.atZone(ZoneId.systemDefault()).toInstant());
		}
		
		public boolean isFinished() {
			return untilExecution() < 0;
		}
		
		@Override
		public void run() {
			channel.sendMessage(String.format("Reminder: %s", message))
				.queue(m -> reminders.remove(this), e -> reminders.remove(this));
		}
		
		public String serialize() {
			return String.format("%s%s%s%s%s", message, SEPARATOR, date, SEPARATOR, channel.getId());
		}
		
		public static Reminder deserialize(String input, JDA jda) {
			String[] split = input.split(Pattern.quote(SEPARATOR));
			if (split.length != 3)
				throw new IllegalArgumentException(String.format("Input '%s' didn't split correctly, need 3 items but got %d", input, split.length));
			LocalDateTime date = LocalDateTime.parse(split[1]);
			MessageChannel channel = jda.getTextChannelById(split[2]);
			return new Reminder(split[0], date, channel);
		}
		
		@Override
		public String toString() {
			return String.format("%s - %s - %s", date, message, channel);
		}
		
		public static class Builder {
			private String message;
			private LocalDateTime date;
			private MessageChannel channel;
			
			public Builder setMessage(String message) {
				this.message = message;
				return this;
			}
			
			public Builder setDate(LocalDateTime date) {
				this.date = date;
				return this;
			}
			
			public Builder setChannel(MessageChannel channel) {
				this.channel = channel;
				return this;
			}
			
			public Reminder build() {
				if (message == null)
					throw new IllegalArgumentException("message is null");
				if (channel == null)
					throw new IllegalArgumentException("channelID is null");
				if (date == null)
					throw new IllegalArgumentException("date is null");
				return new Reminder(message, date, channel);
			}
		}
	}
}
