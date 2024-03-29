package lib.messages;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import bot.hierarchy.UserBot;
//import lib.Emoji;
import lib.messages.ReactionsDispatcher.CustomEmojiConsumer;
import lib.messages.ReactionsDispatcher.NativeEmojiConsumer;
import lib.messages.ReactionsDispatcher.NativeExtendedEmojiConsumer;
import lib.messages.ReactionsDispatcher.ReactionConsumer;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

public class ReactionsHandler implements Consumer<Message> {
	public static final int UNICODE_CLOSE = 0x274C;
	private int count;
	private final Map<Integer, Object> keys;
	private final List<Consumer<MessageReactionAddEvent>> consumers;
	private final List<Consumer<Message>> onCloseConsumers;
	private final List<ReactionConsumer> handlers;
	private final UserBot bot;
	
	public ReactionsHandler(UserBot bot) {
		this.bot = bot;
		count = 0;
		keys = new ConcurrentHashMap<>();
		consumers = new ArrayList<>();
		handlers = new ArrayList<>();
		onCloseConsumers = new ArrayList<>();
	}
	
	public ReactionsHandler addOnCloseHandler(Consumer<Message> consumer) {
		onCloseConsumers.add(consumer);
		return this;
	}
	
	public ReactionsHandler handle(String name, Consumer<MessageReactionAddEvent> consumer) {
		keys.put(count++, name);
		consumers.add(consumer);
		return this;
	}
	
	public ReactionsHandler handle(int unicode, Consumer<MessageReactionAddEvent> consumer) {
		keys.put(count++, unicode);
		consumers.add(consumer);
		return this;
	}
	
	@Override
	public void accept(final Message message) {
		handle(UNICODE_CLOSE, reaction -> onDelete(message));	// always close button last
		ReactionConsumer consumer;
		Object value;
		for (int key=0; key<count; key++) {
			value = keys.get(key);
			if (String.class.isInstance(value)) {
				String name = String.class.cast(value);
				consumer = name.matches("[a-zA-Z ]+") ? 
					handleCustom(message, name, key) : 
					handleNativeExtended(message, name, key);
			} else if (Integer.class.isInstance(value))
				consumer = handleNative(message, Integer.class.cast(value), key);
			 else continue;
			handlers.add(consumer);
		}
		handlers.forEach(bot.getReactionsTracker()::subscribe);
	}

	protected void onDelete(Message message) {
		handlers.forEach(bot.getReactionsTracker()::unsubscribe);
		if (message == null)
			return;
		onCloseConsumers.forEach(consumer -> consumer.accept(message));
		message.delete().queue();
	}
	
	private ReactionConsumer handleCustom(Message message, String name, int key) {
		message.addReaction(message.getGuild().getEmojisByName(name, true).get(0)).queue();
		return new CustomEmojiConsumer(message, name, consumers.get(key));
	}
	
	private ReactionConsumer handleNative(Message message, int unicode, int key) {
		message.addReaction(Emoji.fromUnicode(lib.Emoji.toCodepoint(unicode))).queue();
		return new NativeEmojiConsumer(message, unicode, consumers.get(key));
	}
	
	private ReactionConsumer handleNativeExtended(Message message, String name, int key) {
		message.addReaction(Emoji.fromUnicode(name)).queue();
		return new NativeExtendedEmojiConsumer(message, name, consumers.get(key));
	}
}
