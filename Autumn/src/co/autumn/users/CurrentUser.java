package co.autumn.users;

import org.json.JSONException;
import org.json.JSONObject;

import co.autumn.android.LinkedInClient;
import co.autumn.utils.AsyncRequest;
import co.autumn.utils.OnTaskCompleted;
import android.content.Context;
import android.content.SharedPreferences;

import static co.autumn.android.Constants.*;

public class CurrentUser extends User {
	
	protected String authToken;
	protected Context context;
	SharedPreferences userAuthTokenSP;
	
	public CurrentUser(Context context, String auth_token){
		this.context = context;
		this.authToken = auth_token;
		this.userAuthTokenSP = context.getSharedPreferences(AUTH_TOKEN_SP, Context.MODE_PRIVATE);
	}
	
	public void show(Context context, OnTaskCompleted listener) throws JSONException{
		String url = SERVER_ADDRESS+"users/"+authToken+".json";
		AsyncRequest request = new AsyncRequest(context, url, "GET", "", listener);
		request.execute(url);
	}
	
	public void createStatus(Context context, String staus, OnTaskCompleted onTaskCompleted) throws JSONException {
		this.context = context;
		JSONObject json = new JSONObject();
		json.put("auth_token", this.authToken);
		JSONObject statusJSON = new JSONObject();
		statusJSON.put("status", staus);
		json.put("status", statusJSON);
		
		String url = SERVER_ADDRESS+"statuses.json";
		AsyncRequest request = new AsyncRequest(context, url, "POST", json.toString(), onTaskCompleted);
		request.execute(url);
	}
	
	public void createInfoFromLinkedIn(Context context, String accessKey, String accessSecret){
		LinkedInClient client = new LinkedInClient(context, accessKey, accessSecret);
		client.getEduInfo(new OnTaskCompleted() {
			@Override
			public void onTaskCompleted(JSONObject object) {
				try {
					CurrentUser.this.setEducation(object.getJSONArray("values"));
					CurrentUser.this.createEducationInfoFromLinkedIn();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
		client.getWorkInfo(new OnTaskCompleted() {
			@Override
			public void onTaskCompleted(JSONObject object) {
				try {
					CurrentUser.this.setWorkInfo(object.getJSONArray("values"));
					CurrentUser.this.createWorkInfoFromLinkedIn();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	protected void createWorkInfoFromLinkedIn() throws JSONException {
		JSONObject workJSON = new JSONObject();
		workJSON.put("auth_token", authToken);
		workJSON.put("companies", getWorkInfo());
		
		String url = SERVER_ADDRESS+"companies/from_linkedin.json";
		AsyncRequest request = new AsyncRequest(context, url, "POST", workJSON.toString());
		request.execute(url);
	}

	protected void createEducationInfoFromLinkedIn() throws JSONException {
		JSONObject eduJSON = new JSONObject();
		eduJSON.put("auth_token", authToken);
		eduJSON.put("institutes", getEducation());
		
		String url = SERVER_ADDRESS+"institutes/from_linkedin.json";
		AsyncRequest request = new AsyncRequest(context, url, "POST", eduJSON.toString());
		request.execute(url);
	}
	
	public void getChatMeta(Context context, int chatUserID, OnTaskCompleted listener) throws JSONException {
		String url = SERVER_ADDRESS+"users/get_chat_meta.json";
		JSONObject meta = new JSONObject();
		meta.put("auth_token", authToken);
		meta.put("other_user_id", chatUserID);
		AsyncRequest request = new AsyncRequest(context, url, "POST", meta.toString(), listener);
		request.execute(url);
	}
	
	public void nearby(Context context, JSONObject location, OnTaskCompleted onTaskCompleted) throws JSONException{
		JSONObject eduJSON = new JSONObject();
		eduJSON.put("auth_token", authToken);
		eduJSON.put("location", location);
		
		String url = SERVER_ADDRESS+"users/nearby.json";
		AsyncRequest request = new AsyncRequest(context, url, "POST", eduJSON.toString(), onTaskCompleted);
		request.execute(url);
	}
	
	public CurrentUser(Context context){
		this.context = context;
		this.userAuthTokenSP = context.getSharedPreferences(AUTH_TOKEN_SP, Context.MODE_PRIVATE);
		this.authToken = userAuthTokenSP.getString(AUTH_TOKEN_STRING, "");
	}
	
	public boolean logout(){
		SharedPreferences.Editor editor = userAuthTokenSP.edit();
		editor.remove(AUTH_TOKEN_STRING);
		this.authToken = null;
		return editor.commit();
	}
	
	public boolean exists(){
		return userAuthTokenSP.contains(AUTH_TOKEN_STRING);
	}

	public String getAuthToken() {
		return authToken;
	}

	public void setAuthToken(String auth_token) {
		this.authToken = auth_token;
	}
	
	public boolean save() {
		SharedPreferences.Editor authEditor = userAuthTokenSP.edit();
		authEditor.putString(AUTH_TOKEN_STRING, authToken);
		return authEditor.commit();
	}
}
