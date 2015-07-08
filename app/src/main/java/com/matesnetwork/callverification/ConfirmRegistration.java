package com.matesnetwork.callverification;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

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
import android.telephony.TelephonyManager;
import android.util.Log;

class ConfirmRegistration extends AsyncTask<String, Void, JSONObject> {
private String sha;
private String app_id;
private String access_token;
private String mobile;
private Context context;

	public ConfirmRegistration(Context context,String tokenId,String sha,String app_id,String mobile) {
       this.access_token=tokenId;
       this.sha=sha;
       this.app_id=app_id;
       this.context=context;
       this.mobile= mobile;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected JSONObject doInBackground(String... params) {
		TelephonyManager tel = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
	    //String networkOperator = tel.getNetworkOperator();
        String networkOperator = tel.getSimOperator();

        int mcc = 0;
	    if (networkOperator != null) {
	         mcc = Integer.parseInt(networkOperator.substring(0, 3));
            //mcc = 219;

        }
		//
		final int TIMEOUT_MILLISEC = 10000;
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_MILLISEC);
		HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
		HttpClient httpclient = new DefaultHttpClient(httpParams);
		try {
		String url =Constants.BASE_URL+Constants.CONF_URL+"?"+"access_token="+access_token
				+"&app_id="+app_id
				+"&mobile="+mobile
				+"&mcc="+mcc
				+"&sha="+sha;
		Log.d("cognalys", "authenticating url ="+url);
		 URL urlr = new URL(url);
		HttpGet httpget = new HttpGet(urlr.toString());
		JSONObject inputJson = null; 

		

			//httpget.addHeader("Authorization", "Basic " + auth);

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
		

	}
@Override
protected void onPostExecute(JSONObject result) {
	
	super.onPostExecute(result);
	
	if (result != null) {
		
		try {
			
			if(result.get("status").equals("success") ) {
				
				Log.d("abx", "confirmation success id:"+result.getString("app_user_id"));
				Methods.setUserId(context, result.getString("app_user_id"));
				Methods.setaccessTok(context, result.getString("access_token"));
				Methods.setappId(context, result.getString("app_id"));
				
			} else 
				if(result.get("status").equals("failed") ) {
					Log.d("abx", "confirmation failed");
					
					
				} 	
		} catch (JSONException e) {
			e.printStackTrace();
		}
	} 
	
}



}
