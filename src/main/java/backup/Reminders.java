package backup;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bot.hierarchy.UserBot;
import commands.model.ThreadsManager;
import lib.DateUtil;

public abstract class Reminders {
	private static final String REMINDERS_FILE = "database/reminders.tasks";
	private static final ScheduledExecutorService executor = ThreadsManager.POOL;
	private static final Logger logger = LoggerFactory.getLogger(Reminders.class);
	
	private static Map<LocalDateTime, Reminder> reminders;
	
	public static void add(Reminder reminder) {
		if (reminders == null)
			reminders = new ConcurrentHashMap<>();
		reminders.put(reminder.time, reminder);
	}
	
	/* Backup and restore handling */
	 
	public static void restoreAll(UserBot bot) {	// Matches UserBot.OnLoadListener interface
		if (reminders == null || reminders.size() == 0) { 
			Map<LocalDateTime, Reminder> restored = restore();
			if (restored != null)
				restored.forEach(Reminders::load);
			reminders = restored;
		}
	}
	
	private static void load(LocalDateTime date, Reminder reminder) {
		long until = DateUtil.millis(date) - System.currentTimeMillis();
		if (until < 0)
			reminders.remove(date);	// if concurrent modification exception then use iterator.remove()
		else
			executor.schedule(reminder, until, TimeUnit.MILLISECONDS);
	}
	
	public static void backup() {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(REMINDERS_FILE))) {
			oos.writeObject(reminders);
			oos.flush();
		} catch (IOException e) {
			logger.error("Could not backup reminders", e);
		}
	}
	
	@SuppressWarnings("unchecked")	// If the file exists, then we're sure of the serialised type
	public static Map<LocalDateTime, Reminder> restore() {
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(REMINDERS_FILE))) {
			return (Map<LocalDateTime, Reminder>) ois.readObject();
		} catch (IOException | ClassNotFoundException e) {
			logger.error("Could not restore reminders", e);
			return null;
		}
	}
	
	public static void main(String[] args) {
		
	}
	
	public static class Reminder implements Runnable, Serializable {
		private static final long serialVersionUID = -2320313003235843084L;
		private final LocalDateTime time;
		
		public Reminder(LocalDateTime time) {
			this.time = time;
		}
		
		@Override
		public void run() {
			System.out.println("I RAN AT "+time);
		}
	}
}
