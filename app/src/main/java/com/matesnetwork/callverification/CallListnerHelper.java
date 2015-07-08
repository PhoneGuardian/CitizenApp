package com.matesnetwork.callverification;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.os.CountDownTimer;
import android.os.RemoteException;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.telephony.ITelephony;

public class CallListnerHelper {
	private Context context;
	private TelephonyManager tm;
	private CallStateListener callStateListener;
	CallHelperListner regInterface;
	String[] numberarray;
	CountDownTimer countDownTimer;
	public CallListnerHelper(Context context,String[] numberArray,CallHelperListner regInterface) {
		this.context = context;
	
		
		this.numberarray = numberArray;
		callStateListener = new CallStateListener();
		tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		this.regInterface=regInterface;
	}
	
	public interface CallHelperListner{
		void onVerificationSucess();
		 void onVerificationFailed();;
	}
	/**
	 * Listener to detect incoming calls.
	 */
	private class CallStateListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {

			switch (state) {

			case TelephonyManager.CALL_STATE_RINGING:
				Log.d("abx", "CALL_STATE_RINGING");
				Log.d("abx", "CALL_STATE_RINGING from server incomingNumber="+ incomingNumber);
				boolean flag=false;
				for (String number : numberarray) {
					if (TextUtils.equals(incomingNumber, number)) {
						Log.d("abx", "number amtched");
						blockThisCaller();
						//deleteLastMissedCall();
						flag=true;

					}
				}
				if (flag) {
					countDownTimer.cancel();
					regInterface.onVerificationSucess();
				}else{
					regInterface.onVerificationFailed();	
				}

				break;

			case TelephonyManager.CALL_STATE_OFFHOOK:

				break;

			case TelephonyManager.CALL_STATE_IDLE:

				break;
			}
		}
	}

	/**
	 * Start calls detection.
	 */
	public void start() {
		Log.d("abx", "call listnening started");
		
		tm.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE);
		//add 40 sec timerhere
		 countDownTimer=new CountDownTimer(30000, 1000) {

		     public void onTick(long millisUntilFinished) {
		    	 if (millisUntilFinished / 1000==1) {
		    		 stop();
		    		 regInterface.onVerificationFailed();
				}
		     }

		     public void onFinish() {
		    	
		     }
		  }.start();

	}

	/**
	 * Stop calls detection.
	 */
	public void stop() {
		
		tm.listen(callStateListener, PhoneStateListener.LISTEN_NONE);
	}
	private void blockThisCaller() {

		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		ITelephony telephonyService = null;
		// Java Reflections
		Class<?> c = null;
		try {
			c = Class.forName(telephonyManager.getClass().getName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		Method m = null;
		try {
			m = c.getDeclaredMethod("getITelephony");
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		m.setAccessible(true);//Issue had nul pointer here
		try {
			 telephonyService = (ITelephony) m.invoke(telephonyManager);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		try {
			telephonyService.endCall();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

/*	private void deleteLastMissedCall() {

		Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null,null, null, "");
		cursor.moveToLast();
		Log.e("Size", "::" + cursor.getCount());
		if (cursor.getCount() > 0) {

			int idOfRowToDelete = cursor.getInt(cursor.getColumnIndex(CallLog.Calls._ID));
			Log.e("ID", "::" + idOfRowToDelete);
			context.getContentResolver().delete(CallLog.Calls.CONTENT_URI,	CallLog.Calls._ID + "= ? ",	new String[] { String
									.valueOf(idOfRowToDelete) });

			Log.e("reg num", "deleted");
		}
		cursor.close();
	}*/
}
