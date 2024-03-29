package lib.messages;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bot.hierarchy.UserBot;
import lib.Consumers;
import lib.ListUtil;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

public class PagedHandler<T> extends ReactionsHandler {
	public static final Logger logger = LoggerFactory.getLogger(PagedHandler.class);
	public static final int DEFAULT_COUNT = 10;	// code-blocks can't handle a lot
	
	protected boolean handlerButtons, defaultBehaviour;
	protected Supplier<List<T>> supplier;
	protected Message tracked;
	protected int page, count;
	private Supplier<String> titleSuffix;
	private Consumer<T> onSelection;
	
	public PagedHandler(UserBot bot, Supplier<List<T>> supplier) {
		this(bot, supplier, 10);
	}
	
	public PagedHandler(UserBot bot, Supplier<List<T>> supplier, int count) {
		super(bot);
		this.supplier = supplier;
		handlerButtons = false;
		defaultBehaviour = true;
		page = 0;
		this.count = count;	// e.g. items per page
		titleSuffix = () -> "";
	}
	
	public PagedHandler<T> setOnSelectionConsumer(Consumer<T> onSelection) {
		this.onSelection = onSelection;
		return this;
	}
	
	public PagedHandler<T> setTitleSuffix(Supplier<String> suffix) {
		this.titleSuffix = suffix;
		return this;
	}
	
	public PagedHandler<T> disableDefault() {
		defaultBehaviour = false;
		return this;
	}
	
	public PagedHandler<T> enableHandlerButtons() {
		handlerButtons = true;
		return this;
	}
	
	public List<T> data() {
		return supplier.get();
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
		update();
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
	
	// Either overriden by child type or used with onSelection consumer and anonymous classes
	protected void onSelect(T element) {
		if (onSelection != null)
			onSelection.accept(element);
	}
	
	private void onNext(MessageReactionAddEvent reaction) {
		if (page + 1 > (data().size()-1)/count)
			page = 0;
		else 
			page++;
		update(false);
	}
	
	private void onPrev(MessageReactionAddEvent reaction) {
		if (page - 1 < 0)
			page = totalPages() - 1;
		else {
			// Since the total count of elements might have changed
			do { 
				page--;
			} while (page * count > data().size());
		}
		update(false);
	}
	
	public void update() {
		update(true);
	}
	
	public void update(boolean stayOnCurrent) {
		try { tracked.editMessage(parsePage(page)).queue(Consumers::ignore, Consumers::ignore); } 
		catch (Exception e) { logger.error("Exception trying to print paged handler", e); }
	}
	
	protected void handleElement(int index) {
		if (data() == null || index < 0 || index >= getPage(page).size())
			return;
		onSelect(getPage(page).get(index));
	}
	
	protected int totalPages() {
		if (data() == null) 
			return 0;
		double residue = (double) data().size() / (double) count;
		return (int) ((residue == data().size()/count) ? residue : residue+1);	// +1 since we're using 0 based indices
	}
	
	protected List<T> getPage(int page) {
		int start = page*count;
		return (data() == null || page < 0 || start >= data().size()) ? 
			new ArrayList<>() : 
			ListUtil.subset(data(), start, count);
	}
	
	protected String parsePage(int page) {
		String NEW_LINE = "\n", CODE_BLOCK = "```", CODE_BLOCK_START = CODE_BLOCK + "md";
		List<T> elements = getPage(page);
		StringBuilder sb = new StringBuilder(CODE_BLOCK_START+NEW_LINE);
		sb.append("#Page "+(page+1)+"/"+totalPages()+" ("+data().size()+" results) "+titleSuffix.get()+NEW_LINE);
		for (int i=0; i<elements.size(); i++)
			sb.append(parseElement(i, i+page*count, elements.get(i))+NEW_LINE);
		return sb.append(CODE_BLOCK).toString();
	}
}
