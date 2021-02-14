package commands.hierarchy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import bot.hierarchy.UserBot;
import lib.StringLib;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;

public abstract class RoleCommand extends PrintCommand {
	private static final List<Role> EMPTY = new ArrayList<>();
	private Set<String> allowedRoles;
	
	public RoleCommand(UserBot bot, Message message, String[] names) {
		super(bot, message, names);
		allowedRoles = new HashSet<>();
	}

	protected String[] getAllowedRoles() {
		return allowedRoles.toArray(new String[0]);
	}
	
	protected RoleCommand allowRole(String...roles) {
		for (String role : roles)
			allowedRoles.add(role);
		return this;
	}
	
	protected boolean allowAll() {
		return allowedRoles.size() == 0;
	}
	
	protected List<Role> getCallerRoles() {
		return message == null ? 
				EMPTY :
				message.getMember() == null ? 
						EMPTY : 
						message.getMember().getRoles();
	}
	
	protected boolean callerAllowed() {
		if (allowAll()) 
			return true;
		for (Role role : getCallerRoles())
			if (allowedRoles.contains(role.getName()))
				return true;
		return false;
	}
	
	protected boolean callerHasRole(Role role) {
		for (Role r : getCallerRoles()) 
			if (r.equals(role)) 
				return true;
		return false;
	}
	
	protected void addRoles(Member member, Collection<Role> roles) {
		getGuild().modifyMemberRoles(member, roles, new ArrayList<>()).queue();
	}
	
	protected void removeRoles(Member member, Collection<Role> roles) {
		getGuild().modifyMemberRoles(member, new ArrayList<>(), roles).queue();
	}
	
	protected List<Role> getRoles(String match, boolean createIfNonExistant) {
		List<Role> roles = guild.getRolesByName(match, true);
		if (roles.size() == 0 && createIfNonExistant)
			roles.add(guild.createRole().setName(StringLib.capitalize(match)).complete());
		return roles;
	}
}