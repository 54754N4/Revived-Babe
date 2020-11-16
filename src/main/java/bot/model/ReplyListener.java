package bot.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import commands.model.Invoker;
import lib.StringLib;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
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
    	ignoredUsers.put(user.getId(), user.getAsMention());
    	return true;
    }
    
    public boolean ignore(TextChannel channel, boolean store) {
    	ignoredChannels.put(channel.getId(), channel.getAsMention());
    	return true;
    }
    
    public void stopIgnoring(User user) {
    	ignoredUsers.remove(user.getId());
    }
    
    public void stopIgnoring(GuildChannel channel) {
    	ignoredChannels.remove(channel.getId());
    }
    
    public Collection<String> getIgnoredUsers() {
    	return ignoredUsers.values();
    }
    
    public Collection<String> getIgnoredChannels() {
    	return ignoredChannels.values();
    }

	/* Reply filtering */
	
	public void consume(Message message) {
		String content = message.getContentDisplay();
    	logger.info("Looking for prefix :"+Arrays.toString(bot.getPrefixes()));
    	if (!StringLib.startsWithPrefix(content, bot.getPrefixes())) 
    		return;
    	for (String input : StringLib.split(content, COMMAND_SEPARATOR)) 
    		treatInput(message, input.trim());
	}
    	
    private void treatInput(Message message, String input) {
    	if (isIgnored(message.getAuthor()) || isIgnored(message.getChannel())) 
    		return; 
    	else if (message.getAuthor().isBot()) 
    		return;
    	Invoker.Reflector.Type type = message.getAuthor().getIdLong() == OWNER_ID ? 
    			Invoker.Reflector.Type.ADMIN : 
    			Invoker.Reflector.Type.NORMAL;
    	input = StringLib.consumePrefix(message.getContentDisplay(), bot.getPrefixes());		// remove bot prefix
        if (!input.trim().equals(""))
        	Invoker.invoke(bot, message, input, type);
    	else message.getChannel()
        	.sendMessage(bot.prefixHelp())
        	.queue();
	}

	/* Event handling */
    
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
//		logger.info(String.format("GUILD> %s", event.getMessage().getContentDisplay()));
		consume(event.getMessage());
	}
	
	@Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
//		logger.info("PRIVATE> %s", event.getMessage().getContentDisplay());
		consume(event.getMessage());
	}
}
