package commands.level.normal;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import backup.Reminders;
import bot.hierarchy.UserBot;
import commands.hierarchy.DiscordCommand;
import commands.name.Command;
import lib.StringLib;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

public class Reminder extends DiscordCommand {
	private static final String TIME_PATTERN = "dd/MM/yyyy@HH:mm:ss";
	private static final ZoneId DEFAULT_ZONE = ZoneId.systemDefault();
	private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(TIME_PATTERN);
	
	public Reminder(UserBot bot, Message message) {
		super(bot, message, Command.REMINDER.names);
	}

	@Override
	public String helpMessage() {
		return helpBuilder("<message>",
			"--format=F\twhere F is the time format to use",
			"--time=T\twhere T stands for the time",
			"Makes me send a message reminder at a specific time in this default format : "+TIME_PATTERN);
	}

	@Override
	protected void execute(String input) throws Exception {
		if (!hasArgs("--time")) {
			print("Please specify the time T like this : `--time=T`, for example : ```..rem --time=09/02/2021@22:30:00```");
			return;
		}
		LocalDateTime date = hasArgs("--format") ?
				LocalDateTime.parse(
					getParams().getNamed().get("--time"),
					DateTimeFormatter.ofPattern(getParams().getNamed().get("--format"))) :
				LocalDateTime.parse(getParams().getNamed().get("--time"), TIME_FORMATTER);
		getLogger().info(input);
		for (Member user : getMentions().getMembers()) {
			getLogger().info("Replacing \""+"@"+user.getEffectiveName()+"\" with \""+ user.getAsMention());
			input = StringLib.replaceAll(input, "@"+user.getEffectiveName(), user.getAsMention());
		}
		getLogger().info(input);
		Reminders.Reminder reminder = new Reminders.Reminder(getMessage().getAuthor().getAsMention()+" "+input);
		Reminders.add(date, reminder, getChannel());
		println("Event@%s#%s: %s", date.toString(), getChannel().getName(), reminder.message);
	}
	
	public static LocalDateTime getTime(long fromSeconds) {
		return LocalDateTime.ofInstant(Instant.ofEpochSecond(fromSeconds), DEFAULT_ZONE);
	}
}
