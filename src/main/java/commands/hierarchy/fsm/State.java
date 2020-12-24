package commands.hierarchy.fsm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class State implements Cloneable {
	private String name;
	private List<Transition> transitions;
	private boolean sort;
	
	private State(String name, List<Transition> transitions) {
		this(name, transitions, true);
	}
	
	private State(String name, List<Transition> transitions, boolean sort) {
		this.name = name;
		this.transitions = transitions;
		this.sort = sort;
	}
	
	public State addTransition(Transition transition) {
		transitions.add(transition);
		sort = true;
		return this;
	}
	
	public List<Transition> getTransitions() {
		return transitions;
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
		return new State(name, transitions, sort);
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public static class Builder implements Cloneable {
		private String name;
		private List<Transition> transitions = new ArrayList<>();

		public Builder(String stateName) {
			setName(stateName);
		}
		
		public Builder setName(String name) {
			this.name = name;
			return this;
		}
		
		public Builder addTransition(Transition transition) {
			transitions.add(transition);
			return this;
		}
		
		public State build() {
			return new State(name, transitions);
		}
		
		@Override
		public State.Builder clone() {
			State.Builder builder = new Builder(name);
			for (Transition transition : transitions)
				builder.addTransition(transition);
			return builder;
		}
	}
}