package commands.hierarchy.fsm;

import java.util.function.Consumer;
import java.util.function.Predicate;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Transition implements Cloneable, Comparable<Transition> {
	public static final int DEFAULT_PRIORITY = 0;
	private final Integer priority;
	private final Predicate<GuildMessageReceivedEvent> predicate;
	private final State nextState;
	private final Consumer<GuildMessageReceivedEvent> action;
	
	private Transition(int priority, Predicate<GuildMessageReceivedEvent> predicate, State nextState, Consumer<GuildMessageReceivedEvent> action) {
		this.priority = priority;
		this.predicate = predicate;
		this.nextState = nextState;
		this.action = action;
	}
	
	public int getPriority() {
		return priority;
	}
	
	boolean occured(GuildMessageReceivedEvent event) {
		return predicate.test(event);
	}
	
	public State handle(GuildMessageReceivedEvent event) {
		action.accept(event);
		return nextState;
	}
	
	@Override
	public int compareTo(Transition o) {
		return o.priority - priority;
	}
	
	@Override
	public Transition clone() {
		return new Transition(priority, predicate, nextState, action);
	}
	
	@Override
	public String toString() {
		return predicate.toString() + ": " + priority;
	}
	
	public static class Builder implements Cloneable {
		private Integer priority = DEFAULT_PRIORITY;
		private Predicate<GuildMessageReceivedEvent> predicate;
		private State nextState;
		private Consumer<GuildMessageReceivedEvent> action = event -> {};		// by default no action
		
		public Transition.Builder setPriority(int priority) {
			this.priority = priority;
			return this;
		}
		
		public Transition.Builder setCondition(Predicate<GuildMessageReceivedEvent> predicate) {
			this.predicate = predicate;
			return this;
		}
		
		public Transition.Builder setConditionNot(Predicate<GuildMessageReceivedEvent> predicate) {
			this.predicate = predicate.negate();
			return this;
		}
		
		public Transition.Builder setNextState(State nextState) {
			this.nextState = nextState;
			return this;
		}
		
		public Transition.Builder setAction(Consumer<GuildMessageReceivedEvent> action) {
			this.action = action;
			return this;
		}
		
		public Transition.Builder setPassthrough() {
			predicate = event -> true;
			return this;
		}
		
		public Transition.Builder setDisabled() {
			predicate = event -> false;
			return this;
		}
		
		public Transition build() {
			if (predicate == null)
				throw new IllegalArgumentException("How can a FSM transition to with a null predicate ? =v");
			if (nextState == null)
				throw new IllegalArgumentException("How can a FSM transition to a null state ? =v");
			return new Transition(priority, predicate, nextState, action);
		}
		
		@Override
		public Transition.Builder clone() {
			return new Builder()
					.setPriority(priority)
					.setCondition(predicate)
					.setAction(action)
					.setNextState(nextState);
		}
	}
}