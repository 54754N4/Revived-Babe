package lib.messages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import lib.Consumers;
import lib.Emoji;
import lib.ListUtil;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ReactionsDispatcher extends ListenerAdapter {
	private List<ReactionConsumer> consumers = new ArrayList<>();
	
	@Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
		if (event.getUser().isBot()) 
			return;
    	final String reactionName = event.getEmoji().getAsReactionCode();
    	final boolean isCustom = reactionName.matches("[a-zA-Z0-9_]+");	// getAsReactionCode returns str for custom and unicode2str for native emojis
    	List<ReactionConsumer> handlers = consumers.stream()
    		.filter(handler -> handler.id == event.getMessageIdLong())
			.filter(handler -> handler.name.equals(isCustom ? reactionName : event.getEmoji().getName()))
			.collect(Collectors.toList());
    	if (handlers.size() != 0) {
    		event.getReaction()
    			.removeReaction(event.getMember().getUser())
    			.queue(Consumers::ignore, Consumers::ignore);		// consume handled reaction
    		handlers.forEach(handler -> handler.consumer.accept(event)); 
    	}
    }
	
	public void subscribe(ReactionConsumer consumer) {
		consumers.add(consumer);
	}
	
	public void unsubscribe(ReactionConsumer consumer) {
		consumers.remove(consumer);
	}
	
	public void subscribe(Message message, String reactionName, Consumer<MessageReactionAddEvent> consumer) {
		consumers.add(new CustomEmojiConsumer(message, reactionName, consumer));
	}
	
	public void subscribe(Message message, Integer unicode, Consumer<MessageReactionAddEvent> consumer) {
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
	
	public void stopTracking() {
		consumers.forEach(handler -> 
			handler.channel.deleteMessageById(handler.id)
				.queue(Consumers::ignore, Consumers::ignore)); 
		consumers.clear();
	}
	
	public static final class CustomEmojiConsumer extends ReactionConsumer {		
		public CustomEmojiConsumer(Message msg, String reactionName, Consumer<MessageReactionAddEvent> consumer) {
			super(msg.getIdLong(), reactionName, msg.getChannel(), consumer);
		}
	}
	
	public static final class NativeEmojiConsumer extends ReactionConsumer {
		public final int unicode;
		
		public NativeEmojiConsumer(Message msg, int unicode, Consumer<MessageReactionAddEvent> consumer) {
			super(msg.getIdLong(), Emoji.fromUnicode(unicode), msg.getChannel(), consumer);
			this.unicode = unicode;
		}
	}
	
	public static final class NativeExtendedEmojiConsumer extends ReactionConsumer {
		public NativeExtendedEmojiConsumer(Message msg, String extended, Consumer<MessageReactionAddEvent> consumer) {
			super(msg.getIdLong(), extended, msg.getChannel(), consumer);
		}
	}
	
	public static class ReactionConsumer {
		public long id;				// message id
		public String name;
		public MessageChannel channel;
		public Consumer<MessageReactionAddEvent> consumer; 
		
		protected ReactionConsumer(long id, String name, MessageChannel channel, Consumer<MessageReactionAddEvent> consumer) {
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
