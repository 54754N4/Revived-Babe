 package commands.level.normal;

import bot.model.UserBot;
import commands.model.DiscordCommand;
import commands.name.Command;
import net.dv8tion.jda.api.entities.Message;

public class Praise extends DiscordCommand {
	public static final String[] praises = new String[] {"That's Incredible", "How Extraordinary!", "Far Out!", "Great!", 
			"Outstanding Performance", "Marvelous", "I Can't Get Over It!", "Wonderful!", "Amazing Effort!", "Unbelievable Work", 
			"You Should Be Proud", "Phenomenal!", "You've Got It", "Superb!", "You're Special", "Excellent!", "Cool!", 
			"You're Definitely Part of the Elite!", "Way to Go!", "You've Outdone Yourself", "Thumbs Up", "What A Great Listener", 
			"Your Help Counts!", "You Came Through!", "Terrific", "You Tried Hard", "You're OK", "Fabulous", "You Made It Happen", 
			"You're a Real Trooper", "It Couldn't Be Better", "The Time You Put in Shows", "Bravo!", "You're Unique", 
			"Exceptional", "Fantastic Work", "Breathtaking!", "You're a Great Example For Others", "Keep Up the Good Work", 
			"Awesome!", "I Knew You Had It In You", "You've Made Progress", "Your Work Is Out of Sight", "What an Imagination!", 
			"It's Everything I Hoped For", "Stupendous", "You're Sensational", "Very Good!", "Thanks for Caring", 
			"What a Genius!", "You Made The Difference", "Good For You", "A+ Work", "Take a Bow", "Super Job", "How Thoughtful of You", 
			"Nice Going", "Class Act", "Well Done", "You're Inspiring", "How Artistic", "You Go the Extra Mile", 
			"Hooray for You", "You're a Joy", "You're a Shining Star", "You're Amazing", "What a Great Idea", "Great Answer", 
			"Extra-Special Work", "You Deserve a Hug", "You're Getting Better", "You're Tops", "You're Catching On", 
			"You've Got What It Takes", "You're Neat", "Spectacular Work", "You're #1", "Remarkable", "You're a Winner", "Beautiful", 
			"Clever", "You're So Kind", "Wow!", "Magnificent!", "You're Sharp", "Great Discovery", "You're Very Responsible", 
			"Brilliant", "Thanks for Helping", "You've Earned My Respect", "You're a Pleasure to Know", "You're Very Talented", 
			"How Original", "Very Brave", "Congratulations!", "You're a Champ", "You're Super", "You Figured It Out", "Right On!", 
			"You Make Me Smile", "You're the Greatest!"};

	public Praise(UserBot bot, Message message) {	// constructor
		super(bot, message, Command.PRAISE.names);
	}
	
	@Override
	public String helpMessage() {
		return helpBuilder("", "Just in case you need some praising.");
	}

	@Override
	protected void execute(String input) throws Exception {
		println(praise());
	}
		
	public static String praise() {
		return praises[rand.nextInt(praises.length)];
	}
}
