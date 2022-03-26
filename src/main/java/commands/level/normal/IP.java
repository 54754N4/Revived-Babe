package commands.level.normal;

import java.io.File;
import java.net.URL;

import bot.hierarchy.UserBot;
import commands.hierarchy.DiscordCommand;
import commands.name.Command;
import json.BabeIPResult;
import json.GeolocateResult;
import lib.GPS;
import lib.encode.Encoder;
import lib.messages.ValidatingEmbedBuilder;
import lib.scrape.Browser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;

public class IP extends DiscordCommand {
	public static final String IP_FORMAT = "^((25[0-5]|(2[0-4]|1[0-9]|[1-9]|)[0-9])(\\.(?!$)|$)){4}$";
	private static final String API_FORMAT = "https://get.geojs.io/v1/ip/geo/%s.json",
			BABE_IP_API_CALL = "https://get.geojs.io/v1/ip.json",
			GOOGLE_MAPS_FORMAT = "https://www.google.com/maps/place/%s";

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
			println("Your IP is: ", restRequest(BabeIPResult.class, BABE_IP_API_CALL).ip);
		if (input.equals("")) 
			return;
		if (input.matches(IP_FORMAT)) {
			GeolocateResult geolocation = restRequest(GeolocateResult.class, API_FORMAT, input);
			// Get map location from latitude and longitude
			String gps = GPS.toString(Float.parseFloat(geolocation.latitude), Float.parseFloat(geolocation.longitude)),
				url = String.format(GOOGLE_MAPS_FORMAT, Encoder.encodeURL(gps));
			File map = Browser.getInstance()
				.visit(new URL(url))
				.screenshotFullAsFile();
			channel.sendMessageEmbeds(buildEmbed(geolocation).build())
				.addFile(map)
				.queue();
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
