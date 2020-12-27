package commands.level.normal;

import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import bot.hierarchy.MusicBot;
import bot.hierarchy.UserBot;
import commands.hierarchy.DiscordCommand;
import commands.name.Command;
import lib.HTTP.ResponseHandler;
import lib.scrape.Dependency;
import net.dv8tion.jda.api.entities.Message;

public class Speak extends DiscordCommand {
	public static final String API = "http://www.voicerss.org/api/",  
			API_FORMAT = "http://api.voicerss.org/?key=%s&src=%s&hl=%s&f=%s&r=%s&v=%s",
			API_KEY = System.getenv("VOICE_RSS_API"),
			DEFAULT_FILE_FORMAT = "8khz_16bit_stereo",
			DEFAULT_LANGUAGE = "en-us",
			DEFAULT_RATE = "0",
			DEFAULT_VOICE = "Linda";
	private static Map<String, String> languages, voices;
	
	public Speak(UserBot bot, Message message) {
		super(bot, message, Command.SPEAK.names);
	}

	@Override
	public String helpMessage() {
		return helpBuilder("<text>", 
			"-l or --languages\tmakes me list languages",
			"-v or --voices\tmakes me list voices",
			"--lang=L\twhere L stands for a valid language",
			"--voice=V\twhere V stands for a valid voice name",
			"--rate=S\twhere S is speech rate (ranging between [-10, 10], default is 0)",
			"Makes me text-to-speech.");
	}

	@Override
	protected void execute(String input) throws Exception {
		String language = hasArgs("--lang") ?  params.named.get("--lang") : DEFAULT_LANGUAGE, 
			rate = hasArgs("--rate") ?  params.named.get("--rate") : DEFAULT_RATE, 
			voice = hasArgs("--voice") ?  params.named.get("--voice") : DEFAULT_VOICE;
		if (hasArgs("-l", "--languages")) { languages(); return; }
		else if (hasArgs("-v", "--voices")) { voices(); return; }
		// Connect to channel
		MusicBot bot = getMusicBot();
		boolean connected = bot.isConnected(guild),
				authorConnected = message.getMember().getVoiceState().inVoiceChannel();
		if (!connected && !authorConnected) {
			println("I have to be in a voice channel to talk.. So you also have to be in one so I can join you lol..");
			return;
		} else if (!connected) 
			bot.connectTo(message.getMember().getVoiceState().getChannel());
		// Generate WAV from API
		try (ResponseHandler handler = restRequest(API_FORMAT, API_KEY, URLEncoder.encode(input, "UTF-8"), 
				language, DEFAULT_FILE_FORMAT, rate, voice)) {
			bot.play(guild, handler.saveResponse("speak/out.wav"));
		}
	}
	
	private void languages() {
		if (languages == null) {	// lazy loading
			Map<String, String> map = new TreeMap<>();
			WebElement table = Dependency.browser.visit(API)
				.waitFor(By.cssSelector(".api-content > section:nth-child(6) > div.table"));
			List<String> names = table.findElements(By.cssSelector(".table-row > .table-cell:nth-child(1)"))
					.stream()
					.map(WebElement::getText)
					.collect(Collectors.toList()),
				values = table.findElements(By.cssSelector(".table-row > .table-cell:nth-child(2)"))
					.stream()
					.map(WebElement::getText)
					.collect(Collectors.toList());
			for (int i=0; i<names.size(); i++)
				map.put(names.get(i), values.get(i));
			languages = map;
		}
		printMap(languages);
	}
	
	private void voices() {
		if (voices == null) {
			Map<String, String> map = new TreeMap<>();
			WebElement table = Dependency.browser.visit("http://www.voicerss.org/api/")
					.waitFor(By.cssSelector(".api-content > section:nth-child(7) > div.table"));
			int size = table.findElements(By.cssSelector(".table-row")).size();
			String tableCellAccessor = ".table-row:nth-child(%d) > .table-cell:nth-child(%d)",
				lang, name, previous = "ERROR";
			for (int i=2; i<=size; i++) {
				lang = table.findElement(By.cssSelector(String.format(tableCellAccessor, i, 1))).getText();
				name = table.findElement(By.cssSelector(String.format(tableCellAccessor, i, 2))).getText();
				if (lang.equals("")) // empty == use previously set name
					lang = previous;
				previous = lang;
				map.put(lang, name);
			}
			voices = map;
		}
		printMap(voices);
	}
}