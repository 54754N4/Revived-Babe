package commands.level.normal;

import java.util.HashMap;
import java.util.Map;

import bot.hierarchy.UserBot;
import commands.hierarchy.DiscordCommand;
import commands.name.Command;
import json.ExchangeRatesResult;
import lib.HTTP;
import lib.messages.ValidatingEmbedBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;

public class ExchangeRates extends DiscordCommand {
	private static final String API_FORMAT = "https://api.ratesapi.io/api/%s?%s",
			BASE = "base", SYMBOLS = "symbols";
	private Map<String, String> dict;
	
	public ExchangeRates(UserBot bot, Message message) {
		super(bot, message, Command.RATES.names);
		dict = new HashMap<>();
	}

	@Override
	public String helpMessage() {
		return helpBuilder("", 
			"--base=B\twhere B is the currency to quote against",
			"--symbols=S\twhere S is a comma-delimited list of currencies",
			"--date=YYYY-MM-DD\tto get historical rates for any day since 1999",
			"Retrieves rates quoted against the Euro by default.");
	}

	@Override
	protected void execute(String input) throws Exception {
		if (hasArgs("--base"))
			dict.put(BASE, params.named.get("--base").toUpperCase());
		if (hasArgs("--symbols"))
			dict.put(SYMBOLS, params.named.get("--symbols").toUpperCase());
		String target = hasArgs("--date") ? 
				params.named.get("--date") : 
				"latest";
		ExchangeRatesResult result = restRequest(
				ExchangeRatesResult.class,
				API_FORMAT,
				target, 
				HTTP.getParamsString(dict, "&", true));
		channel.sendMessageEmbeds(buildEmbed(result).build()).queue();;
	}
	
	private static EmbedBuilder buildEmbed(ExchangeRatesResult result) {
		ValidatingEmbedBuilder eb = new ValidatingEmbedBuilder();
		eb.setTitle(result.base);
		eb.addField("Date", result.date);
		eb.addField("AUD", result.getRates().getAUD());
		eb.addField("BGN", result.getRates().getBGN());
		eb.addField("BRL", result.getRates().getBRL());
		eb.addField("CAD", result.getRates().getCAD());
		eb.addField("CHF", result.getRates().getCHF());
		eb.addField("CNY", result.getRates().getCNY());
		eb.addField("CZK", result.getRates().getCZK());
		eb.addField("DKK", result.getRates().getDKK());
		eb.addField("GBP", result.getRates().getGBP());
		eb.addField("HKD", result.getRates().getHKD());
		eb.addField("HRK", result.getRates().getHRK());
		eb.addField("HRK", result.getRates().getHRK());
		eb.addField("HUF", result.getRates().getHUF());
		eb.addField("IDR", result.getRates().getIDR());
		eb.addField("ILS", result.getRates().getILS());
		eb.addField("INR", result.getRates().getINR());
		eb.addField("ISK", result.getRates().getISK());
		eb.addField("JPY", result.getRates().getJPY());
		eb.addField("KRW", result.getRates().getKRW());
		eb.addField("MXN", result.getRates().getMXN());
		eb.addField("MYR", result.getRates().getMYR());
		eb.addField("NOK", result.getRates().getNOK());
		eb.addField("NZD", result.getRates().getNZD());
		eb.addField("PHP", result.getRates().getPHP());
		eb.addField("PLN", result.getRates().getPLN());
		eb.addField("RON", result.getRates().getRON());
		eb.addField("RUB", result.getRates().getRUB());
		eb.addField("SEK", result.getRates().getSEK());
		eb.addField("SGD", result.getRates().getSGD());
		eb.addField("THB", result.getRates().getTHB());
		eb.addField("TRY", result.getRates().getTRY());
		eb.addField("USD", result.getRates().getUSD());
		eb.addField("ZAR", result.getRates().getZAR());
		return eb;
	}

}
