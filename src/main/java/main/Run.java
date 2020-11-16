package main;

import bot.BabeBot;
import bot.model.Bot;
import bot.model.UserBot;
import commands.model.ThreadsManager;

public class Run {
	public static final BabeBot BABE = new BabeBot();
	
	public static void main(String[] args) {
		BABE.addOnLoadListener(Run::debug);
		ThreadsManager.newNativeThread(BABE).start();
	}
	
	public static void debug(UserBot bot) {
//		for (Bot b : Bot.values())
//			System.out.println(String.format("%s: %s", b.key, b.getToken()));
	}
}