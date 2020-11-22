package commands.level.normal;

import java.util.List;

import bot.model.UserBot;
import commands.model.DiscordCommand;
import commands.name.Command;
import lib.StringLib;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

public class Prune extends DiscordCommand {

	public Prune(UserBot bot, Message message) {
		super(bot, message, Command.PRUNE.names);
	}

	@Override
	public String helpMessage() {
		return helpBuilder("<integer> [<mentions>]", 
				"# Args",
				"-a or --all\tmakes me delete everything in a text channel",
				"Deletes previous <integer> messages. You can optionally mention to target specific people.");
	}

	@Override
	protected void execute(String input) throws Exception {
		boolean bypass = hasArgs("-a", "--all");
		if (!StringLib.isInteger(input) && !bypass) {
			println("Tell me how many messages to delete..");
			return;
		}
		Message last = message;
		List<Message> messages;
		int count = 0, total = bypass ? 1 : Integer.parseInt(input);
		deleting_loop: do {
			messages = channel.getHistoryBefore(last, 100).complete().getRetrievedHistory();
			if (mentioned.users.size() == 0) {
				for (Message message : messages) {
					message.delete().queue();
					count++;
					if (count == total && !bypass) break deleting_loop;
				}
			} else for (final Message message : messages) {
				for (User user : mentioned.users) {
					if (user.getIdLong() == message.getAuthor().getIdLong()) {
						message.delete().queue();
						count++;
						if (count == total && !bypass) break deleting_loop;
					}
				}
			}
			if (messages.size() != 0)
				last = messages.get(messages.size()-1);
		} while (messages.size() != 0);
		message.delete().queue();
	}

}
