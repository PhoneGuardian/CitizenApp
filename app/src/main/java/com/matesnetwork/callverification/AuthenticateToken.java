package com.matesnetwork.callverification;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.util.Log;

public class AuthenticateToken extends AsyncTask<String, Void, JSONObject> {
	AuthenticateListner verifyTokenListner;
String sha;
String app_id;
String access_token;
String mcc;
Context context;
	protected interface AuthenticateListner {
		//public String onVerifyStart();
		public String[] onVerifySucess(String[] numberArray);
		public String onVerifyFailed(ArrayList<String> errorList);
	}

	public AuthenticateToken(Context context,String tokenId,String sha,String app_id,AuthenticateListner verifyTokenListner) {
		this.verifyTokenListner = verifyTokenListner;
       this.access_token=tokenId;
       this.sha=sha;
       this.app_id=app_id;
       this.context=context;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected JSONObject doInBackground(String... params) {
		TelephonyManager tel = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
	    String networkOperator = tel.getNetworkOperator();
	    int mcc = 0;
	    if (networkOperator != null) {
	         mcc = Integer.parseInt(networkOperator.substring(0, 3));
	       // int mnc = Integer.parseInt(networkOperator.substring(3));
	    }
		final int TIMEOUT_MILLISEC = 10000;
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_MILLISEC);
		HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
		HttpClient httpclient = new DefaultHttpClient(httpParams);
		try {
		String url =Constants.BASE_URL+Constants.AUTH_URL+"?"+"access_token="+access_token+"&app_id="+app_id+"&mcc="+mcc+"&sha="+sha;
		Log.d("cognalys", "authenticating url ="+url);
		 URL urlr = new URL(url);
		HttpGet httpget = new HttpGet(urlr.toString());
		JSONObject inputJson = null;

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

		}  catch (Exception e) {
			return null;
		} 

	}
@Override
protected void onPostExecute(JSONObject result) {
	
	super.onPostExecute(result);
	
	if (result != null) {
		
		try {
			
			if(result.get("status").equals("success") ) {
				
				Log.d("abx", "success");
				if (result.has("numbers")) {
					JSONArray jNumber=result.getJSONArray("numbers");
					 String[] stringarray = new String[jNumber.length()]; //{"",""};//jsonArray.t;
						
						for (int i=0;i<jNumber.length();i++) {
							stringarray[i]=	jNumber.getString(i);
						}
					verifyTokenListner.onVerifySucess(stringarray);
				}	
				
			} else 
				if(result.get("status").equals("failed") ) {
					Log.d("abx", "failed");
					ArrayList<String> errorList = new ArrayList<String>();     
					JSONArray jsonArray = result.getJSONArray("codes"); 
					if (jsonArray != null) { 
					   int len = jsonArray.length();
					   for (int i=0;i<len;i++){ 
						   errorList.add(jsonArray.get(i).toString());
					   } 
					} 
					verifyTokenListner.onVerifyFailed(errorList);
					
				} 	
		} catch (JSONException e) {
			e.printStackTrace();
		}
	} else{
		
	}
	
}
	
}
