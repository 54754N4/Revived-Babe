package commands.level.normal.role;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
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
import lib.StringLib;
import net.dv8tion.jda.api.Region;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.VoiceChannel;
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
		Guild guild = getGuild();
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
		else if (hasArgs("-mf", "--mfa")) 
			handleServerEnum(input, manager::mfaLevels, guild::getRequiredMFALevel);
		else if (hasArgs("-ve", "--verification")) 
			handleServerEnum(input, manager::verificationLevels, guild::getVerificationLevel);
		else if (hasArgs("-re", "--region")) 
			handleRegion(input);
		else if (hasArgs("-in", "--invite")) 
			manager.retrieveInvites(this::onRetrieveInvites);
		else 
			print("Give me params or use -h for help.");
	}
	
	/* Convenience methods */

	private boolean hasAttachments() {
		return getMessage().getAttachments().size() != 0;
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
			println(getGuild().getName());
		else if (input.equals("")) 
			giveMe("name");
		else {
			manager.setName(input)
				.applyChanges();
			println("Set name to : `%s`", input);
		}
	}
	
	private void handleDescription(String input) {
		if (wantsCurrent())
			println(getGuild().getDescription());
		else if (input.equals(""))
			giveMe("description");
		else {
			manager.setDescription(input)
				.applyChanges();
			println("Set description to : `%s`", input);
		}
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
					getMessage().getAttachments()
						.get(0)
						.downloadToFile(DOWNLOAD_PATH)
						.get()))
				.applyChanges();
		else {
			applier.apply(Icon.from(new URL(input).openStream()))
				.applyChanges();
			println("Set new icon.", input);
		}
	}
	
	private void handleSystemChannel(String input) {
		if (wantsCurrent())
			println(getGuild().getSystemChannel().getAsMention());
		else if (getMentions().getChannels().size() == 0)
			giveMe("new system channel");
		else {
			manager.setSystemChannel(getMentions().getChannels().iterator().next())
				.applyChanges();
			println("Set system channel to : %s", getMentions().getChannels().iterator().next().getAsMention());
		}
	}

	private void handleAFK(String input) {
		if (wantsCurrent())
			println(inline(getGuild().getAfkChannel().getName()));
		else if (input.equals(""))
			giveMe("new AFK channel");
		else {
			VoiceChannel channel = getMessage().getJDA()
					.getVoiceChannelsByName(input, true)
					.get(0);
			manager.setAfkChannel(channel)
				.applyChanges();
			println("Set voice channel to : `%s`", channel.getName());
		}
	}
	
	private void handleVanityCode(String input) {
		if (wantsCurrent())
			println("Code: `%s`%nURL: %s", getGuild().getVanityCode(), getGuild().getVanityUrl());
		else if (input.equals(""))
			giveMe("new vanity code");
		else
			println("Cannot set vanity code anymore in v5");
	}
	
	private <T extends Enum<T>> void handleServerEnum(String input, Supplier<ServerSetting<T>> settingsSupplier, Supplier<T> currentSupplier) {
		List<T> elements = settingsSupplier.get().retrieve();
		if (wantsCurrent())
			println(currentSupplier.get().toString());
		else if (input.equals(""))
			printItemsIndexed(elements);
		else {
			settingsSupplier.get()
				.select(input)
				.applyChanges();
			Optional<T> element = settingsSupplier.get().find(input);
			if (element.isPresent()) 
				println("Updated to : %s", element.get());
			else 
				println("No elements matched");
		}
	}
	
	private void handleRegion(String input) {
		AudioChannel channel = getMessage().getMember().getVoiceState().getChannel();
		if (channel == null)
			println("You have to be in a voice channel to change it's region.");
		else if (wantsCurrent())
			println(channel.getRegion().toString());
		else if (input.equals(""))
			printItemsIndexed(Stream.of(Region.values())
					.filter(r -> !r.isVip())
					.toArray());
		else { 
			Region found = null;
			for (Region region : Region.values())
				if (!region.isVip() && StringLib.matchSimplified(region.getName(), input))
					found = region;
			if (found == null)
				println("No regions matched your selection: "+input);
			else {
				channel.getManager()
					.setRegion(found)
					.queue();
				println("Region changed to: "+found);
			}
		}
	}
	
	private void onRetrieveInvites(List<Invite> invites) {
		Stream<InvitesParser> stream = invites.stream()
			.map(manager::parseInvite)
			.filter(invite -> invite.matches(getInput()));
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
				.map(getChannel()::sendMessageEmbeds)
				.forEach(MessageAction::queue);
	}
}
