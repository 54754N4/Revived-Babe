package commands.level.normal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import bot.hierarchy.UserBot;
import commands.hierarchy.DiscordCommand;
import commands.name.Command;
import json.TraceMoeResult;
import json.TraceMoeResult.Result;
import lib.encode.Encoder;
import lib.messages.ReactionsHandler;
import lib.messages.ValidatingEmbedBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class TraceMoe extends DiscordCommand {
	private static final String API_FORMAT = "https://api.trace.moe/search%s",
			API_MULTIPART_FORMAT = "https://api.trace.moe/search";

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
			result = formRequest(TraceMoeResult.class, 
					builder -> builder.addFormDataPart("image", filename, RequestBody.create(MediaType.parse("application/octet-stream"), image)), 
					API_MULTIPART_FORMAT);
		} else 
			result = restRequest(TraceMoeResult.class, API_FORMAT, "?url=" + Encoder.encodeURL(input));
		List<EmbedBuilder> packages = buildEmbed(result);
		for (EmbedBuilder pkg : packages)
			channel.sendMessageEmbeds(pkg.build())
				.queue(new ReactionsHandler(bot));	// adds close button by default
	}

	private List<EmbedBuilder> buildEmbed(TraceMoeResult result) {
		List<EmbedBuilder> builders = new ArrayList<>();
		ValidatingEmbedBuilder search = new ValidatingEmbedBuilder();
		search.setTitle("Search Results");
		search.addField("Frame Count", result.frameCount);
		search.addField("Error", result.error);
		builders.add(search);
		for (int i=0 ; i < result.result.length; i++) {
			final Result doc = result.result[i];
			ValidatingEmbedBuilder eb = new ValidatingEmbedBuilder();
			eb.setTitle(doc.filename+" ("+(i+1)+"/"+result.result.length+")");
			eb.addField("Anilist ID", doc.anilist);
			eb.addField("Episode", doc.episode);
			eb.addField("From", doc.from);
			eb.addField("To", doc.to);
			eb.addField("Similarity", doc.similarity*100+" %");
			eb.addField("Video Preview", doc.video);
			eb.addField("Image Preview", doc.image);
			builders.add(eb);
		}
		return builders;
	}
}
