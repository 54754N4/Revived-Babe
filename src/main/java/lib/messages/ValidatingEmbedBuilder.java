package lib.messages;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class ValidatingEmbedBuilder extends EmbedBuilder {
	
	public ValidatingEmbedBuilder addField(String name, Object object) {
		if (object != null)
			addField(name, object.toString(), false);
		return this;
	}
	
	@Override
	public ValidatingEmbedBuilder addField(String name, String value, boolean inline) {
		if (name != null && value != null) 
			super.addField(name, value, inline);
		return this;
	}
	
	@Override
	public MessageEmbed build() {
		if (getFields().size() == 0) 
			addField("Error", "request failed");
		return super.build();
	}
}
