package lib.messages;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import lib.Emoji;
import lib.messages.ReactionHandler.CustomEmojiConsumer;
import lib.messages.ReactionHandler.NativeEmojiConsumer;
import lib.messages.ReactionHandler.ReactionConsumer;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;

public class ReactionsTracker implements Consumer<Message> {
	private int count = 0;
	private final Map<Integer, Object> keys = new ConcurrentHashMap<>();
	private final List<Consumer<MessageReaction>> consumers = new ArrayList<>();
	
	public ReactionsTracker handle(String name, Consumer<MessageReaction> consumer) {
		keys.put(count++, name);
		consumers.add(consumer);
		return this;
	}
	
	public ReactionsTracker handle(int unicode, Consumer<MessageReaction> consumer) {
		keys.put(count++, unicode);
		consumers.add(consumer);
		return this;
	}
	
	@Override
	public void accept(Message message) {
		List<ReactionConsumer> handlers = new ArrayList<>();
		ReactionConsumer consumer;
		Object value;
		for (int key=0; key<count; key++) {
			value = keys.get(key);
			if (String.class.isInstance(value)) {
				String name = String.class.cast(value);
				consumer = new CustomEmojiConsumer(message, name, consumers.get(key));
				message.addReaction(message.getGuild().getEmotesByName(name, true).get(0)).queue();
			} else if (Integer.class.isInstance(value)) {
				int unicode = Integer.class.cast(value);
				consumer = new NativeEmojiConsumer(message, unicode, consumers.get(key));
				message.addReaction(Emoji.toCodepoint(unicode)).queue();
			} else continue;
			handlers.add(consumer);
		}
		handlers.forEach(ReactionHandler.INSTANCE::subscribe);
	}
}
