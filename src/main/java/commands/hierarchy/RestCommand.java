package commands.hierarchy;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.function.Function;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public interface RestCommand extends ICommand {
	public static final Gson GSON = new Gson();
	public static final OkHttpClient HTTP_CLIENT = new OkHttpClient();
	
	/* Rest + multipart/form requests convenience methods */
	
	default Response restRequest(String apiFormat, Object...args) throws IOException {
		return rest(apiFormat, args);
	}
	
	default <T> T restRequest(Class<T> cls, String apiFormat, Object... args) throws IOException {
		return rest(cls, apiFormat, args);
	}
	
	default Response formRequest(Function<MultipartBody.Builder, MultipartBody.Builder> setup, String apiFormat, Object...args) throws IOException {
		return form(setup, apiFormat, args);
	}
	
	default <T> T formRequest(Class<T> cls, Function<MultipartBody.Builder, MultipartBody.Builder> setup, String apiFormat, Object...args) throws IOException {
		return form(cls, setup, apiFormat, args);
	}
	
	/* Equivalent static convenience methods */
	
	public static File writeFile(Response response, String filepath) throws FileNotFoundException, IOException {
		try (FileOutputStream fos = new FileOutputStream(filepath)) {
			write(response.body().byteStream(), fos);
		}
		return Paths.get(filepath).toFile();
	}
	
	public static long write(InputStream in, OutputStream out) throws IOException {
		try (BufferedInputStream input = new BufferedInputStream(in)) {
			byte[] dataBuffer = new byte[4096];
			int readBytes;
			long totalBytes = 0;
			while ((readBytes = input.read(dataBuffer)) != -1) {
				totalBytes += readBytes;
				out.write(dataBuffer, 0, readBytes);
			}
			out.flush();
			return totalBytes;
		}
	}
	
	public static Response rest(String apiFormat, Object...args) throws IOException {
		Request request = new Request.Builder()
				.url(String.format(apiFormat, args))
				.build();
		return execute(request);
	}
	
	public static <T> T rest(Class<T> cls, String apiFormat, Object... args) throws IOException {
		return convert(cls, rest(apiFormat, args));
	}
	
	public static Response form(Function<MultipartBody.Builder, MultipartBody.Builder> setup, String apiFormat, Object...args) throws IOException {
		MultipartBody.Builder requestBody = new MultipartBody.Builder()
				.setType(MultipartBody.FORM);
		requestBody = setup.apply(requestBody);
		RequestBody body = requestBody.build();
		Request request = new Request.Builder()
				.url(String.format(apiFormat, args))
				.post(body)
				.build();
		return execute(request);
	}
	
	public static <T> T form(Class<T> cls, Function<MultipartBody.Builder, MultipartBody.Builder> setup, String apiFormat, Object...args) throws IOException {
		return convert(cls, form(setup, apiFormat, args));
	}
	
	public static Response execute(Request request) throws IOException {
		return HTTP_CLIENT.newCall(request).execute();
	}
	
	public static <T> T convert(Class<T> cls, Response response) throws JsonSyntaxException, IOException {
		return GSON.fromJson(response.body().string(), cls);
	}
}
