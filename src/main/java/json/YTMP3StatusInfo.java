package json;

import java.io.Serializable;

public final class YTMP3StatusInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	public String filename;
	public boolean error;
	public String url;
	public String status;

	public YTMP3StatusInfo(String filename, boolean error, String url, String status) {
		this.filename = filename;
		this.error = error;
		this.url = url;
		this.status = status;
	}

	public String getFilename() {
		return filename;
	}

	public boolean getError() {
		return error;
	}

	public String getUrl() {
		return url;
	}

	public String getStatus() {
		return status;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return new StringBuilder()
			.append(getClass().getName()).append("{\n")
			.append("filename: ").append(filename+",\n")
			.append("error: ").append(error+",\n")
			.append("url: ").append(url+",\n")
			.append("status: ").append(status+",\n")
			.append("\n}").toString();
	}

	public static class Builder {
		public String filename;
		public boolean error;
		public String url;
		public String status;

		public Builder setFilename(String filename) {
			this.filename = filename;
			return this;
		}

		public Builder setError(boolean error) {
			this.error = error;
			return this;
		}

		public Builder setUrl(String url) {
			this.url = url;
			return this;
		}

		public Builder setStatus(String status) {
			this.status = status;
			return this;
		}

		public YTMP3StatusInfo build() {
			return new YTMP3StatusInfo(filename,error,url,status);
		}
	}
}