package bot.model;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

public enum Bot {
	BABE, ECHO, MIRROR, SLAVE_1, SLAVE_2, SLAVE_3, SLAVE_4, SLAVE_5, SLAVE_6, SLAVE_7, SLAVE_8, SLAVE_9;
	
	public final String key;
	
	private Bot() {
		key = name() + "_BOT";
	}
	
	public String getToken() {
		return System.getenv(key);
	}
	
	public static final class Slaves {
		private static final int CAPACITY = 9, INDEX = 6;
		private static final Bot[] slaves = new Bot[] { SLAVE_1, SLAVE_2, SLAVE_3, SLAVE_4, SLAVE_5, SLAVE_6, SLAVE_7, SLAVE_8, SLAVE_9 };
		private static Queue<Bot> queue;

		static { reset(); }
		
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