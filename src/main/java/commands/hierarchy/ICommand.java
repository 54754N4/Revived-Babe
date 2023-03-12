package commands.hierarchy;

import java.util.Comparator;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.slf4j.Logger;

import bot.hierarchy.UserBot;
import commands.model.Mentions;
import commands.model.Params;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

public interface ICommand extends Callable<Void> {	
	String[] getNames();
	UserBot getBot();
	boolean isFinished();
	boolean keepAlive();
	Logger getLogger();
	Guild getGuild();
	Message getMessage();
	MessageChannel getChannel();
	StringBuilder getStdout();
	String getInput();
	Params getParams();
	Mentions getMentions();
	Future<?> getThread();
	
	ICommand kill();
	ICommand setKeepAlive();
	ICommand setFinished();
	void actTyping();
	void clearStdout();
	
	boolean hasArgs(String...args);
	String helpMessage();
	ICommand start(String command);
	void execute(String input) throws Exception;
	
	public static class CommandNameComparator implements Comparator<Command> {
		@Override
		public int compare(Command cmd0, Command cmd1) {
			return cmd0.names[0].compareTo(cmd1.names[0]);
		}
	}
}
