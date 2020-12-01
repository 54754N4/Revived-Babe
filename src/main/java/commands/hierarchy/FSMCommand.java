package commands.hierarchy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import bot.model.UserBot;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public abstract class FSMCommand extends DiscordCommand {
	protected final State start, end;
	private long issuer;
	private State state;	// tracks current state
	
	public FSMCommand(UserBot bot, Message message, String[] names) {
		super(bot, message, names);
		state = start = new State.Builder().build();
		end = new State.Builder().build();
	}
	
	// FSM don't need execute so replace with
	protected void setup() {}
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		if (event.getAuthor().getIdLong() != issuer)
			return;
		state = state.check(event);
		if (state == end)
			bot.getJDA().removeEventListener(this);
	}
	
	@Override
	protected void execute(String input) throws Exception {
		bot.getJDA().addEventListener(this);
		issuer = message.getAuthor().getIdLong();
		setup();
	}

	/* Since thread would have died, override to print independently and
	 * not use default print-on-command-death  */
	@Override
	public void println() {
		printlnIndependently();
	}
	
	@Override
	public void print(String format, Object... args) { 
		printIndependently(format, args);
	}
	
	@Override
	public void println(String format, Object... args) {
		printlnIndependently(format, args);
	}
	
	protected static class State {
		private Collection<Transition> transitions;
		
		private State(Collection<Transition> transitions) {
			this.transitions = transitions;
		}
		
		public State addTransition(Transition transition) {
			transitions.add(transition);
			return this;
		}
		
		private State check(GuildMessageReceivedEvent event) {
			for (Transition transition : transitions)
				if (transition.occured(event))
					return transition.handle(event);
			return this;
		}
		
		public static class Builder {
			private List<Transition> transitions = new ArrayList<>();
			
			public Builder addTransition(Transition transition) {
				transitions.add(transition);
				return this;
			}
			
			public State build() {
				return new State(transitions);
			}
		}
	}
	
	protected static class Transition {
		private final Predicate<GuildMessageReceivedEvent> predicate;
		private final State nextState;
		private final Consumer<GuildMessageReceivedEvent> action;
		
		private Transition(Predicate<GuildMessageReceivedEvent> predicate, State nextState, Consumer<GuildMessageReceivedEvent> action) {
			this.predicate = predicate;
			this.nextState = nextState;
			this.action = action;
		}
		
		private boolean occured(GuildMessageReceivedEvent event) {
			return predicate.test(event);
		}
		
		private State handle(GuildMessageReceivedEvent event) {
			action.accept(event);
			return nextState;
		}
		
		public static class Builder {
			private Predicate<GuildMessageReceivedEvent> predicate;
			private State nextState;
			private Consumer<GuildMessageReceivedEvent> action = event -> {};		// by default no action
			
			public Builder setCondition(Predicate<GuildMessageReceivedEvent> predicate) {
				this.predicate = predicate;
				return this;
			}
			
			public Builder setConditionNot(Predicate<GuildMessageReceivedEvent> predicate) {
				this.predicate = predicate.negate();
				return this;
			}
			
			public Builder setNextState(State nextState) {
				this.nextState = nextState;
				return this;
			}
			
			public Builder setAction(Consumer<GuildMessageReceivedEvent> action) {
				this.action = action;
				return this;
			}
			
			public Builder setPassthrough() {
				predicate = event -> true;
				return this;
			}
			
			public Builder setDisabled() {
				predicate = event -> false;
				return this;
			}
			
			public Transition build() {
				if (predicate == null)
					throw new IllegalArgumentException("How can a FSM transition to with a null predicate ? =v");
				if (nextState == null)
					throw new IllegalArgumentException("How can a FSM transition to a null state ? =v");
				return new Transition(predicate, nextState, action);
			}
		}
	}
}