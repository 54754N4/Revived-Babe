package commands.level.normal;

import bot.model.UserBot;
import commands.model.hierarchy.DiscordCommand;
import commands.name.Command;
import json.BabeIPResult;
import json.GeolocateResult;
import lib.messages.ValidatingEmbedBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;

public class IP extends DiscordCommand {
	public static final String IP_FORMAT = "^((25[0-5]|(2[0-4]|1[0-9]|[1-9]|)[0-9])(\\.(?!$)|$)){4}$";
	private static final String API_FORMAT = "https://get.geojs.io/v1/ip/geo/%s.json",
			BABE_IP_API_CALL = "https://get.geojs.io/v1/ip.json";

	public IP(UserBot bot, Message message) {
		super(bot, message, Command.IP.names);
	}

	@Override
	public String helpMessage() {
		return helpBuilder("<ip address>", "Makes me geolocate the ip address given.");
	}

	@Override
	protected void execute(String input) throws Exception {
		if (isOwner() && hasArgs("-me"))
			println("Your IP is: ", restRequest(BABE_IP_API_CALL, BabeIPResult.class).ip);
		if (input.equals("")) 
			return;
		if (input.matches(IP_FORMAT)) {
			GeolocateResult geolocation = restRequest(API_FORMAT, GeolocateResult.class, input);
			channel.sendMessage(buildEmbed(geolocation).build()).queue();
		} else 
			println("Invalid ip format..");
	}
	
	private static EmbedBuilder buildEmbed(GeolocateResult result) {
		ValidatingEmbedBuilder eb = new ValidatingEmbedBuilder();
		eb.setTitle(result.ip);
		eb.addField("Organization Name", result.organization_name);
		eb.addField("Organization", result.organization);
		eb.addField("Continent", result.continent_code);
		eb.addField("Region", result.region);
		eb.addField("Timezone", result.timezone);
		eb.addField("Country", result.country);
		eb.addField("City", result.city);
		eb.addField("Accuracy", ""+result.accuracy);
		eb.addField("ASN", ""+result.asn);
		eb.addField("Longitude", result.longitude);
		eb.addField("Latitude", result.latitude);
		return eb;
	}
}
