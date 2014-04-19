package co.autumn.utils;

import org.json.JSONException;
import org.json.JSONObject;

import co.autumn.users.User;

public class UserJSON {
	
	User user;
	
	public UserJSON(User user){
		this.user = user;
	}
	
	
	public String getJSON() throws JSONException{
		JSONObject userJSON = new JSONObject();
		JSONObject userDetails = new JSONObject();
		userDetails.put("name", user.getName());
		userDetails.put("email", user.getEmail());
		userDetails.put("auth_token", user.getAuthToken());
		userDetails.put("dob", user.getDob());
		userDetails.put("maretial_status", user.getMaretialStatus());
		userDetails.put("gender", user.getGender());
		userDetails.put("fb_id", user.getFbId());
		userDetails.put("linkedin_id", user.getLinkedinId());
		userDetails.put("profile_pic_url", user.getProfilePicURL());
		userJSON.put("user", userDetails);
		return userJSON.toString();
	}
}
