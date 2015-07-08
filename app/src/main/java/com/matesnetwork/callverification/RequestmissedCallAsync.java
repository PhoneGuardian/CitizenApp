package com.matesnetwork.callverification;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
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

import com.matesnetwork.callverification.CallListnerHelper.CallHelperListner;

public class RequestmissedCallAsync extends AsyncTask<String, Void, JSONObject> {
	RequestMissedCallListnr requestMissedCallListner;
	Context context;
	String sha;
	String app_id;
	String access_token;
	String mobnumber;
	CallListnerHelper callListnerHelper;
	String[] numberArray;
    double lat;
    double lon;
	protected interface RequestMissedCallListnr {
		public String onRequestStart();

		public String[] onRequestSucess(String numb[]);

		public String onRequestFailed(ArrayList<String>  errorCodeList);
		public void onNumberVerified();
		public void onNumberNotVerified(ArrayList<String>  errorCodeList);
	}

	public RequestmissedCallAsync(Context context, String tokenId, String sha,
			String app_id, String mobnumber ,String[] numberArray,RequestMissedCallListnr requestMissedCallListner) {
		this.requestMissedCallListner = requestMissedCallListner;
		this.context = context;
		this.access_token = tokenId;
		this.sha = sha;
		this.app_id = app_id;
		this.mobnumber=mobnumber;
		this.numberArray=numberArray;
		lat=Methods.getLat(context);
		lon=Methods.getLon(context);
	}

	@Override
	protected void onPreExecute() {
		requestMissedCallListner.onRequestStart();
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
			int mnc = Integer.parseInt(networkOperator.substring(3));
           // mcc = 219;
		}
		final int TIMEOUT_MILLISEC = 10000;
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_MILLISEC);
		HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
		HttpClient httpclient = new DefaultHttpClient(httpParams);
		try {
			String url = Constants.BASE_URL + Constants.REQ_URL + "?"
					+ "access_token=" + access_token + "&app_id=" + app_id
					+ "&imei=" + Methods.getImei(context)
					+ "&brand_name=" + Methods.getDeviceName()
					+ "&model_number=" + Methods.getDeviceModelNumber()
					+ "&os_version=" + Methods.getOSVersion()
					+ "&gmail_id=" + Methods.getEmail(context)
					+ "&lat=" + lat
					+ "&lon=" + lon
					+ "&mcc=" + mcc +"&mobile="+mobnumber+ "&sha=" + sha;
			Log.d("cognalys", "authenticating url =" + url);
            String saferUrl = url.replace(" ", "%20");
            Log.d("cognalys", "safer authenticating url =" + saferUrl);

            URL urlr = new URL(saferUrl);
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
					return null;
					
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

				if (result.get("status").equals("success")) {
					
					String[] stringarray = null;
					Log.d("abx", "request missed call sucess ");
	                callListnerHelper= new CallListnerHelper(context, numberArray, new CallHelperListner() {
							
							@Override
							public void onVerificationSucess() {
								callListnerHelper.stop();
								requestMissedCallListner.onNumberVerified();
							}
							
							@Override
							public void onVerificationFailed() {
								callListnerHelper.stop();
								 ArrayList<String> errorCode = new ArrayList<String>();
								   errorCode.add("605");
								
								requestMissedCallListner.onNumberNotVerified(errorCode);
							}
						});
	                callListnerHelper.start();
					requestMissedCallListner.onRequestSucess(stringarray);
					
				} else if (result.get("status").equals("failed")) {
					
					ArrayList<String> errorList = new ArrayList<String>();     
					JSONArray jsonArray = result.getJSONArray("codes"); 
					if (jsonArray != null) { 
					   int len = jsonArray.length();
					   for (int i=0;i<len;i++){ 
						   errorList.add(jsonArray.get(i).toString());
					   } 
					} 
					requestMissedCallListner.onRequestFailed(errorList);

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else{
			 ArrayList<String> errorCode = new ArrayList<String>();
			   errorCode.add("606");
			requestMissedCallListner.onNumberNotVerified(errorCode);
		}

	}
}
