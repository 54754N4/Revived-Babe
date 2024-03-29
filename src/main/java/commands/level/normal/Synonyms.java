package commands.level.normal;

import java.util.Arrays;

import bot.hierarchy.UserBot;
import commands.hierarchy.DiscordCommand;
import commands.name.Command;
import json.ThesaurusResult;
import json.ThesaurusResult.Response;
import lib.StringLib;
import lib.encode.Encoder;
import lib.messages.PagedEmbedHandler;
import net.dv8tion.jda.api.entities.Message;

public class Synonyms extends DiscordCommand {
	// https://thesaurus.altervista.org/service
	public static final String[] languages = "cs_CZ, da_DK, de_CH, de_DE, en_US, el_GR, es_ES, fr_FR, hu_HU, it_IT, no_NO, pl_PL, pt_PT, ro_RO, ru_RU, sk_SK".split(", ");
	public static final String API_FORMAT = "http://thesaurus.altervista.org/thesaurus/v1?key=%s&word=%s&language=%s&output=json",
			DEFAULT_LANG = "en_US";
	
	public Synonyms(UserBot bot, Message message) {
		super(bot, message, Command.SYNONYMS.names);
	}

	@Override
	public String helpMessage() {
		return helpBuilder("<word>", 
				"-l\t\tlists the languages that can be used",
				"--lang=L\twhere L is the language you chose",
				"Lists synonyms of a word in a specific language");
	}

	@Override
	public void execute(String input) throws Exception {
		String lang = DEFAULT_LANG;
		if (hasArgs("-l")) {
			printItemsIndexed(languages);
			return;
		} else if (hasArgs("--lang")) {
			lang = getParams().getNamed().get("--lang");
			if (StringLib.isInteger(lang)) 
				lang = languages[Integer.parseInt(lang)];
		}
		if (input.trim().length() == 0) {
			println("Please give me a word");
			return;
		}
		final ThesaurusResult response = restRequest(
				ThesaurusResult.class, 
				API_FORMAT, 
				System.getenv("THESAURUS_API"),
				Encoder.encodeURL(input),
				lang);
		getChannel().sendMessage("Loading...")
			.queue(new PagedEmbedHandler<Response>(getBot(), () -> Arrays.asList(response.response))
				.setItemHandler((index, total, r, builder) -> 
					builder.setTitle(String.format("Synonyms for %s - Page %d/%d", getInput(), index+1, total))
						.addField("Category", r.list.category, true)
						.addField("Synonyms", r.list.synonyms, true)));
	}
}
