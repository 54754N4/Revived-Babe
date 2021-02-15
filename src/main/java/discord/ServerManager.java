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

import lib.Consumers;
import lib.Pair;
import lib.messages.ValidatingEmbedBuilder;
import net.dv8tion.jda.api.Region;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Guild.ExplicitContentLevel;
import net.dv8tion.jda.api.entities.Guild.MFALevel;
import net.dv8tion.jda.api.entities.Guild.NotificationLevel;
import net.dv8tion.jda.api.entities.Guild.Timeout;
import net.dv8tion.jda.api.entities.Guild.VerificationLevel;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.GuildManager;

public class ServerManager {
	private final Guild guild;
	private final GuildManager manager;
	private final ServerSetting<Region> regions;
	private final ServerSetting<Timeout> timeouts;
	private final ServerSetting<NotificationLevel> notificationLevels;
	private final ServerSetting<ExplicitContentLevel> explicitContentLevels;
	private final ServerSetting<MFALevel> mfaLevels;
	private final ServerSetting<VerificationLevel> verificationLevels;
	
	private ServerManager(final Guild guild) {
		this.guild = guild;
		manager = guild.getManager();
		regions = new ServerSetting<>(Region.class, manager::setRegion);
		timeouts = new ServerSetting<>(Timeout.class, manager::setAfkTimeout);
		mfaLevels = new ServerSetting<>(MFALevel.class, manager::setRequiredMFALevel);
		verificationLevels = new ServerSetting<>(VerificationLevel.class, manager::setVerificationLevel);
		notificationLevels = new ServerSetting<>(NotificationLevel.class, manager::setDefaultNotificationLevel);
		explicitContentLevels = new ServerSetting<>(ExplicitContentLevel.class, manager::setExplicitContentLevel);
	}
	
	public static ServerManager manage(Guild guild) {
		return new ServerManager(guild);
	}
	
	public void applyChanges() {
		applyChanges(Consumers::ignore, Consumers::ignore);
	}
	
	public void applyChanges(@Nullable Consumer<? super Void> onSuccess) {
		applyChanges(onSuccess, Consumers::ignore);
	}
	
	public void applyChanges(
			@Nullable Consumer<? super Void> onSuccess, 
			@Nullable Consumer<? super Throwable> onFailure) {
		manager.queue(onSuccess, onFailure);
	}
	
	public ServerManager setAfkChannel(@Nonnull VoiceChannel channel) {
		manager.setAfkChannel(channel);
		return this;
	}
	
	public ServerManager setBanner(Icon banner) {
		manager.setBanner(banner);
		return this;
	}

	public ServerManager setDescription(String description) {
		manager.setDescription(description);
		return this;
	}
	
	public ServerManager setIcon(Icon icon) {
		manager.setIcon(icon);
		return this;
	}
	
	public ServerManager setName(String name) {
		manager.setName(name);
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

	public ServerManager setVanityCode(String code) {
		manager.setVanityCode(code);
		return this;
	}
	
	public ServerManager retrieveInvites(@Nullable Consumer<List<Invite>> handler) {
		guild.retrieveInvites().queue(handler);
		return this;
	}
	
	public InvitesParser parse(Invite invite) {
		return new InvitesParser(invite);
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
	
	public ServerSetting<Region> regions() {
		return regions;
	}
	
	public ServerSetting<MFALevel> mfaLevels() {
		return mfaLevels;
	}
	
	public ServerSetting<VerificationLevel> verificationLevels() {
		return verificationLevels;
	}
	
	public class InvitesParser {
		private final String[] FIELD_NAMES = { "Code", "Max Age", "Uses/Max", "By", "Channel", "URL", "Type", "Temp", "Creation" };
		
		private Invite invite;
		
		private InvitesParser(Invite invite) {
			this.invite = invite;
		}
		
		public <I, F> F forEachData(
				Supplier<? extends I> initialiser, 
				BiFunction<Pair, I, ? extends I> accumulator,
				Function<I, ? extends F> finaliser) {
			I builder = initialiser.get();
			String[] fields = getFields();
			Pair entry = new Pair();		// reuse same instance
			for (int i=0; i<FIELD_NAMES.length; i++) {
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
		
		/* Convenience methods */
		
		public String[] getFields() {
			return new String[] {
					invite.getCode(), 
					""+invite.getMaxAge(), 
					invite.getUses()+"/"+invite.getMaxUses(),
					invite.getInviter().getName(),
					invite.getChannel().getName(),
					invite.getUrl(),
					invite.getType().toString(),
					invite.isTemporary() ? "Temp" : "Permanent",
					invite.getTimeCreated().toString()
			};
		}
	}
}