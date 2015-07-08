package com.matesnetwork.callverification;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;


public class SendLocationInBG extends AsyncTask<String, Void, JSONObject> {
	private Context context;
	private String access_token;
	private String app_id;
	private double lat;
	private double lon;
	private String app_user_id;
	boolean locEnable;
	boolean activeEnable;
	public SendLocationInBG(Context context,String access_token,String app_id,String userID,boolean locEnable,boolean activeEnable) {

		this.context = context;
		this.access_token=access_token;
		this.app_id = app_id;
		this.app_user_id=userID;
		this.locEnable=locEnable;
		this.activeEnable=activeEnable;
		if (locEnable) {
			lat=Methods.getLat(context);
			lon=Methods.getLon(context);
		}else{
			lat=0.0;
			lon=0.0;
		}
		
	}
	@Override
	protected void onPreExecute() {
		super.onPreExecute();


	}

	@Override
	protected JSONObject doInBackground(String... arg0) {

		final int TIMEOUT_MILLISEC = 10000;
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_MILLISEC);
		HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
		HttpClient httpclient = new DefaultHttpClient(httpParams);
		try {
			String url = Constants.BASE_URL + Constants.LOCUPDATE_URL + "?"
					+ "access_token=" + access_token
					+ "&app_id=" + app_id
					+ "&lat=" + lat
					+ "&lon=" + lon
				    + "&app_user_id=" + app_user_id
					+ "&sha=" + Methods.getSHA(context);
			Log.d("cognalys", "daily ping url =" + url);
			URL urlr = new URL(url);
			HttpGet httpget = new HttpGet(urlr.toString());
			JSONObject inputJson = null;

			// httpget.addHeader("Authorization", "Basic " + auth);

			HttpResponse response = httpclient.execute(httpget);

			HttpEntity entity = response.getEntity();

			if (entity != null) {

				Log.e("entity", "::" + entity);
				InputStream instream = entity.getContent();

				String result = RestClient.convertStreamToString(instream);

				Log.e("String result", result);

				try {
					inputJson = new JSONObject(result);
				} catch (JSONException e) {
					e.printStackTrace();
				}

			} else
				Log.e("Entity", "null");

			return inputJson;

		} catch (ClientProtocolException e) {
			return null;
		} catch (IOException e) {
			return null;
		}

		//

	}

	@Override
	protected void onPostExecute(JSONObject result) {
		super.onPostExecute(result);
		if (result != null) {
			try {

				if(result.get("status").equals("success") ) {
					
					//Log.d("abx", "confirmation success id:"+result.getString("app_user_id"));
					//Methods.setUserId(context, result.getString("app_user_id"));
				    //set alarm here if not already running
					
				} else 
					if(result.get("status").equals("failed") ) {
						Log.d("abx", "confirmation failed");
						
						
					} 	

				
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	private String getDateTimeString() {

		DateFormat formatter = new SimpleDateFormat("ddMMyyyy",Locale.US);

		Calendar calendar = Calendar.getInstance();
		String date = formatter.format(calendar.getTime());

		formatter = new SimpleDateFormat("HHmm",Locale.US);

		String time = formatter.format(calendar.getTime());

		return date.concat(time);
	}
}
	
	
