package commands.model;

import java.util.ArrayList;
import java.util.Collection;

import lib.StringLib;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class Mentions {
	private final Collection<User> users;
	private final Collection<Member> members;
	private final Collection<TextChannel> channels;
	private final Collection<Role> roles;
	private final String[] filteredMessage;
	
	public Mentions(Message message, String command) {
		Collection<Member> tMembers = new ArrayList<>(); 
		Collection<TextChannel> tChannels = new ArrayList<>(); 
		Collection<Role> tRoles = new ArrayList<>();
		users = message.getMentionedUsers();
		try { tMembers = message.getMentionedMembers(); }	// might fail if not in guild (e.g. private chat) 
		catch (IllegalStateException e) {}
		finally { members = tMembers; }
		try { tChannels = message.getMentionedChannels(); }
		catch (IllegalStateException e) {}
		finally { channels = tChannels; }
		try { tRoles = message.getMentionedRoles(); }
		catch (IllegalStateException e) {}
		finally { roles = tRoles; }
		filteredMessage = filter(command);
	}

	private String[] filter(String command) {
		String match;
		for (User user : users)
			if (command.contains(match = "@"+user.getName())) 
				command = StringLib.replaceAll(command, match, "");
		for (Role role : roles)
			if (command.contains(match = "@"+role.getName()))
				command = StringLib.replaceAll(command, match, "");
		for (TextChannel channel : channels) 
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