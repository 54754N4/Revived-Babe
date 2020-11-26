import java.io.Serializable;

public final class ExchangeRate implements Serializable {
	private static final long serialVersionUID = 1L;
	public int unixtime;
	public String utc_offset;
	public boolean dst;
	public int day_of_year;
	public String timezone;
	public String abbreviation;
	public int dst_offset;
	public String utc_datetime;
	public String datetime;
	public Object dst_until;
	public String client_ip;
	public Object dst_from;
	public int week_number;
	public int day_of_week;
	public int raw_offset;

	public ExchangeRate(int unixtime, String utc_offset, boolean dst, int day_of_year, String timezone, String abbreviation, int dst_offset, String utc_datetime, String datetime, Object dst_until, String client_ip, Object dst_from, int week_number, int day_of_week, int raw_offset) {
		this.unixtime = unixtime;
		this.utc_offset = utc_offset;
		this.dst = dst;
		this.day_of_year = day_of_year;
		this.timezone = timezone;
		this.abbreviation = abbreviation;
		this.dst_offset = dst_offset;
		this.utc_datetime = utc_datetime;
		this.datetime = datetime;
		this.dst_until = dst_until;
		this.client_ip = client_ip;
		this.dst_from = dst_from;
		this.week_number = week_number;
		this.day_of_week = day_of_week;
		this.raw_offset = raw_offset;
	}

	public int getUnixtime() {
		return unixtime;
	}

	public String getUtc_offset() {
		return utc_offset;
	}

	public boolean getDst() {
		return dst;
	}

	public int getDay_of_year() {
		return day_of_year;
	}

	public String getTimezone() {
		return timezone;
	}

	public String getAbbreviation() {
		return abbreviation;
	}

	public int getDst_offset() {
		return dst_offset;
	}

	public String getUtc_datetime() {
		return utc_datetime;
	}

	public String getDatetime() {
		return datetime;
	}

	public Object getDst_until() {
		return dst_until;
	}

	public String getClient_ip() {
		return client_ip;
	}

	public Object getDst_from() {
		return dst_from;
	}

	public int getWeek_number() {
		return week_number;
	}

	public int getDay_of_week() {
		return day_of_week;
	}

	public int getRaw_offset() {
		return raw_offset;
	}

	public void setUnixtime(int unixtime) {
		this.unixtime = unixtime;
	}

	public void setUtc_offset(String utc_offset) {
		this.utc_offset = utc_offset;
	}

	public void setDst(boolean dst) {
		this.dst = dst;
	}

	public void setDay_of_year(int day_of_year) {
		this.day_of_year = day_of_year;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}

	public void setDst_offset(int dst_offset) {
		this.dst_offset = dst_offset;
	}

	public void setUtc_datetime(String utc_datetime) {
		this.utc_datetime = utc_datetime;
	}

	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}

	public void setDst_until(Object dst_until) {
		this.dst_until = dst_until;
	}

	public void setClient_ip(String client_ip) {
		this.client_ip = client_ip;
	}

	public void setDst_from(Object dst_from) {
		this.dst_from = dst_from;
	}

	public void setWeek_number(int week_number) {
		this.week_number = week_number;
	}

	public void setDay_of_week(int day_of_week) {
		this.day_of_week = day_of_week;
	}

	public void setRaw_offset(int raw_offset) {
		this.raw_offset = raw_offset;
	}

	@Override
	public String toString() {
		return new StringBuilder()
			.append(getClass().getName()).append("{\n")
			.append("unixtime: ").append(unixtime+",\n")
			.append("utc_offset: ").append(utc_offset+",\n")
			.append("dst: ").append(dst+",\n")
			.append("day_of_year: ").append(day_of_year+",\n")
			.append("timezone: ").append(timezone+",\n")
			.append("abbreviation: ").append(abbreviation+",\n")
			.append("dst_offset: ").append(dst_offset+",\n")
			.append("utc_datetime: ").append(utc_datetime+",\n")
			.append("datetime: ").append(datetime+",\n")
			.append("dst_until: ").append(dst_until+",\n")
			.append("client_ip: ").append(client_ip+",\n")
			.append("dst_from: ").append(dst_from+",\n")
			.append("week_number: ").append(week_number+",\n")
			.append("day_of_week: ").append(day_of_week+",\n")
			.append("raw_offset: ").append(raw_offset+",\n")
			.append("\n}").toString();
	}

	public static class Builder {
		public int unixtime;
		public String utc_offset;
		public boolean dst;
		public int day_of_year;
		public String timezone;
		public String abbreviation;
		public int dst_offset;
		public String utc_datetime;
		public String datetime;
		public Object dst_until;
		public String client_ip;
		public Object dst_from;
		public int week_number;
		public int day_of_week;
		public int raw_offset;

		public Builder setUnixtime(int unixtime) {
			this.unixtime = unixtime;
			return this;
		}

		public Builder setUtc_offset(String utc_offset) {
			this.utc_offset = utc_offset;
			return this;
		}

		public Builder setDst(boolean dst) {
			this.dst = dst;
			return this;
		}

		public Builder setDay_of_year(int day_of_year) {
			this.day_of_year = day_of_year;
			return this;
		}

		public Builder setTimezone(String timezone) {
			this.timezone = timezone;
			return this;
		}

		public Builder setAbbreviation(String abbreviation) {
			this.abbreviation = abbreviation;
			return this;
		}

		public Builder setDst_offset(int dst_offset) {
			this.dst_offset = dst_offset;
			return this;
		}

		public Builder setUtc_datetime(String utc_datetime) {
			this.utc_datetime = utc_datetime;
			return this;
		}

		public Builder setDatetime(String datetime) {
			this.datetime = datetime;
			return this;
		}

		public Builder setDst_until(Object dst_until) {
			this.dst_until = dst_until;
			return this;
		}

		public Builder setClient_ip(String client_ip) {
			this.client_ip = client_ip;
			return this;
		}

		public Builder setDst_from(Object dst_from) {
			this.dst_from = dst_from;
			return this;
		}

		public Builder setWeek_number(int week_number) {
			this.week_number = week_number;
			return this;
		}

		public Builder setDay_of_week(int day_of_week) {
			this.day_of_week = day_of_week;
			return this;
		}

		public Builder setRaw_offset(int raw_offset) {
			this.raw_offset = raw_offset;
			return this;
		}

		public ExchangeRate build() {
			return new ExchangeRate(unixtime,utc_offset,dst,day_of_year,timezone,abbreviation,dst_offset,utc_datetime,datetime,dst_until,client_ip,dst_from,week_number,day_of_week,raw_offset);
		}
	}
}

