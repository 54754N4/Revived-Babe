package json;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

// generated at : http://www.jsonschema2pojo.org/
public class TraceMoeResult implements Serializable	{

	@SerializedName("RawDocsCount")
	@Expose
	private long rawDocsCount;
	@SerializedName("RawDocsSearchTime")
	@Expose
	private long rawDocsSearchTime;
	@SerializedName("ReRankSearchTime")
	@Expose
	private long reRankSearchTime;
	@SerializedName("CacheHit")
	@Expose
	private boolean cacheHit;
	@SerializedName("trial")
	@Expose
	private long trial;
	@SerializedName("limit")
	@Expose
	private long limit;
	@SerializedName("limit_ttl")
	@Expose
	private long limitTtl;
	@SerializedName("quota")
	@Expose
	private long quota;
	@SerializedName("quota_ttl")
	@Expose
	private long quotaTtl;
	@SerializedName("docs")
	@Expose
	private List<Doc> docs = new ArrayList<Doc>();
	private final static long serialVersionUID = -1376242909089035194L;

	/**
	 * No args constructor for use in serialization
	 *
	 */
	public TraceMoeResult() {
	}

	/**
	 *
	 * @param rawDocsSearchTime
	 * @param quotaTtl
	 * @param docs
	 * @param quota
	 * @param cacheHit
	 * @param limit
	 * @param limitTtl
	 * @param rawDocsCount
	 * @param reRankSearchTime
	 * @param trial
	 */
	public TraceMoeResult(long rawDocsCount, long rawDocsSearchTime, long reRankSearchTime, boolean cacheHit, long trial, long limit, long limitTtl, long quota, long quotaTtl, List<Doc> docs) {
		super();
		this.rawDocsCount = rawDocsCount;
		this.rawDocsSearchTime = rawDocsSearchTime;
		this.reRankSearchTime = reRankSearchTime;
		this.cacheHit = cacheHit;
		this.trial = trial;
		this.limit = limit;
		this.limitTtl = limitTtl;
		this.quota = quota;
		this.quotaTtl = quotaTtl;
		this.docs = docs;
	}

	public long getRawDocsCount() {
		return rawDocsCount;
	}

	public void setRawDocsCount(long rawDocsCount) {
		this.rawDocsCount = rawDocsCount;
	}

	public long getRawDocsSearchTime() {
		return rawDocsSearchTime;
	}

	public void setRawDocsSearchTime(long rawDocsSearchTime) {
		this.rawDocsSearchTime = rawDocsSearchTime;
	}

	public long getReRankSearchTime() {
		return reRankSearchTime;
	}

	public void setReRankSearchTime(long reRankSearchTime) {
		this.reRankSearchTime = reRankSearchTime;
	}

	public boolean isCacheHit() {
		return cacheHit;
	}

	public void setCacheHit(boolean cacheHit) {
		this.cacheHit = cacheHit;
	}

	public long getTrial() {
		return trial;
	}

	public void setTrial(long trial) {
		this.trial = trial;
	}

	public long getLimit() {
		return limit;
	}

	public void setLimit(long limit) {
		this.limit = limit;
	}

	public long getLimitTtl() {
		return limitTtl;
	}

	public void setLimitTtl(long limitTtl) {
		this.limitTtl = limitTtl;
	}

	public long getQuota() {
		return quota;
	}

	public void setQuota(long quota) {
		this.quota = quota;
	}

	public long getQuotaTtl() {
		return quotaTtl;
	}

	public void setQuotaTtl(long quotaTtl) {
		this.quotaTtl = quotaTtl;
	}

	public List<Doc> getDocs() {
		return docs;
	}

	public void setDocs(List<Doc> docs) {
		this.docs = docs;
	}

	public static final class Doc implements Serializable
	{

		@SerializedName("from")
		@Expose
		private double from;
		@SerializedName("to")
		@Expose
		private double to;
		@SerializedName("anilist_id")
		@Expose
		private long anilistId;
		@SerializedName("at")
		@Expose
		private double at;
		@SerializedName("season")
		@Expose
		private String season;
		@SerializedName("anime")
		@Expose
		private String anime;
		@SerializedName("filename")
		@Expose
		private String filename;
		@SerializedName("episode")
		@Expose
		private long episode;
		@SerializedName("tokenthumb")
		@Expose
		private String tokenthumb;
		@SerializedName("similarity")
		@Expose
		private double similarity;
		@SerializedName("title")
		@Expose
		private String title;
		@SerializedName("title_native")
		@Expose
		private String titleNative;
		@SerializedName("title_chinese")
		@Expose
		private String titleChinese;
		@SerializedName("title_english")
		@Expose
		private String titleEnglish;
		@SerializedName("title_romaji")
		@Expose
		private String titleRomaji;
		@SerializedName("mal_id")
		@Expose
		private long malId;
		@SerializedName("synonyms")
		@Expose
		private List<String> synonyms = new ArrayList<String>();
		@SerializedName("synonyms_chinese")
		@Expose
		private List<Object> synonymsChinese = new ArrayList<Object>();
		@SerializedName("is_adult")
		@Expose
		private boolean isAdult;
		private final static long serialVersionUID = 7094738446903601833L;

		/**
		 * No args constructor for use in serialization
		 *
		 */
		public Doc() {
		}

		/**
		 *
		 * @param synonyms
		 * @param synonymsChinese
		 * @param titleChinese
		 * @param episode
		 * @param title
		 * @param titleNative
		 * @param tokenthumb
		 * @param titleEnglish
		 * @param anilistId
		 * @param at
		 * @param filename
		 * @param similarity
		 * @param season
		 * @param from
		 * @param to
		 * @param titleRomaji
		 * @param anime
		 * @param malId
		 * @param isAdult
		 */
		public Doc(double from, double to, long anilistId, double at, String season, String anime, String filename, long episode, String tokenthumb, double similarity, String title, String titleNative, String titleChinese, String titleEnglish, String titleRomaji, long malId, List<String> synonyms, List<Object> synonymsChinese, boolean isAdult) {
			super();
			this.from = from;
			this.to = to;
			this.anilistId = anilistId;
			this.at = at;
			this.season = season;
			this.anime = anime;
			this.filename = filename;
			this.episode = episode;
			this.tokenthumb = tokenthumb;
			this.similarity = similarity;
			this.title = title;
			this.titleNative = titleNative;
			this.titleChinese = titleChinese;
			this.titleEnglish = titleEnglish;
			this.titleRomaji = titleRomaji;
			this.malId = malId;
			this.synonyms = synonyms;
			this.synonymsChinese = synonymsChinese;
			this.isAdult = isAdult;
		}

		public double getFrom() {
			return from;
		}

		public void setFrom(double from) {
			this.from = from;
		}

		public double getTo() {
			return to;
		}

		public void setTo(double to) {
			this.to = to;
		}

		public long getAnilistId() {
			return anilistId;
		}

		public void setAnilistId(long anilistId) {
			this.anilistId = anilistId;
		}

		public double getAt() {
			return at;
		}

		public void setAt(double at) {
			this.at = at;
		}

		public String getSeason() {
			return season;
		}

		public void setSeason(String season) {
			this.season = season;
		}

		public String getAnime() {
			return anime;
		}

		public void setAnime(String anime) {
			this.anime = anime;
		}

		public String getFilename() {
			return filename;
		}

		public void setFilename(String filename) {
			this.filename = filename;
		}

		public long getEpisode() {
			return episode;
		}

		public void setEpisode(long episode) {
			this.episode = episode;
		}

		public String getTokenthumb() {
			return tokenthumb;
		}

		public void setTokenthumb(String tokenthumb) {
			this.tokenthumb = tokenthumb;
		}

		public double getSimilarity() {
			return similarity;
		}

		public void setSimilarity(double similarity) {
			this.similarity = similarity;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getTitleNative() {
			return titleNative;
		}

		public void setTitleNative(String titleNative) {
			this.titleNative = titleNative;
		}

		public String getTitleChinese() {
			return titleChinese;
		}

		public void setTitleChinese(String titleChinese) {
			this.titleChinese = titleChinese;
		}

		public String getTitleEnglish() {
			return titleEnglish;
		}

		public void setTitleEnglish(String titleEnglish) {
			this.titleEnglish = titleEnglish;
		}

		public String getTitleRomaji() {
			return titleRomaji;
		}

		public void setTitleRomaji(String titleRomaji) {
			this.titleRomaji = titleRomaji;
		}

		public long getMalId() {
			return malId;
		}

		public void setMalId(long malId) {
			this.malId = malId;
		}

		public List<String> getSynonyms() {
			return synonyms;
		}

		public void setSynonyms(List<String> synonyms) {
			this.synonyms = synonyms;
		}

		public List<Object> getSynonymsChinese() {
			return synonymsChinese;
		}

		public void setSynonymsChinese(List<Object> synonymsChinese) {
			this.synonymsChinese = synonymsChinese;
		}

		public boolean isIsAdult() {
			return isAdult;
		}

		public void setIsAdult(boolean isAdult) {
			this.isAdult = isAdult;
		}

	}
}