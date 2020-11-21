package lib.messages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lib.Emoji;
import lib.ListUtil;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ReactionHandler extends ListenerAdapter {
	public static final ReactionHandler INSTANCE = new ReactionHandler();
	private final Logger logger = LoggerFactory.getLogger(ReactionHandler.class);
	private List<ReactionConsumer> consumers = new ArrayList<>();
	
	public void subscribe(ReactionConsumer consumer) {
		consumers.add(consumer);
	}
	
	public void subscribe(Message message, String reactionName, Consumer<MessageReaction> consumer) {
		consumers.add(new CustomEmojiConsumer(message, reactionName, consumer));
	}
	
	public void subscribe(Message message, Integer unicode, Consumer<MessageReaction> consumer) {
		consumers.add(new NativeEmojiConsumer(message, unicode, consumer));
	}
	
	public void unsubscribe(long msgID) {
		List<Integer> indices = new ArrayList<>();
		for (int i=0; i<consumers.size(); i++)
			if (consumers.get(i).id == msgID)
				indices.add(i);
		Collections.sort(indices, ListUtil.descendingOrder());
		for (int i=0; i<indices.size(); i++)
			consumers.remove(indices.get(i).intValue());
	}
	
	@Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
		if (event.getUser().isBot()) return;
		try {
			event.getReaction().removeReaction(event.getUser())	// consume handled reaction
				.queue(null, (e) -> logger.error("Error removing reaction 1", e)); 
		} catch (Exception e) {
			logger.error("Error removing reaction 2", e);
		}
    	final String reactionName = event.getReactionEmote().getAsReactionCode();
    	final boolean isCustom = reactionName.matches("[a-zA-Z0-9_]+");	// getAsReactionCode returns str for custom and unicode2str for native emojis
    	consumers.stream()
    		.filter(handler -> handler.id == event.getMessageIdLong())
			.filter(handler -> handler.name.equals(isCustom ? reactionName : event.getReactionEmote().getName()))
			.forEach(handler -> handler.consumer.accept(event.getReaction()));
    }
	
	public void stopTracking() {
		consumers.forEach(handler -> handler.channel.deleteMessageById(handler.id).queue()); 
		consumers.clear();
	}
	
	public static final class CustomEmojiConsumer extends ReactionConsumer {		
		public CustomEmojiConsumer(Message msg, String reactionName, Consumer<MessageReaction> consumer) {
			super(msg.getIdLong(), reactionName, msg.getChannel(), consumer);
		}
	}
	
	public static final class NativeEmojiConsumer extends ReactionConsumer {
		public final int unicode;
		
		public NativeEmojiConsumer(Message msg, int unicode, Consumer<MessageReaction> consumer) {
			super(msg.getIdLong(), Emoji.fromUnicode(unicode), msg.getChannel(), consumer);
			this.unicode = unicode;
		}
	}
	
	public static class ReactionConsumer {
		public long id;				// message id
		public String name;
		public MessageChannel channel;
		public Consumer<MessageReaction> consumer; 
		
		protected ReactionConsumer(long id, String name, MessageChannel channel, Consumer<MessageReaction> consumer) {
			this.id = id;
			this.name = name;
			this.channel = channel;
			this.consumer = consumer; 
		}
		
		public boolean isCustom() {
			return name.matches("[a-zA-Z0-9_]+");
		}
	}
}
