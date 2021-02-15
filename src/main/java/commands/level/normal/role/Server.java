package commands.level.normal.role;

import bot.hierarchy.UserBot;
import commands.hierarchy.DiscordCommand;
import commands.name.Command;
import net.dv8tion.jda.api.entities.Message;

public class Server extends DiscordCommand {

	public Server(UserBot bot, Message message) {
		super(bot, message, Command.SERVER.names);
		allowRole("Server Manager");
	}

	@Override
	public String helpMessage() {
		return helpBuilder("<category_arg> <input>",
			"# Category Args",
			"-re or --region\thandles server region",
			"-n or --name\thandles server name",
			"-a or --afk\thandles moving afks timeout",
			"-ic or --icon\thandles the server's icon (by default sets new one by URL)",
			"-in or --invite\thandles server invites",
//			"-u or --usage\tdisplays bot JVM metrics",
			"# Args (not for all categories, use when sensible)",
			"-r or --remove\tremoves entries (for Invites)",
			"-t or --temp or --temporary\tonly retrieves temporary matching invites",
			"-f or --file\tretrieves input as a file (for Icon)",
			"-c or --current\tdisplays currently set setting",
			"By default, if the command only contains a category argument AND there's multiple choices for the setting, they'll be listed");
	}

	@Override
	protected void execute(String input) throws Exception {
		println("You were allowed to execute this cmd nice.");
	}

}
