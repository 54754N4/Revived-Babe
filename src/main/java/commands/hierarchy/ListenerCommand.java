package commands.hierarchy;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

import bot.hierarchy.UserBot;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public abstract class ListenerCommand extends RoleCommand {
	private List<ReplyHandler> handlers;
	
	public ListenerCommand(UserBot bot, Message message, String[] names) {
		super(bot, message, names);
		handlers = new ArrayList<>();
	}
	
	/* Replies events (un-)hooking */

	public ListenerCommand attachListener() {
		getLogger().info("{} started listening to replies", getClass());
		getBot().getJDA().addEventListener(this);
		return this;
	}
	
	public ListenerCommand removeListener() {
		getLogger().info("{} stopped listening to replies", getClass());
		getBot().getJDA().removeEventListener(this);
		return this;
	}
	
	/* Handlers handling + dispatching */
	
	protected ListenerCommand addReplyHandler(ReplyHandler filter) {
		handlers.add(filter);
		return this;
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		final boolean isBot = event.getAuthor().isBot();
		final boolean isAuthor = event.getAuthor().getIdLong() == getMessage().getAuthor().getIdLong();
		final String message = event.getMessage().getContentDisplay();
		handlers.forEach(handler -> dispatch(handler, isBot, isAuthor, message));
	}
	
	// Conditionally dispatches based on predicate config
	public void dispatch(ReplyHandler handler, boolean isBot, boolean isAuthor, String text) {
		if (handler.handleBots != isBot	||			// bot dispatching (handleBots ^ isBot = XOR)
			(handler.authorOnly && !isAuthor) ||	// don't dispatch on state 10 in truth table	
			!handler.predicate.test(text, this))
			return;
		handler.actions.forEach(action -> action.accept(this));
	}
	
	public static final class ReplyHandler {
		private final boolean handleBots, authorOnly;
		private final BiPredicate<String, ListenerCommand> predicate;
		private final List<Consumer<? super ListenerCommand>> actions;
		
		private ReplyHandler(
				boolean handleBots, 
				boolean ownerOnly, 
				BiPredicate<String, ListenerCommand> predicate, 
				List<Consumer<? super ListenerCommand>> actions) {
			this.handleBots = handleBots;
			this.authorOnly = ownerOnly;
			this.predicate = predicate;
			this.actions = actions;
		}

		public boolean handleBots() {
			return handleBots;
		}
		
		public boolean ownerOnly() {
			return authorOnly;
		}

		public BiPredicate<String, ListenerCommand> getPredicate() {
			return predicate;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (handleBots ? 1231 : 1237);
			result = prime * result + ((predicate == null) ? 0 : predicate.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ReplyHandler other = (ReplyHandler) obj;
			if (handleBots != other.handleBots)
				return false;
			if (predicate == null) {
				if (other.predicate != null)
					return false;
			} else if (!predicate.equals(other.predicate))
				return false;
			return true;
		}
		
		public static final class Builder {
			private boolean handleBots, authorOnly;
			private BiPredicate<String, ListenerCommand> predicate;
			private List<Consumer<? super ListenerCommand>> actions;
			
			public Builder() {
				predicate = (text, cmd) -> false;	// default predicate never validates
				actions = new ArrayList<>();
			}
			
			public Builder ignoreBots() {
				handleBots = false;
				return this;
			}
			
			public Builder handleBots() {
				handleBots = true;
				return this;
			}
			
			public Builder handleAnyone() {
				authorOnly = false;
				return this;
			}
			
			public Builder authorOnly() {
				authorOnly = true;
				return this;
			}
			
			public Builder ifTrue(BiPredicate<String, ListenerCommand> predicate) {
				this.predicate = predicate;
				return this;
			}
			
			public Builder ifFalse(BiPredicate<String, ListenerCommand> predicate) {
				this.predicate = predicate.negate();
				return this;
			}
			
			public Builder then(Consumer<? super ListenerCommand> action) {
				actions.add(action);
				return this;
			}
			
			public Builder print(final String format, final Object...objects) {
				actions.add(cmd -> cmd.print(format, objects));
				return this;
			}
			
			public Builder println(final String format, final Object...objects) {
				actions.add(cmd -> cmd.println(format, objects));
				return this;
			}
			
			/* Default builder setters */
			
			public Builder setHandleBots(boolean handleBots) {
				this.handleBots = handleBots;
				return this;
			}
			
			public Builder setOwnerOnly(boolean ownerOnly) {
				this.authorOnly = ownerOnly;
				return this;
			}
			
			public Builder setActions(List<Consumer<? super ListenerCommand>> actions) {
				this.actions = actions;
				return this;
			}
			
			public ReplyHandler build() {
				return new ReplyHandler(handleBots, authorOnly, predicate, actions);
			}
		}
	}
}
