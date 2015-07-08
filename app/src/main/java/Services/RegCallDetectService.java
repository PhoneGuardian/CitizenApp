package Services;

import Services.RegCallHelper.RegInterface;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;


public class RegCallDetectService extends Service {
	private RegCallHelper callHelper;
 
    public RegCallDetectService() {
    	
    }

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("abx", "onStartCommand");
		Log.e("Service", "services");
		
		callHelper = new RegCallHelper(this,intent.getExtras().getStringArray("numberarray"),new RegInterface() {
			
			@Override
			public void regSucess() {
				
			}
			
			@Override
			public void regFailed() {
				
			}
		});
		
		int res = super.onStartCommand(intent, flags, startId);
		callHelper.start();
		return res;
	}
	
    @Override
	public void onDestroy() {
		super.onDestroy();
		
		callHelper.stop();
	}

	@Override
    public IBinder onBind(Intent intent) {
		// not supporting binding
    	return null;
    }
}

