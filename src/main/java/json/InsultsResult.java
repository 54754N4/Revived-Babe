package json;

public class InsultsResult {
	public final String number, language, insult, created, shown, createdby, active, comment;
	
	public InsultsResult(String number, String language, String insult, 
			String created, String shown, String createdby, 
			String active, String comment) {
		this.number = number;
		this.language = language;
		this.insult = insult;
		this.created = created;
		this.shown = shown;
		this.createdby = createdby;
		this.active = active;
		this.comment = comment;
	}
}

/*

{
  "number": "123",
  "language": "en",
  "insult": "You're a failed abortion whose birth certificate is an apology from the condom factory.",
  "created": "2018-10-24 06:52:02",
  "shown": "2688",
  "createdby": "someone",
  "active": "1",
  "comment": "Sourced from some website"
}

*/