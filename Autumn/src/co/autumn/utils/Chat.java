package co.autumn.utils;

import static co.autumn.android.Constants.SERVER_ADDRESS;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class Chat {
	
	private int chatUserID;
	private String authToken;
	private String message;
	
	
	
	public Chat(int chatUserID, String authToken, String message) {
		this.chatUserID = chatUserID;
		this.authToken = authToken;
		this.message = message;
	}

	public Chat(int chatUserID, String authToken) {
		this.chatUserID = chatUserID;
		this.authToken = authToken;
	}

	public void create(Context context, int start, OnTaskCompleted listener) throws JSONException{
		ChatJSON json = new ChatJSON(this);
		String url = SERVER_ADDRESS+"chats.json";
		AsyncRequest request = new AsyncRequest(context, url, "POST", json.getJSON(start), listener);
		request.execute(url);
	}
	
	public void show(Context context, int start, OnTaskCompleted listener) throws JSONException{
		String url = SERVER_ADDRESS+"chats/"+chatUserID+".json";
		ChatJSON json = new ChatJSON();
		AsyncRequest request = new AsyncRequest(context, url, "POST", json.getJSONForShow(authToken, start), listener);
		request.execute(url);
	}
	

	public int getChatUserID() {
		return chatUserID;
	}

	public void setChatUserID(int chatUserID) {
		this.chatUserID = chatUserID;
	}

	public String getAuthToken() {
		return authToken;
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
