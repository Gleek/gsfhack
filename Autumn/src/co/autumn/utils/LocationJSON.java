package co.autumn.utils;

import org.json.JSONException;
import org.json.JSONObject;


public class LocationJSON {
	
	private LocationUtil location;

	public LocationJSON(LocationUtil location) {
		this.location = location;
	}
	
	public JSONObject getLocationJSON() throws JSONException{
		JSONObject locationJSON = new JSONObject();
		locationJSON.put("longitude", location.getUserLocation().getLongitude());
		locationJSON.put("latitude", location.getUserLocation().getLatitude());
		return locationJSON;
	}
	
	public String getJSON() throws JSONException{
		JSONObject locationJSONObject = new JSONObject();
		locationJSONObject.put("location", getLocationJSON());
		locationJSONObject.put("auth_token", location.getUser().getAuthToken());
		return locationJSONObject.toString();
	}
	
}
