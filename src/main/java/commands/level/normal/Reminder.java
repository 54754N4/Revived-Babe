package commands.level.normal;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Collection;

import backup.Reminders;
import bot.hierarchy.UserBot;
import commands.hierarchy.DiscordCommand;
import commands.name.Command;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

public class Reminder extends DiscordCommand {
	private static final String TIME_PATTERN = "dd/MM/yyyy@HH:mm:ss";
	private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(TIME_PATTERN),
			HUMAN_READABLE = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL);
	
	public Reminder(UserBot bot, Message message) {
		super(bot, message, Command.REMINDER.names);
	}

	@Override
	public String helpMessage() {
		return helpBuilder("<message>",
			"--format=F\twhere F is the time format to use",
			"--time=T\twhere T stands for the time",
			"-l or --list\tlists scheduled reminders",
			"-c or --clear\tremoves all scheduled reminders",
			"Makes me send a message reminder at a specific time in this default format : "+TIME_PATTERN);
	}

	@Override
	public void execute(String input) throws Exception {
		if (hasArgs("-l", "--list")) {
			Collection<Reminders.Reminder> reminders = Reminders.getReminders();
			if (reminders.size() == 0)
				println("No reminders scheduled.");
			else
				printItems(Reminders.getReminders());
			return;
		}
		if (hasArgs("-c", "--clear")) {
			Reminders.clear();
			println("Cleared all reminders.");
			return;
		}
		if (!hasArgs("--time")) {
			print("Please specify the time T like this : `--time=T`, for example : ```..rem --time=09/02/2021@22:30:00```");
			return;
		}
		String time = getParams().getNamed().get("--time");
		DateTimeFormatter formatter = TIME_FORMATTER;
		if (hasArgs("--format"))
			formatter = DateTimeFormatter.ofPattern(getParams().getNamed().get("--format"));
		LocalDateTime date = LocalDateTime.parse(time, formatter);
		input = inline(input);
		for (Member user : getMentions().getMembers())
			input += " " + user.getAsMention();
		Reminders.add(new Reminders.Reminder.Builder()
				.setMessage(input)
				.setDate(date)
				.setChannel(getChannel())
				.build());
		println("Setup reminder for %s", HUMAN_READABLE.format(date.atZone(ZoneId.systemDefault())));
	}
}
