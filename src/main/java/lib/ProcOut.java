package lib;

public class ProcOut implements Appender {
	public final StringBuilder out, err;
	
	public ProcOut(Process p) throws InterruptedException {
		out = new StringBuilder();	
		err = new StringBuilder();
		StreamGobbler outGobble = new StreamGobbler(p.getInputStream(), this, 0),
				errGobble = new StreamGobbler(p.getErrorStream(), this, 1);
		outGobble.start(); 	
		errGobble.start();
		outGobble.join(); 	
		errGobble.join();
	}
	
	public String merge() {
		return out.toString() + err.toString();
	}

	@Override
	public void append(int target, String line) {
		if (target == 0)
			out.append(line);
		else if (target == 1)
			err.append(line);
		else 
			throw new IllegalArgumentException("Target value should be 0 or 1 for stdout or stderr.");
	}
}
