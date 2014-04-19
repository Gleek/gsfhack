package co.autumn.android;


import java.util.Arrays;

import org.brickred.socialauth.Profile;
import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthError;
import org.brickred.socialauth.android.SocialAuthAdapter.Provider;
import org.brickred.socialauth.android.SocialAuthListener;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import co.autumn.users.CurrentUser;
import co.autumn.users.User;
import co.autumn.utils.OnTaskCompleted;

import static co.autumn.android.Constants.*;

import com.facebook.*;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;


public class MainActivity extends FragmentActivity {
	
	private UiLifecycleHelper uiHelper;
	SocialAuthAdapter linkedInAdapter;
	
	ImageButton viewPeopleBtn, viewSelfProfile;
	ImageButton linkedInLoginButton;
	LoginButton button;

	private Session.StatusCallback callback = new Session.StatusCallback() {
	    @Override
	    public void call(Session session, SessionState state, Exception exception) {
	        onSessionStateChange(session, state, exception);
	    }
	};
    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
        linkedInAdapter = new SocialAuthAdapter(new ResponseListener());
        final CurrentUser currentUser = new CurrentUser(this);
        button = (LoginButton) findViewById(R.id.login_button);
        viewSelfProfile = (ImageButton) findViewById(R.id.update_status_btn);
        linkedInLoginButton = (ImageButton) findViewById(R.id.btn_login_with_linkedin);
        viewPeopleBtn = (ImageButton) findViewById(R.id.nearby_btn);
    	
        if(currentUser.exists()){
        	button.setVisibility(View.INVISIBLE);
        	linkedInLoginButton.setVisibility(View.INVISIBLE);
        	viewSelfProfile.setVisibility(View.VISIBLE);
        	viewSelfProfile.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(MainActivity.this, SelfProfileActivity.class);
					startActivity(intent);
				}
			});
        	/*viewSelfProfile.
        	*/
        	viewPeopleBtn.setVisibility(View.VISIBLE);
        	viewPeopleBtn.setOnClickListener(new OnClickListener() {
    			@Override
    			public void onClick(View v) {
    				Intent peopleIntent = new Intent(MainActivity.this, PeopleNearbyActivity.class);
    				startActivity(peopleIntent);
    			}
    		});
        } else {
        	Session session = new Session(getApplicationContext());
        	session.closeAndClearTokenInformation();
//        	linkedInAdapter.signOut(Provider.LINKEftoasDIN.toString());
        	viewSelfProfile.setVisibility(View.INVISIBLE);
    	    viewPeopleBtn.setVisibility(View.INVISIBLE);
        	button.setVisibility(View.VISIBLE);
    	    button.setBackgroundResource(R.drawable.facebook);
    	    button.setReadPermissions(Arrays.asList("user_likes", "user_status", "email", "user_relationships", "user_education_history"));
    	  
    	    linkedInLoginButton.setVisibility(View.VISIBLE);
    	    linkedInLoginButton.setOnClickListener(new OnClickListener() {
    			@Override
    			public void onClick(View v) {
    				linkedInLogin();
    			}
    		});
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }
	
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    uiHelper = new UiLifecycleHelper(this, callback);
    uiHelper.onCreate(savedInstanceState);
  }
  
  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      uiHelper.onActivityResult(requestCode, resultCode, data);
  }

	private void onSessionStateChange(Session session, SessionState state,
				Exception exception) {
		if(session != null && session.isOpened()){
			Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

		        @Override
		        public void onCompleted(GraphUser user, Response response) {
		            if (user != null) {
		                final User newUser = new User(user);
		                try {
							newUser.create(getApplicationContext(), new OnTaskCompleted() {
								@Override
								public void onTaskCompleted(JSONObject authToken) {
					                if(authToken != null && authToken.has(AUTH_TOKEN_STRING)){
					                	CurrentUser currentUser = null;
										try {
											currentUser = new CurrentUser(getApplicationContext(), authToken.getString(AUTH_TOKEN_STRING));
											newUser.createWorkInfo(getApplicationContext(), currentUser.getAuthToken());
											newUser.createEducationInfo(getApplicationContext(), currentUser.getAuthToken());
										} catch (JSONException e) {
											e.printStackTrace();
										}
					                	currentUser.save();
					                	Intent peopleIntent = new Intent(MainActivity.this, PeopleNearbyActivity.class);
					            		startActivity(peopleIntent);
					                }
								}
							});
						} catch (JSONException e) {
							e.printStackTrace();
						}
		            }
		        }
		    });
		}
			
	}
	
	private void linkedInLogin(){
		linkedInAdapter = new SocialAuthAdapter(new ResponseListener());
		linkedInAdapter.authorize(MainActivity.this, Provider.LINKEDIN);
	}
	
	private final class ResponseListener implements DialogListener {

		@Override
		public void onComplete(Bundle values) {
			Log.d("Custom-UI", "Successful");
			linkedInAdapter.getUserProfileAsync(new ProfileDataListener());
		}

		@Override
		public void onError(SocialAuthError error) {
			Log.d("Custom-UI", "Error");
			error.printStackTrace();
		}

		@Override
		public void onCancel() {
			Log.d("Custom-UI", "Cancelled");
		}

		@Override
		public void onBack() {
			Log.d("Custom-UI", "Dialog Closed by pressing Back Key");

		}
	}
	
	
	protected class ProfileDataListener implements SocialAuthListener<Profile>{

		@Override
		public void onError(SocialAuthError arg0) {
		}

		@Override
		public void onExecute(String arg0, Profile profile) {
			final User newUser = new User(profile);
			try {
				newUser.create(getApplicationContext(), new OnTaskCompleted() {
					@Override
					public void onTaskCompleted(JSONObject authToken) {
						if(authToken != null && authToken.has(AUTH_TOKEN_STRING)){
				        	CurrentUser currentUser = null;
							try {
								currentUser = new CurrentUser(getApplicationContext(), authToken.getString(AUTH_TOKEN_STRING));
								currentUser.createInfoFromLinkedIn(getApplicationContext(), 
										linkedInAdapter.getCurrentProvider().getAccessGrant().getKey(), 
										linkedInAdapter.getCurrentProvider().getAccessGrant().getSecret());
							
							} catch (JSONException e) {
								e.printStackTrace();
							}
				        	currentUser.save();
				        	Intent peopleIntent = new Intent(MainActivity.this, PeopleNearbyActivity.class);
				    		startActivity(peopleIntent);
				        }
					}
				});
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}		
	}
}