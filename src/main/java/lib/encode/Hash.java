package lib.encode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Set;
import java.util.function.Consumer;

// Convenience wrapper over native java message digests
public interface Hash {
	public static final byte[] HEX_ARRAY = "0123456789ABCDEF".getBytes(StandardCharsets.US_ASCII);
	public static final int BUFFER_SIZE = 8192, EOF = -1;
	
	static byte[] hash(String algorithm, byte[] bytes) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance(algorithm);
		md.update(bytes);
		return md.digest();
	}
	
	static byte[] hash(String algorithm, File file, Consumer<byte[]> consumer) throws NoSuchAlgorithmException, IOException {
		MessageDigest md = MessageDigest.getInstance(algorithm);
		try (DigestInputStream dis = new DigestInputStream(new FileInputStream(file), md)) {
			byte[] buffer = new byte[BUFFER_SIZE];
			while (dis.read(buffer) != EOF)
				consumer.accept(buffer);
			return md.digest();
		}
	}
	
	static byte[] hash(String algorithm, File file) throws NoSuchAlgorithmException, IOException {
		return hash(algorithm, file, buffer -> {});
	}
	
	static String hashString(String algorithm, String input) throws NoSuchAlgorithmException {
		return bytesToHex(hash(algorithm, input.getBytes(StandardCharsets.UTF_8)));
	}
	
	static String hashString(String algorithm, File file, Consumer<byte[]> consumer) throws IOException, NoSuchAlgorithmException {
		return bytesToHex(hash(algorithm, file, consumer));
	}
	
	static String hashString(String algorithm, File file) throws IOException, NoSuchAlgorithmException {
		return hashString(algorithm, file, buffer -> {});	// no consumer given => do nothing
	}
	
	static String bytesToHex(byte[] bytes) {
	    byte[] hexChars = new byte[bytes.length * 2];
	    for (int j = 0; j < bytes.length; j++) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = HEX_ARRAY[v >>> 4];
	        hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
	    }
	    return new String(hexChars, StandardCharsets.UTF_8);
	}
	
	static Set<String> algorithms() {
		return Security.getAlgorithms("MessageDigest");
	}
}