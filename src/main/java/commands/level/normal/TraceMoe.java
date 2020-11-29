package commands.level.normal;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import bot.model.UserBot;
import commands.model.DiscordCommand;
import commands.name.Command;
import json.TraceMoeResult;
import json.TraceMoeResult.Doc;
import lib.messages.ValidatingEmbedBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.requests.RestAction;

public class TraceMoe extends DiscordCommand {
	private static final String API_FORMAT = "https://trace.moe/api/search%s";

	public TraceMoe(UserBot bot, Message message) {
		super(bot, message, Command.TRACE.names);
	}

	@Override
	public String helpMessage() {
		return helpBuilder("<url or file upload>", 
			"Sends back data on potential anime matched from image. (Max image size is 10mb)");
	}

	@Override
	protected void execute(String input) throws Exception {
		boolean hasAttachment = message.getAttachments().size() != 0,
			hasInput = !input.equals(""); 
		if (!hasAttachment && !hasInput) {
			println("Give me either an image url or file upload.");
			return;
		}
		TraceMoeResult result;
		if (hasAttachment) {
			Attachment attachment = message.getAttachments().get(0);
			String filename = ".\\download\\trace\\"+attachment.getFileName();
			final File image = attachment.downloadToFile(filename).get();
			result = formRequest(API_FORMAT, TraceMoeResult.class, builder -> builder.addFile("image", image), "");	// empty string because of %s
		} else 
			result = restRequest(API_FORMAT, TraceMoeResult.class, "?url=" + URLEncoder.encode(input, StandardCharsets.UTF_8));
		buildEmbed(result)
			.map(EmbedBuilder::build)
			.map(channel::sendMessage)
			.forEach(RestAction::queue);
	}

	private static Stream<EmbedBuilder> buildEmbed(TraceMoeResult result) {
		List<EmbedBuilder> builders = new ArrayList<>();
		ValidatingEmbedBuilder search = new ValidatingEmbedBuilder();
		search.setTitle("Search Results");
		search.addField("Total Frames Searched", result.RawDocsCount);
		search.addField("Retrieval Time", result.RawDocsSearchTime);
		search.addField("Search Time", result.ReRankSearchTime);
		search.addField("From Cache", result.CacheHit);
		search.addField("Trials", result.trial);
		search.addField("Searches Remaining", result.limit);
		search.addField("Time until Reset", result.limit_ttl);
		search.addField("Quota Remaining", result.quota);
		builders.add(search);
		for (Doc doc : result.getDocs()) {
			ValidatingEmbedBuilder eb = new ValidatingEmbedBuilder();
			eb.setTitle(doc.title_romaji);
			eb.addField("Native Title", doc.title_native);
			eb.addField("Chinese Title", doc.title_chinese);
			eb.addField("English Title", doc.title_english);
			eb.addField("Similarity", doc.similarity*100+" %");
			eb.addField("Episode", doc.episode);
			eb.addField("Hentai", doc.is_adult);
			eb.addField("Match Start Time", doc.from);
			eb.addField("Match End Time", doc.to);
			eb.addField("Match Position", doc.at);
			eb.addField("AniList ID", doc.anilist_id);
			eb.addField("MyAnimeList ID", doc.mal_id);
			eb.addField("Filename", doc.filename);
			eb.addField("Token Thumb", doc.tokenthumb);
			builders.add(eb);
		}
		return builders.parallelStream();
	}
}
