package discord;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import lib.StringPair;
import lib.StringLib;
import lib.messages.ValidatingEmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Guild.ExplicitContentLevel;
import net.dv8tion.jda.api.entities.Guild.MFALevel;
import net.dv8tion.jda.api.entities.Guild.NotificationLevel;
import net.dv8tion.jda.api.entities.Guild.Timeout;
import net.dv8tion.jda.api.entities.Guild.VerificationLevel;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

public class ServerManager extends ServerRestAction {
	private final Guild guild;
	private final ServerSetting<Timeout> timeouts;
	private final ServerSetting<NotificationLevel> notificationLevels;
	private final ServerSetting<ExplicitContentLevel> explicitContentLevels;
	private final ServerSetting<MFALevel> mfaLevels;
	private final ServerSetting<VerificationLevel> verificationLevels;
	
	private ServerManager(final Guild guild) {
		super(guild.getManager());
		this.guild = guild;
		timeouts = new ServerSetting<>(Timeout.class, manager::setAfkTimeout, manager);
		mfaLevels = new ServerSetting<>(MFALevel.class, manager::setRequiredMFALevel, manager);
		verificationLevels = new ServerSetting<>(VerificationLevel.class, manager::setVerificationLevel, manager);
		notificationLevels = new ServerSetting<>(NotificationLevel.class, manager::setDefaultNotificationLevel, manager);
		explicitContentLevels = new ServerSetting<>(ExplicitContentLevel.class, manager::setExplicitContentLevel, manager);
	}
	
	public static ServerManager manage(Guild guild) {
		return new ServerManager(guild);
	}
	
	public ServerManager setName(String name) {
		manager.setName(name);
		return this;
	}
	
	public ServerManager setDescription(String description) {
		manager.setDescription(description);
		return this;
	}
	
	public ServerManager setBanner(Icon banner) {
		manager.setBanner(banner);
		return this;
	}
	
	public ServerManager setIcon(Icon icon) {
		manager.setIcon(icon);
		return this;
	}
	
	public ServerManager setSplash(Icon splash) {
		manager.setSplash(splash);
		return this;
	}
	
	public ServerManager setSystemChannel(TextChannel channel) {
		manager.setSystemChannel(channel);
		return this;
	}
	
	public ServerManager setAfkChannel(@Nonnull VoiceChannel channel) {
		manager.setAfkChannel(channel);
		return this;
	}

	public ServerSetting<Timeout> timeouts() {
		return timeouts;
	}
	
	public ServerSetting<NotificationLevel> notificationLevels() {
		return notificationLevels;
	}
	
	public ServerSetting<ExplicitContentLevel> explicitContentLevels() {
		return explicitContentLevels;
	}
	
	public ServerSetting<MFALevel> mfaLevels() {
		return mfaLevels;
	}
	
	public ServerSetting<VerificationLevel> verificationLevels() {
		return verificationLevels;
	}
	
	public InvitesParser parseInvite(Invite invite) {
		return new InvitesParser(invite);
	}
	
	public ServerManager retrieveInvites(@Nullable Consumer<List<Invite>> handler) {
		guild.retrieveInvites().queue(handler);
		return this;
	}
	
	public static class InvitesParser {
		private final String[] FIELD_NAMES = { "Code", "Max Age", "Uses/Max", "By", "Channel", "URL", "Type", "Temp", "Creation" },
				fields;			// actual values
		private Invite invite;
		
		private InvitesParser(Invite invite) {
			this.invite = invite;
			fields = getFields(invite);
		}
		
		/* Convenience methods */
		
		public static String[] getFields(Invite invite) {
			return new String[] {
				invite.getCode(), 
				Integer.toString(invite.getMaxAge()), 
				invite.getUses()+"/"+invite.getMaxUses(),
				invite.getInviter().getName(),
				invite.getChannel().getName(),
				invite.getUrl(),
				invite.getType().toString(),
				invite.isTemporary() ? "Temp" : "Permanent",
				invite.getTimeCreated().toString()
			};
		}
		
		public Invite getInvite() {
			return invite;
		}
		
		public boolean matches(String input) {
			return StringLib.matchesSimplified(input, fields);
		}
		
		public boolean isTemporary() {
			return invite.isTemporary();
		}
		
		/* Iterator + parse methods */
		
		public <I, F> F forEachData(
				Supplier<? extends I> initialiser, 
				BiFunction<StringPair, I, ? extends I> accumulator,
				Function<I, ? extends F> finaliser) {
			I builder = initialiser.get();
			StringPair entry = new StringPair();		// reuse same instance
			for (int i=0; i<fields.length; i++) {
				entry.key = FIELD_NAMES[i];
				entry.value = fields[i];
				builder = accumulator.apply(entry, builder);
			}
			return finaliser.apply(builder);
		}
		
		public Map<String, String> toDict() {
			return forEachData(
				HashMap<String, String>::new,
				(entry, map) -> {
					map.put(entry.key, entry.value);
					return map;
				},
				m -> m
			);
		}
		
		public MessageEmbed toEmbed() {
			return forEachData(
				ValidatingEmbedBuilder::new,
				(entry, builder) -> builder.addField(entry.key, entry.value),
				ValidatingEmbedBuilder::build
			);
		}
		
		@Override
		public String toString() {
			return forEachData(
				StringBuilder::new,
				(entry, sb) -> sb.append(String.format(entry.key+": %s%n", entry.value)),
				StringBuilder::toString
			);
		}
	}
}