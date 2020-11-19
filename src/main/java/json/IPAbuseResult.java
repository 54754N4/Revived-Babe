package json;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

// generated at : http://www.jsonschema2pojo.org/
class IPAbuseResult implements Serializable {
	private final static long serialVersionUID = -4226594804161711238L;
	@SerializedName("data")
	@Expose
	private Data data;

	public Data getData() {
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	}

	public static final class Data implements Serializable {
		@SerializedName("ipAddress")
		@Expose
		private String ipAddress;
		@SerializedName("isPublic")
		@Expose
		private boolean isPublic;
		@SerializedName("ipVersion")
		@Expose
		private long ipVersion;
		@SerializedName("isWhitelisted")
		@Expose
		private boolean isWhitelisted;
		@SerializedName("abuseConfidenceScore")
		@Expose
		private long abuseConfidenceScore;
		@SerializedName("countryCode")
		@Expose
		private String countryCode;
		@SerializedName("countryName")
		@Expose
		private String countryName;
		@SerializedName("usageType")
		@Expose
		private String usageType;
		@SerializedName("isp")
		@Expose
		private String isp;
		@SerializedName("domain")
		@Expose
		private String domain;
		@SerializedName("hostnames")
		@Expose
		private List<Object> hostnames = new ArrayList<Object>();
		@SerializedName("totalReports")
		@Expose
		private long totalReports;
		@SerializedName("numDistinctUsers")
		@Expose
		private long numDistinctUsers;
		@SerializedName("lastReportedAt")
		@Expose
		private String lastReportedAt;
		@SerializedName("reports")
		@Expose
		private List<Report> reports = new ArrayList<Report>();
		private final static long serialVersionUID = 4128427517387727490L;

		/**
		 * No args constructor for use in serialization
		 *
		 */
		public Data() {
		}

		/**
		 *
		 * @param lastReportedAt
		 * @param reports
		 * @param abuseConfidenceScore
		 * @param numDistinctUsers
		 * @param isp
		 * @param ipAddress
		 * @param totalReports
		 * @param hostnames
		 * @param isWhitelisted
		 * @param ipVersion
		 * @param countryCode
		 * @param domain
		 * @param isPublic
		 * @param countryName
		 * @param usageType
		 */
		public Data(String ipAddress, boolean isPublic, long ipVersion, boolean isWhitelisted, long abuseConfidenceScore, String countryCode, String countryName, String usageType, String isp, String domain, List<Object> hostnames, long totalReports, long numDistinctUsers, String lastReportedAt, List<Report> reports) {
			super();
			this.ipAddress = ipAddress;
			this.isPublic = isPublic;
			this.ipVersion = ipVersion;
			this.isWhitelisted = isWhitelisted;
			this.abuseConfidenceScore = abuseConfidenceScore;
			this.countryCode = countryCode;
			this.countryName = countryName;
			this.usageType = usageType;
			this.isp = isp;
			this.domain = domain;
			this.hostnames = hostnames;
			this.totalReports = totalReports;
			this.numDistinctUsers = numDistinctUsers;
			this.lastReportedAt = lastReportedAt;
			this.reports = reports;
		}

		public String getIpAddress() {
			return ipAddress;
		}

		public void setIpAddress(String ipAddress) {
			this.ipAddress = ipAddress;
		}

		public boolean isIsPublic() {
			return isPublic;
		}

		public void setIsPublic(boolean isPublic) {
			this.isPublic = isPublic;
		}

		public long getIpVersion() {
			return ipVersion;
		}

		public void setIpVersion(long ipVersion) {
			this.ipVersion = ipVersion;
		}

		public boolean isIsWhitelisted() {
			return isWhitelisted;
		}

		public void setIsWhitelisted(boolean isWhitelisted) {
			this.isWhitelisted = isWhitelisted;
		}

		public long getAbuseConfidenceScore() {
			return abuseConfidenceScore;
		}

		public void setAbuseConfidenceScore(long abuseConfidenceScore) {
			this.abuseConfidenceScore = abuseConfidenceScore;
		}

		public String getCountryCode() {
			return countryCode;
		}

		public void setCountryCode(String countryCode) {
			this.countryCode = countryCode;
		}

		public String getCountryName() {
			return countryName;
		}

		public void setCountryName(String countryName) {
			this.countryName = countryName;
		}

		public String getUsageType() {
			return usageType;
		}

		public void setUsageType(String usageType) {
			this.usageType = usageType;
		}

		public String getIsp() {
			return isp;
		}

		public void setIsp(String isp) {
			this.isp = isp;
		}

		public String getDomain() {
			return domain;
		}

		public void setDomain(String domain) {
			this.domain = domain;
		}

		public List<Object> getHostnames() {
			return hostnames;
		}

		public void setHostnames(List<Object> hostnames) {
			this.hostnames = hostnames;
		}

		public long getTotalReports() {
			return totalReports;
		}

		public void setTotalReports(long totalReports) {
			this.totalReports = totalReports;
		}

		public long getNumDistinctUsers() {
			return numDistinctUsers;
		}

		public void setNumDistinctUsers(long numDistinctUsers) {
			this.numDistinctUsers = numDistinctUsers;
		}

		public String getLastReportedAt() {
			return lastReportedAt;
		}

		public void setLastReportedAt(String lastReportedAt) {
			this.lastReportedAt = lastReportedAt;
		}

		public List<Report> getReports() {
			return reports;
		}

		public void setReports(List<Report> reports) {
			this.reports = reports;
		}

	}

	public static final class Report implements Serializable
	{

		@SerializedName("reportedAt")
		@Expose
		private String reportedAt;
		@SerializedName("comment")
		@Expose
		private String comment;
		@SerializedName("categories")
		@Expose
		private List<Long> categories = new ArrayList<Long>();
		@SerializedName("reporterId")
		@Expose
		private long reporterId;
		@SerializedName("reporterCountryCode")
		@Expose
		private String reporterCountryCode;
		@SerializedName("reporterCountryName")
		@Expose
		private String reporterCountryName;
		private final static long serialVersionUID = 3192603984182518176L;

		/**
		 * No args constructor for use in serialization
		 *
		 */
		public Report() {
		}

		/**
		 *
		 * @param reportedAt
		 * @param reporterCountryName
		 * @param comment
		 * @param reporterId
		 * @param categories
		 * @param reporterCountryCode
		 */
		public Report(String reportedAt, String comment, List<Long> categories, long reporterId, String reporterCountryCode, String reporterCountryName) {
			super();
			this.reportedAt = reportedAt;
			this.comment = comment;
			this.categories = categories;
			this.reporterId = reporterId;
			this.reporterCountryCode = reporterCountryCode;
			this.reporterCountryName = reporterCountryName;
		}

		public String getReportedAt() {
			return reportedAt;
		}

		public void setReportedAt(String reportedAt) {
			this.reportedAt = reportedAt;
		}

		public String getComment() {
			return comment;
		}

		public void setComment(String comment) {
			this.comment = comment;
		}

		public List<Long> getCategories() {
			return categories;
		}

		public void setCategories(List<Long> categories) {
			this.categories = categories;
		}

		public long getReporterId() {
			return reporterId;
		}

		public void setReporterId(long reporterId) {
			this.reporterId = reporterId;
		}

		public String getReporterCountryCode() {
			return reporterCountryCode;
		}

		public void setReporterCountryCode(String reporterCountryCode) {
			this.reporterCountryCode = reporterCountryCode;
		}

		public String getReporterCountryName() {
			return reporterCountryName;
		}

		public void setReporterCountryName(String reporterCountryName) {
			this.reporterCountryName = reporterCountryName;
		}
	} 
}