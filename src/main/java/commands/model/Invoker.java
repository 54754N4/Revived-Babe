package commands.model;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bot.model.UserBot;
import commands.hierarchy.Command;
import commands.hierarchy.DiscordCommand;
import commands.level.All;
import commands.level.admin.Exit;
import commands.level.admin.Test;
import commands.level.normal.Echo;
import net.dv8tion.jda.api.entities.Message;
import spelling.SpellingCorrector;

/* Finds the appropriate command based on string command */
public class Invoker {
	private static final SpellingCorrector corrector = new SpellingCorrector();
	private static final Logger logger = LoggerFactory.getLogger(Invoker.class);
	
	public static void main(String[] args) throws Exception {
		String term = "exit";
		System.out.println(
				Arrays.toString(
						Reflector.find(term, Reflector.Type.ADMIN)
							.getConstructor(UserBot.class, Message.class)
							.newInstance(null, null).names));
	}
	
	public static Command invoke(UserBot bot, Message message, String input, Reflector.Type type) {
		String name;
        int space = input.indexOf(" ");
        if (space != -1) 
        	name = input.substring(0, space).toLowerCase();
        else 
        	name = input;
        logger.info("Checking command : "+name);
        Class<? extends Command> command = Reflector.find(name, type);
        Command created;
        if (command != null && (created = instantiate(bot, message, command)) != null)
			return created.start(input);
		else {
			String correction = corrector.correct(name), postfix = "";
			if (!correction.equals(name))
				postfix = String.format("%nDid you mean `%s` ?", correction);
			message.getChannel().sendMessage("Unrecognized command verb `"+name+"`"+postfix).queue();
			return null;
		}
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
				Echo.class.getPackage().getName(),
				cls -> cls.getPackage().getName().equals(Echo.class.getPackage().getName())),
			ADMIN(
				Exit.class.getPackage().getName(),
				cls -> cls.getPackage().getName().equals(Exit.class.getPackage().getName())),
			ALL(
				All.class.getPackage().getName(),
				cls -> true);
			
			public final String name;
			public Predicate<Class<? extends Command>> predicate;
			
			private Type(String name, Predicate<Class<? extends Command>> predicate) {
				this.name = name;
				this.predicate = predicate;
			}
		}
		
		/* Reflection methods */ 
		
		private static Reflections createReflections(String packageName, Class<?>[] include, Class<?>[] exclude) {
			ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
			FilterBuilder filterBuilder = new FilterBuilder();
			configurationBuilder.addUrls(ClasspathHelper.forPackage(packageName));
			for (Class<?> keep : include) filterBuilder.includePackage(keep);
			for (Class<?> skip : exclude) filterBuilder.excludePackage(skip);
			configurationBuilder.filterInputsBy(filterBuilder);
		    return new Reflections(configurationBuilder);
		}
		
		private static Class<?>[] pack(Class<?>... classes) {
			if (classes == null)
				return new Class<?>[] {};
			return classes;
		}
		
		private static Map<List<String>, Class<? extends Command>> buildDictionary(Type type) {
			Map<List<String>, Class<? extends Command>> map = new HashMap<>();
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
					if (!commandClass.getPackageName().startsWith(type.name))
						continue;
					Constructor<? extends Command> constructor = commandClass.getConstructor(UserBot.class, Message.class);
					Command command = constructor.newInstance(null, null);		// we just need names
					map.put(Collections.unmodifiableList(Arrays.asList(command.names)), commandClass);
				}
			} catch (Exception e) {	logger.error("Failed to build database", e); }
			return map;
		}
		
		public static Class<? extends Command> find(String input, Type type) {
			logger.debug("Looking for : {}", input);
			Map<List<String>, Class<? extends Command>> dict = (type == Type.ADMIN) ? admin : normal;
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
