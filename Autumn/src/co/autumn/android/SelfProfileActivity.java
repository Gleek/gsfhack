package co.autumn.android;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import co.autumn.users.CurrentUser;
import co.autumn.users.User;
import co.autumn.utils.ImageRequest;
import co.autumn.utils.OnImageReqCompleted;
import co.autumn.utils.OnTaskCompleted;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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

public class SelfProfileActivity extends Activity {

	LayoutInflater inflater;
	JSONObject userDetails = new JSONObject();
	JSONArray statuses = new JSONArray();
	ImageView profilePic;
	ImageButton logOutButton, updateStatus;
	TextView userName, userJob, userEducation, recentStatus, header;
	CurrentUser user;
	
	String personName = "";
	
	StatusAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		user = new CurrentUser(getApplicationContext());
		setContentView(R.layout.self_profile_view);
		
		ImageButton backButton = (ImageButton) findViewById(R.id.back_btn);
	    backButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SelfProfileActivity.this.finish();
			}
		});
		
		profilePic = (ImageView) findViewById(R.id.profile_pic);
		logOutButton = (ImageButton) findViewById(R.id.logout);		
		userName = (TextView) findViewById(R.id.txt_name);
		userJob = (TextView) findViewById(R.id.txt_job);
		userEducation = (TextView) findViewById(R.id.txt_study);
		recentStatus = (TextView) findViewById(R.id.txt_status);
		header = (TextView) findViewById(R.id.txt_header);
		updateStatus = (ImageButton) findViewById(R.id.update_status_btn);
		updateStatus.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final Dialog statusDialog = new Dialog(SelfProfileActivity.this);
				statusDialog.setContentView(R.layout.status_update_dialog);
				statusDialog.setTitle("Update your Autumn Status");
				final EditText status = (EditText) statusDialog.findViewById(R.id.status_txt);
				Button cancelButton = (Button) statusDialog.findViewById(R.id.cancel_btn);
				Button postStatusButton = (Button) statusDialog.findViewById(R.id.post_btn);
				cancelButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						statusDialog.dismiss();
					}
				});
				postStatusButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if(status.getText().toString().length() < 5)
							Toast.makeText(getApplicationContext(), "Too short to be status.", Toast.LENGTH_SHORT).show();
						else{
							String statusText = status.getText().toString();
							try {
								user.createStatus(getApplicationContext(), statusText, new OnTaskCompleted() {
									@Override
									public void onTaskCompleted(JSONObject object) {
										Toast.makeText(getApplicationContext(), "Posted Status.", Toast.LENGTH_SHORT).show();
										statusDialog.dismiss();
									}
								});
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
				});
				statusDialog.show();
			}
		});
		
		ListView listStatuses = (ListView) findViewById(R.id.statuses);
		
		logOutButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				user.logout();
				SelfProfileActivity.this.finish();
			}
		});
		
		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
		adapter = new StatusAdapter();
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
						header.setText("Howdy, "+personName);
						userJob.setText(userDetails.getString(PROFILE_JOB));
						recentStatus.setText(userDetails.getString(PROFILE_STATUS));
						userEducation.setText(userDetails.getString(PROFILE_EDUCATION));
						req.execute(userDetails.getString(PROFILE_PIC_URL));
						getStatuses(new User(userDetails.getInt(PROFILE_USER_ID)));
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	protected void getStatuses(User user){
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
