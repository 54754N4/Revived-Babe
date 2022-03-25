package json;

public class GeolocateResult {
	public final String organization_name, region, organization, timezone, longitude, country_code3, area_code, ip, city, country, continent_code, country_code, latitude;
	public final int accuracy, asn;
	
	public GeolocateResult(String organization_name, String region, int accuracy, int asn, 
			String organization, String timezone, String longitude, String country_code3, 
			String area_code, String ip, String city, String country, String continent_code, 
			String country_code, String latitude) {
		this.organization_name = organization_name;
		this.region = region;
		this.accuracy = accuracy;
		this.asn = asn;
		this.organization = organization;
		this.timezone = timezone;
		this.longitude = longitude;
		this.country_code3 = country_code3;
		this.area_code = area_code;
		this.ip = ip;
		this.city = city;
		this.country = country;
		this.continent_code = continent_code;
		this.country_code = country_code;
		this.latitude = latitude;
	}
}

/*
{
    "organization_name": "Viettel Group",
    "region": "Ho Chi Minh",
    "accuracy": 5,
    "asn": 7552,
    "organization": "AS7552 Viettel Group",
    "timezone": "Asia/Ho_Chi_Minh",
    "longitude": "106.6438",
    "country_code3": "VNM",
    "area_code": "0",
    "ip": "115.79.140.65",
    "city": "Ho Chi Minh City",
    "country": "Vietnam",
    "continent_code": "AS",
    "country_code": "VN",
    "latitude": "10.8142"
}
*/