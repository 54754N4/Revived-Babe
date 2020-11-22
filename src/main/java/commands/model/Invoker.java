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
import net.dv8tion.jda.api.entities.Message;

/* Finds the appropriate command based on string command */
public class Invoker {
	private static final Logger logger = LoggerFactory.getLogger(Invoker.class);
	
	public static void main(String[] args) throws Exception {
		String term = "pi";
		System.out.println(
				Arrays.toString(
						Reflector.find(term, Reflector.Type.NORMAL)
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
        logger.info("Command is : "+command);
        if (command != null && (created = instantiate(bot, message, command)) != null)
			return created.start(input);
		else {
			message.getChannel().sendMessage("`Unrecognized command verb : "+name+"`").queue();
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
		private static final String NORMAL_PACKAGE_NAME = "commands.level.normal",
				ADMIN_PACKAGE_NAME = "commands.level.admin";  
		
		public static Map<List<String>, Class<? extends Command>> normal, admin;
		
		static { update(); }
		
		public static enum Type { 
			NORMAL("commands.level.normal"), 
			ADMIN("commands.level.admin"), 
			ALL("commands.level");
			
			public final String name;
			public final Predicate<Class<? extends Command>> predicate;
			
			private Type(String name) {
				this.name = name;
				switch (name) {
					case NORMAL_PACKAGE_NAME: predicate = cls -> cls.getPackage().getName().equals(NORMAL_PACKAGE_NAME); break;
					case ADMIN_PACKAGE_NAME: predicate = cls -> cls.getPackage().getName().equals(ADMIN_PACKAGE_NAME); break;
					default: predicate = cls -> true; break;
				}
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
		
		private static Map<List<String>, Class<? extends Command>> buildDictionary(Type type) {
			Map<List<String>, Class<? extends Command>> map = new HashMap<>();
			Class<?>[] include = {}, exclude = {};
			Reflections reflections = createReflections(type.name, include, exclude);
			Set<Class<? extends Command>> classes = reflections.getSubTypesOf(DiscordCommand.class)
					.stream()
					.filter(type.predicate)
					.collect(Collectors.toSet());
			try {
				for (Class<? extends Command> commandClass : classes) {
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
