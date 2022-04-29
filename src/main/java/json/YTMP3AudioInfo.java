package json;

import java.io.Serializable;

public final class YTMP3AudioInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	public Formats formats;
	public boolean error;
	public String status;

	public YTMP3AudioInfo(Formats formats, boolean error, String status) {
		this.formats = formats;
		this.error = error;
		this.status = status;
	}

	public Formats getFormats() {
		return formats;
	}

	public boolean getError() {
		return error;
	}

	public String getStatus() {
		return status;
	}

	public void setFormats(Formats formats) {
		this.formats = formats;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return new StringBuilder()
			.append(getClass().getName()).append("{\n")
			.append("formats: ").append(formats+",\n")
			.append("error: ").append(error+",\n")
			.append("status: ").append(status+",\n")
			.append("\n}").toString();
	}

	public static class Builder {
		public Formats formats;
		public boolean error;
		public String status;

		public Builder setFormats(Formats formats) {
			this.formats = formats;
			return this;
		}

		public Builder setError(boolean error) {
			this.error = error;
			return this;
		}

		public Builder setStatus(String status) {
			this.status = status;
			return this;
		}

		public YTMP3AudioInfo build() {
			return new YTMP3AudioInfo(formats,error,status);
		}
	}

	public static final class Formats implements Serializable {
		private static final long serialVersionUID = 1L;
		public int duration;
		public String thumbnail;
		public String basename;
		public String id;
		public Video[] video;
		public Audio[] audio;
		public String title;

		public Formats(int duration, String thumbnail, String basename, String id, Video[] video, Audio[] audio, String title) {
			this.duration = duration;
			this.thumbnail = thumbnail;
			this.basename = basename;
			this.id = id;
			this.video = video;
			this.audio = audio;
			this.title = title;
		}

		public int getDuration() {
			return duration;
		}

		public String getThumbnail() {
			return thumbnail;
		}

		public String getBasename() {
			return basename;
		}

		public String getId() {
			return id;
		}

		public Video[] getVideo() {
			return video;
		}

		public Audio[] getAudio() {
			return audio;
		}

		public String getTitle() {
			return title;
		}

		public void setDuration(int duration) {
			this.duration = duration;
		}

		public void setThumbnail(String thumbnail) {
			this.thumbnail = thumbnail;
		}

		public void setBasename(String basename) {
			this.basename = basename;
		}

		public void setId(String id) {
			this.id = id;
		}

		public void setVideo(Video[] video) {
			this.video = video;
		}

		public void setAudio(Audio[] audio) {
			this.audio = audio;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		@Override
		public String toString() {
			return new StringBuilder()
				.append(getClass().getName()).append("{\n")
				.append("duration: ").append(duration+",\n")
				.append("thumbnail: ").append(thumbnail+",\n")
				.append("basename: ").append(basename+",\n")
				.append("id: ").append(id+",\n")
				.append("video: ").append(video+",\n")
				.append("audio: ").append(audio+",\n")
				.append("title: ").append(title+",\n")
				.append("\n}").toString();
		}

		public static class Builder {
			public int duration;
			public String thumbnail;
			public String basename;
			public String id;
			public Video[] video;
			public Audio[] audio;
			public String title;

			public Builder setDuration(int duration) {
				this.duration = duration;
				return this;
			}

			public Builder setThumbnail(String thumbnail) {
				this.thumbnail = thumbnail;
				return this;
			}

			public Builder setBasename(String basename) {
				this.basename = basename;
				return this;
			}

			public Builder setId(String id) {
				this.id = id;
				return this;
			}

			public Builder setVideo(Video[] video) {
				this.video = video;
				return this;
			}

			public Builder setAudio(Audio[] audio) {
				this.audio = audio;
				return this;
			}

			public Builder setTitle(String title) {
				this.title = title;
				return this;
			}

			public Formats build() {
				return new Formats(duration,thumbnail,basename,id,video,audio,title);
			}
		}

		public static final class Audio implements Serializable {
			private static final long serialVersionUID = 1L;
			public String formatId;
			public int fileSize;
			public Description description;
			public boolean needConvert;
			public String fileType;
			public String url;
			public String quality;

			public Audio(String formatId, int fileSize, Description description, boolean needConvert, String fileType, String url, String quality) {
				this.formatId = formatId;
				this.fileSize = fileSize;
				this.description = description;
				this.needConvert = needConvert;
				this.fileType = fileType;
				this.url = url;
				this.quality = quality;
			}

			public String getFormatId() {
				return formatId;
			}

			public int getFileSize() {
				return fileSize;
			}

			public Description getDescription() {
				return description;
			}

			public boolean getNeedConvert() {
				return needConvert;
			}

			public String getFileType() {
				return fileType;
			}

			public String getUrl() {
				return url;
			}

			public String getQuality() {
				return quality;
			}

			public void setFormatId(String formatId) {
				this.formatId = formatId;
			}

			public void setFileSize(int fileSize) {
				this.fileSize = fileSize;
			}

			public void setDescription(Description description) {
				this.description = description;
			}

			public void setNeedConvert(boolean needConvert) {
				this.needConvert = needConvert;
			}

			public void setFileType(String fileType) {
				this.fileType = fileType;
			}

			public void setUrl(String url) {
				this.url = url;
			}

			public void setQuality(String quality) {
				this.quality = quality;
			}

			@Override
			public String toString() {
				return new StringBuilder()
					.append(getClass().getName()).append("{\n")
					.append("formatId: ").append(formatId+",\n")
					.append("fileSize: ").append(fileSize+",\n")
					.append("description: ").append(description+",\n")
					.append("needConvert: ").append(needConvert+",\n")
					.append("fileType: ").append(fileType+",\n")
					.append("url: ").append(url+",\n")
					.append("quality: ").append(quality+",\n")
					.append("\n}").toString();
			}

			public static class Builder {
				public String formatId;
				public int fileSize;
				public Description description;
				public boolean needConvert;
				public String fileType;
				public String url;
				public String quality;

				public Builder setFormatId(String formatId) {
					this.formatId = formatId;
					return this;
				}

				public Builder setFileSize(int fileSize) {
					this.fileSize = fileSize;
					return this;
				}

				public Builder setDescription(Description description) {
					this.description = description;
					return this;
				}

				public Builder setNeedConvert(boolean needConvert) {
					this.needConvert = needConvert;
					return this;
				}

				public Builder setFileType(String fileType) {
					this.fileType = fileType;
					return this;
				}

				public Builder setUrl(String url) {
					this.url = url;
					return this;
				}

				public Builder setQuality(String quality) {
					this.quality = quality;
					return this;
				}

				public Audio build() {
					return new Audio(formatId,fileSize,description,needConvert,fileType,url,quality);
				}
			}

			public static final class Description implements Serializable {
				private static final long serialVersionUID = 1L;
				public String fragment;
				public boolean block;

				public Description(String fragment, boolean block) {
					this.fragment = fragment;
					this.block = block;
				}

				public String getFragment() {
					return fragment;
				}

				public boolean getBlock() {
					return block;
				}

				public void setFragment(String fragment) {
					this.fragment = fragment;
				}

				public void setBlock(boolean block) {
					this.block = block;
				}

				@Override
				public String toString() {
					return new StringBuilder()
						.append(getClass().getName()).append("{\n")
						.append("fragment: ").append(fragment+",\n")
						.append("block: ").append(block+",\n")
						.append("\n}").toString();
				}

				public static class Builder {
					public String fragment;
					public boolean block;

					public Builder setFragment(String fragment) {
						this.fragment = fragment;
						return this;
					}

					public Builder setBlock(boolean block) {
						this.block = block;
						return this;
					}

					public Description build() {
						return new Description(fragment,block);
					}
				}
			}

		}

		public static final class Video implements Serializable {
			private static final long serialVersionUID = 1L;
			public String formatId;
			public int fileSize;
			public Description description;
			public boolean needConvert;
			public String fileType;
			public String url;
			public String quality;

			public Video(String formatId, int fileSize, Description description, boolean needConvert, String fileType, String url, String quality) {
				this.formatId = formatId;
				this.fileSize = fileSize;
				this.description = description;
				this.needConvert = needConvert;
				this.fileType = fileType;
				this.url = url;
				this.quality = quality;
			}

			public String getFormatId() {
				return formatId;
			}

			public int getFileSize() {
				return fileSize;
			}

			public Description getDescription() {
				return description;
			}

			public boolean getNeedConvert() {
				return needConvert;
			}

			public String getFileType() {
				return fileType;
			}

			public String getUrl() {
				return url;
			}

			public String getQuality() {
				return quality;
			}

			public void setFormatId(String formatId) {
				this.formatId = formatId;
			}

			public void setFileSize(int fileSize) {
				this.fileSize = fileSize;
			}

			public void setDescription(Description description) {
				this.description = description;
			}

			public void setNeedConvert(boolean needConvert) {
				this.needConvert = needConvert;
			}

			public void setFileType(String fileType) {
				this.fileType = fileType;
			}

			public void setUrl(String url) {
				this.url = url;
			}

			public void setQuality(String quality) {
				this.quality = quality;
			}

			@Override
			public String toString() {
				return new StringBuilder()
					.append(getClass().getName()).append("{\n")
					.append("formatId: ").append(formatId+",\n")
					.append("fileSize: ").append(fileSize+",\n")
					.append("description: ").append(description+",\n")
					.append("needConvert: ").append(needConvert+",\n")
					.append("fileType: ").append(fileType+",\n")
					.append("url: ").append(url+",\n")
					.append("quality: ").append(quality+",\n")
					.append("\n}").toString();
			}

			public static class Builder {
				public String formatId;
				public int fileSize;
				public Description description;
				public boolean needConvert;
				public String fileType;
				public String url;
				public String quality;

				public Builder setFormatId(String formatId) {
					this.formatId = formatId;
					return this;
				}

				public Builder setFileSize(int fileSize) {
					this.fileSize = fileSize;
					return this;
				}

				public Builder setDescription(Description description) {
					this.description = description;
					return this;
				}

				public Builder setNeedConvert(boolean needConvert) {
					this.needConvert = needConvert;
					return this;
				}

				public Builder setFileType(String fileType) {
					this.fileType = fileType;
					return this;
				}

				public Builder setUrl(String url) {
					this.url = url;
					return this;
				}

				public Builder setQuality(String quality) {
					this.quality = quality;
					return this;
				}

				public Video build() {
					return new Video(formatId,fileSize,description,needConvert,fileType,url,quality);
				}
			}

			public static final class Description implements Serializable {
				private static final long serialVersionUID = 1L;
				public String fragment;
				public boolean block;

				public Description(String fragment, boolean block) {
					this.fragment = fragment;
					this.block = block;
				}

				public String getFragment() {
					return fragment;
				}

				public boolean getBlock() {
					return block;
				}

				public void setFragment(String fragment) {
					this.fragment = fragment;
				}

				public void setBlock(boolean block) {
					this.block = block;
				}

				@Override
				public String toString() {
					return new StringBuilder()
						.append(getClass().getName()).append("{\n")
						.append("fragment: ").append(fragment+",\n")
						.append("block: ").append(block+",\n")
						.append("\n}").toString();
				}

				public static class Builder {
					public String fragment;
					public boolean block;

					public Builder setFragment(String fragment) {
						this.fragment = fragment;
						return this;
					}

					public Builder setBlock(boolean block) {
						this.block = block;
						return this;
					}

					public Description build() {
						return new Description(fragment,block);
					}
				}
			}

		}

	}

}