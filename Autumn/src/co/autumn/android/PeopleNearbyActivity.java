package co.autumn.android;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;

import static co.autumn.android.Constants.*;

import co.autumn.users.CurrentUser;
import co.autumn.utils.DeviceLocationListener;
import co.autumn.utils.ImageRequest;
import co.autumn.utils.LocationUtil;
import co.autumn.utils.OnImageReqCompleted;
import co.autumn.utils.OnTaskCompleted;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PeopleNearbyActivity extends Activity implements 
	GooglePlayServicesClient.ConnectionCallbacks,
	GooglePlayServicesClient.OnConnectionFailedListener
{
	
	LocationClient mLocationClient;
	
	@Override
	protected void onStart() {
		super.onStart();
		mLocationClient.connect();
	}

	@Override
	protected void onStop() {
		super.onStop();
		mLocationClient.disconnect();
	}

	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

	CurrentUser currentUser;
	LocationManager locationManager;
	Location lastKnownLocation;
	DeviceLocationListener locationListener;
	JSONArray nearbyUsers = new JSONArray();
	PeopleAdapter peopleAdapter = new PeopleAdapter();

	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.people_nearby);
	    ListView people_nearby = (ListView) findViewById(R.id.list_people_nearby);
	    people_nearby.setAdapter(peopleAdapter);
	    mLocationClient = new LocationClient(this, this, this);
	    
		ImageButton backButton = (ImageButton) findViewById(R.id.back_btn);
	    backButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				PeopleNearbyActivity.this.finish();
			}
		});
	}
	
	private void showNearbyPeople() throws JSONException {
	   	lastKnownLocation = mLocationClient.getLastLocation();
	    
	    currentUser = new CurrentUser(getApplicationContext());
	    if(lastKnownLocation != null){
	    	LocationUtil location = new LocationUtil(lastKnownLocation, currentUser);
		    currentUser.nearby(getApplicationContext(), location.getJSON(), new OnTaskCompleted() {
				
				@Override
				public void onTaskCompleted(JSONObject nearby) {
					try {
						nearbyUsers = nearby.getJSONArray("users");
					} catch (JSONException e) {
						e.printStackTrace();
					}
					peopleAdapter.notifyDataSetChanged();
				}
			});
		}else{
			Toast.makeText(getApplicationContext(), "Location can't be predicted.", Toast.LENGTH_LONG).show();
		}
	    Button btn = (Button) findViewById(R.id.btn_map);
	    btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent newIntent = new Intent(PeopleNearbyActivity.this, MapTest.class);
				newIntent.putExtra(USER_LAT, lastKnownLocation.getLatitude());
				newIntent.putExtra(USER_LONG, lastKnownLocation.getLongitude());
				startActivity(newIntent);
			}
		});
	}

	public class PeopleAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return nearbyUsers.length();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) PeopleNearbyActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if(view == null)
				view = inflater.inflate(R.layout.list_view_people_nearby, null);
			final ViewHolder holder = new ViewHolder();
			try {
				final JSONObject user = (JSONObject) nearbyUsers.get(position);
				final int userID = user.getInt("user_id");
				final JSONObject userLocation = user.getJSONObject("location");
//				final String pic_url = user.getString("profile_pic_url");
				holder.container = (LinearLayout) view.findViewById(R.id.main_layout);
				holder.name = (TextView) view.findViewById(R.id.txt_name);
				holder.status = (TextView) view.findViewById(R.id.txt_status);
				holder.profileDetail = (TextView) view.findViewById(R.id.txt_user_detail);
				holder.lastKnownTime = (TextView) view.findViewById(R.id.txt_last_known_time);
				holder.profilePic = (ImageView) view.findViewById(R.id.profile_pic);
				holder.mapView = (ImageView) view.findViewById(R.id.map_view);
				
				holder.name.setText(user.getString("name"));
				holder.status.setText(user.getString("status"));
				JSONObject desc = new JSONObject(user.getString("description"));
				holder.profileDetail.setText(desc.getString("status"));
				String type = desc.getString("type");
				
				if(type.equalsIgnoreCase("job"))
					holder.profileDetail.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_job, 0, 0, 0);
				else if(type.equalsIgnoreCase("degree"))
					holder.profileDetail.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_study, 0, 0, 0);
				
				ImageRequest req = new ImageRequest(new OnImageReqCompleted() {
					
					@Override
					public void onTaskCompleted(Drawable object) {
						holder.profilePic.setImageDrawable(object);
					}
				});
				req.execute(user.getString("profile_pic_url"));
				
				holder.mapView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(PeopleNearbyActivity.this, MapTest.class);
						intent.putExtra(USER_LAT, lastKnownLocation.getLatitude());
						intent.putExtra(USER_LONG, lastKnownLocation.getLongitude());
						
						intent.putExtra(PROFILE_USER_ID, userID);
						try {
							intent.putExtra(OTHER_LAT, userLocation.getDouble("latitude"));
							intent.putExtra(OTHER_LONG, userLocation.getDouble("longitude"));
							intent.putExtra(OTHER_USER, user.toString());
						} catch (JSONException e) {
							e.printStackTrace();
						}
						startActivity(intent);
					}
				});
				holder.container.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(PeopleNearbyActivity.this, ProfileActivity.class);
						intent.putExtra(PROFILE_USER_ID, userID);
						startActivity(intent);
					}
				});
			} catch (JSONException e) {
				Log.d("view error", e.toString());
			}
			return view;
		}
		
		class ViewHolder {
			LinearLayout container;
			TextView name, status, profileDetail, lastKnownTime;
			ImageView profilePic, mapView;
		}
		
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
//            showErrorDialog(connectionResult.getErrorCode());
        }		
	}

	@Override
	public void onConnected(Bundle arg0) {
		Log.d("on connected", "connected");
		 try {
				showNearbyPeople();
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	@Override
	public void onDisconnected() {
		
	}
}
