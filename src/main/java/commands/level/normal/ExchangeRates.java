package commands.level.normal;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import bot.hierarchy.UserBot;
import commands.hierarchy.DiscordCommand;
import commands.name.Command;
import json.ExchangeRatesResult;
import json.ExchangeRatesResult.Rates;
import lib.StringLib;
import net.dv8tion.jda.api.entities.Message;

public class ExchangeRates extends DiscordCommand {
	public static final String API_FORMAT = "http://openexchangerates.org/api/latest.json?app_id=%s";
	public static final Map<String, Double> currencies = new ConcurrentHashMap<>();
	
	public ExchangeRates(UserBot bot, Message message) {
		super(bot, message, Command.RATES.names);
	}

	@Override
	public String helpMessage() {
		return helpBuilder("[<currencies>]",
			"-f or --fetch\tupdates rates from API",
			"--base=B\twhere B is the currency to use as base",
			"Retrieves rates quoted against the USD by default. Can take comma separated list of currencies to retrieve specifically.");
	}

	@Override
	protected void execute(String input) throws Exception {
		if (currencies.size() == 0 || hasArgs("-f", "--fetch"))
			setup();
		Map<String, Double> map = currencies;
		String base = "USD";
		if (hasArgs("--base")) {
			base = getParams().getNamed().get("--base");
			if (currencies.containsKey(base))
				map = changeBase(base);
			else {
				println("%s is not a valid currency", inline(base));
				return;
			}
		}
		println("> Comparing to base : %s", base);
		if (input.equals(""))
			printMap(map);
		else
			for (String key : map.keySet())
				if (StringLib.matchSimplified(input, key))
					print(markdown("%s = %s"), key, map.get(key));
	}
	
	private Map<String, Double> changeBase(String currency) {
		Map<String, Double> changed = new HashMap<>();
		double base = currencies.get(currency), coef;
		Function<Double, Double> convert;
		if (base < 1) {
			coef = 1d / base;
			convert = d -> d * coef;
		} else if (base == 1)
			return currencies;
		else {
			coef = base;
			convert = d -> d / coef;
		}
		for (Entry<String, Double> entry : currencies.entrySet())
			changed.put(entry.getKey(), convert.apply(entry.getValue()));
		changed.put(currency, 1d);
		return changed;
	}
	
	private final void setup() throws IOException, IllegalArgumentException, IllegalAccessException {
		ExchangeRatesResult result = restRequest(
				ExchangeRatesResult.class, 
				API_FORMAT, 
				System.getenv("OPEN_EXCHANGE_RATES_API"));
		Rates rates = result.rates;
		// Use reflection cause too many attributes/fields to handle
		Field[] fields = Rates.class.getDeclaredFields();
		String name; double value;
		for (Field field : fields) {
			try {
				if (Modifier.isPublic(field.getModifiers()) && field.canAccess(rates)) {
					name = field.getName();
					value = field.getDouble(rates);
					currencies.put(name, value);
				}
			} catch (Exception e) {
				getLogger().error("Field failed to be read "+field.getName()+" "+field, e);
			}
		}
	}
}
