package co.autumn.android;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import static co.autumn.android.Constants.*;

import co.autumn.users.CurrentUser;
import co.autumn.utils.ImageRequest;
import co.autumn.utils.LocationUtil;
import co.autumn.utils.OnImageReqCompleted;
import co.autumn.utils.OnTaskCompleted;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapTest extends FragmentActivity {
  private GoogleMap map;
  private LatLng userLocation, otherUserLocation;
  private JSONArray nearbyUsers = new JSONArray();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.map_test);
    
    ImageButton backButton = (ImageButton) findViewById(R.id.back_btn);
    backButton.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
			MapTest.this.finish();
		}
	});
    
    Bundle bundle = getIntent().getExtras();
    map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
	        .getMap();
    if(map != null){
	    if(bundle.getInt(PROFILE_USER_ID, -1) == -1){
		    userLocation = new LatLng(bundle.getDouble(USER_LAT), bundle.getDouble(USER_LONG));
		    map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
		    CurrentUser currentUser = new CurrentUser(getApplicationContext());
		    if(userLocation != null){
		    	LocationUtil location = new LocationUtil(userLocation, currentUser);
			    try {
					currentUser.nearby(getApplicationContext(), location.getJSON(), new OnTaskCompleted() {		
						@Override
						public void onTaskCompleted(JSONObject nearby) {
							try {
								nearbyUsers = nearby.getJSONArray("users");
							} catch (JSONException e) {
								e.printStackTrace();
							}
							addMarkersOverMap();
						}
					});
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}else{
				Toast.makeText(getApplicationContext(), "Location can't be predicted.", Toast.LENGTH_LONG).show();
			}
		    // Zoom in, animating the camera.
//		    map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
	    }
	    else{
	    	userLocation = new LatLng(bundle.getDouble(USER_LAT), bundle.getDouble(USER_LONG));
	    	otherUserLocation = new LatLng(bundle.getDouble(OTHER_LAT), bundle.getDouble(OTHER_LONG));
	    	JSONObject user = null;
			try {
				user = new JSONObject(getIntent().getExtras().getString(OTHER_USER));
			} catch (JSONException e) {
				e.printStackTrace();
			}
	    	if(userLocation != null && otherUserLocation != null) {
	    		map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
	    		nearbyUsers = new JSONArray();
	    		nearbyUsers.put(user);
	    		addMarkersOverMap();
	    	}
	    }
    }else{
    	Toast.makeText(getApplicationContext(), "Map Couldn't be loaded.", Toast.LENGTH_LONG).show();
    }
  }
  private void addMarkersOverMap() {
	  
	 final HashMap<Marker, JSONObject> people = new HashMap<Marker, JSONObject>();
	 
	 for(int i=0; i<nearbyUsers.length();i++){
		 try {
			JSONObject user = nearbyUsers.getJSONObject(i);
			 JSONObject location = user.getJSONObject("location");
			 final LatLng userLatLng = new LatLng(location.getDouble("latitude"), location.getDouble("longitude"));
			 final Marker userMarker = map.addMarker(
					 new MarkerOptions()
					 	.position(userLatLng)
					 	.title(user.getString("name"))
					 );
			 people.put(userMarker, user);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		 map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
			@Override
			public void onInfoWindowClick(Marker marker) {
				Intent intent = new Intent(MapTest.this, ProfileActivity.class);
				Log.d("log", "asd");
				try {
					intent.putExtra(PROFILE_USER_ID, people.get(marker).getInt("user_id"));
					startActivity(intent);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
			
		 map.setInfoWindowAdapter(new InfoWindowAdapter() {
			 
				class ViewHolder {
					TextView name, profileDetail;
					ImageView profilePic;
				}
				
				@Override
				public View getInfoWindow(Marker marker) {
					LayoutInflater inflater = (LayoutInflater) MapTest.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					View view = inflater.inflate(R.layout.list_people_on_map, null);
					final ViewHolder holder = new ViewHolder();
					
					holder.name = (TextView) view.findViewById(R.id.txt_name);
					holder.profileDetail = (TextView) view.findViewById(R.id.txt_user_detail);
					holder.profilePic = (ImageView) view.findViewById(R.id.profile_pic);
				
					try {
						holder.name.setText(people.get(marker).getString("name"));
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
					try{
						holder.profileDetail.setText(people.get(marker).getJSONObject("description").getString("status"));
					} catch(JSONException e){
						e.printStackTrace();
					}
					
					
					try {
						ImageRequest req = new ImageRequest(new OnImageReqCompleted() {
							@Override
							public void onTaskCompleted(Drawable object) {
								holder.profilePic.setImageDrawable(object);
							}
						});
						req.execute(people.get(marker).getString("profile_pic_url"));
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
					return view;
				}
				
				@Override
				public View getInfoContents(Marker arg0) {
					return null;
				}
			}); 
	 }
  }
}

