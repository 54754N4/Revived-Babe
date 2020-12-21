package lib;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ThreadOutput {
	private String output, error;

	public ThreadOutput(Process process) throws InterruptedException, ExecutionException {
		StreamGobbler out = new StreamGobbler(process.getInputStream()),
				err = new StreamGobbler(process.getErrorStream());
		Future<String> fout = ThreadsManager.POOL.submit(out),
				ferr = ThreadsManager.POOL.submit(err);
		// wait for completion
		setOutput(fout.get());
		setError(ferr.get());	
	}
	
	public String getOutput() {
		return output;
	}

	public String getError() {
		return error;
	}

	private void setOutput(String output) {
		this.output = output;
	}
	
	private void setError(String error) {
		this.error = error;
	}
}
