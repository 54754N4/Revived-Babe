package lib;

public class GPS {
	public static String toString(float latitude, float longitude) {
		return degreefy(latitude, true) + "," + degreefy(longitude, false);
	}
	
	public static final String degreefy(float location, boolean latitude) {
		StringBuilder sb = new StringBuilder();
		boolean negative = location < 0;
		if (negative)
			location *= -1;
		// First coord
		int truncated = (int) location;
		sb.append(truncated + "°");
		// Second coord
		location = (location - truncated) * 60;
		truncated = (int) location;
		sb.append(truncated + "'");
		// Third coord
		location = Math.round((location - truncated) * 60f * 10f) / 10f;
		sb.append(location + "\"");
		// Set cardinal
		if (latitude)
			sb.append(negative ? "S" : "N");
		else
			sb.append(negative ? "W" : "E");
		return sb.toString();
	}
	
	public static GeoCoordinates from(String string) {
		String[] coords = string.split(",");
		return new GeoCoordinates(degreefy(coords[0], true), degreefy(coords[1], false));
	}
	
	public static final float degreefy(String location, boolean latitude) {
		float value = 0, t1 = 0, t2 = 0, t3 = 0;
		// First coord
		int index = location.indexOf("°"); 
		if (index != -1) {
			t1 = Float.parseFloat(location.substring(0, index));
			location = location.substring(index+1);
		}
		// Second coord
		index = location.indexOf("'");
		if (index != -1) {
			t2 = Float.parseFloat(location.substring(0, index));
			location = location.substring(index+1);
		}
		// Third coord
		index = location.indexOf("\"");
		if (index != -1) {
			t3 = Float.parseFloat(location.substring(0, index));
			location = location.substring(index+1);
		}
		// Merge components
		value = (t3 / 60f + t2) / 60 + t1;
		boolean negative = location.contains("W") || location.contains("S");
		return negative ? -value : value;
	}
	
	public static void main(String[] args) {
		float longitude = 40.806984f, 
			latitude = -73.962831f;
		System.out.println(from(toString(longitude, latitude)));
	}
	
	public static final class GeoCoordinates {
		public final float latitude, longitude;
		
		public GeoCoordinates(float latitude, float longitude) {
			this.latitude = latitude;
			this.longitude = longitude;
		}

		@Override
		public String toString() {
			return "GeoCoordinates [latitude=" + latitude + ", longitude=" + longitude + "]";
		}
	}
}
 