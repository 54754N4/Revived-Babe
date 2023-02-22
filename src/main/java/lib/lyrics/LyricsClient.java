package lib.lyrics;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Safelist;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;

import lib.Pipeline;

/**
 * JLyrics did the heavy lifting, but they fucked up the lyrics
 * parsing.. Decided to just fix it myself. It was so easy that
 * everything is basically just copy pasted except for 1 or 2 lines.. 
 */
public class LyricsClient {
    private final Config config = ConfigFactory.load();
    private final HashMap<String, Lyrics> cache = new HashMap<>();
    private final Safelist newlineSafelist = Safelist.none().addTags("br");
    private final Executor executor;
    private final String defaultSource, userAgent;
    private final int timeout;
    
    public LyricsClient() {
        this(null, null);
    }
    
    public LyricsClient(String defaultSource) {
        this(defaultSource, null);
    }
    
    public LyricsClient(Executor executor) {
        this(null, executor);
    }
    
    public LyricsClient(String defaultSource, Executor executor) {
        this.defaultSource = defaultSource == null ? config.getString("lyrics.default") : defaultSource;
        this.userAgent = config.getString("lyrics.user-agent");
        this.timeout = config.getInt("lyrics.timeout");
        this.executor = executor == null ? Executors.newCachedThreadPool() : executor;
    }
    
    public CompletableFuture<Lyrics> getLyrics(String search) {
        return getLyrics(search, defaultSource);
    }
    
    public CompletableFuture<Lyrics> getLyrics(String search, String source) {
        String cacheKey = source + "||" + search;
        if (cache.containsKey(cacheKey))
            return CompletableFuture.completedFuture(cache.get(cacheKey));
        try {
            CompletableFuture<String> futureToken;
            boolean jsonSearch = config.getBoolean("lyrics." + source + ".search.json");
            String select = config.getString("lyrics." + source + ".search.select");
            String titleSelector = config.getString("lyrics." + source + ".parse.title");
            String authorSelector = config.getString("lyrics." + source + ".parse.author");
            String contentSelector = config.getString("lyrics." + source + ".parse.content");
            if (config.hasPath("lyrics." + source + ".token"))
                futureToken = getToken(source);
            else
                futureToken = CompletableFuture.completedFuture("");
            return futureToken.thenCompose(token -> {
                String searchUrl = String.format(config.getString("lyrics." + source + ".search.url"), search, token);
                return CompletableFuture.supplyAsync(() -> {
                    try {
                        Document doc;
                        Connection connection = Jsoup.connect(searchUrl).userAgent(userAgent).timeout(timeout);
                        if (jsonSearch) {
                            String body = connection.ignoreContentType(true).execute().body();
                            JSONObject json = new JSONObject(body);
                            doc = Jsoup.parse(XML.toString(json));
                        } else
                            doc = connection.get();
                        Element urlElement = doc.selectFirst(select);
                        String url;
                        if (jsonSearch)
                            url = urlElement.text();
                        else
                            url = urlElement.attr("abs:href");
                        if (url == null || url.isEmpty())
                            return null;
                        doc = Jsoup.connect(url).userAgent(userAgent).timeout(timeout).get();
                        Lyrics lyrics = new Lyrics(doc.selectFirst(titleSelector).ownText(),
                                doc.selectFirst(authorSelector).ownText(),
                                cleanWithNewlines(doc.selectFirst(contentSelector)),
                                url,
                                source);
                        cache.put(cacheKey, lyrics);
                        return lyrics;
                    }
                    catch (IOException | NullPointerException | JSONException ex) {
                        return null;
                    }
                }, executor);
            });
        }
        catch (ConfigException ex) {
            throw new IllegalArgumentException(String.format("Source '%s' does not exist or is not configured correctly", source));
        }
        catch (Exception ignored) {
            return null;
        }
    }

    private CompletableFuture<String> getToken(String source) {
        try {
            String tokenUrl = config.getString("lyrics." + source + ".token.url");
            String select = config.getString("lyrics." + source + ".token.select");
            boolean textSearch = config.getBoolean("lyrics." + source + ".token.text");

            return CompletableFuture.supplyAsync(() -> {
                try {
                    Pattern pattern = null;

                    // Optional regex for post-processing
                    // Helpful if token is not accessible using HTML accessors (e.g, inlined in a JS file)
                    if (config.hasPath("lyrics." + source + ".token.regex")) {
                        String regexPattern = config.getString("lyrics." + source + ".token.regex");
                        pattern = Pattern.compile(regexPattern);
                    }

                    Connection connection = Jsoup.connect(tokenUrl).userAgent(userAgent).timeout(timeout);
                    String body;

                    if (textSearch) {
                        body = connection.ignoreContentType(true).execute().body();
                    } else {
                        // HTML -- apply selectors to derive body string
                        Document doc = connection.get();
                        body = doc.selectFirst(select).ownText();
                    }

                    if (pattern != null) {
                        Matcher matcher = pattern.matcher(body);
                        if (matcher.find()) {
                            return matcher.group();
                        }
                    }
                    return null;
                } catch (IOException | NullPointerException ex) {
                    return null;
                }
            }, executor);
        } catch (ConfigException ex) {
            throw new IllegalArgumentException(String.format("Source '%s' does not exist or is not configured correctly", source));
        } catch (Exception ignored) {
            return null;
        }
    }

    private String cleanWithNewlines(Element element) {
    	return Pipeline.<Element, String>of(e -> Jsoup.clean(e.html(), newlineSafelist))
        		.then(s -> s.replaceAll("<br>", "\n"))
        		.apply(element);
    }
}