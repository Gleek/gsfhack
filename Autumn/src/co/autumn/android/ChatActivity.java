package co.autumn.android;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import co.autumn.users.CurrentUser;
import co.autumn.utils.Chat;
import co.autumn.utils.ImageRequest;
import co.autumn.utils.OnImageReqCompleted;
import co.autumn.utils.OnTaskCompleted;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import static co.autumn.android.Constants.*;



public class ChatActivity extends Activity implements OnClickListener{
	
	ListView chatListView;
	Button sendMessageButton;
	EditText messageBox;
	String profilePicURL;
	TextView otherPersonName;
	
	LayoutInflater inflator;
	ChatAdapter chatAdapter;
	int otherUserID;
	int chatTilID = 0;
	CurrentUser currentUser;
	Chat userChat;
	Drawable userProfilePic= null, otherUserProfilePic= null;
	
	JSONArray userChats = new JSONArray();
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		otherUserID = getIntent().getExtras().getInt(CHAT_USER_ID);
		profilePicURL = getIntent().getExtras().getString(CHAT_USER_PROFILE_PIC);

		setContentView(R.layout.chat_view);
		otherPersonName = (TextView) findViewById(R.id.other_user_name);
		try{
			otherPersonName.setText(getIntent().getExtras().getString(CHAT_USER_NAME, ""));
		} catch(Exception e){
			e.printStackTrace();
		}
		try {
			setChatDetails();
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		
		ImageButton backButton = (ImageButton) findViewById(R.id.back_btn);
	    backButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ChatActivity.this.finish();
			}
		});
		inflator = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		chatAdapter = new ChatAdapter();
		chatListView = (ListView) findViewById(R.id.chat_list_view);
		sendMessageButton = (Button) findViewById(R.id.send_message_btn);
		messageBox = (EditText) findViewById(R.id.message_edt_box);
		
		currentUser = new CurrentUser(getApplicationContext());
		
		try {
			setUpPrevChat();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		sendMessageButton.setOnClickListener(this);
		chatListView.setAdapter(chatAdapter);
		Timer loadChatsTimer = new Timer();
		loadChatsTimer.scheduleAtFixedRate(new ChatTask(), 10000, 20000);
	}

	private void setChatDetails() throws JSONException {
		CurrentUser user = new CurrentUser(getApplicationContext());
		user.getChatMeta(getApplicationContext(), otherUserID, new OnTaskCompleted() {
			@Override
			public void onTaskCompleted(JSONObject meta) {
				ImageRequest req = new ImageRequest(new OnImageReqCompleted() {
					@Override
					public void onTaskCompleted(Drawable object) {
						userProfilePic = object;
					}
				});
				try {
					req.execute(meta.getJSONObject("user").getString("profile_pic_url"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				ImageRequest req1 = new ImageRequest(new OnImageReqCompleted() {
					@Override
					public void onTaskCompleted(Drawable object) {
						otherUserProfilePic = object;
					}
				});
				try {
					req1.equals(meta.getJSONObject("other_user").getString("profile_pic_url"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
	
	private class ChatAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return userChats.length();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressWarnings("finally")
		@Override
		public View getView(int position, View view, ViewGroup parent) {
			JSONObject chat;
			try {
				chat = userChats.getJSONObject(position);
				boolean isOtherUserMessage = chat.getInt("sender_id") == otherUserID;
				final ViewHolder holder = new ViewHolder();
				if(isOtherUserMessage){
					view = inflator.inflate(R.layout.other_chat_list, null);
					holder.chat = (TextView) view.findViewById(R.id.chat);
					holder.chat.setText(chat.getString("message"));
					
					holder.profilePic = (ImageView) view.findViewById(R.id.profile_pic);
					/*ImageRequest req = new ImageRequest(new OnImageReqCompleted() {
						
						@Override
						public void onTaskCompleted(Drawable object) {
							holder.profilePic.setImageDrawable(object);
						}
					});
					req.execute(profilePicURL);*/
					if(otherUserProfilePic != null)
						holder.profilePic.setImageDrawable(otherUserProfilePic);
				}
				else{
					view = inflator.inflate(R.layout.self_chat_list, null);
					
					holder.chat = (TextView) view.findViewById(R.id.chat);
					holder.chat.setText(chat.getString("message"));	
					holder.profilePic = (ImageView) view.findViewById(R.id.self_profile_pic);
					if(userProfilePic != null)
						holder.profilePic.setImageDrawable(userProfilePic);
				}	
				
			} catch (JSONException e) {
				Log.d("Image", e.toString());
				e.printStackTrace();
			}
			finally{
				return view;
			}
			
		}
		
		class ViewHolder {
			TextView chat;
			ImageView profilePic;
		}
		
	}
	
	private class ChatTask extends TimerTask {
		@Override
		public void run() {
			
			try {
				userChat.show(getApplicationContext(), chatTilID, new OnTaskCompleted() {
					@Override
					public void onTaskCompleted(JSONObject chats) {
						try {
						JSONArray newChats = chats.getJSONArray(CHATS_STRING);
						if(newChats.length() > 0){
							chatTilID = chats.getInt(CHATS_TIL_STRING);
							JSONAdd(userChats, newChats);
							runOnUiThread(new Runnable() {
							    @Override
							    public void run() {
							    	chatAdapter.notifyDataSetChanged();
							    }
							});
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	private void JSONAdd(JSONArray userChats, JSONArray newChats) throws JSONException {
		for(int i=0;i<newChats.length();i++)
			userChats.put(newChats.get(i));
	}
	
	private void setUpPrevChat() throws JSONException {
		userChat = new Chat(otherUserID, currentUser.getAuthToken());
		userChat.show(getApplicationContext(), chatTilID, new OnTaskCompleted() {
			@Override
			public void onTaskCompleted(JSONObject chats) {
				messageBox.clearFocus();
				try {
					userChats = chats.getJSONArray(CHATS_STRING);
					chatTilID = chats.getInt(CHATS_TIL_STRING);
					chatAdapter.notifyDataSetChanged();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void onClick(View view) {
		final Button v = (Button) view;
		if(messageBox.getText().toString().length() < 2)
			Toast.makeText(getApplicationContext(), "Too Short to be message!", Toast.LENGTH_SHORT).show();
		else{
			v.setEnabled(false);
			userChat= new Chat(otherUserID, currentUser.getAuthToken(), messageBox.getText().toString());
			try {
				userChat.create(getApplicationContext(), chatTilID, new OnTaskCompleted() {
					@Override
					public void onTaskCompleted(JSONObject newChat) {
						try {
							if(newChat.getString("status").equalsIgnoreCase("success")) {
								JSONArray newChats = newChat.getJSONArray(CHATS_STRING);
								if(newChats.length() > 0){
									chatTilID = newChat.getInt(CHATS_TIL_STRING);
									JSONAdd(userChats, newChats);
									chatAdapter.notifyDataSetChanged();
								}
								messageBox.setText("");
								v.setEnabled(true);
							}
							else
								Toast.makeText(getApplicationContext(), "Oops. Please try again!", Toast.LENGTH_SHORT).show();
						} catch (JSONException e) {
							Toast.makeText(getApplicationContext(), "Oops. Please try again!", Toast.LENGTH_SHORT).show();
							e.printStackTrace();
						}
					}
				});
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}
