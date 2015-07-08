package Services;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

public class RegCallHelper {
	private Context context;
	private TelephonyManager tm;
	private CallStateListener callStateListener;

	// private OutgoingReceiver outgoingReceiver;
	String[] numberarray;

	public interface RegInterface{
		void regSucess();
		 void regFailed();;
	}
	RegInterface regInterface;
	public RegCallHelper(Context context, String[] numberarray,RegInterface regInterface) {
		this.context = context;
		this.numberarray = numberarray;
		callStateListener = new CallStateListener();
		this.regInterface=regInterface;
		// outgoingReceiver = new OutgoingReceiver();
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
						
						flag=true;
					
					}
				}
				if (flag) {
					regInterface.regSucess();
				}else{
					regInterface.regFailed();	
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
		tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		tm.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE);

	}

	/**
	 * Stop calls detection.
	 */
	public void stop() {
		tm.listen(callStateListener, PhoneStateListener.LISTEN_NONE);
	}
}
