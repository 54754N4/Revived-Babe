package commands.level.normal;

import java.io.File;

import bot.hierarchy.UserBot;
import commands.hierarchy.DiscordCommand;
import commands.name.Command;
import json.QrCodeResult;
import json.QrCodeResult.Symbol;
import lib.HTTP.ResponseHandler;
import lib.StringLib;
import lib.encode.Encoder;
import net.dv8tion.jda.api.entities.Message;

public class QrCode extends DiscordCommand {
	// https://goqr.me/api/doc/read-qr-code/
	private static final String API_ENCODE = "http://api.qrserver.com/v1/create-qr-code/?data=%s&color=%s&bgcolor=%s",
			API_DECODE_FILE = "http://api.qrserver.com/v1/read-qr-code/?outputformat=json",
			API_DECODE_URL = "http://api.qrserver.com/v1/read-qr-code/?fileurl=%s",
			DEFAULT_COLOR = "000000",
			DEFAULT_BG_COLOR = "ffffff";
	
	public QrCode(UserBot bot, Message message) {
		super(bot, message, Command.QR_CODE.names);
	}

	@Override
	public String helpMessage() {
		return helpBuilder("<file>",
				"Decodes qr code as file",
				"<text_or_url>",
				"Encodes into qr code the given text, or decodes it if url given",
				"# Encoding args",
				"--color=C\twhere C can be rgb or 3 or 6 hex values",
				"--bgcolor=C\tsame as color but for background");
	}

	@Override
	protected void execute(String input) throws Exception {
		if (message.getAttachments().size() != 0) {
			File attached = message.getAttachments().get(0).downloadToFile().get();
			QrCodeResult[] result = formRequest(QrCodeResult[].class, builder -> builder.addFile("file", attached), API_DECODE_FILE);
			handle(result);
			attached.delete();
			return;
		} else if (input.length() == 0) {
			println("Please give me text to encode or url to decode");
			return;
		} else if (StringLib.isURL(input)) {
			QrCodeResult[] result = restRequest(QrCodeResult[].class, API_DECODE_URL, Encoder.encodeURL(input));
			handle(result);
			return;
		}
		String color = DEFAULT_COLOR,
				bgcolor = DEFAULT_BG_COLOR,
				data = Encoder.encodeURL(input);
		if (hasArgs("--color"))
			color = params.named.get("--color");
		if (hasArgs("--bgcolor"))
			bgcolor = params.named.get("--bgcolor");
		try (ResponseHandler handler = restRequest(API_ENCODE, data, color, bgcolor)) {
			File file = handler.saveResponse("download/qr.png");
			channel.sendFile(file)
				.queue(e -> file.delete(), t -> file.delete());
		}
	}

	private void handle(QrCodeResult[] result) {
		String message;
		for (QrCodeResult r : result) {
			for (Symbol symbol : r.symbol) {
				if (symbol.data != null)
					message = String.format("Result: `%s`", symbol.data);
				else
					message = String.format("Error: `%s`", symbol.error);
				channel.sendMessage(message).queue();
			}
		}
	}
}
