package lib.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLHandler extends DefaultHandler {
	private Map<String, Action> tagHandlers;
	
	private XMLHandler(Map<String, Action> tagHandlers) {
		this.tagHandlers = tagHandlers;
	}

	public void startElement(String uri, String localName, String tagName, final Attributes attributes) throws SAXException {
		final Action action = tagHandlers.get(tagName);
		if (action == null) 
			return;
		action.attributeHandlers.entrySet().stream()
			.filter(entry -> attributes.getIndex(entry.getKey()) != -1)
			.forEach(entry -> 
				entry.getValue()
					.forEach(consumer -> consumer.accept(attributes.getValue(entry.getKey()))));
	}
	
	public static final class Builder {
		private Map<String, Action> tagHandlers = new HashMap<>();
		
		public Builder add(Action action) {
			tagHandlers.put(action.tag, action);
			return this;
		}
		
		public XMLHandler build() {
			return new XMLHandler(tagHandlers);
		}
	}
	
	public static final class Action {
		private final String tag;
		private final Map<String, List<Consumer<String>>> attributeHandlers;
		
		private Action(String tag, Map<String, List<Consumer<String>>> attributeHandlers) {
			this.tag = tag;
			this.attributeHandlers = attributeHandlers;
		}
		
		@Override
		public String toString() {
			return tag;
		}
		
		public static final class Builder {
			private String tag;
			private Map<String, List<Consumer<String>>> attributeHandlers = new HashMap<>();
			
			public Builder(String tag) {
				this.tag = tag;
			}
			
			public Builder setTag(String tag) {
				this.tag = tag;
				return this;
			}
			
			public Builder handle(String name, Consumer<String> handler) {
				attributeHandlers.putIfAbsent(name, new ArrayList<>());
				attributeHandlers.get(name).add(handler);
				return this;
			}
			
			public Action build() {
				return new Action(tag, attributeHandlers);
			}
		} 
	}
}
