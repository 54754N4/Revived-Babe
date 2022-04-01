package json;

public class ThesaurusResult {
	public Response[] response;
	
	public ThesaurusResult(Response[] response) {
		this.response = response;
	}
	
	public static class Response {
		public ListResult list;
		
		public Response(ListResult list) {
			this.list = list;
		}
	}
	
	public static class ListResult {
		public String category, synonyms;
		
		public ListResult(String category, String synonyms) {
			this.category = category;
			this.synonyms = synonyms;
		}
	}
}
