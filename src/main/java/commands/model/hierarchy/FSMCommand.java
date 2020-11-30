package commands.model.hierarchy;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import bot.model.UserBot;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public abstract class FSMCommand extends DiscordCommand {
	private State state;
	
	public FSMCommand(UserBot bot, Message message, String[] names) {
		super(bot, message, names);
		state = initialState();
	}
	
	// Force implementors to give us initial state
	protected abstract State initialState();
	
	public void attachListener() {
		bot.getJDA().addEventListener(this);
	}
	
	public void unattachListener() {
		bot.getJDA().removeEventListener(this);
	}
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		state = state.check(event);
	}

	public static class State {
		private List<Transition> transitions = new ArrayList<>();
		
		public State check(GuildMessageReceivedEvent event) {
			for (Transition transition : transitions)
				if (transition.occured(event))
					return transition.handle(event);
			return this;
		}
		
		public State addTransition(Transition transition) {
			transitions.add(transition);
			return this;
		}
		
		public static class Transition {
			private final Predicate<GuildMessageReceivedEvent> condition;
			private final Consumer<GuildMessageReceivedEvent> consumer;
			private final State to;
			
			private Transition(Predicate<GuildMessageReceivedEvent> condition, Consumer<GuildMessageReceivedEvent> consumer, State to) {
				this.condition = condition;
				this.consumer = consumer;
				this.to = to;
			}
			
			private boolean occured(GuildMessageReceivedEvent event) {
				return condition.test(event);
			}
			
			private State handle(GuildMessageReceivedEvent event) {
				consumer.accept(event);
				return to;
			}
		}
	}
}