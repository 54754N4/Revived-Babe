package bot.hierarchy;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import commands.model.Invoker;
import lib.StringLib;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ReplyListener extends ListenerAdapter {
	private static long OWNER_ID = 188033164864782336l;
	public static final char COMMAND_SEPARATOR = ';';
	private final Logger logger;
	private final UserBot bot;
	private Map<String, String> ignoredUsers, ignoredChannels;

	public ReplyListener(UserBot bot) {
		this.bot = bot;
		ignoredUsers = new HashMap<>();
		ignoredChannels = new HashMap<>();
		logger = LoggerFactory.getLogger(String.format("%s-reply-listener", bot.name));
	}

	/* Ignore users + channels methods */
	
	public boolean isIgnored(User user) {
    	return ignoredUsers.keySet().contains(user.getId());
    }
    
    public boolean isIgnored(MessageChannel channel) {
    	return ignoredChannels.keySet().contains(channel.getId());
    }
    
    public boolean ignore(User user) {
    	logger.info("Started ignoring {}", user.getAsMention());
    	ignoredUsers.put(user.getId(), user.getAsMention());
    	return true;
    }
    
    public boolean ignore(TextChannel channel) {
    	logger.info("Started ignoring {}", channel.getAsMention());
    	ignoredChannels.put(channel.getId(), channel.getAsMention());
    	return true;
    }
    
    public void stopIgnoring(User user) {
    	logger.info("Stopped ignoring {}", user.getAsMention());
    	ignoredUsers.remove(user.getId());
    }
    
    public void stopIgnoring(TextChannel channel) {
    	logger.info("Stopped ignoring {}", channel.getAsMention());
    	ignoredChannels.remove(channel.getId());
    }
    
    public Collection<String> getIgnoredUsers() {
    	return ignoredUsers.values();
    }
    
    public Collection<String> getIgnoredChannels() {
    	return ignoredChannels.values();
    }

	/* Reply filtering */
    
    private static final boolean isOwner(Message m) {
    	return m.getAuthor().getIdLong() == OWNER_ID;
    }
	
	public void consume(Message message) {
		String content = message.getContentDisplay();
    	for (String input : StringLib.split(content, COMMAND_SEPARATOR))
    		if (StringLib.startsWith(input.trim(), bot.getPrefixes())) 
        		treatInput(message, input.trim());
	}
    	
    private void treatInput(Message message, String input) {
    	if (isIgnored(message.getAuthor()) || isIgnored(message.getChannel())) 
    		return; 
    	else if (message.getAuthor().isBot()) 
    		return;
    	Invoker.Reflector.Type type = isOwner(message) ? 
    			Invoker.Reflector.Type.ADMIN : Invoker.Reflector.Type.NORMAL;
    	input = StringLib.consumePrefix(input, bot.getPrefixes());		// remove bot prefix
        if (!input.trim().equals(""))
        	Invoker.invoke(bot, message, input, type);
    	else message.getChannel()
        	.sendMessage(bot.prefixHelp())
        	.queue();
	}

	/* Event handling */
    
	@Override
	public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
		consume(event.getMessage());
	}
}
