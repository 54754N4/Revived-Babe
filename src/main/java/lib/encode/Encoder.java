package lib.encode;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.function.Function;

public abstract class Encoder {
	public static enum Type {
		MORSE(Encoder::encodeMorse, Encoder::decodeMorse), 
		BASE64(Encoder::encodeBase64, Encoder::decodeBase64),
		CHUCK_NORRIS(Encoder::encodeCN, Encoder::decodeCN),
		URL(Encoder::encodeURL, Encoder::decodeURL);
		
		public final Function<String, String> encoder, decoder;
		
		private Type(Function<String, String> encoder, Function<String, String> decoder) {
			this.encoder = encoder;
			this.decoder = decoder;
		}
	}
	
	public static String encodeURL(String url) { 
		try { 
			String encodeURL = URLEncoder.encode(url, "UTF-8"); 
			return encodeURL; 
		} catch (UnsupportedEncodingException e) { 
			return "Issue while encoding" +e.getMessage(); 
		} 
	}
	
	public static String decodeURL(String url) { 
		try { 
			String prevURL = "", decodeURL = url; 
			while(!prevURL.equals(decodeURL)) { 
				prevURL = decodeURL; 
				decodeURL = URLDecoder.decode(decodeURL, "UTF-8"); 
			}
			return decodeURL; 
		} catch (UnsupportedEncodingException e) { return "Issue while decoding" +e.getMessage(); } 
	} 
	
	public static String encodeMorse(String msg) {
		return Morse.encode(msg);
	}
	
	public static String decodeMorse(String encoded) {
		return Morse.decode(encoded);
	}
	
	public static String encodeCN(String msg) {
		return CNCipher.encrypt(msg);
	}
	
	public static String decodeCN(String encoded) {
		return CNCipher.decrypt(encoded);
	}
	
	public static String encodeBase64(String msg) {
		return Base64.getEncoder().encodeToString(msg.getBytes());
	}	
	
	public static String decodeBase64(String encoded) {
		return new String(Base64.getDecoder().decode(encoded));
	}
}
