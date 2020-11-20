package json.interpreter;


public interface Visitable {
	public void accept(Visitor visitor) throws Exception;
}
