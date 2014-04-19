package co.autumn.android;

import android.content.Context;
import co.autumn.utils.AsyncRequest;
import co.autumn.utils.OnTaskCompleted;


import static co.autumn.android.Constants.*;

public class LinkedInClient {
	private String accessToken;
	private String tokenSecret;
	private Context context;
	
	public LinkedInClient(Context context, String accessToken, String tokenSecret) {
		this.context = context;
		this.accessToken = accessToken;
		this.tokenSecret = tokenSecret;
	}
	
	public void getEduInfo( OnTaskCompleted listener){
		AsyncRequest request = new AsyncRequest(context, LINKEDIN_EDU_URL, "OAUTH_GET", "", listener);
		request.execute(LINKEDIN_EDU_URL, accessToken, tokenSecret);
	}

	public void getWorkInfo(OnTaskCompleted listener) {
		AsyncRequest request = new AsyncRequest(context, LINKEDIN_WORK_URL, "OAUTH_GET", "", listener);
		request.execute(LINKEDIN_WORK_URL, accessToken, tokenSecret);
	}
}
