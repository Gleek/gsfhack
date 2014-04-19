package co.autumn.utils;



import java.net.URL;
import java.net.URLConnection;


import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

public class ImageRequest extends AsyncTask<String, Void, Drawable>{

	String pricURL;
	OnImageReqCompleted listener;
	
	public ImageRequest(OnImageReqCompleted onTaskCompleted) {
		this.listener = onTaskCompleted;
	}

	@Override
	protected Drawable doInBackground(String... urls) {
		Drawable image = null;
		URL url;
		Log.d("in doinbg", urls[0]);
		try {
			url = new URL(urls[0]);
			URLConnection connection = url.openConnection();
			connection.setUseCaches(true);
			image = Drawable.createFromStream(connection.getInputStream(), "sdsd");			 
		} catch (Exception e) {
			Log.d("error - bg", e.toString());
			e.printStackTrace();
		}
		return image;
	}
	

	@Override
	protected void onProgressUpdate(Void... values) {
		super.onProgressUpdate(values);
	}

	@Override
	protected void onPostExecute(Drawable result) {
		super.onPostExecute(result);
		if(result != null)
			listener.onTaskCompleted(result);
	}

}
