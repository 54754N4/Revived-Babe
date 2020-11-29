package lib.encode;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Base64;

public abstract class Encoder {
	public static enum Type {MORSE, BASE64, CHUCK_NORRIS, URL}
	
	public static String encode(String input, Type type) {
		return handle(input, type, true);
	}
	
	public static String decode(String input, Type type) {
		return handle(input, type, false);
	}
	
	public static String handle(String input, Type type, boolean encode) {
		switch (type) {
			case BASE64:
				if (encode) return encodeBase64(input);
				else return decodeBase64(input);
			case CHUCK_NORRIS:
				if (encode) return encodeCN(input);
				else return decodeCN(input);
			case MORSE:
				if (encode) return encodeMorse(input);
				else return decodeMorse(input);
			case URL:
				if (encode) return encodeURL(input);
				else return decodeURL(input);
			default: throw new IllegalArgumentException("Type can't be null.");
		}
	}
	
	public static String encodeURL(String url) { 
		try { 
			String encodeURL = URLEncoder.encode(url, "UTF-8"); 
			return encodeURL; 
		} catch (UnsupportedEncodingException e) { return "Issue while encoding" +e.getMessage(); } 
	}
	
	public static String decodeURL(String url) { 
		try { 
			String prevURL=""; 
			String decodeURL=url; 
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
	
	public static void main(String[] args) {
		Type type = Type.URL;
		String text =  "Hello Gunter",
			encoded = handle(text, type, true),
			decoded = handle(encoded, type, false);
		System.out.println(text+"\n"+encoded+"\n"+decoded);
	}
}
