package commands.level.normal;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import bot.hierarchy.UserBot;
import commands.hierarchy.Command;
import commands.hierarchy.DiscordCommand;
import commands.model.Invoker.Reflector;
import commands.model.Invoker.Reflector.Type;
import net.dv8tion.jda.api.entities.Message;

public class Help extends DiscordCommand {

	public Help(UserBot bot, Message message) {
		super(bot, message, commands.name.Command.HELP.names);
	}

	@Override
	public String helpMessage() {
		return helpBuilder("",
			"# Args",
			"-g or --general\tgives a general help",
			"-gl or --global\tgives info on global parameters",
			"-r or --read\texplains how to read the man page",
			"-f or --full \t Lists all current commands' help",
			"Or, prints the help of the commands that match parameter;",
			"Otherwise prints all available commands and their possible names if no argument is given.");
	}

	@Override
	protected void execute(String input) throws Exception {
		Map<List<String>, Class<? extends Command>> dict = isOwner() ? Reflector.admin : Reflector.normal;
		Type type = isOwner() ? Type.ADMIN : Type.NORMAL;
		if (hasArgs("-g", "--general")) print(helpGeneral());
		if (hasArgs("-gl", "--global")) print(helpGlobal());
		if (hasArgs("-r", "--read")) println(helpRead());
		if (hasArgs("-f", "--full")) 
			for (Entry<List<String>, Class<? extends Command>> entry : dict.entrySet()) 
				print(instantiate(Reflector.find(entry.getKey().get(0), type)).helpMessage());
		else if (!input.equals(""))
			for (Entry<List<String>, Class<? extends Command>> entry : dict.entrySet())
				if (entry.getKey().stream().anyMatch(key -> key.contains(input)))
					print(markdown(Arrays.toString(entry.getKey().toArray())));
		if (input.equals("") && !hasArgs("-g", "--general", "-gl", "-r", "--read", "--global", "-f", "--full")) {
			printlnIndependently("**__>> Commands List:__**  (Total = %d)", dict.size());
			for (List<String> key : dict.keySet()) 
				print(markdown(Arrays.toString(key.toArray())));
		}
	}
	
	private static Command instantiate(Class<? extends Command> cls) throws NoSuchMethodException, SecurityException, ReflectiveOperationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Constructor<? extends Command> constructor = cls.getConstructor(UserBot.class, Message.class);
		return constructor.newInstance(null, null);
	}
	
	private static String helpify(String... lines) {
		StringBuilder sb = new StringBuilder();
		for (String line : lines) sb.append(line+"\n");
		return sb.toString();
	}
	
	private static String helpGeneral() {
		return helpify("**__>> GENERAL HELP__**",
			"Babe's _prefixes_ can be listed just by mentioning her.",
			"\tThere's 3 ways to execute commands so 3 types of _prefixes_. To put it simply there's the **__SLOW__**, **__FAST__**, and **__MENTION__** prefixes, here's an example of each respectively :",
			"\t" + inline("hey babe help -h"),
			"\t" + inline("..help -h"),
			"\t" + inline("@Babe help -h"),
			"\n\tIn the previous examples, `-h` is what we call a global arg/parameter since every command has it, so to get any other command's docs all you need to do is `<cmd> -h`.\nOr you could just call the help command and pass the command name as argument like this `help <cmd>`."+markdown("# Note\n\tEvery command executes in a seperate thread."));
	}
	
	private static String helpGlobal() {
		return helpify("**__>> Global Params__**",
			inline("-h or --help")+"\tPrints the command's help/manual.",
			inline("--timed")+"\t\t\tPrints how long the command took to execute.",
			inline("-ed or --edit")+"\tPrints its output by editing its previously sent messages.",
			inline("-rep or --reply")+"\tSends output of command as a private message reply.",
			inline("-d or --delete")+"\tDeletes your msg that invoked the command.",
			inline("-s or --silent")+"\tPrevents the command from outputting anything.",
			inline("--after=<seconds>")+"\tExecutes the command after delay in seconds.");
	}
	
	/*
		DELAYED("--after"),
		SCHEDULED("--every");
	 */
	
	private static String helpRead() {
		return helpify("**__>> Reading Help__**",
			"Every command's help/documentation will always start like this :",
			"```markdown\n#[name0,name1,name2] <- this just defines the different names you can use to call this command```",
			"Now to understand how parameters work, all you need to know is that there's only **2 types**; __named__ vs __unnamed__ params.",
			"\n**_Unnamed_** parameters always start with a single _hyphen_ `-`, and just need to be written as is. (e.g. `-h` `-t` `-yt` etc..)",
			"\n**_Named_** parameters can optionaly take a value as well, and these always start with double _hypens_ `--`.",
			"Here's an example for both kinds of _named_ params :",
			"\t- One that takes a value (1337 in this case) : `--count=1337`",
			"\t- And one that doesn't take any : \t\t\t\t\t`--timed`");
	}

}
