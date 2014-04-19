package co.autumn.utils;

import static co.autumn.android.Constants.SERVER_ADDRESS;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;

import co.autumn.users.CurrentUser;

import android.content.Context;
import android.location.Location;

public class LocationUtil {
	
	private Location userLocation;
	private CurrentUser user;

	public LocationUtil(LatLng location, CurrentUser user) {
		this.userLocation = new Location("");
		this.userLocation.setLatitude(location.latitude);
		this.userLocation.setLongitude(location.longitude);
		this.user = user;
	}

	public LocationUtil(Location userLocation) {
		this.userLocation = userLocation;
	}
	
	public LocationUtil(Location lastKnownLocation, CurrentUser currentUser) {
		this.userLocation = lastKnownLocation;
		this.user = currentUser;
	}

	public void create(Context context) throws JSONException{
		LocationJSON json = new LocationJSON(this);
		String url = SERVER_ADDRESS+"locations.json";
		AsyncRequest request = new AsyncRequest(context, url, "POST", json.getJSON());
		request.execute(url);
	}
	
	public JSONObject getJSON() throws JSONException{
		LocationJSON json = new LocationJSON(this);
		return json.getLocationJSON();
	}

	public CurrentUser getUser() {
		return user;
	}

	public void setUser(CurrentUser user) {
		this.user = user;
	}

	public Location getUserLocation() {
		return userLocation;
	}

	public void setUserLocation(Location userLocation) {
		this.userLocation = userLocation;
	}

}
