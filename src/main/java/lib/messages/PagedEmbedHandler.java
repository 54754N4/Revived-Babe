package lib.messages;

import java.util.List;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bot.hierarchy.UserBot;
import lib.Consumers;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

public class PagedEmbedHandler<T> extends ReactionsHandler {
	public static final Logger logger = LoggerFactory.getLogger(PagedEmbedHandler.class);
	protected Supplier<List<T>> supplier;
	protected EmbedBuildHandler<T> itemHandler;
	protected boolean defaultBehaviour;
	protected Message tracked;
	protected int page;
	
	private List<T> elements;
	
	public PagedEmbedHandler(UserBot bot, Supplier<List<T>> supplier) {
		super(bot);
		this.supplier = supplier;
		itemHandler = getDefaultItemHandler();
		defaultBehaviour = true;
		page = 0;
	}
	
	public PagedEmbedHandler<T> setItemHandler(EmbedBuildHandler<T> itemHandler) {
		this.itemHandler = itemHandler;
		return this;
	}
	
	public static final <T> EmbedBuildHandler<T> getDefaultItemHandler() {
		return (index, total, element, builder) -> 
			builder.setTitle(String.format("Page %d/%d", index, total))
				.setDescription(element.toString());
	}
	
	public PagedEmbedHandler<T> disableDefault() {
		defaultBehaviour = false;
		return this;
	}
	
	@Override
	public void accept(Message message) {
		tracked = message;
		elements = supplier.get();
		if (defaultBehaviour && elements.size() > 1) {
			handle(0x2B05, this::onPrev);
			handle(0x27A1, this::onNext);
		}
		message.editMessage(".").queue();
		super.accept(message);
		update();
	}
	
	private void onNext(MessageReactionAddEvent reaction) {
		if (page + 1 >= elements.size())
			page = 0;
		else 
			page++;
		update(false);
	}
	
	private void onPrev(MessageReactionAddEvent reaction) {
		if (page - 1 < 0)
			page = elements.size() - 1;
		else 
			page--;
		update(false);
	}
	
	public void update() {
		update(true);
	}
	
	public void update(boolean stayOnCurrent) {
		try { 
			EmbedBuilder embed = itemHandler.config(
					page, 
					elements.size(), 
					elements.get(page), 
					new ValidatingEmbedBuilder());
			tracked.editMessageEmbeds(embed.build())
				.queue(Consumers::ignore, Consumers::ignore); 
		} catch (Exception e) { 
			logger.error("Exception trying to print paged item handler", e);
		}
	}
	
	@FunctionalInterface
	public static interface EmbedBuildHandler<T> {
		EmbedBuilder config(int index, int total, T element, EmbedBuilder builder);
	}
}
