package commands.level.normal;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

import bot.hierarchy.UserBot;
import commands.hierarchy.DiscordCommand;
import commands.name.Command;
import lib.Pair;
import lib.StringLib;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;

public class Hash extends DiscordCommand {
	public static final String DEFAULT_ALGORITHM = "MD5";

	public Hash(UserBot bot, Message message) {
		super(bot, message, Command.HASH.names);
	}

	@Override
	public String helpMessage() {
		return helpBuilder("<attachement>",
				"Hashes the attachement tied to the message",
				"<text>",
				"-l or --list\tmakes me list all available algorithms",
				"--algorithm=A\twhere A is the hash algorithm to use (default: MD5)",
				"Hashes all uploaded files, or your text input.");
	}

	@Override
	protected void execute(String input) throws Exception {
		List<String> algorithms = new ArrayList<>(Security.getAlgorithms("MessageDigest"));
		if (hasArgs("-l", "--list")) {
			printItemsIndexed(algorithms);
			return;
		}
		String algorithm = DEFAULT_ALGORITHM,
				value = getParams().getNamed().get("--algorithm");
		if (hasArgs("--algorithm")) {
			algorithm = StringLib.isInteger(value) ? 
					algorithms.get(Integer.parseInt(value)) :
					value;
			if (!algorithms.contains(algorithm)) {
				println("Unrecognized/unsupported hashing algorithm");
				return;
			}
		}
		if (hasAttachment())
			convertAttachments(algorithm, getMessage().getAttachments())
				.forEach(pair -> println("`%s`: ```%s```", pair.getFirst(), pair.getSecond()));
		else {
			try {
				println(markdown(lib.encode.Hash.hashString(algorithm, input)));
			} catch (NoSuchAlgorithmException e) {
				println("Unrecognized hash algorithm");
				return;
			}
		}
	}
	
	private Stream<Pair<String, String>> convertAttachments(String algorithm, List<Attachment> attachments) {
		return attachments.stream()
			.map(attachment -> {
				try {
					File file = attachment.getProxy().downloadToPath().get().toFile(); 
					String name = file.getName();
					return new Pair<>(name, file);
				} catch (InterruptedException | ExecutionException e) {
					return null;
				}
			})
			.filter(pair -> pair.getSecond() != null)
			.map(pair -> {
				try {
					String hash = lib.encode.Hash.hashString(algorithm, pair.getSecond());
					return new Pair<>(pair.getFirst(), hash);
				} catch (NoSuchAlgorithmException | IOException e) {
					return null;
				}
			})
			.filter(pair -> pair.getSecond() != null);
	}
}