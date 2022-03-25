package commands.hierarchy.fsm;

import java.util.function.Consumer;
import java.util.function.Predicate;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Transition implements Cloneable, Comparable<Transition> {
	public static final int DEFAULT_PRIORITY = 0;
	private final Integer priority;
	private final Predicate<MessageReceivedEvent> predicate;
	private final State nextState;
	private final Consumer<MessageReceivedEvent> action;

	private Transition(int priority, Predicate<MessageReceivedEvent> predicate, State nextState, Consumer<MessageReceivedEvent> action) {
		this.priority = priority;
		this.predicate = predicate;
		this.nextState = nextState;
		this.action = action;
	}
	
	// Accessors
	
	public int getPriority() {
		return priority;
	}
	
	public Predicate<MessageReceivedEvent> getPredicate() {
		return predicate;
	}

	public State getNextState() {
		return nextState;
	}

	public Consumer<MessageReceivedEvent> getAction() {
		return action;
	}
	
	// Transition methods
	
	boolean occured(MessageReceivedEvent event) {
		return predicate.test(event);
	}
	
	public State handle(MessageReceivedEvent event) {
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
		private Predicate<MessageReceivedEvent> predicate;
		private State nextState;
		private Consumer<MessageReceivedEvent> action = event -> {};		// by default no action
		
		public Transition.Builder setPriority(int priority) {
			this.priority = priority;
			return this;
		}
		
		public Transition.Builder setCondition(Predicate<MessageReceivedEvent> predicate) {
			this.predicate = predicate;
			return this;
		}
		
		public Transition.Builder setConditionNot(Predicate<MessageReceivedEvent> predicate) {
			this.predicate = predicate.negate();
			return this;
		}
		
		public Transition.Builder setNextState(State nextState) {
			this.nextState = nextState;
			return this;
		}
		
		public Transition.Builder setAction(Consumer<MessageReceivedEvent> action) {
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