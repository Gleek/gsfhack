package co.autumn.utils;

import org.json.JSONException;
import org.json.JSONObject;

public class ChatJSON {
	
	private Chat chat;

	public ChatJSON(Chat chat) {
		this.chat = chat;
	}

	public ChatJSON() {
	}

	public String getJSON(int start) throws JSONException {
		JSONObject chatJSON = new JSONObject();
		JSONObject chatObject = new JSONObject();
		chatObject.put("reciever_id", chat.getChatUserID());
		chatObject.put("message", chat.getMessage());
		chatJSON.put("chat", chatObject);
		chatJSON.put("start", start);
		chatJSON.put("auth_token", chat.getAuthToken());
		return chatJSON.toString();
	}

	public String getJSONForShow(String authToken, int start) throws JSONException {
		JSONObject chatJSON = new JSONObject();
		chatJSON.put("auth_token", authToken);
		chatJSON.put("start", start);
		return chatJSON.toString();
	}

}
