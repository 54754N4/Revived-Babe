package commands.hierarchy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import lib.StringLib;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;

public interface RoleCommand extends ICommand {
	static final List<Role> EMPTY = new ArrayList<>();

	/* Do not override and simply return a new Set,
	 * otherwise every time you try to add a role
	 * it will forget all previously added roles.
	 * Create the Set somewhere else, and simply return
	 * the pointer in this method.
	 */
	Set<String> getAllowedRoles();
	
	default RoleCommand allowRole(String...roles) {
		for (String role : roles)
			getAllowedRoles().add(role);
		return this;
	}
	
	default boolean allowAll() {
		return getAllowedRoles().size() == 0;
	}
	
	default List<Role> getCallerRoles() {
		Message message = getMessage();
		return message == null ? 
				EMPTY :
				message.getMember() == null ? 
						EMPTY : 
						message.getMember().getRoles();
	}
	
	default boolean callerAllowed() {
		if (allowAll()) 
			return true;
		for (Role role : getCallerRoles())
			if (getAllowedRoles().contains(role.getName()))
				return true;
		return false;
	}
	
	default boolean callerHasRole(Role role) {
		for (Role r : getCallerRoles()) 
			if (r.equals(role)) 
				return true;
		return false;
	}
	
	default void addRoles(Member member, Collection<Role> roles) {
		getGuild().modifyMemberRoles(member, roles, new ArrayList<>()).queue();
	}
	
	default void removeRoles(Member member, Collection<Role> roles) {
		getGuild().modifyMemberRoles(member, new ArrayList<>(), roles).queue();
	}
	
	default List<Role> getRoles(String match, boolean createIfNonExistant) {
		Guild guild = getGuild();
		List<Role> roles = guild.getRolesByName(match, true);
		if (roles.size() == 0 && createIfNonExistant)
			roles.add(guild.createRole().setName(StringLib.capitalize(match)).complete());
		return roles;
	}
}