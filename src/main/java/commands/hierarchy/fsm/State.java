package commands.hierarchy.fsm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class State implements Cloneable {
	private List<Transition> transitions;
	private boolean sort;
	
	private State(List<Transition> transitions) {
		this(transitions, true);
	}
	
	private State(List<Transition> transitions, boolean sort) {
		this.transitions = transitions;
		this.sort = sort;
	}
	
	public State addTransition(Transition transition) {
		transitions.add(transition);
		sort = true;
		return this;
	}
	
	public State check(GuildMessageReceivedEvent event) {
		if (sort) {
			Collections.sort(transitions);
			sort = false;
		}
		for (Transition transition : transitions) 
			if (transition.occured(event))
				return transition.handle(event);
		return this;
	}
	
	@Override
	public State clone() {
		return new State(transitions, sort);
	}
	
	public static class Builder implements Cloneable {
		private List<Transition> transitions = new ArrayList<>();
		
		public State.Builder addTransition(Transition transition) {
			transitions.add(transition);
			return this;
		}
		
		public State build() {
			return new State(transitions);
		}
		
		@Override
		public State.Builder clone() {
			State.Builder builder = new Builder();
			for (Transition transition : transitions)
				builder.addTransition(transition);
			return builder;
		}
	}
}