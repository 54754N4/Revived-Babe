package json;

import java.io.Serializable;

public final class TraceMoeResult implements Serializable {
	private static final long serialVersionUID = 1L;
	public Result[] result;
	public int frameCount;
	public String error;

	public TraceMoeResult(Result[] result, int frameCount, String error) {
		this.result = result;
		this.frameCount = frameCount;
		this.error = error;
	}

	public Result[] getResult() {
		return result;
	}

	public int getFrameCount() {
		return frameCount;
	}

	public String getError() {
		return error;
	}

	public void setResult(Result[] result) {
		this.result = result;
	}

	public void setFrameCount(int frameCount) {
		this.frameCount = frameCount;
	}

	public void setError(String error) {
		this.error = error;
	}

	@Override
	public String toString() {
		return new StringBuilder()
			.append(getClass().getName()).append("{\n")
			.append("result: ").append(result+",\n")
			.append("frameCount: ").append(frameCount+",\n")
			.append("error: ").append(error+",\n")
			.append("\n}").toString();
	}

	public static class Builder {
		public Result[] result;
		public int frameCount;
		public String error;

		public Builder setResult(Result[] result) {
			this.result = result;
			return this;
		}

		public Builder setFrameCount(int frameCount) {
			this.frameCount = frameCount;
			return this;
		}

		public Builder setError(String error) {
			this.error = error;
			return this;
		}

		public TraceMoeResult build() {
			return new TraceMoeResult(result,frameCount,error);
		}
	}

	public static final class Result implements Serializable {
		private static final long serialVersionUID = 1L;
		public int anilist;
		public String image;
		public String filename;
		public double similarity;
		public int episode;
		public double from;
		public double to;
		public String video;

		public Result(int anilist, String image, String filename, double similarity, int episode, double from, double to, String video) {
			this.anilist = anilist;
			this.image = image;
			this.filename = filename;
			this.similarity = similarity;
			this.episode = episode;
			this.from = from;
			this.to = to;
			this.video = video;
		}

		public int getAnilist() {
			return anilist;
		}

		public String getImage() {
			return image;
		}

		public String getFilename() {
			return filename;
		}

		public double getSimilarity() {
			return similarity;
		}

		public Object getEpisode() {
			return episode;
		}

		public double getFrom() {
			return from;
		}

		public double getTo() {
			return to;
		}

		public String getVideo() {
			return video;
		}

		public void setAnilist(int anilist) {
			this.anilist = anilist;
		}

		public void setImage(String image) {
			this.image = image;
		}

		public void setFilename(String filename) {
			this.filename = filename;
		}

		public void setSimilarity(double similarity) {
			this.similarity = similarity;
		}

		public void setEpisode(int episode) {
			this.episode = episode;
		}

		public void setFrom(double from) {
			this.from = from;
		}

		public void setTo(double to) {
			this.to = to;
		}

		public void setVideo(String video) {
			this.video = video;
		}

		@Override
		public String toString() {
			return new StringBuilder()
				.append(getClass().getName()).append("{\n")
				.append("anilist: ").append(anilist+",\n")
				.append("image: ").append(image+",\n")
				.append("filename: ").append(filename+",\n")
				.append("similarity: ").append(similarity+",\n")
				.append("episode: ").append(episode+",\n")
				.append("from: ").append(from+",\n")
				.append("to: ").append(to+",\n")
				.append("video: ").append(video+",\n")
				.append("\n}").toString();
		}

		public static class Builder {
			public int anilist;
			public String image;
			public String filename;
			public double similarity;
			public int episode;
			public double from;
			public double to;
			public String video;

			public Builder setAnilist(int anilist) {
				this.anilist = anilist;
				return this;
			}

			public Builder setImage(String image) {
				this.image = image;
				return this;
			}

			public Builder setFilename(String filename) {
				this.filename = filename;
				return this;
			}

			public Builder setSimilarity(double similarity) {
				this.similarity = similarity;
				return this;
			}

			public Builder setEpisode(int episode) {
				this.episode = episode;
				return this;
			}

			public Builder setFrom(double from) {
				this.from = from;
				return this;
			}

			public Builder setTo(double to) {
				this.to = to;
				return this;
			}

			public Builder setVideo(String video) {
				this.video = video;
				return this;
			}

			public Result build() {
				return new Result(anilist,image,filename,similarity,episode,from,to,video);
			}
		}
	}

}

