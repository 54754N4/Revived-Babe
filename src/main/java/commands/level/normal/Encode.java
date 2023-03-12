package commands.level.normal;

import bot.hierarchy.UserBot;
import commands.hierarchy.DiscordCommand;
import commands.name.Command;
import lib.encode.Encoder;
import net.dv8tion.jda.api.entities.Message;

public class Encode extends DiscordCommand {

	public Encode(UserBot bot, Message message) {
		super(bot, message, Command.ENCODE.names);
	}

	@Override
	public String helpMessage() {
		return helpBuilder("<name> <input>",
				"-en or --encode\tencodes plaintext <input>",
				"-de or --decode\tdecodes encoded <input>",
				"-l or --list\tlists all the encoders available",
				"--type=T\twhere T specifies the number of the type of encoder",
				"Makes me encode or decode text.");
	}

	@Override
	public void execute(String input) throws Exception {
		Encoder.Type[] types = Encoder.Type.values();
		if (hasArgs("-l", "--list")) {
			printItemsIndexed(types);
			return;
		}
		int type = Integer.parseInt(getParams().getNamed().get("--type"));
		if (type < 0 || type > types.length) {
			println("Invalid type entered, required between: [0, %d]", types.length-1);
			return;
		}
		Encoder.Type encoderType = types[type];
		boolean encode = hasArgs("-en", "--encode"),
				decode = hasArgs("-de", "--decode");
		if (!encode && !decode) {
			println("Tell me if i need to decode or encode.");
			return;
		}
		String result = encode ?
				encoderType.encoder.apply(input) :
				encoderType.decoder.apply(input);
		println(codeBlock(result));
	}

}
