package lib.messages;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bot.hierarchy.UserBot;
import lib.Consumers;
import lib.ListUtil;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;

public class PagedHandler<T> extends ReactionsHandler {
	public static final Logger logger = LoggerFactory.getLogger(PagedHandler.class);
	
	protected boolean handlerButtons, defaultBehaviour;
	protected List<T> data;
	private int page, count;
	protected Message tracked;
	
	public PagedHandler(UserBot bot, List<T> data) {
		this(bot, data, 10);
	}
	
	public PagedHandler(UserBot bot, List<T> data, int count) {
		super(bot);
		this.data = data;
		handlerButtons = false;
		defaultBehaviour = true;
		page = 0;
		this.count = count;	// code block + digit reactions can't handle more
	}
	
	public PagedHandler<T> disableDefault() {
		defaultBehaviour = false;
		return this;
	}
	
	public PagedHandler<T> enableHandlerButtons() {
		handlerButtons = true;
		return this;
	}
	
	@Override
	public void accept(Message message) {
		tracked = message;
		if (defaultBehaviour) {
			handle(0x2B05, this::onPrev);
			handle(0x27A1, this::onNext);
		}
		if (handlerButtons)
			setupElementHandlers();
		super.accept(message);
		print();
	}
	
	protected void setupElementHandlers() {
		final int zero = "\u0030".codePointAt(0);	// base codepoint for unicode digit 0
		for (int i=0; i<count; i++) {
			final int newInt = i;					// lambdas require final
			final String extendedUnicode = String.format("%c\uFE0F\u20E3", zero+i); 
			handle(extendedUnicode, r -> handleElement(newInt));
		}
	}
	
	protected String parseElement(int index, int queueIndex, T t) {
		return t.toString();
	}
	
	protected void onSelect(T element) {
		// do something with element
	}
	
	protected void onUpdate(MessageReaction reaction) {
		print();
	}
	
	private void onNext(MessageReaction reaction) {
		if (page + 1 > (data.size()-1)/count) return;
		page++;
	}
	
	private void onPrev(MessageReaction reaction) {
		if (page - 1 < 0) return;
		page--;
	}
	
	protected void print() {
		try { tracked.editMessage(parsePage(page)).queue(Consumers::ignore, Consumers::ignore); } 
		catch (Exception e) { logger.error("Exception trying to print paged handler", e); }
	}
	
	private void handleElement(int index) {
		if (data == null || index < 0 || index >= getPage(page).size())
			return;
		onSelect(getPage(page).get(index));
	}
	
	private int totalPages() {
		if (data == null) 
			return 0;
		double residue = (double) data.size() / (double) count;
		return (int) ((residue == data.size()/count) ? residue : residue+1);	// +1 since we're using 0 based indices
	}
	
	private List<T> getPage(int page) {
		int start = page*count;
		return (data == null || page < 0 || start >= data.size()) ? 
			new ArrayList<>() : 
			ListUtil.subset(data, start, count);
	}
	
	private String parsePage(int page) {
		String NEW_LINE = "\n", CODE_BLOCK = "```", CODE_BLOCK_START = CODE_BLOCK + "md";
		List<T> elements = getPage(page);
		StringBuilder sb = new StringBuilder(CODE_BLOCK_START+NEW_LINE);
		sb.append("#Page "+(page+1)+"/"+totalPages()+" ("+data.size()+" results)"+NEW_LINE);
		for (int i=0; i<elements.size(); i++)
			sb.append(parseElement(i, i+page*count, elements.get(i))+NEW_LINE);
		return sb.append(CODE_BLOCK).toString();
	}
	
	// Compose all consumers so we update text after every action 
	
	@Override
	public ReactionsHandler handle(String name, Consumer<MessageReaction> consumer) {
		super.handle(name, wrap(consumer));
		return this;
	}
	
	@Override
	public ReactionsHandler handle(int unicode, Consumer<MessageReaction> consumer) {
		super.handle(unicode, wrap(consumer));
		return this;
	}
	
	private Consumer<MessageReaction> wrap(Consumer<MessageReaction> consumer) {
		return consumer.andThen(this::onUpdate);
	}
}
