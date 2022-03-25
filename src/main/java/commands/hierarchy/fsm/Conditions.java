package commands.hierarchy.fsm;

import java.util.function.Predicate;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public final class Conditions {
	public static final Predicate<MessageReceivedEvent> 
		ALWAYS = event -> true,
		NEVER = event -> false,
		EXIT = event -> 
			event.getMessage().getContentDisplay().toLowerCase().startsWith("quit") || 
			event.getMessage().getContentDisplay().toLowerCase().startsWith("exit"),
		NOT_EXIT = EXIT.negate();
}