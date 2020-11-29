package lib.encode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.function.Consumer;

// Convenience wrapper over native java message digests
public abstract class Hash {
	private static final byte[] HEX_ARRAY = "0123456789ABCDEF".getBytes(StandardCharsets.US_ASCII);
	private static final int BUFFER_SIZE = 8192, EOF = -1;
	// Singletons pattern
	public static final MD5 MD5 = new MD5();
	public static final SHA256 SHA256 = new SHA256();
	public static final SHA356 SHA356 = new SHA356();
	
	private String algorithm;
	
	protected Hash(String algorithm) {
		this.algorithm = algorithm;
	}
	
	public MessageDigest getMessageDigest() throws NoSuchAlgorithmException {
		return MessageDigest.getInstance(algorithm);
	}
	
	public byte[] hash(byte[] bytes) throws NoSuchAlgorithmException {
		MessageDigest md = getMessageDigest();
		md.update(bytes);
		return md.digest();
	}
	
	public byte[] hash(File file, Consumer<byte[]> consumer) throws NoSuchAlgorithmException, IOException {
		MessageDigest md = getMessageDigest();
		try (DigestInputStream dis = new DigestInputStream(new FileInputStream(file), md)) {
			byte[] buffer = new byte[BUFFER_SIZE];
			while (dis.read(buffer) != EOF)
				consumer.accept(buffer);
			return md.digest();
		}
	}
	
	public byte[] hash(File file) throws NoSuchAlgorithmException, IOException {
		return hash(file, buffer -> {});
	}
	
	public String hashString(String input) throws NoSuchAlgorithmException {
		return bytesToHex(hash(input.getBytes(StandardCharsets.UTF_8)));
	}
	
	public String hashString(File file, Consumer<byte[]> consumer) throws IOException, NoSuchAlgorithmException {
		return bytesToHex(hash(file, consumer));
	}
	
	public String hashString(File file) throws IOException, NoSuchAlgorithmException {
		return hashString(file, buffer -> {});	// no consumer given => do nothing
	}
	
	public static String bytesToHex(byte[] bytes) {
	    byte[] hexChars = new byte[bytes.length * 2];
	    for (int j = 0; j < bytes.length; j++) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = HEX_ARRAY[v >>> 4];
	        hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
	    }
	    return new String(hexChars, StandardCharsets.UTF_8);
	}
	
	private static class MD5 extends Hash {
		private MD5() {
			super("MD5");
		}
	}

	public static class SHA256 extends Hash {
		private SHA256() {
			super("SHA-256");
		}
	}
	
	public static class SHA356 extends Hash {
		private SHA356() {
			super("SHA3-256");
		}
	}
	
	public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
		System.out.println(SHA256.hashString(new File("out.java")));
	}
}