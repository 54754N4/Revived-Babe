import java.io.Serializable;


public final class NestedObject implements Serializable {
	private static final long serialVersionUID = 1L;
	public boolean CacheHit;
	public String[] strings;
	public Location location;
	public double time;
	public int trial;

	public NestedObject(boolean CacheHit, String[] strings, Location location, double time, int trial) {
		this.CacheHit = CacheHit;
		this.strings = strings;
		this.location = location;
		this.time = time;
		this.trial = trial;
	}

	public boolean getCacheHit() {
		return CacheHit;
	}

	public String[] getStrings() {
		return strings;
	}

	public Location getLocation() {
		return location;
	}

	public double getTime() {
		return time;
	}

	public int getTrial() {
		return trial;
	}

	public void setCacheHit(boolean CacheHit) {
		this.CacheHit = CacheHit;
	}

	public void setStrings(String[] strings) {
		this.strings = strings;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public void setTime(double time) {
		this.time = time;
	}

	public void setTrial(int trial) {
		this.trial = trial;
	}

	@Override
	public String toString() {
		return new StringBuilder()
			.append(getClass().getName()).append("{\n")
			.append("CacheHit: ").append(CacheHit+",\n")
			.append("strings: ").append(strings+",\n")
			.append("location: ").append(location+",\n")
			.append("time: ").append(time+",\n")
			.append("trial: ").append(trial+",\n")
			.append("\n}").toString();
	}

	public static class Builder {
		public boolean CacheHit;
		public String[] strings;
		public Location location;
		public double time;
		public int trial;

		public Builder setCacheHit(boolean CacheHit) {
			this.CacheHit = CacheHit;
			return this;
		}

		public Builder setStrings(String[] strings) {
			this.strings = strings;
			return this;
		}

		public Builder setLocation(Location location) {
			this.location = location;
			return this;
		}

		public Builder setTime(double time) {
			this.time = time;
			return this;
		}

		public Builder setTrial(int trial) {
			this.trial = trial;
			return this;
		}

		public NestedObject build() {
			return new NestedObject(CacheHit,strings,location,time,trial);
		}
	}

	public static final class Location implements Serializable {
		private static final long serialVersionUID = 1L;
		public Address address;

		public Location(Address address) {
			this.address = address;
		}

		public Address getAddress() {
			return address;
		}

		public void setAddress(Address address) {
			this.address = address;
		}

		@Override
		public String toString() {
			return new StringBuilder()
				.append(getClass().getName()).append("{\n")
				.append("address: ").append(address+",\n")
				.append("\n}").toString();
		}

		public static class Builder {
			public Address address;

			public Builder setAddress(Address address) {
				this.address = address;
				return this;
			}

			public Location build() {
				return new Location(address);
			}
		}

		public static final class Address implements Serializable {
			private static final long serialVersionUID = 1L;
			public String ip;
			public double latitude;
			public double longitude;

			public Address(String ip, double latitude, double longitude) {
				this.ip = ip;
				this.latitude = latitude;
				this.longitude = longitude;
			}

			public String getIp() {
				return ip;
			}

			public double getLatitude() {
				return latitude;
			}

			public double getLongitude() {
				return longitude;
			}

			public void setIp(String ip) {
				this.ip = ip;
			}

			public void setLatitude(double latitude) {
				this.latitude = latitude;
			}

			public void setLongitude(double longitude) {
				this.longitude = longitude;
			}

			@Override
			public String toString() {
				return new StringBuilder()
					.append(getClass().getName()).append("{\n")
					.append("ip: ").append(ip+",\n")
					.append("latitude: ").append(latitude+",\n")
					.append("longitude: ").append(longitude+",\n")
					.append("\n}").toString();
			}

			public static class Builder {
				public String ip;
				public double latitude;
				public double longitude;

				public Builder setIp(String ip) {
					this.ip = ip;
					return this;
				}

				public Builder setLatitude(double latitude) {
					this.latitude = latitude;
					return this;
				}

				public Builder setLongitude(double longitude) {
					this.longitude = longitude;
					return this;
				}

				public Address build() {
					return new Address(ip,latitude,longitude);
				}
			}
		}

	}

}

