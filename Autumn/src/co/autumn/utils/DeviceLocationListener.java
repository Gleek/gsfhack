package co.autumn.utils;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.Toast;

public class DeviceLocationListener implements LocationListener {
	
	Context context;
	
	public DeviceLocationListener(Context context){
		this.context = context;
	}

	@Override
	public void onLocationChanged(Location location) {
		if(location != null)
			Toast.makeText(context, location+"", Toast.LENGTH_LONG).show();
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
   
}