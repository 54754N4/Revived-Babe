package sqlite;

public class Match extends OnTransaction {
	private String name;
	
	public Match(String name) {
		this.name = name;
	}
	
	@Override
	public String asString() {
		return "MATCH " + name;
	}
}