package commands.level.normal;

import java.io.File;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import bot.hierarchy.MusicBot;
import bot.hierarchy.UserBot;
import commands.hierarchy.DiscordCommand;
import commands.name.Command;
import lib.encode.Encoder;
import lib.scrape.Browser;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import okhttp3.Response;

public class Speak extends DiscordCommand implements AudioLoadResultHandler {
	public static final String API = "http://www.voicerss.org/api/",  
			API_FORMAT = "http://api.voicerss.org/?key=%s&src=%s&hl=%s&f=%s&r=%s&v=%s&c=MP3",
			DEFAULT_FILE_FORMAT = "48khz_16bit_stereo",
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
		if (languages == null || voices == null) {
			destructibleMessage("Loading voices and languages..", 5).queue();
			languages = loadLanguages();
			voices = loadVoices();
		}
		String language = hasArgs("--lang") ? getParams().getNamed().get("--lang") : DEFAULT_LANGUAGE, 
			rate = hasArgs("--rate") ? getParams().getNamed().get("--rate") : DEFAULT_RATE, 
			voice = hasArgs("--voice") ? getParams().getNamed().get("--voice") : DEFAULT_VOICE;
		if (hasArgs("-l", "--languages")) { 
			printMapFiltered(languages, input);
			return;
		} else if (hasArgs("-v", "--voices")) { 
			printMapFiltered(voices, input);
			return;
		}
		// Connect to channel
		Guild guild = getGuild();
		MusicBot bot = getMusicBot();
		boolean connected = bot.isConnected(guild),
				authorConnected = getMessage().getMember().getVoiceState().inAudioChannel();
		if (!connected && !authorConnected) {
			println("I have to be in a voice channel to talk.. So you also have to be in one so I can join you lol..");
			return;
		} else if (!connected)
			bot.connect(getMessage().getMember().getVoiceState().getChannel());
		// Generate WAV from API
		Response response = restRequest(
				API_FORMAT, 
				System.getenv("VOICE_RSS_API"),
				Encoder.encodeURL(input), 
				language, 
				DEFAULT_FILE_FORMAT, 
				rate, 
				voice);
		getLogger().info(String.format(API_FORMAT, System.getenv("VOICE_RSS_API"), URLEncoder.encode(input, "UTF-8"), language, DEFAULT_FILE_FORMAT, rate, voice));
		if (!response.isSuccessful()) {
			println("HTTP error code %d", response.code());
			response.body().string().lines().forEach(this::println);
		} else {
			File file = writeFile(response, "speak/out.mp3");
			bot.play(guild, file.getAbsolutePath(), this);
		}
	}
	
	private static Map<String, String> loadLanguages() {
		Map<String, String> map = new TreeMap<>();
		WebElement table = Browser.getInstance().visit(API)
			.waitGet(By.cssSelector(".api-content > section:nth-child(6) > div.table"));
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
		return map;
	}
	
	private static Map<String, String> loadVoices() {
		Map<String, String> map = new TreeMap<>();
		WebElement table = Browser.getInstance().visit(API)
				.waitGet(By.cssSelector(".api-content > section:nth-child(7) > div.table"));
		int size = table.findElements(By.cssSelector(".table-row")).size();
		String tableCellAccessor = ".table-row:nth-child(%d) > .table-cell:nth-child(%d)",
			lang, name, previous = "ERROR";
		for (int i=2; i<=size; i++) {
			lang = table.findElement(By.cssSelector(String.format(tableCellAccessor, i, 1))).getText();
			name = table.findElement(By.cssSelector(String.format(tableCellAccessor, i, 2))).getText();
			if (lang.equals("")) // empty == use previously set name
				lang = previous;
			previous = lang;
			map.put(name, lang);
		}
		return map;
	}

	@Override
	public void trackLoaded(AudioTrack track) {
		getMusicBot().getScheduler(getGuild())
			.playThenBacktrack(track);
	}

	@Override
	public void playlistLoaded(AudioPlaylist playlist) {
		printIndependently("Wtf received a playlist instead of a track. Tell my owner");
	}

	@Override
	public void noMatches() {
		printIndependently("No matches found. Tell my owner");
	}

	@Override
	public void loadFailed(FriendlyException exception) {
		printIndependently("Error: %s", exception.getMessage());
	}
}