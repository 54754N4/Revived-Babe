package json;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TimeResult implements Serializable {
	@SerializedName("abbreviation")
	@Expose
	private String abbreviation;
	@SerializedName("client_ip")
	@Expose
	private String clientIp;
	@SerializedName("datetime")
	@Expose
	private String datetime;
	@SerializedName("day_of_week")
	@Expose
	private long dayOfWeek;
	@SerializedName("day_of_year")
	@Expose
	private long dayOfYear;
	@SerializedName("dst")
	@Expose
	private boolean dst;
	@SerializedName("dst_from")
	@Expose
	private Object dstFrom;
	@SerializedName("dst_offset")
	@Expose
	private long dstOffset;
	@SerializedName("dst_until")
	@Expose
	private Object dstUntil;
	@SerializedName("raw_offset")
	@Expose
	private long rawOffset;
	@SerializedName("timezone")
	@Expose
	private String timezone;
	@SerializedName("unixtime")
	@Expose
	private long unixtime;
	@SerializedName("utc_datetime")
	@Expose
	private String utcDatetime;
	@SerializedName("utc_offset")
	@Expose
	private String utcOffset;
	@SerializedName("week_number")
	@Expose
	private long weekNumber;
	private final static long serialVersionUID = -3224785767973625536L;

	/**
	 * No args constructor for use in serialization
	 *
	 */
	public TimeResult() {
	}

	/**
	 *
	 * @param unixtime
	 * @param utcOffset
	 * @param dst
	 * @param dayOfYear
	 * @param timezone
	 * @param dstOffset
	 * @param abbreviation
	 * @param dstUntil
	 * @param weekNumber
	 * @param datetime
	 * @param dayOfWeek
	 * @param rawOffset
	 * @param clientIp
	 * @param utcDatetime
	 * @param dstFrom
	 */
	public TimeResult(String abbreviation, String clientIp, String datetime, long dayOfWeek, long dayOfYear, boolean dst, Object dstFrom, long dstOffset, Object dstUntil, long rawOffset, String timezone, long unixtime, String utcDatetime, String utcOffset, long weekNumber) {
		this.abbreviation = abbreviation;
		this.clientIp = clientIp;
		this.datetime = datetime;
		this.dayOfWeek = dayOfWeek;
		this.dayOfYear = dayOfYear;
		this.dst = dst;
		this.dstFrom = dstFrom;
		this.dstOffset = dstOffset;
		this.dstUntil = dstUntil;
		this.rawOffset = rawOffset;
		this.timezone = timezone;
		this.unixtime = unixtime;
		this.utcDatetime = utcDatetime;
		this.utcOffset = utcOffset;
		this.weekNumber = weekNumber;
	}

	public String getAbbreviation() {
		return abbreviation;
	}

	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}

	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	public String getDatetime() {
		return datetime;
	}

	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}

	public long getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(long dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	public long getDayOfYear() {
		return dayOfYear;
	}

	public void setDayOfYear(long dayOfYear) {
		this.dayOfYear = dayOfYear;
	}

	public boolean isDst() {
		return dst;
	}

	public void setDst(boolean dst) {
		this.dst = dst;
	}

	public Object getDstFrom() {
		return dstFrom;
	}

	public void setDstFrom(Object dstFrom) {
		this.dstFrom = dstFrom;
	}

	public long getDstOffset() {
		return dstOffset;
	}

	public void setDstOffset(long dstOffset) {
		this.dstOffset = dstOffset;
	}

	public Object getDstUntil() {
		return dstUntil;
	}

	public void setDstUntil(Object dstUntil) {
		this.dstUntil = dstUntil;
	}

	public long getRawOffset() {
		return rawOffset;
	}

	public void setRawOffset(long rawOffset) {
		this.rawOffset = rawOffset;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public long getUnixtime() {
		return unixtime;
	}

	public void setUnixtime(long unixtime) {
		this.unixtime = unixtime;
	}

	public String getUtcDatetime() {
		return utcDatetime;
	}

	public void setUtcDatetime(String utcDatetime) {
		this.utcDatetime = utcDatetime;
	}

	public String getUtcOffset() {
		return utcOffset;
	}

	public void setUtcOffset(String utcOffset) {
		this.utcOffset = utcOffset;
	}

	public long getWeekNumber() {
		return weekNumber;
	}

	public void setWeekNumber(long weekNumber) {
		this.weekNumber = weekNumber;
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder().append("abbreviation", abbreviation).append("clientIp", clientIp).append("datetime", datetime).append("dayOfWeek", dayOfWeek).append("dayOfYear", dayOfYear).append("dst", dst).append("dstFrom", dstFrom).append("dstOffset", dstOffset).append("dstUntil", dstUntil).append("rawOffset", rawOffset).append("timezone", timezone).append("unixtime", unixtime).append("utcDatetime", utcDatetime).append("utcOffset", utcOffset).append("weekNumber", weekNumber).toString();
	}
	
	class ToStringBuilder{
		private StringBuilder sb = new StringBuilder();
		
		public ToStringBuilder append(String name, Object value) {
			sb.append(name).append(": ").append(value).append(",\n");
			return this;
		}
		
		@Override
		public String toString() {
			return sb.toString();
		}
	}
}
