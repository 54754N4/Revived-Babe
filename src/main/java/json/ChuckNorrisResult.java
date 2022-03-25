package json;

public class ChuckNorrisResult {
	public final String icon_url, id, url, value;
	
	public ChuckNorrisResult(String icon_url, String id, String url, String value) {
		this.icon_url = icon_url;
		this.id = id;
		this.url = url;
		this.value = value;
	}
}

/*

{
"icon_url" : "https://assets.chucknorris.host/img/avatar/chuck-norris.png",
"id" : "wR38lmcPQEWz2t9XJL4aDg",
"url" : "",
"value" : "According to Forrest Gump: "Life is not like a box of chocolates. Life is actually not pissing off Chuck Norris. And that's all I have to say about that!""
} 

*/