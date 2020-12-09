package commands.level.normal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import bot.hierarchy.UserBot;
import commands.hierarchy.DiscordCommand;
import commands.name.Command;
import json.TraceMoeResult;
import json.TraceMoeResult.Doc;
import lib.encode.Encoder;
import lib.messages.ReactionsHandler;
import lib.messages.ValidatingEmbedBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.MessageReaction;

public class TraceMoe extends DiscordCommand {
	private static final String API_FORMAT = "https://trace.moe/api/search%s",
			PREVIEW_FORMAT = "https://media.trace.moe/video/%s/%s?t=%s&token=%s";

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
			result = restRequest(API_FORMAT, TraceMoeResult.class, "?url=" + Encoder.encodeURL(input));
		List<Package> packages = buildEmbed(result);
		for (Package pkg : packages)
			channel.sendMessage(pkg.builder.build())
				.queue(new ReactionsHandler(bot)
						.handle(0x25B6, pkg.consumer));
	}

	private List<Package> buildEmbed(TraceMoeResult result) {
		List<Package> builders = new ArrayList<>();
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
		builders.add(new Package(search, action -> {}));
		for (int i=0 ; i < result.docs.length; i++) {
			final Doc doc = result.docs[i];
			ValidatingEmbedBuilder eb = new ValidatingEmbedBuilder();
			eb.setTitle(doc.title_romaji+" ("+(i+1)+"/"+result.docs.length+")");
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
			builders.add(new Package(eb, action -> 
				printlnIndependently(
						PREVIEW_FORMAT, 
						""+doc.anilist_id, 
						Encoder.encodeURL(doc.filename), 
						""+doc.at, 
						""+doc.tokenthumb)));
		}
		return builders;
	}
	
	private class Package {
		public final EmbedBuilder builder;
		public final Consumer<MessageReaction> consumer;
		
		public Package(EmbedBuilder builder, Consumer<MessageReaction> consumer) {
			this.builder = builder;
			this.consumer = consumer;
		}
	}
}
