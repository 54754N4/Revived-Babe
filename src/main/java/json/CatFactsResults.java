package json;

public class CatFactsResults {
	public Result[] all;
	
	public CatFactsResults(Result[] all) {
		this.all = all;
	}
	
	public static final class Result {
		public String _id, text, type;
		public User user;
		public Integer upvotes, userUpvoted;
		
		public Result(String _id, String text, String type, User user, Integer upvotes, Integer userUpvoted) {
			this._id = _id;
			this.text = text;
			this.type = type;
			this.user = user;
			this.upvotes = upvotes;
			this.userUpvoted = userUpvoted;
		}
	}

	public static final class User {
		public String _id;
		public Name name;
		
		public User(String _id, Name name) {
			this._id = _id;
			this.name = name;
		}
	}
	
	public static class Name {
		String first, last;
		
		public Name(String first, String last) {
			this.first = first;
			this.last = last;
		}
	}
}
