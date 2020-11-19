package json.interpreter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AST {

	public static final class Member extends AST {
		public String key;
		public Object value;	
		
		public Member(String key, Object value) {
			this.key = key;
			this.value = value;
		}
	}
	
	public static final class JsonObject extends AST {
		public final Map<String, Object> dict = new HashMap<>();
		
		public void add(Member member) {
			dict.put(member.key, member.value);
		}
		
		@Override
		public String toString() {
			return dict.toString();
		}
	} 
	
	public static final class JsonArray extends AST {
		public String name;
		public final List<Object> list = new ArrayList<>();
		
		@Override
		public String toString() {
			return Arrays.deepToString(list.toArray());
		}
	}
}
