package tests;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import annotations.SlowTest;
import commands.hierarchy.RestCommand;
import commands.level.normal.Chuck;
import commands.level.normal.ExchangeRates;
import commands.level.normal.Food;
import commands.level.normal.Insult;
import commands.level.normal.Lyrics;
import commands.level.normal.QrCode;
import commands.level.normal.Recipe;
import commands.level.normal.Speak;
import commands.level.normal.Synonyms;
import commands.level.normal.Time;
import json.ChuckNorrisResult;
import json.ExchangeRatesResult;
import json.FoodImageResult;
import json.FoodRecipeResults;
import json.InsultsResult;
import json.LyricsOVHResult;
import json.QrCodeResult;
import json.QrCodeResult.Symbol;
import json.ThesaurusResult;
import json.TimeResult;
import lambda.ThrowableSupplier;
import lib.encode.Encoder;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TestApis {
	
	@SlowTest
	public void givenChuckRequest_whenExecuted_succeeds() {
		request(ChuckNorrisResult.class)
			.setHost(Chuck.API_CALL)
			.then(r -> assertNotNull(r.value))
			.andThen(r -> assertNotEquals(r.value, ""))
			.execute();
	}
	
	@SlowTest
	public void givenRatesRequest_whenExecuted_succeeds() {
		request(ExchangeRatesResult.class)
			.setHost(ExchangeRates.API_FORMAT)
			.setArgs(System.getenv("OPEN_EXCHANGE_RATES_API"))
			.then(r -> assertNotNull(r.rates))
			.execute();
	}
	
	@SlowTest
	public void givenFoodRequest_whenExecuted_succeeds() {
		request(FoodImageResult.class)
			.setHost(Food.API_CALL)
			.then(r -> assertNotNull(r.image))
			.andThen(r -> assertNotEquals(r.image, ""))
			.execute();
	}
	
	@SlowTest
	public void givenInsultRequest_whenExecuted_succeeds() {
		request(InsultsResult.class)
			.setHost(Insult.API_FORMAT)
			.setArgs(Insult.DEFAULT_LANG, Insult.DEFAULT_TYPE)
			.then(r -> assertNotNull(r.insult))
			.andThen(r -> assertNotEquals(r.insult, ""))
			.execute();
	}
	
	@SlowTest
	public void givenLyricsRequest_whenExecuted_succeeds() {
		request(LyricsOVHResult.class)
			.setHost(Lyrics.API_FORMAT)
			.setArgs(Encoder.encodeURL("takida"), Encoder.encodeURL("to have and to hold"))
			.then(r -> assertNotNull(r.lyrics))
			.andThen(r -> assertNotEquals(r.lyrics, ""))
			.execute();
	}
	
	@SlowTest
	public void givenQrRequest_whenExecuted_succeeds() throws IOException {
		String text = "Hello world please!",
			filename = "qr.out";
		// Test encode
		Response response = request()
			.setHost(QrCode.API_ENCODE)
			.setArgs(Encoder.encodeURL(text), QrCode.DEFAULT_COLOR, QrCode.DEFAULT_BG_COLOR)
			.execute();
		File file = RestCommand.writeFile(response, filename);
		assertNotNull(file);
		// Decoding checker
		Consumer<QrCodeResult[]> assertDecoded = results -> {
			assertNotNull(results);
			assertNotEquals(results.length, 0);
			QrCodeResult result = results[0];
			assertNotNull(result);
			Symbol[] symbols = result.symbol;
			assertNotEquals(symbols.length, 0);
			Symbol symbol = symbols[0];
			assertNotNull(symbol.data);
			assertEquals(symbol.data, text);
		};
		// Test file decode
		request(QrCodeResult[].class)
			.setHost(QrCode.API_DECODE_FILE)
			.setForm(builder -> builder.addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), file)))
			.then(assertDecoded)
			.execute();
		assertTrue(file.delete());
		// Test decode
		String encoded = String.format(QrCode.API_ENCODE, Encoder.encodeURL(text), QrCode.DEFAULT_COLOR, QrCode.DEFAULT_BG_COLOR);
		request(QrCodeResult[].class)
			.setHost(QrCode.API_DECODE_URL)
			.setArgs(Encoder.encodeURL(encoded))
			.then(assertDecoded)
			.execute();
	}
	
	@SlowTest
	public void givenRecipeRequest_whenExecuted_succeeds() {
		request(FoodRecipeResults.class)
			.setHost(Recipe.API_FORMAT)
			.setArgs("", "", "p=1")
			.then(r -> assertNotNull(r.title))
			.andThen(r -> assertNotEquals(r.title, ""))
			.andThen(r -> assertNotNull(r.href))
			.andThen(r -> assertNotEquals(r.href, ""))
			.execute();
	}
	
	@SlowTest
	public void givenSpeakRequest_whenExecuted_succeeds() throws FileNotFoundException, IOException {
		Response response = request()
			.setHost(Speak.API_FORMAT)
			.setArgs(System.getenv("VOICE_RSS_API"),
				Encoder.encodeURL("hello world"),
				Speak.DEFAULT_LANGUAGE,
				Speak.DEFAULT_FILE_FORMAT,
				Speak.DEFAULT_RATE,
				Speak.DEFAULT_VOICE)
			.execute();
		File file = RestCommand.writeFile(response, "out.mp3");
		assertNotEquals(file.length(), 0);
		assertTrue(file.delete());
	}
	
	@SlowTest
	public void givenSynonymsRequest_whenExecuted_succeeds() {
		String word = "joy";
		request(ThesaurusResult.class)
			.setHost(Synonyms.API_FORMAT)
			.setArgs(System.getenv("THESAURUS_API"),
					Encoder.encodeURL(word),
					Synonyms.DEFAULT_LANG)
			.then(r -> assertNotEquals(r.response.length, 0))
			.execute();
	} 
	
	@SlowTest
	public void givenTimeRequest_whenExecuted_succeeds() throws IOException {
		List<String> timezones = Time.getTimezones();
		request(TimeResult.class)
			.setHost(Time.API_FORMAT)
			.setArgs("timezone/" + timezones.get(0))
			.then(r -> assertNotNull(r.getDatetime()))
			.execute();
	}
	
	// TODO Test case trace moe
	
	/* Convenience methods */
	
	public static <T> ReturnableRequest<T> request(Class<T> cls) {
		return new ReturnableRequest<>(cls);
	}
	
	public static Request request() {
		return new Request();
	}
	
	/* Convenience/helper classes */
	
	public static class Request {
		private String host;
		private Object[] args;
		private Consumer<Response> consumer;
		private Function<MultipartBody.Builder, MultipartBody.Builder> setup;
		private Response response;
		
		public Request setHost(String host) {
			this.host = host;
			return this;
		}
		
		public Request setArgs(Object...args) {
			this.args = args;
			return this;
		}
		
		public Request then(Consumer<Response> consumer) {
			this.consumer = consumer;
			return this;
		}
		
		public Request andThen(Consumer<Response> consumer) {
			this.consumer.andThen(consumer);
			return this;
		}
		
		public Request setForm(Function<MultipartBody.Builder, MultipartBody.Builder> setup) {
			this.setup = setup;
			return this;
		}
		
		public Response execute() {
			ThrowableSupplier<Response> requester = setup == null ? 
					() -> RestCommand.restRequest(host, args) :
					() -> RestCommand.formRequest(setup, host, args);
			assertDoesNotThrow(() -> {
				response = requester.get();
				int code = response.code();
				assertEquals(200, code, "Request failed");
				if (consumer != null)
					consumer.accept(response);
			});
			return response;
		}
	}
	
	public static class ReturnableRequest<T> {
		private String host;
		private Object[] args;
		private Class<T> cls;
		private Consumer<T> consumer;
		private Function<MultipartBody.Builder, MultipartBody.Builder> setup;
		private T result;
		
		private ReturnableRequest(Class<T> cls) {
			this.cls = cls;
		}

		public ReturnableRequest<T> setHost(String host) {
			this.host = host;
			return this;
		}
		
		public ReturnableRequest<T> setArgs(Object...args) {
			this.args = args;
			return this;
		}
		
		public ReturnableRequest<T> then(Consumer<T> consumer) {
			this.consumer = consumer;
			return this;
		}
		
		public ReturnableRequest<T> andThen(Consumer<T> consumer) {
			this.consumer.andThen(consumer);
			return this;
		}
		
		public ReturnableRequest<T> setForm(Function<MultipartBody.Builder, MultipartBody.Builder> setup) {
			this.setup = setup;
			return this;
		}
		
		public T execute() {
			ThrowableSupplier<T> requester = setup == null ? 
					() -> RestCommand.restRequest(cls, host, args) :
					() -> RestCommand.formRequest(cls, setup, host, args);
			assertDoesNotThrow(() -> {
				result = requester.get();
				assertNotNull(result);
				if (consumer != null)
					consumer.accept(result);
			});
			return result;
		}
	}
}
