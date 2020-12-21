package commands.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import commands.hierarchy.Command;
import lib.ThreadsManager;

public class TypingWatchdog implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(TypingWatchdog.class);
	private static AtomicBoolean alive = new AtomicBoolean();
	private static List<Command> targets = new ArrayList<>(); 
	
	static { start(); }
	
	private TypingWatchdog() {}
	
	public static void handle(Command command) {
		command.actTyping();
		targets.add(command);
	}
	
	public static void start() {
		if (!alive.get()) {
			alive.set(true);
			ThreadsManager.newNativeThread(new TypingWatchdog()).start();
		}
	}
	
	public static void kill() {
		alive.set(false);
	}
	
	@Override
	public void run() {
		while (alive.get()) {
			Iterator<Command> iterator = targets.iterator();
			Command command;
			while (iterator.hasNext()) {
				command = iterator.next();
				if (command.isFinished())
					iterator.remove();	// avoids concurrent modification
				else 
					command.actTyping();
			}
			try { Thread.sleep(10 * 1000); }	// discord sends typing for 10s
			catch (Exception e) {
				logger.error("Could not sleep watchdog", e);
			}
		}
	}
}
