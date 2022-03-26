package commands.level.normal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bot.hierarchy.UserBot;
import commands.hierarchy.DiscordCommand;
import commands.name.Command;
import json.TimeResult;
import lib.HTTP.ResponseHandler;
import lib.StringLib;
import lib.messages.ValidatingEmbedBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;

public class Time extends DiscordCommand {
	private static final Logger logger = LoggerFactory.getLogger(Time.class);
	public static final String API_FORMAT = "http://worldtimeapi.org/api/%s", DEFAULT_TIMEZONE = "Asia/Amman";
	public static List<String> TIMEZONES;
	
	public Time(UserBot bot, Message message) {
		super(bot, message, Command.TIME.names);
	}

	@Override
	public String helpMessage() {
		return helpBuilder("<timezone>", 
			"-l or --list\tmakes me list timezones",
			"Lists a specific timezone's time.");
	}

	@Override
	protected void execute(String input) throws Exception {
		if (TIMEZONES == null) {
			println("Retrieving list of timezone codes (only done first time #lazyLoading)");
			TIMEZONES = getTimezones();
		}
		if (hasArgs("-l", "--list")) {
			if (input.equals(""))
				TIMEZONES.forEach(this::println);
			else 
				match(input).forEach(this::println);
		} else {
			Optional<String> match = match(input.equals("") ? DEFAULT_TIMEZONE : input)
				.stream()
				.findFirst();
			match.map(Time::getTime)
				.map(Time::buildEmbed)
				.map(EmbedBuilder::build)
				.map(channel::sendMessageEmbeds)
				.get()
				.queue();
		}
	}
	
	public static ValidatingEmbedBuilder buildEmbed(TimeResult result) {
		ValidatingEmbedBuilder eb = new ValidatingEmbedBuilder();
		eb.setTitle(result.getTimezone());
		eb.addField("Abbreviation", result.getAbbreviation());
		eb.addField("Date Time", convertFromUTC(result.getDatetime()));
		eb.addField("UTC Date Time", convertFromUTC(result.getUtcDatetime()));
		eb.addField("UTC Offset", result.getUtcOffset());
		eb.addField("Day of week", result.getDayOfWeek());
		eb.addField("Day of year", result.getDayOfYear());
		eb.addField("Week number", result.getWeekNumber());
		eb.addField("Unix Time", result.getUnixtime());
		return eb;
	}
	
	public static TimeResult getTime(String timezone) {
		try {
			return restRequest(TimeResult.class, API_FORMAT, "timezone/"+timezone);
		} catch (IOException e) {
			logger.error("Couldn't get time for "+timezone, e);
			return null;
		}	
	}
	
	public static List<String> match(String input) throws IOException {
		if (TIMEZONES == null)
			TIMEZONES = getTimezones();
		return TIMEZONES.stream()
				.filter(tz -> StringLib.matchSimplified(tz, input))
				.toList();
	}

	public static List<String> getTimezones() throws IOException {
		try (ResponseHandler response = restRequest(API_FORMAT, "timezone.txt")) {
			final List<String> timezones = new ArrayList<>();
			response.forEachResponseLine(line -> timezones.add(line));
			return timezones;
		}
	}
	
	public static String convertFromUTC(String utcDate) {
		String date = utcDate.substring(0, 10),
				time = utcDate.substring(11, 26),
				offset = utcDate.substring(26);
		return String.format("%s %s %s", date, time, offset);
	}
}
