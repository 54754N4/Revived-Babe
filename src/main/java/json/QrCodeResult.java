package json;

public class QrCodeResult {
	public String type;
	public Symbol[] symbol;
	
	public QrCodeResult(String type, Symbol[] symbol) {
		this.type = type;
		this.symbol = symbol;
	}
	
	public static class Symbol {
		public int seq;
		public String data, error;
		
		public Symbol(int seq, String data, String error) {
			this.seq = seq;
			this.data = data;
			this.error = error;
		}
	}
}
