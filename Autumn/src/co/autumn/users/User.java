package co.autumn.users;

import org.brickred.socialauth.Profile;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.model.GraphUser;

import android.content.Context;
import android.util.Log;

import co.autumn.utils.AsyncRequest;
import co.autumn.utils.OnTaskCompleted;
import co.autumn.utils.UserJSON;
import static co.autumn.android.Constants.*;


public class User{
	
	private int userID;
	private String email;
	private String name;
	private String authToken;
	private String dob;
	private String profilePicURL;
	private String maretialStatus;
	private String gender;
	private String fbId;
	private String linkedinId;
	private String profilePicUrl;
	private JSONArray workInfo;
	private JSONArray education;
	
	public User(Profile profile){
		email = profile.getEmail();
		name = (profile.getFullName() == null) ? profile.getFirstName()+" "+profile.getLastName() : profile.getFullName();
		dob = profile.getDob().toString();
		profilePicUrl = profile.getProfileImageURL();
		maretialStatus = "";
		gender = profile.getGender();
		linkedinId = profile.getValidatedId();
//		workInfo = profile.get
	}
	
	public User(GraphUser profile) {
		email = (String) profile.getProperty("email");
		name = profile.getName();
		dob = profile.getBirthday();
		profilePicUrl = profile.getId();
		gender = (String) profile.getProperty("gender");
		maretialStatus = (String) profile.getProperty("relationship_status");
		fbId = profile.getId();
		try{
			setWorkInfo((JSONArray) profile.getProperty("work"));
			setEducation((JSONArray) profile.getProperty("education"));
		}catch(Exception e){
			Log.d("error", e.toString());
		}
			linkedinId = "";
	}
	
	public User(int id){
		userID = id;
	}
	
	public User(){
		email="pankaj@engineerinme.com";
		name="Pankaj Sharma";
		dob = "07-07-1992";
		profilePicURL = "abc.png";
		maretialStatus = "Single";
		gender = "male";
		fbId = "adsas";
		linkedinId = "as";
		authToken = "";
	}
	

	public void create(Context context, OnTaskCompleted listener) throws JSONException{
		Log.d("Create", "User create");
		UserJSON json = new UserJSON(this);
		String url = SERVER_ADDRESS+"users.json";
		AsyncRequest request = new AsyncRequest(context, url, "POST", json.getJSON(), listener);
		request.execute(url);
	}
	
	public void createWorkInfo(Context context, String authToken) throws JSONException{
		JSONObject workJSON = new JSONObject();
		workJSON.put("auth_token", authToken);
		workJSON.put("companies", getWorkInfo());
		
		String url = SERVER_ADDRESS+"companies/from_facebook.json";
		AsyncRequest request = new AsyncRequest(context, url, "POST", workJSON.toString());
		request.execute(url);
	}
	
	public void createEducationInfo(Context context, String authToken) throws JSONException{
		JSONObject eduJSON = new JSONObject();
		eduJSON.put("auth_token", authToken);
		eduJSON.put("institutes", getEducation());
		
		String url = SERVER_ADDRESS+"institutes/from_facebook.json";
		AsyncRequest request = new AsyncRequest(context, url, "POST", eduJSON.toString());
		request.execute(url);
	}

	public void show(Context context, OnTaskCompleted listener) throws JSONException{
		Log.d("aS","SD");
		String url = SERVER_ADDRESS+"users/"+userID+".json";
		AsyncRequest request = new AsyncRequest(context, url, "GET", "", listener);
		request.execute(url);
	}
	
	public void getStatuses(Context context, OnTaskCompleted listener) {
		String url = SERVER_ADDRESS+"users/"+userID+"/statuses.json";
		AsyncRequest request = new AsyncRequest(context, url, "POST", "", listener);
		request.execute(url);
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAuthToken() {
		return authToken;
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public String getProfilePicURL() {
		return profilePicURL;
	}

	public void setProfilePicURL(String profilePicURL) {
		this.profilePicURL = profilePicURL;
	}

	public String getMaretialStatus() {
		return maretialStatus;
	}

	public void setMaretialStatus(String maretialStatus) {
		this.maretialStatus = maretialStatus;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getFbId() {
		return fbId;
	}

	public void setFbId(String fbId) {
		this.fbId = fbId;
	}

	public String getLinkedinId() {
		return linkedinId;
	}

	public void setLinkedinId(String linkedinId) {
		this.linkedinId = linkedinId;
	}

	public String getProfilePicUrl() {
		return profilePicUrl;
	}

	public void setProfilePicUrl(String profilePicUrl) {
		this.profilePicUrl = profilePicUrl;
	}

	public JSONArray getEducation() {
		return education;
	}

	public void setEducation(JSONArray education) {
		this.education = education;
	}

	public JSONArray getWorkInfo() {
		return workInfo;
	}

	public void setWorkInfo(JSONArray workInfo) {
		this.workInfo = workInfo;
	}

	
}
