package com.matesnetwork.callverification;

import android.content.Context;
import android.content.SharedPreferences;

public class CogPref{


	private final static String DU_PREFERENCE = "Emer_Preference";
	
	
	private SharedPreferences emerPreference ;
	
	
	public CogPref(Context context) {
		super();
		emerPreference = context.getSharedPreferences(DU_PREFERENCE, Context.MODE_PRIVATE);
	}

	/**
	 * Writes a string to shared preference
	 * @param context
	 * @param key
	 * @param value
	 */
	public void putString(String key, String value) {
		SharedPreferences.Editor editor = emerPreference.edit();
		editor.putString(key, value);
		
		editor.commit();
	}
	
	public void putInt(String key, int value) {
		SharedPreferences.Editor editor = emerPreference.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	/**
	 * Read from shared preference
	 * @param context
	 * @param key
	 * @return
	 */
	public String getString(String key) {

		String result = emerPreference.getString(key, null);
		return result;

	}
	public int getInt(String key) {

		int result = emerPreference.getInt(key, 0);
		return result;

	}
	
	/**
	 * Writes a boolean to shared preference
	 * @param context
	 * @param key
	 * @param value
	 */
	public void putBoolean(String key, boolean value) {
		SharedPreferences.Editor editor = emerPreference.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	/**
	 * Read boolean from shared preference
	 * @param context
	 * @param key
	 * @return
	 */
	public Boolean getBoolean(String key) {

		boolean result = emerPreference.getBoolean(key, false);
		return result;

	}
	public Boolean getBooleanDefTrue(String key) {

		boolean result = emerPreference.getBoolean(key, true);
		return result;

	}

}
