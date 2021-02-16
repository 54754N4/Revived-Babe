package commands.level.normal.role;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import bot.hierarchy.UserBot;
import commands.hierarchy.DiscordCommand;
import commands.name.Command;
import discord.ServerManager;
import discord.ServerManager.InvitesParser;
import discord.ServerRestAction;
import discord.ServerSetting;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

public class Server extends DiscordCommand {
	private static final String DOWNLOAD_PATH = System.getProperty("user.dir") + "/download";
	private ServerManager manager;
	
	public Server(UserBot bot, Message message) {
		super(bot, message, Command.SERVER.names);
		allowRole("Server Manager");
	}

	@Override
	public String helpMessage() {
		return helpBuilder("<category_arg> <input>",
			"# Category Args",
			"-na or --name\thandles guild name",
			"-de or --description\thandles guild description",
			"-ba or --banner\thandles guild banner",
			"-ic or --icon\thandles guild icon",
			"-sp or --splash\thandles guild splash image",
			"-ch or --channel\thandles guild system channel",
			"-af or --afk\thandles guild AFK voice channel",
			"-va or --vanity\thandles guild vanity code",
			"-ti or --timeout\thandles guild AFK timeouts",
			"-no or --notification\thandles guild notification levels",
			"-ex or --explicit\thandles guild explicit content levels",
			"-re or --region\thandles guild region server",
			"-mf or --mfa\thandles guild required MFA level",
			"-ve or --verification\thandles guild verification level",
			"-in or --invite\thandles guild invites",
			"# Action Args",
			"-c or --current\tdisplays currently set setting",
			"-r or --remove\tremoves entries (for Invites)",
			"-te or --temp or --temporary\tonly retrieves temporary matching invites",
			"By default, if you give only a category argument, all possible choices will be listed");
	}

	@Override
	protected void execute(String input) throws Exception {
		manager = ServerManager.manage(guild);
		if (hasArgs("-na", "--name")) 
			handleName(input);
		else if (hasArgs("-de", "--description")) 
			handleDescription(input);
		else if (hasArgs("-ba", "--banner")) 
			handleImageInput(input, guild::getBannerUrl, manager::setBanner);
		else if (hasArgs("-ic", "--icon")) 
			handleImageInput(input, guild::getIconUrl, manager::setIcon);
		else if (hasArgs("-sp", "--splash")) 
			handleImageInput(input, guild::getSplashUrl, manager::setSplash);
		else if (hasArgs("-ch", "--channel")) 
			handleSystemChannel(input);
		else if (hasArgs("-af", "--afk")) 
			handleAFK(input);
		else if (hasArgs("-va", "--vanity"))
			handleVanityCode(input);
		else if (hasArgs("-ti", "--timeout")) 
			handleServerEnum(input, manager::timeouts, guild::getAfkTimeout);
		else if (hasArgs("-no", "--notification")) 
			handleServerEnum(input, manager::notificationLevels, guild::getDefaultNotificationLevel);
		else if (hasArgs("-ex", "--explicit")) 
			handleServerEnum(input, manager::explicitContentLevels, guild::getExplicitContentLevel);
		else if (hasArgs("-re", "--region")) 
			handleServerEnum(input, manager::regions, guild::getRegion);
		else if (hasArgs("-mf", "--mfa")) 
			handleServerEnum(input, manager::mfaLevels, guild::getRequiredMFALevel);
		else if (hasArgs("-ve", "--verification")) 
			handleServerEnum(input, manager::verificationLevels, guild::getVerificationLevel);
		else if (hasArgs("-in", "--invite")) 
			manager.retrieveInvites(this::onRetrieveInvites);
		else 
			print("Give me params or use -h for help.");
	}
	
	/* Convenience methods */

	private boolean hasAttachments() {
		return message.getAttachments().size() != 0;
	}
	
	private boolean wantsCurrent() {
		return hasArgs("-c", "--current");
	}
	
	private void giveMe(String what) {
		println("Give me a %s to set for the guild.", what);
	}
	
	/* Implementation */
	
	private void handleName(String input) {
		if (wantsCurrent())
			println(guild.getName());
		else if (input.equals("")) 
			giveMe("name");
		else 
			manager.setName(input)
				.applyChanges();
	}
	
	private void handleDescription(String input) {
		if (wantsCurrent())
			println(guild.getDescription());
		else if (input.equals(""))
			giveMe("description");
		else
			manager.setDescription(input)
				.applyChanges();
	}
	
	private void handleImageInput(
			String input, 
			Supplier<Object> currentSupplier,
			Function<Icon, ServerRestAction> applier) throws IOException, InterruptedException, ExecutionException {
		if (wantsCurrent())
			println(currentSupplier.get().toString());
		else if (input.equals("") && !hasAttachments())
			giveMe("url or attachment");
		else if (hasAttachments())
			applier.apply(
				Icon.from(
					message.getAttachments()
						.get(0)
						.downloadToFile(DOWNLOAD_PATH)
						.get()))
				.applyChanges();
		else
			applier.apply(Icon.from(new URL(input).openStream()))
				.applyChanges();
	}
	
	private void handleSystemChannel(String input) {
		if (wantsCurrent())
			println(guild.getSystemChannel().getAsMention());
		else if (mentioned.channels.size() == 0)
			giveMe("new system channel");
		else 
			manager.setSystemChannel(mentioned.channels.iterator().next())
				.applyChanges();
	}

	private void handleAFK(String input) {
		if (wantsCurrent())
			println(inline(guild.getAfkChannel().getName()));
		else if (input.equals(""))
			giveMe("new AFK channel");
		else 
			manager.setAfkChannel(
					message.getJDA()
						.getVoiceChannelsByName(input, true)
						.get(0))
				.applyChanges();
	}
	
	private void handleVanityCode(String input) {
		if (wantsCurrent())
			println("Code: `%s`%nURL: %s", guild.getVanityCode(), guild.getVanityUrl());
		else if (input.equals(""))
			giveMe("new vanity code");
		else 
			manager.setVanityCode(input)
				.applyChanges();
	}
	
	private <T extends Enum<T>> void handleServerEnum(String input, Supplier<ServerSetting<T>> settingsSupplier, Supplier<T> currentSupplier) {
		List<T> elements = settingsSupplier.get().retrieve();
		if (wantsCurrent())
			println(currentSupplier.get().toString());
		else if (input.equals(""))
			printItemsIndexed(elements);
		else
			settingsSupplier.get()
				.select(input)
				.applyChanges();
	}
	
	private void onRetrieveInvites(List<Invite> invites) {
		Stream<InvitesParser> stream = invites.stream()
			.map(manager::parseInvite)
			.filter(invite -> invite.matches(input));
		if (hasArgs("-te", "--temp", "--temporary"))
			stream.filter(InvitesParser::isTemporary);
		List<InvitesParser> leftovers = stream.collect(Collectors.toList());
		if (leftovers.size() == 0) {
			println("No invites found.");
			return;
		}
		stream = leftovers.stream();		// continue with matches
		if (hasArgs("-r", "--remove")) 
			stream.map(InvitesParser::getInvite)
				.map(Invite::delete)
				.forEach(AuditableRestAction::queue);
		else
			stream.map(InvitesParser::toEmbed)
				.map(channel::sendMessage)
				.forEach(MessageAction::queue);
	}
}
