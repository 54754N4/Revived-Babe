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

import bot.hierarchy.UserBot;
import net.dv8tion.jda.api.entities.Message;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public abstract class RestCommand extends ListenerCommand {
	private static Gson gson;
	private static OkHttpClient client;
	
	public RestCommand(UserBot bot, Message message, String[] names) {
		super(bot, message, names);
	}

	/* Lazy loaded singletons */
	
	private static final synchronized Gson gson() {
		if (gson == null)
			gson = new Gson();
		return gson;
	}
	
	private static final synchronized OkHttpClient client() {
		if (client == null)
			client = new OkHttpClient();
		return client;
	}
	
	/* Rest + multipart/form requests convenience methods */
	
	public static Response restRequest(String apiFormat, Object...args) throws IOException {
		Request request = new Request.Builder()
				.url(String.format(apiFormat, args))
				.build();
		return execute(request);
	}
	
	public static <T> T restRequest(Class<T> cls, String apiFormat, Object... args) throws IOException {
		return convert(cls, restRequest(apiFormat, args));
	}
	
	public static Response formRequest(Function<MultipartBody.Builder, MultipartBody.Builder> setup, String apiFormat, Object...args) throws IOException {
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
	
	public static <T> T formRequest(Class<T> cls, Function<MultipartBody.Builder, MultipartBody.Builder> setup, String apiFormat, Object...args) throws IOException {
		return convert(cls, formRequest(setup, apiFormat, args));
	}
	
	/* Convenience methods */
	
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
	
	public static Response execute(Request request) throws IOException {
		return client().newCall(request).execute();
	}
	
	public static <T> T convert(Class<T> cls, Response response) throws JsonSyntaxException, IOException {
		return gson().fromJson(response.body().string(), cls);
	}
}
