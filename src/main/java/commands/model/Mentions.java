package commands.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Supplier;

import bot.hierarchy.UserBot;
import lib.StringLib;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;

public class Mentions {
	private final Collection<User> users;
	private final Collection<Member> members;
	private final Collection<TextChannel> channels;
	private final Collection<Role> roles;
	private final String[] filteredMessage;
	private final boolean trim;
	
	public Mentions(UserBot bot, Message message, String command) {
		users = retrieveSafely(message.getMentions()::getUsers);
		members = retrieveSafely(message.getMentions()::getMembers);
		channels = retrieveSafely(() -> message.getMentions().getChannels(TextChannel.class));
		roles = retrieveSafely(message.getMentions()::getRoles);
		filteredMessage = filterAllMentions(command);
		trim = trim(bot, command);
		if (trim) {
			users.removeIf(user -> user.getIdLong() == bot.getIdLong());
			members.removeIf(user -> user.getIdLong() == bot.getIdLong());
		}
	}
	
	/* Since bot can also execute commands through a mention;
	 * trim only if mentioned once at the start */
	private boolean trim(UserBot bot, String command) {
		String prefix = "@"+bot.getBotName();
		if (command.startsWith(prefix)) {
			// check if it isn't mentioned again
			int index = command.indexOf(prefix);
			return command.indexOf(prefix, index+prefix.length()) == -1;
		}
		return false;
	}
	
	private final <T> Collection<T> retrieveSafely(Supplier<Collection<T>> supplier) {
		try { 
			return new ArrayList<>(supplier.get()); 
		} catch (Exception e) { 
			return Collections.emptyList(); 
		}
	}

	private String[] filterAllMentions(String command) {
		String match;
		for (User user : users)
			if (command.contains(match = "@"+user.getName())) 
				command = StringLib.replaceAll(command, match, "");
		for (Role role : roles)
			if (command.contains(match = "@"+role.getName()))
				command = StringLib.replaceAll(command, match, "");
		for (GuildChannel channel : channels) 
			if (command.contains(match = "#"+channel.getName()))
				command = StringLib.replaceAll(command, match, "");
		String[] tokens = StringLib.split(command, ' ');
		for (int i=0; i<tokens.length; i++)
			tokens[i] = tokens[i].trim();
		return tokens;
	}
	
	public Collection<User> getUsers() {
		return users;
	}
	
	public Collection<Member> getMembers() {
		return members;
	}
	
	public Collection<TextChannel> getChannels() {
		return channels;
	}
	
	public Collection<Role> getRoles() {
		return roles;
	}
	
	public String[] getFilteredMessage() {
		return filteredMessage;
	}
}