package main;

import bot.BabeBot;
import commands.model.ThreadsManager;

public class Run {
	public static final BabeBot BABE = new BabeBot();
	
	public static void main(String[] args) {
		ThreadsManager.newNativeThread(BABE).start();
	}
}