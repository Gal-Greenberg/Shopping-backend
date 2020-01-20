package collab.logic.plugins;

public class Location {

	
	String city;
	String streetName;
	int buildNumber;
	double lat;
	double lon;
	
	
	public Location() {
		// TODO Auto-generated constructor stub
	}
	
	public Location(String city, String streetName, int buildNumber, double lat, double lon) {
		super();
		this.city = city;
		this.streetName = streetName;
		this.buildNumber = buildNumber;
		this.lat = lat;
		this.lon = lon;
	}



	public String getCity() {
		return city;
	}



	public void setCity(String city) {
		this.city = city;
	}



	public String getStreetName() {
		return streetName;
	}



	public void setStreetName(String streetName) {
		this.streetName = streetName;
	}



	public int getBuildNumber() {
		return buildNumber;
	}



	public void setBuildNumber(int buildNumber) {
		this.buildNumber = buildNumber;
	}



	public double getLat() {
		return lat;
	}



	public void setLat(double lat) {
		this.lat = lat;
	}



	public double getLon() {
		return lon;
	}



	public void setLon(double lon) {
		this.lon = lon;
	}



	@Override
	public String toString() {
		return "mallLocation [city=" + city + ", StreetName=" + streetName + ", buildNumber=" + buildNumber + ", lat="
				+ lat + ", lon=" + lon + "]";
	}
}
