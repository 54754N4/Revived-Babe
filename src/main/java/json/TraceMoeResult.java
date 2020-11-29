package json;

import java.io.Serializable;


public final class TraceMoeResult implements Serializable {
	private static final long serialVersionUID = 1L;
	public long RawDocsCount;
	public boolean CacheHit;
	public String[] strings;
	public Doc[] docs;
	public long limit_ttl;
	public long RawDocsSearchTime;
	public long quota;
	public long limit;
	public long ReRankSearchTime;
	public long quota_ttl;
	public long trial;

	public TraceMoeResult(long RawDocsCount, boolean CacheHit, String[] strings, Doc[] docs, long limit_ttl, long RawDocsSearchTime, long quota, long limit, long ReRankSearchTime, long quota_ttl, long trial) {
		this.RawDocsCount = RawDocsCount;
		this.CacheHit = CacheHit;
		this.strings = strings;
		this.docs = docs;
		this.limit_ttl = limit_ttl;
		this.RawDocsSearchTime = RawDocsSearchTime;
		this.quota = quota;
		this.limit = limit;
		this.ReRankSearchTime = ReRankSearchTime;
		this.quota_ttl = quota_ttl;
		this.trial = trial;
	}

	public long getRawDocsCount() {
		return RawDocsCount;
	}

	public boolean getCacheHit() {
		return CacheHit;
	}

	public String[] getStrings() {
		return strings;
	}

	public Doc[] getDocs() {
		return docs;
	}

	public long getLimit_ttl() {
		return limit_ttl;
	}

	public long getRawDocsSearchTime() {
		return RawDocsSearchTime;
	}

	public long getQuota() {
		return quota;
	}

	public long getLimit() {
		return limit;
	}

	public long getReRankSearchTime() {
		return ReRankSearchTime;
	}

	public long getQuota_ttl() {
		return quota_ttl;
	}

	public long getTrial() {
		return trial;
	}

	public void setRawDocsCount(long RawDocsCount) {
		this.RawDocsCount = RawDocsCount;
	}

	public void setCacheHit(boolean CacheHit) {
		this.CacheHit = CacheHit;
	}

	public void setStrings(String[] strings) {
		this.strings = strings;
	}

	public void setDocs(Doc[] docs) {
		this.docs = docs;
	}

	public void setLimit_ttl(long limit_ttl) {
		this.limit_ttl = limit_ttl;
	}

	public void setRawDocsSearchTime(long RawDocsSearchTime) {
		this.RawDocsSearchTime = RawDocsSearchTime;
	}

	public void setQuota(long quota) {
		this.quota = quota;
	}

	public void setLimit(long limit) {
		this.limit = limit;
	}

	public void setReRankSearchTime(long ReRankSearchTime) {
		this.ReRankSearchTime = ReRankSearchTime;
	}

	public void setQuota_ttl(long quota_ttl) {
		this.quota_ttl = quota_ttl;
	}

	public void setTrial(long trial) {
		this.trial = trial;
	}

	@Override
	public String toString() {
		return new StringBuilder()
			.append(getClass().getName()).append("{\n")
			.append("RawDocsCount: ").append(RawDocsCount+",\n")
			.append("CacheHit: ").append(CacheHit+",\n")
			.append("strings: ").append(strings+",\n")
			.append("docs: ").append(docs+",\n")
			.append("limit_ttl: ").append(limit_ttl+",\n")
			.append("RawDocsSearchTime: ").append(RawDocsSearchTime+",\n")
			.append("quota: ").append(quota+",\n")
			.append("limit: ").append(limit+",\n")
			.append("ReRankSearchTime: ").append(ReRankSearchTime+",\n")
			.append("quota_ttl: ").append(quota_ttl+",\n")
			.append("trial: ").append(trial+",\n")
			.append("\n}").toString();
	}

	public static class Builder {
		public long RawDocsCount;
		public boolean CacheHit;
		public String[] strings;
		public Doc[] docs;
		public long limit_ttl;
		public long RawDocsSearchTime;
		public long quota;
		public long limit;
		public long ReRankSearchTime;
		public long quota_ttl;
		public long trial;

		public Builder setRawDocsCount(long RawDocsCount) {
			this.RawDocsCount = RawDocsCount;
			return this;
		}

		public Builder setCacheHit(boolean CacheHit) {
			this.CacheHit = CacheHit;
			return this;
		}

		public Builder setStrings(String[] strings) {
			this.strings = strings;
			return this;
		}

		public Builder setDocs(Doc[] docs) {
			this.docs = docs;
			return this;
		}

		public Builder setLimit_ttl(long limit_ttl) {
			this.limit_ttl = limit_ttl;
			return this;
		}

		public Builder setRawDocsSearchTime(long RawDocsSearchTime) {
			this.RawDocsSearchTime = RawDocsSearchTime;
			return this;
		}

		public Builder setQuota(long quota) {
			this.quota = quota;
			return this;
		}

		public Builder setLimit(long limit) {
			this.limit = limit;
			return this;
		}

		public Builder setReRankSearchTime(long ReRankSearchTime) {
			this.ReRankSearchTime = ReRankSearchTime;
			return this;
		}

		public Builder setQuota_ttl(long quota_ttl) {
			this.quota_ttl = quota_ttl;
			return this;
		}

		public Builder setTrial(long trial) {
			this.trial = trial;
			return this;
		}

		public TraceMoeResult build() {
			return new TraceMoeResult(RawDocsCount,CacheHit,strings,docs,limit_ttl,RawDocsSearchTime,quota,limit,ReRankSearchTime,quota_ttl,trial);
		}
	}

	public static final class Doc implements Serializable {
		private static final long serialVersionUID = 1L;
		public String title_chinese;
		public String title_native;
		public String[] synonyms;
		public String title_romaji;
		public String episode;
		public long mal_id;
		public String title;
		public long anilist_id;
		public boolean is_adult;
		public String tokenthumb;
		public String[] synonyms_chinese;
		public double at;
		public String filename;
		public double similarity;
		public String season;
		public String title_english;
		public double from;
		public double to;
		public String anime;

		public Doc(String title_chinese, String title_native, String[] synonyms, String title_romaji, String episode, long mal_id, String title, long anilist_id, boolean is_adult, String tokenthumb, String[] synonyms_chinese, double at, String filename, double similarity, String season, String title_english, double from, double to, String anime) {
			this.title_chinese = title_chinese;
			this.title_native = title_native;
			this.synonyms = synonyms;
			this.title_romaji = title_romaji;
			this.episode = episode;
			this.mal_id = mal_id;
			this.title = title;
			this.anilist_id = anilist_id;
			this.is_adult = is_adult;
			this.tokenthumb = tokenthumb;
			this.synonyms_chinese = synonyms_chinese;
			this.at = at;
			this.filename = filename;
			this.similarity = similarity;
			this.season = season;
			this.title_english = title_english;
			this.from = from;
			this.to = to;
			this.anime = anime;
		}

		public String getTitle_chinese() {
			return title_chinese;
		}

		public String getTitle_native() {
			return title_native;
		}

		public String[] getSynonyms() {
			return synonyms;
		}

		public String getTitle_romaji() {
			return title_romaji;
		}

		public String getEpisode() {
			return episode;
		}

		public long getMal_id() {
			return mal_id;
		}

		public String getTitle() {
			return title;
		}

		public long getAnilist_id() {
			return anilist_id;
		}

		public boolean getIs_adult() {
			return is_adult;
		}

		public String getTokenthumb() {
			return tokenthumb;
		}

		public String[] getSynonyms_chinese() {
			return synonyms_chinese;
		}

		public double getAt() {
			return at;
		}

		public String getFilename() {
			return filename;
		}

		public double getSimilarity() {
			return similarity;
		}

		public String getSeason() {
			return season;
		}

		public String getTitle_english() {
			return title_english;
		}

		public double getFrom() {
			return from;
		}

		public double getTo() {
			return to;
		}

		public String getAnime() {
			return anime;
		}

		public void setTitle_chinese(String title_chinese) {
			this.title_chinese = title_chinese;
		}

		public void setTitle_native(String title_native) {
			this.title_native = title_native;
		}

		public void setSynonyms(String[] synonyms) {
			this.synonyms = synonyms;
		}

		public void setTitle_romaji(String title_romaji) {
			this.title_romaji = title_romaji;
		}

		public void setEpisode(String episode) {
			this.episode = episode;
		}

		public void setMal_id(long mal_id) {
			this.mal_id = mal_id;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public void setAnilist_id(long anilist_id) {
			this.anilist_id = anilist_id;
		}

		public void setIs_adult(boolean is_adult) {
			this.is_adult = is_adult;
		}

		public void setTokenthumb(String tokenthumb) {
			this.tokenthumb = tokenthumb;
		}

		public void setSynonyms_chinese(String[] synonyms_chinese) {
			this.synonyms_chinese = synonyms_chinese;
		}

		public void setAt(double at) {
			this.at = at;
		}

		public void setFilename(String filename) {
			this.filename = filename;
		}

		public void setSimilarity(double similarity) {
			this.similarity = similarity;
		}

		public void setSeason(String season) {
			this.season = season;
		}

		public void setTitle_english(String title_english) {
			this.title_english = title_english;
		}

		public void setFrom(double from) {
			this.from = from;
		}

		public void setTo(double to) {
			this.to = to;
		}

		public void setAnime(String anime) {
			this.anime = anime;
		}

		@Override
		public String toString() {
			return new StringBuilder()
				.append(getClass().getName()).append("{\n")
				.append("title_chinese: ").append(title_chinese+",\n")
				.append("title_native: ").append(title_native+",\n")
				.append("synonyms: ").append(synonyms+",\n")
				.append("title_romaji: ").append(title_romaji+",\n")
				.append("episode: ").append(episode+",\n")
				.append("mal_id: ").append(mal_id+",\n")
				.append("title: ").append(title+",\n")
				.append("anilist_id: ").append(anilist_id+",\n")
				.append("is_adult: ").append(is_adult+",\n")
				.append("tokenthumb: ").append(tokenthumb+",\n")
				.append("synonyms_chinese: ").append(synonyms_chinese+",\n")
				.append("at: ").append(at+",\n")
				.append("filename: ").append(filename+",\n")
				.append("similarity: ").append(similarity+",\n")
				.append("season: ").append(season+",\n")
				.append("title_english: ").append(title_english+",\n")
				.append("from: ").append(from+",\n")
				.append("to: ").append(to+",\n")
				.append("anime: ").append(anime+",\n")
				.append("\n}").toString();
		}

		public static class Builder {
			public String title_chinese;
			public String title_native;
			public String[] synonyms;
			public String title_romaji;
			public String episode;
			public long mal_id;
			public String title;
			public long anilist_id;
			public boolean is_adult;
			public String tokenthumb;
			public String[] synonyms_chinese;
			public double at;
			public String filename;
			public double similarity;
			public String season;
			public String title_english;
			public double from;
			public double to;
			public String anime;

			public Builder setTitle_chinese(String title_chinese) {
				this.title_chinese = title_chinese;
				return this;
			}

			public Builder setTitle_native(String title_native) {
				this.title_native = title_native;
				return this;
			}

			public Builder setSynonyms(String[] synonyms) {
				this.synonyms = synonyms;
				return this;
			}

			public Builder setTitle_romaji(String title_romaji) {
				this.title_romaji = title_romaji;
				return this;
			}

			public Builder setEpisode(String episode) {
				this.episode = episode;
				return this;
			}

			public Builder setMal_id(long mal_id) {
				this.mal_id = mal_id;
				return this;
			}

			public Builder setTitle(String title) {
				this.title = title;
				return this;
			}

			public Builder setAnilist_id(long anilist_id) {
				this.anilist_id = anilist_id;
				return this;
			}

			public Builder setIs_adult(boolean is_adult) {
				this.is_adult = is_adult;
				return this;
			}

			public Builder setTokenthumb(String tokenthumb) {
				this.tokenthumb = tokenthumb;
				return this;
			}

			public Builder setSynonyms_chinese(String[] synonyms_chinese) {
				this.synonyms_chinese = synonyms_chinese;
				return this;
			}

			public Builder setAt(double at) {
				this.at = at;
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

			public Builder setSeason(String season) {
				this.season = season;
				return this;
			}

			public Builder setTitle_english(String title_english) {
				this.title_english = title_english;
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

			public Builder setAnime(String anime) {
				this.anime = anime;
				return this;
			}

			public Doc build() {
				return new Doc(title_chinese,title_native,synonyms,title_romaji,episode,mal_id,title,anilist_id,is_adult,tokenthumb,synonyms_chinese,at,filename,similarity,season,title_english,from,to,anime);
			}
		}

	}

}

