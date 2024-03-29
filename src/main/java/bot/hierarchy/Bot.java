package bot.hierarchy;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import bot.EchoBot;
import bot.SlaveBot;
import bot.SlaveBot.NoMoreSlavesException;

public enum Bot {
	BABE, ECHO, MIRROR, SLAVE_1, SLAVE_2, SLAVE_3, SLAVE_4, SLAVE_5, SLAVE_6, SLAVE_7, SLAVE_8, SLAVE_9;
	
	private static EchoBot echo;
	public final String key;
	
	private Bot() {
		key = name() + "_BOT";
	}
	
	public String getToken() {
		return System.getenv(key);
	}
	
	public static EchoBot startEcho() {
		if (echo == null) {
			echo = new EchoBot();
			echo.start();
		}
		return echo;
	}
	
	public static EchoBot getEcho() {
		return echo;
	}
	
	public static void killEcho(boolean now) {
		if (echo != null)
			echo.kill(now);
	}
	
	public static void killAll(boolean now) {
		killAll(now, null);
	}
	
	public static void killAll(boolean now, @Nullable Consumer<? super MusicBot> consumer) {
		killEcho(now);
		Slaves.killSlaves(now, consumer);
	}
	
	@Override
	public String toString() {
		return key;
	}
	
	public static final class Slaves {
		private static final int CAPACITY = 9, INDEX = 6;
		private static final Bot[] slaves = new Bot[] { SLAVE_1, SLAVE_2, SLAVE_3, SLAVE_4, SLAVE_5, SLAVE_6, SLAVE_7, SLAVE_8, SLAVE_9 };
		private static final List<SlaveBot> alive = new CopyOnWriteArrayList<>();
		private static Queue<Bot> queue;

		static { reset(); }
		
		public static SlaveBot newSlave() throws NoMoreSlavesException {
			SlaveBot slave = new SlaveBot();
			alive.add(slave);
			slave.start();
			return slave;
		}
		
		public static void killSlave(Bot bot) {
			killSlave(bot, false);
		}
		
		public static void killSlave(Bot bot, boolean now) {
			SlaveBot killed = null;
			for (SlaveBot slave : alive)
				if (slave.bot == bot)
					killed = slave;
			alive.remove(killed);
			queue.offer(killed.bot);
			killed.kill(now);
		}
		
		public static void killSlaves(boolean now, @Nullable Consumer<? super MusicBot> consumer) {
			alive.forEach(slave -> {
				queue.offer(slave.bot);
				slave.kill(now);
				if (consumer != null)
					consumer.accept(slave);
			});
			alive.clear();
		}
		
		public static void killSlaves(boolean now) {
			killSlaves(now, null);
		}
		
		public static void reset() {
			queue = new PriorityBlockingQueue<>(CAPACITY, comparator());
			queue.addAll(Arrays.asList(slaves)); 		// initially starts with all slaves queued
		}
		
		public static boolean available() {
			return !queue.isEmpty();
		}
		
		public static Bot pop() {
			return queue.poll();						// returns null if all slaves popped
		}
		
		public static void push(Bot bot) {
			if (!queue.contains(bot))
				queue.offer(bot);
		}
		
		public static Bot get(int i) {
			return slaves[i];
		}
		
		public static SlaveBot getBot(int i) {
			return alive.get(i);
		}
		
		public static Bot[] all() {
			return slaves;
		}
		
		public static Comparator<Bot> comparator() {
			return new Comparator<Bot>() {
				@Override
				public int compare(Bot first, Bot second) {
					return first.key.charAt(INDEX) - second.key.charAt(INDEX);
				}
			};
		}
	}
}