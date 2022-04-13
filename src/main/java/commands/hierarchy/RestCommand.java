package commands.hierarchy;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Paths;
import java.util.Map;
import java.util.function.Function;

import com.google.gson.Gson;

import bot.hierarchy.UserBot;
import net.dv8tion.jda.api.entities.Message;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public abstract class RestCommand extends ListenerCommand {
	protected static final Gson gson = new Gson();
	protected static final OkHttpClient client = new OkHttpClient();
	
	public RestCommand(UserBot bot, Message message, String[] names) {
		super(bot, message, names);
	}

	/* Rest + multipart/form requests convenience methods */
	
	public static Response restRequest(String apiFormat, Object...args) throws IOException {
		Request request = new Request.Builder()
				.url(String.format(apiFormat, args))
				.build();
		return client.newCall(request).execute();
	}
	
	public static <T> T restRequest(Class<T> cls, String apiFormat, Object... args) throws IOException {
		Response response = restRequest(apiFormat, args);
		return gson.fromJson(response.body().string(), cls);
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
		return client.newCall(request).execute();
	}
	
	public static <T> T formRequest(Class<T> cls, Function<MultipartBody.Builder, MultipartBody.Builder> setup, String apiFormat, Object...args) throws IOException {
		Response response = formRequest(setup, apiFormat, args);
		return gson.fromJson(response.body().string(), cls);
	}
	
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
	
	public static String getParamsString(Map<String, String> params, String separator, boolean encode) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
        	if (result.length() != 0) result.append(separator);
        	if (entry.getValue() == null)
        		result.append(entry.getKey());
        	else if (encode)
        		result.append(URLEncoder.encode(entry.getKey(), "UTF-8"))
        			.append("=")
        			.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        	else 
        		result.append(entry.getKey())
    				.append("=")
    				.append(entry.getValue());
        }
        return result.toString(); 
    }
}
