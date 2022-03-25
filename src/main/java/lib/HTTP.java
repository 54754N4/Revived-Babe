package lib;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public final class HTTP {
	public static final String NEWLINE = "\r\n";
	public static final byte[] NEWLINE_BYTES = NEWLINE.getBytes(StandardCharsets.UTF_8);
	public static enum Method { GET, POST, HEAD, OPTIONS, PUT, DELETE, TRACE }
	
	public static void setFollowRedirects(boolean b) {
		HttpURLConnection.setFollowRedirects(b);
	}
	
	public static void setDefaultAllowUserInteraction(boolean b) {
		HttpURLConnection.setDefaultAllowUserInteraction(true);
	}
	
	public static void setDefaultKeepAlive(boolean b) {
		System.setProperty(
				"http.keepAlive", 
				(b) ? "true" : "false");
	}
	
	public  static void setAllowRestrictedHeaders(boolean b) {
		System.setProperty(
				"sun.net.http.allowRestrictedHeaders", 
				(b) ? "true" : "false");
	}
	
	public static void setDefaultCookieManager(CookieManager manager) {
		CookieHandler.setDefault(manager);
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
	
	public static String parseCookies(String separator, List<HttpCookie> cookies) {
		StringBuilder sb = new StringBuilder();
		for (HttpCookie cookie : cookies) {
			if (sb.length() != 0)
				sb.append(separator);
			sb.append(cookie.toString());
		}
		return sb.toString();
	}
	
	public static StringBuilder readStream(InputStream stream) throws IOException {
		StringBuilder buffer = new StringBuilder();
		if (stream != null) { 
			String line;
			try (BufferedReader in = new BufferedReader(new InputStreamReader(stream))) {
				while ((line = in.readLine()) != null)
					buffer.append(line)
						.append(System.lineSeparator());
			}
		}
		return buffer;
	}
	
	public static class Permissions {
		public static TrustManager[] trustAllCertificates = new TrustManager[] {
	        new X509TrustManager() {
	            @Override
	            public X509Certificate[] getAcceptedIssuers() {
	                return null; // Not relevant.
	            }
	            @Override
	            public void checkClientTrusted(X509Certificate[] certs, String authType) {
	                // Do nothing. Just allow them all.
	            }
	            @Override
	            public void checkServerTrusted(X509Certificate[] certs, String authType) {
	                // Do nothing. Just allow them all.
	            }
	        }
	    };

		public static HostnameVerifier trustAllHostnames = new HostnameVerifier() {
	        @Override
	        public boolean verify(String hostname, SSLSession session) {
	            return true; // Just allow them all.
	        }
	    };
	    private static SSLSocketFactory factory = null;
	    private static HostnameVerifier verifier = null;
	    
	    public static void allowAll() {
	    	factory = HttpsURLConnection.getDefaultSSLSocketFactory();
	    	verifier = HttpsURLConnection.getDefaultHostnameVerifier();
	    	try {
		        System.setProperty("jsse.enableSNIExtension", "false");
		        SSLContext sc = SSLContext.getInstance("SSL");
		        sc.init(null, trustAllCertificates, new SecureRandom());
		        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		        HttpsURLConnection.setDefaultHostnameVerifier(trustAllHostnames);
		    }
		    catch (GeneralSecurityException e) {
		        throw new ExceptionInInitializerError(e);
		    }
	    }
	    
	    public static void reset() {
	    	HttpsURLConnection.setDefaultSSLSocketFactory(factory);
	        HttpsURLConnection.setDefaultHostnameVerifier(verifier);
	    }
	}
	
	public static class RequestBuilder implements Closeable {
		protected Method verb;
		protected Integer timeout;
		protected String charset, endpoint, body;
		protected boolean useCaches, followRedirections;
		protected List<String> unnamedGetParams;
		protected Map<String, String> headers, getParams, postParams;
		protected CookieManager cookies;
		
		protected OutputStream os;
		protected BufferedOutputStream bos;
		protected BufferedWriter bw;
		protected HttpURLConnection connection;
		
		public RequestBuilder(String endpoint, String charset) throws MalformedURLException, IOException {
			setDefaultKeepAlive(false);
			cookies = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
			this.charset = charset;
			this.endpoint = endpoint;
			body = null;
			verb = Method.GET;
			useCaches = followRedirections = true;
			unnamedGetParams = new ArrayList<>();
			headers = new HashMap<>();
			getParams = new HashMap<>();
			postParams = new HashMap<>();
		}
		
		public RequestBuilder(String endpoint) throws MalformedURLException, IOException {
			this(endpoint, "UTF-8");
		}
		
		public RequestBuilder setCookieManager(CookieManager manager) {
			this.cookies = manager;
			return this;
		}
		
		public RequestBuilder setMethod(Method verb) throws IOException {
			this.verb = verb;
			return this;
		}
		
		public RequestBuilder addHeader(String key, String value) {
			headers.put(key, value);
			return this;
		}
		
		public RequestBuilder addGETParam(String param) {
			unnamedGetParams.add(param);
			return this;
		}
		
		public RequestBuilder addGETParam(String key, Object value) {
			getParams.put(key, value.toString());
			return this;
		}
		
		public RequestBuilder addPOSTParam(String key, Object value) {
			postParams.put(key, value.toString());
			return this;
		}
		
		//https://stackoverflow.com/questions/16150089/how-to-handle-cookies-in-httpurlconnection-using-cookiemanager/16171708#16171708
		public RequestBuilder addCookie(String key, Object value) {
			cookies.getCookieStore().add(
					null, 
					HttpCookie.parse(String.format("%s=%s", key, value.toString())).get(0));
			return this;
		}
		
		public RequestBuilder setTimeout(int milli) {
			timeout = milli;
			return this;
		}
		
		public RequestBuilder setFollowRedirections(boolean b) {
			followRedirections = b;
			return this;
		}
		
		public RequestBuilder setUseCaches(boolean b) {
			useCaches = b;
			return this;
		}
		
		// convenience
		
		public RequestBuilder setBasicAuth(String username, String password) {
			String basicAuth = "Basic " + new String(
				Base64.getEncoder()
					.encode(String.format("%s:%s", username, password).getBytes()));
			return addHeader("Authorization", basicAuth);
		}
		
		public RequestBuilder setContentType(String type) {
			return addHeader("Content-Type", type);
		}
		
		public RequestBuilder setContentEncoding(String value) {
			return addHeader("Content-Encoding", value);
		}
		
		public RequestBuilder setContentLanguage(String value) {
			return addHeader("Content-Language", value);
		}
		
		public RequestBuilder setContentLength(String value) {
			return addHeader("Content-Length", value);
		}
		
		public RequestBuilder setContentLocation(String value) {
			return addHeader("Content-Location", value);
		}
		
		public RequestBuilder setContentMD5(String value) {
			return addHeader("Content-MD5", value);
		}
		
		public RequestBuilder setContentRange(String value) {
			return addHeader("Content-Range", value);
		}
		
		public RequestBuilder setConnection(String value) {
			return addHeader("Connection", value);
		}
		
		public RequestBuilder setCacheControl(String value) {
			return addHeader("Cache-Control", value);
		}
		
		public RequestBuilder setPragma(String value) {
			return addHeader("Pragma", value);
		}
		
		public RequestBuilder setAccept(String type) {
			return addHeader("Accept", type);
		}
		
		public RequestBuilder setAcceptCharset(String type) {
			return addHeader("Accept-Charset", type);
		}
		
		public RequestBuilder setAcceptEncoding(String type) {
			return addHeader("Accept-Encoding", type);
		}
		
		public RequestBuilder setAcceptLanguage(String type) {
			return addHeader("Accept-Language", type);
		}
		
		public RequestBuilder setAuthorization(String value) {
			return addHeader("Authorization", value);
		}
		
		public RequestBuilder setExpect(String value) {
			return addHeader("Expect", value);
		}
		
		public RequestBuilder setFrom(String value) {
			return addHeader("From", value);
		}
		
		public RequestBuilder setHost(String value) {
			return addHeader("Host", value);
		}
		
		public RequestBuilder setIfMatch(String value) {
			return addHeader("If-Match", value);
		}
		
		public RequestBuilder setIfModifiedSince(String value) {
			return addHeader("If-Modified-Since", value);
		}
		
		public RequestBuilder setIfNoneMatch(String value) {
			return addHeader("If-None-Match", value);
		}
		
		public RequestBuilder setIfRange(String value) {
			return addHeader("If-Range", value);
		}
		
		public RequestBuilder setIfUnmodifiedSince(String value) {
			return addHeader("If-Unmodified-Since", value);
		}
		
		public RequestBuilder setMaxForwards(String value) {
			return addHeader("Max-Forwards", value);
		}
		
		public RequestBuilder setProxyAuthorization(String value) {
			return addHeader("Proxy-Authorization", value);
		}
		
		public RequestBuilder setRange(String value) {
			return addHeader("Range", value);
		}
		
		public RequestBuilder setReferer(String value) {
			return addHeader("Referer", value);
		}
		
		public RequestBuilder setUserAgent(String value) {
			return addHeader("User-Agent", value);
		}
		
		public RequestBuilder setTrailer(String value) {
			return addHeader("Trailer", value);
		}
		
		public RequestBuilder setTransferEncoding(String value) {
			return addHeader("Transfer-Encoding", value);
		}
		
		public RequestBuilder setUpgrade(String value) {
			return addHeader("Upgrade", value);
		}
		
		public RequestBuilder setVia(String value) {
			return addHeader("Via", value);
		}
		
		public RequestBuilder setTE(String value) {
			return addHeader("TE", value);
		}
		
		public RequestBuilder setWarning(String value) {
			return addHeader("Warning", value);
		}
		
		public RequestBuilder setOrigin(String value) {
			return addHeader("Origin", value);
		}
		
		public RequestBuilder setBody(String body) {
			this.body = body;
			return this;
		}
		
		protected void createOutputStream() throws IOException {
			os = connection.getOutputStream();
			bos = new BufferedOutputStream(os);				// byte transfer
			bw = new BufferedWriter(new PrintWriter(os));	// text/string transfer
		}
		
		public HttpURLConnection build() throws IOException {
			if (getParams.size() > 0 || unnamedGetParams.size() > 0) {
				String unnamed = getUnnamedParams(), 
					params = getParamsString(getParams, "&", true);
				if (params.length() != 0 || unnamed.length() != 0) 
					endpoint += "?";
				if (params.length() != 0)
					endpoint += params;
				if (unnamed.length() != 0) {
					if (params.length() != 0)
						endpoint += "&";
					endpoint += unnamed;
				}
			}
			connection = (HttpURLConnection) new URL(endpoint).openConnection();
			connection.setRequestMethod(verb.toString());
			connection.setDoInput(true);	
			if (verb == Method.POST)
				connection.setDoOutput(true);
			connection.setInstanceFollowRedirects(followRedirections);
			connection.setUseCaches(useCaches);
			if (timeout != null) {
				connection.setConnectTimeout(timeout);
				connection.setReadTimeout(timeout);
			}
			if (headers.size() > 0)
				for (Map.Entry<String, String> entry : headers.entrySet())
					connection.setRequestProperty(entry.getKey(), entry.getValue());
			if (cookies.getCookieStore().getCookies().size() > 0)
				connection.setRequestProperty(
					"Cookie", 
					parseCookies(";",  cookies.getCookieStore().getCookies()));
			if (body != null) {
				int length = body.length();
				connection.setRequestProperty("Content-Type", "text/plain");
				connection.setRequestProperty("Content-Length", Integer.toString(length));
			}
//			if (debug) {
//				System.out.println(verb+" "+endpoint);
//				for (Entry<String, String> entry : headers.entrySet())
//					System.out.println(
//							String.format(
//									"%s: %s", 
//									entry.getKey(), 
//									entry.getValue()));
//				System.out.println();
//				if (postParams.size() > 0)
//					System.out.println(getParamsString(postParams, "&", false));
//				else
//					System.out.println(body);
//			}
			if (postParams.size() > 0 || body != null) {
				createOutputStream();
				bw.write((postParams.size() > 0) ? 
						getParamsString(postParams, "&", false)
						: body);
				bw.write(NEWLINE);
				bw.flush();
			}
			return connection;
		}
		
		private String getUnnamedParams() {
			if (unnamedGetParams.size() == 0) 
				return "";
			StringBuilder sb = new StringBuilder();
			for (String token : unnamedGetParams) {
				if (sb.length() != 0)
					sb.append("&");
				sb.append(token);
			}
			return sb.toString();
		}

		@Override
		public void close() throws IOException {
			if (os != null) {
				os.close();
				bw.close();
				bos.close();
			}
		}
	}
	
	public static class MultipartRequestBuilder extends RequestBuilder {
		private final String boundary;
		private Map<String, String> fields;
		private Map<String, File> files;
		
		public MultipartRequestBuilder(String endpoint) throws MalformedURLException, IOException {
			super(endpoint);
			boundary = "--"+System.nanoTime();
			fields = new HashMap<>();
			files = new HashMap<>();
			setContentType(String.format("multipart/form-data; boundary=%s", boundary));
		}
		
		public MultipartRequestBuilder addField(String key, String value) {
			fields.put(key, value);
			return this;
		}
		
		public MultipartRequestBuilder addFile(String key, File file) {
			files.put(key, file);
			return this;
		}
		
		private void writeField(String key, Object value) throws UnsupportedEncodingException, IOException {
			bw.append("--"+boundary)
				.append(NEWLINE)
				.append("Content-Disposition: form-data; name=\""+key+"\"")
				.append(NEWLINE)
				.append("Content-Type: text/plain; charset="+charset)
				.append(NEWLINE)
				.append(NEWLINE)
				.append(value.toString())
				.append(NEWLINE);
			bw.flush();
		}
		
		private void writeFile(String key, File file) throws UnsupportedEncodingException, IOException {
			bw.append("--"+boundary)
				.append(NEWLINE)
				.append("Content-Disposition: form-data; name=\""+key+"\"; filename=\"" +file.getName()+"\"")
				.append(NEWLINE)
				.append("Content-Type: "+URLConnection.guessContentTypeFromName(file.getName()))
				.append(NEWLINE)
				.append("Content-Transfer-Encoding: binary")
				.append(NEWLINE)
				.append(NEWLINE);
			bw.flush();
			// write file bytes
			byte[] buffer = new byte[4096];
			try (BufferedInputStream is = new BufferedInputStream(new FileInputStream(file))) {
				int read = -1;
				while ((read = is.read(buffer)) != -1) 
					bos.write(buffer, 0, read);
			} finally {
				bos.write(NEWLINE_BYTES);
				bos.flush();
			}
		}
		
		@Override
		public HttpURLConnection build() throws IOException {
			HttpURLConnection connection = super.build();
			if (os == null)
				createOutputStream();
			for (Map.Entry<String, String> field : fields.entrySet())
				writeField(field.getKey(), field.getValue());
			for (Map.Entry<String, File> entry : files.entrySet())
				writeFile(entry.getKey(), entry.getValue());
			bw.write(NEWLINE);
			bw.write("--"+boundary+"--");
			bw.write(NEWLINE);
			bw.flush();
			return connection;
		}
		
		@Override
		public void close() throws IOException {
			os.close(); 
			bw.close(); 
			bos.close();
			super.close();
		}
	}
	
	public static class ResponseHandler implements Closeable {
		private HttpURLConnection connection;
		public Integer status;
		public Throwable throwable;
		
		public ResponseHandler(HttpURLConnection connection) {
			this.connection = connection;
			try {
				status = connection.getResponseCode();
				throwable = null;
			} catch (Exception e) {
				status = null;
				throwable = e;
			}
		}
		
		public boolean isOk() {
			return status == HttpURLConnection.HTTP_OK;
		}
		
		public File saveResponse(String path) throws IOException {
			if (!isOk())
				throw new IOException(String.format("status: %s", status));
			File file = Paths.get(path).toFile();
			try (
				BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
				BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
			) {
				int read;
				while ((read = in.read()) != -1)
					out.write(read);
			}
			return file;
		}
		
		public void forEachResponseLine(Consumer<String> consumer) throws IOException {
			try (BufferedReader in = new BufferedReader(
					new InputStreamReader(connection.getInputStream()))) {
				String line;
				while ((line = in.readLine()) != null)
					consumer.accept(line);
			}
		}
		
		public StringBuilder getResponseBuffer() throws IOException {
			return (throwable != null) ? 
					getErrorResponseBuffer() : 
					readStream(connection.getInputStream());
		}
		
		public String getResponse() throws IOException {
			return (throwable != null) ? 
					getErrorResponse() : 
					readStream(connection.getInputStream()).toString();
		}
		
		public StringBuilder getErrorResponseBuffer() throws IOException {
			return readStream(connection.getErrorStream());
		}
		
		public String getErrorResponse() throws IOException {
			return getErrorResponseBuffer().toString();
		}

		public List<HttpCookie> getCookies() {
			return HttpCookie.parse(connection.getHeaderField("Set-Cookie"));
		}
		
		@Override
		public void close() throws IOException {
			connection.disconnect();
		}
	}
	
	
	public static void main(String[] args) throws MalformedURLException, IOException {
//		String endpoint = "http://api.qrserver.com/v1/create-qr-code/";
//		RequestBuilder builder = new RequestBuilder(endpoint);
//		builder.setMethod(Method.GET)
//			.addGETParam("data", "Hello world!");
////			.addGETParam("color", "FF0000")
////			.addGETParam("bgcolor", "000000");
//		ResponseHandler handler = new ResponseHandler(builder.build());
//		System.out.println(handler.saveResponse("qrcode.png"));
//		handler.close();
//		builder.close();

//		RequestBuilder builder = new RequestBuilder("http://thesaurus.altervista.org/thesaurus/v1");
//		builder.addGETParam("key", "YOUR_OWN_API_KEY")
//			.addGETParam("word", "pussy")
//			.addGETParam("language", Language.FRENCH.getCode())
//			.addGETParam("output", "json");
//		ResponseHandler handler = new ResponseHandler(builder.build());
//		System.out.println(handler.getResponse());
//		handler.close();
//		builder.close();
		
//		RequestBuilder builder = new RequestBuilder("http://192.168.11.1/cgi");
//		builder.setMethod(Method.POST)
//			.addGETParam("7")
//			.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36")
//			.addHeader("DNT", "1")
//			.setAccept("*/*")
//			.setOrigin("http://192.168.11.1")
//			.setReferer("http://192.168.11.1/mainFrame.htm")
//			.setAcceptEncoding("gzip, deflate")
//			.setAcceptLanguage("en-US,en-GB;q=0.9,en;q=0.8")
//			.addCookie("Authorization", "Basic YWRtaW46YWRtaW4=")
//			.setConnection("close")
//			.setBody("[ACT_REBOOT#0,0,0,0,0,0#0,0,0,0,0,0]0,0");
//		ResponseHandler handler = new ResponseHandler(builder.build());
//		System.out.println(handler.getResponse());
//		builder.close();
//		handler.close();
		
//		RequestBuilder builder = new RequestBuilder("http://worldtimeapi.org/api/timezone.txt");
//		ResponseHandler handler = new ResponseHandler(builder.build());
//		handler.forEachResponseLine(System.out::println);
//		builder.close();
//		handler.close();
		
//		RequestBuilder builder = new RequestBuilder("https://translate.yandex.net/api/v1.5/tr.json/getLangs");
//		builder.setMethod(Method.POST)
//			.addPOSTParam("key", "YOUR_OWN_KEY")
//			.addPOSTParam("ui", "en");
//		ResponseHandler handler = new ResponseHandler(builder.build());
//		System.out.println(handler.getResponse());
//		handler.close();
//		builder.close();
		
//		String endpoint = "http://api.qrserver.com/v1/read-qr-code/";
//		MultipartRequestBuilder builder = new MultipartRequestBuilder(endpoint);
//		builder.addFile("file", new File("qrcode.png"));
//		builder.setMethod(Method.POST);
//		HttpURLConnection con = builder.build();
//		ResponseHandler handler = new ResponseHandler(con);
//		System.out.println(handler.getResponse());
//		handler.close();
//		builder.close();

//		String endpoint = "https://www.floristone.com/api/rest/flowershop/getproducts";
//		RequestBuilder builder = new RequestBuilder(endpoint);
//		builder.setMethod(Method.GET)
//			.addGETParam("category", "lr")
//			.addGETParam("count", "999")
//			.addGETParam("start", "1")
//			.addGETParam("sorttype", "az")
//			.setBasicAuth("YOUR_USERNAME", "YOUR_PASSWORD");
//		ResponseHandler handler = new ResponseHandler(builder.build());
//		System.out.println(handler.getResponse());
//		builder.close();
//		handler.close();
	}
}