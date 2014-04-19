package co.autumn.android;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import co.autumn.users.User;
import co.autumn.utils.ImageRequest;
import co.autumn.utils.OnImageReqCompleted;
import co.autumn.utils.OnTaskCompleted;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import static co.autumn.android.Constants.*;

public class ProfileActivity extends Activity {

	LayoutInflater inflater;
	int userID = 1;
	JSONObject userDetails = new JSONObject();
	JSONArray statuses = new JSONArray();
	ImageView profilePic;
	ImageButton chatBtn;
	TextView userName, userJob, userEducation, recentStatus, header;
	
	String personName = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile_view);
		
		ImageButton backButton = (ImageButton) findViewById(R.id.back_btn);
	    backButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ProfileActivity.this.finish();
			}
		});
		
		userID = getIntent().getExtras().getInt(PROFILE_USER_ID);
		User user = new User(userID);
		
		profilePic = (ImageView) findViewById(R.id.profile_pic);
		chatBtn = (ImageButton) findViewById(R.id.chat);
		
		chatBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent chatIntent = new Intent(ProfileActivity.this, ChatActivity.class);
				chatIntent.putExtra(CHAT_USER_ID, userID);
				chatIntent.putExtra(CHAT_USER_NAME, personName);
				startActivity(chatIntent);
			}
		});
		
		userName = (TextView) findViewById(R.id.txt_name);
		userJob = (TextView) findViewById(R.id.txt_job);
		userEducation = (TextView) findViewById(R.id.txt_study);
		recentStatus = (TextView) findViewById(R.id.txt_status);
		header = (TextView) findViewById(R.id.txt_header);
		
		ListView listStatuses = (ListView) findViewById(R.id.statuses);
		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
		final StatusAdapter adapter = new StatusAdapter();
		listStatuses.setAdapter(adapter);
		
		try{
			final ImageRequest req = new ImageRequest(new OnImageReqCompleted() {
				@Override
				public void onTaskCompleted(Drawable object) {
					profilePic.setImageDrawable(object);
				}
			});
			user.show(getApplicationContext(), new OnTaskCompleted() {
				@Override
				public void onTaskCompleted(JSONObject userDetails) {
					try {
						personName = userDetails.getString(PROFILE_USER_NAME);
						userName.setText(personName);
						header.setText(userDetails.getString(PROFILE_USER_NAME));
						userJob.setText(userDetails.getString(PROFILE_JOB));
						recentStatus.setText(userDetails.getString(PROFILE_STATUS));
						userEducation.setText(userDetails.getString(PROFILE_EDUCATION));
						req.execute(userDetails.getString(PROFILE_PIC_URL));
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});
			user.getStatuses(getApplicationContext(), new OnTaskCompleted() {
				@Override
				public void onTaskCompleted(JSONObject object) {
					try {
						statuses = object.getJSONArray("statuses");
					} catch (JSONException e) {
						e.printStackTrace();
					}
					adapter.notifyDataSetChanged();
				}
			});
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	protected class StatusAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return statuses.length();
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			if(view == null){
				view = inflater.inflate(R.layout.status_detail, parent, false);
			}
			ViewHolder holder = new ViewHolder();
			holder.chat = (TextView) view.findViewById(R.id.txt_status);
			try {
				holder.chat.setText(statuses.getString(position));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return view;
		}
		
		class ViewHolder {
			TextView chat;
		}
		
	}
}
