package com.matesnetwork.callverification;

import java.security.MessageDigest;
import java.util.ArrayList;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

public class Methods {
	static Methods methods;
	
	public static Methods getInstance(){
		if (methods==null) {
			methods= new  Methods();
		}
		return methods;
		
	}
	
	protected   static String getImei( Context context){
		String identifier = null;
		TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		if (tm != null)
		      identifier = tm.getDeviceId();
		if (identifier == null || identifier .length() == 0)
		      identifier = Secure.getString(context.getContentResolver(),Secure.ANDROID_ID); 
		return identifier;
	}
	
	protected static String getSHA(Context context) {
		String sign=null;
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				 sign = Base64	.encodeToString(md.digest(), Base64.DEFAULT);
				Log.e("key:", sign);
			}
		} catch (Exception e) {
			Log.e("key:", e.toString());
		} 

		return sign;}
	protected String getMCC(Context context){return null;}

	protected static String getOSVersion(){
		return Build.VERSION.RELEASE;
		}

	protected static String getEmail(Context context) {
        AccountManager accountManager = AccountManager.get(context); 
        Account account = getAccount(accountManager);

        if (account == null) {
          return null;
        } else {
          return account.name;
        }
      }	
	private static Account getAccount(AccountManager accountManager) {
        Account[] accounts = accountManager.getAccountsByType("com.google");
        Account account;
        if (accounts.length > 0) {
          account = accounts[0];      
        } else {
          account = null;
        }
        return account;
      }
	
	protected static String getDeviceName() {
		  String manufacturer = Build.MANUFACTURER;
		 /* String model = Build.MODEL;
		  if (model.startsWith(manufacturer)) {
		    return capitalize(model);
		  } else {*/
		    return capitalize(manufacturer);// + " " + model;
		 // }
		}
	protected static String getDeviceModelNumber() {
		 // String manufacturer = Build.MANUFACTURER;
		  String model = Build.MODEL;
		/*  if (model.startsWith(manufacturer)) {
		    return capitalize(model);
		  } else {
		    return capitalize(manufacturer) + " " + model;
		  }*/
		  return model;
		}

		private static String capitalize(String s) {
		  if (s == null || s.length() == 0) {
		    return "";
		  }
		  char first = s.charAt(0);
		  if (Character.isUpperCase(first)) {
		    return s;
		  } else {
		    return Character.toUpperCase(first) + s.substring(1);
		  }
		}
		
	protected static double getLat(Context context){
		  GPSTracker tracker = new GPSTracker(context);
		  return tracker.getLatitude();
	}
	protected static double getLon(Context context){
		  GPSTracker tracker = new GPSTracker(context);
		  return tracker.getLongitude();
	}
	
	protected static String getUserId(Context context){
		CogPref  cogPref = new CogPref(context);
		return cogPref.getString("userID");
	}
	protected static void setUserId(Context context,String id){
		CogPref  cogPref = new CogPref(context);
		 cogPref.putString("userID",id);
	}
	protected static String getappId(Context context){
		CogPref  cogPref = new CogPref(context);
		return cogPref.getString("appID");
	}
	protected static void setappId(Context context,String id){
		CogPref  cogPref = new CogPref(context);
		 cogPref.putString("appID",id);
	}
	protected static String getaccessTok(Context context){
		CogPref  cogPref = new CogPref(context);
		return cogPref.getString("accessTok");
	}
	protected static void setaccessTok(Context context,String id){
		CogPref  cogPref = new CogPref(context);
		 cogPref.putString("accessTok",id);
	}
protected static void getError(ArrayList<String> errorCodeList){
	String message="";
	for (String code : errorCodeList) {
		if (TextUtils.equals(code, "601")) {
			message=message+" Invalid phone number format";
		}
		if (TextUtils.equals(code, "602")) {
			message=message+"Invalid access token ";
		}
		if (TextUtils.equals(code, "603")) {
			message=message+" Invalid app id";
		}
		if (TextUtils.equals(code, "604")) {
			message=message+" Invalid phone number format";
		}
		
	}
	
	
}
	
}
