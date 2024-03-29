package commands.model;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backup.SpellingCorrector;
import bot.hierarchy.UserBot;
import commands.hierarchy.Command;
import commands.hierarchy.DiscordCommand;
import commands.level.All;
import commands.level.admin.Exit;
import commands.level.admin.Test;
import commands.level.normal.Echo;
import lib.ListUtil;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

/* Finds the appropriate command based on string command 
 * and appropriate role based on package names			 */
public class Invoker {
	private static final String NORMAL_NAME = Echo.class.getPackage().getName(),
			ADMIN_NAME = Exit.class.getPackage().getName(),
			ALL_NAME = All.class.getPackage().getName();
	
	private static final SpellingCorrector corrector = SpellingCorrector.deserialize();
	private static final Logger logger = LoggerFactory.getLogger(Invoker.class);
	
	public static SpellingCorrector getCorrector() {
		return corrector;
	}
	
	public static Command invoke(UserBot bot, Message message, String input, Reflector.Type type) {
		String name;
        int space = input.indexOf(" ");
        if (space != -1) 
        	name = input.substring(0, space).toLowerCase();
        else 
        	name = input;
        logger.info("Checking command : "+name);
        MessageChannel channel = message.getChannel();
        Class<? extends Command> command = Reflector.find(name, type);
        Command created;
        if (command != null) {
        	corrector.increment(name);
        	created = instantiate(bot, message, command);
        	if (created != null)
        		return created.start(input);
        }
		String correction = corrector.correct(name);
		channel.sendMessage("Unrecognized command verb `"+name+"`")
			.queue();
		if (!correction.equals(name))
			channel.sendMessage(String.format("Did you mean `%s` ?", correction))
				.setMessageReference(message)
				.queue();
		return null;
	}
	
	private static Command instantiate(UserBot bot, Message message, Class<? extends Command> cls) {
		try { 
			logger.info("Instantiating class : "+cls);
			return cls.getConstructor(UserBot.class, Message.class)
					.newInstance(bot, message); 
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			logger.error("Failed to instantiate class : "+cls, e);
			return null;
		}
	}
	
	public static final class Reflector {				
		public static Map<List<String>, Class<? extends Command>> normal, admin;
		
		static { update(); }
		
		public static enum Type { 
			NORMAL(
				NORMAL_NAME,
				cls -> cls.getPackage().getName().startsWith(NORMAL_NAME)),
			ADMIN(
				ADMIN_NAME,
				cls -> cls.getPackage().getName().startsWith(ADMIN_NAME)),
			ALL(
				ALL_NAME,
				cls -> true);
			
			public final String name;
			public Predicate<Class<? extends Command>> predicate;
			
			private Type(String name, Predicate<Class<? extends Command>> predicate) {
				this.name = name;
				this.predicate = predicate;
			}
		}
		
		public static class NamesComparator implements Comparator<List<String>> {
			@Override
			public int compare(List<String> o1, List<String> o2) {
				int biggest1 = ListUtil.max(o1, String::length),
					biggest2 = ListUtil.max(o2, String::length);
				return o1.get(biggest1).compareTo(o2.get(biggest2));
			}
		}
		
		/* Reflection methods */ 
		
		private static Reflections createReflections(String packageName, Class<?>[] include, Class<?>[] exclude) {
			ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
			FilterBuilder filterBuilder = new FilterBuilder();
			configurationBuilder.addUrls(ClasspathHelper.forPackage(packageName));
			for (Class<?> keep : include) filterBuilder.includePackage(keep.getPackageName());
			for (Class<?> skip : exclude) filterBuilder.excludePackage(skip.getPackageName());
			configurationBuilder.filterInputsBy(filterBuilder);
		    return new Reflections(configurationBuilder);
		}
		
		private static Class<?>[] pack(Class<?>... classes) {
			if (classes == null)
				return new Class<?>[] {};
			return classes;
		}
		
		private static Map<List<String>, Class<? extends Command>> buildDictionary(Type type) {
			Map<List<String>, Class<? extends Command>> map = new TreeMap<>(new NamesComparator());
			Class<?>[] empty = pack();
			Reflections reflections = type == Type.ALL ?
					createReflections(type.name, pack(All.class), empty) :
					type == Type.ADMIN ? 
							createReflections(type.name, pack(Test.class), empty) :
							createReflections(type.name, pack(Echo.class), empty);
			Set<Class<? extends Command>> classes = reflections.getSubTypesOf(DiscordCommand.class)
					.stream()
					.filter(type.predicate)
					.collect(Collectors.toSet());
			logger.info("Type = {} dictionary = {}", type, Arrays.toString(classes.toArray()));
			try {
				for (Class<? extends Command> commandClass : classes) {
					if (!commandClass.getPackage().getName().startsWith(type.name))
						continue;
					Constructor<? extends Command> constructor = commandClass.getConstructor(UserBot.class, Message.class);
					Command command = constructor.newInstance(null, null);		// we just need names
					map.put(Collections.unmodifiableList(Arrays.asList(command.names)), commandClass);
				}
			} catch (Exception e) {	logger.error("Failed to build database", e); }
			return map;
		}
		
		public static Map<List<String>, Class<? extends Command>> getDictionary(Type type) {
			switch (type) {
				case ALL:
				case ADMIN: return admin;
				case NORMAL: return normal;
				default: return null;
			}
		}
		
		public static Class<? extends Command> find(String input, Type type) {
			logger.debug("Looking for : {}", input);
			Map<List<String>, Class<? extends Command>> dict = getDictionary(type);
			for (List<String> names : dict.keySet())
				for (String name : names)
					if (name.equals(input))
						return dict.get(names);
			logger.debug("Matched no entry");
			return null;
		}
		
		public static void update() {
			if (normal != null) normal.clear();
			if (admin != null) admin.clear();
			normal = buildDictionary(Type.NORMAL);
			admin = buildDictionary(Type.ALL);
		}
	}
}
