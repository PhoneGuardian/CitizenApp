package it.polimi.guardian.citizenapp;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class NotificationService extends Service implements LocationListener {

	private LocationManager locationManager;
	private String provider;
	private static final String TAG = "DisplayGPSInfoActivity";
	
	boolean isRunning = true;
	
	
	Marker[] markers;
	float radius;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		this.locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		Criteria criteria = new Criteria();
		provider = locationManager.getBestProvider(criteria, false);
		
		locationManager.getLastKnownLocation(provider);
		
		locationManager.requestLocationUpdates(provider, 400, 1, this);
		
		
		
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		radius = intent.getFloatExtra("radius", 0);
		DataWrapper dw = (DataWrapper) intent.getSerializableExtra("markers");
		
		markers = dw.getMarkers();
		
		return super.onStartCommand(intent, flags, startId);
		
		
	}
	

	@Override
	public void onLocationChanged(Location location) {
		

		
		// TODO Auto-generated method stub
		if(location!=null)
			{
				
				
				Log.d(TAG, "GPS LocationChanged");
				double lat = location.getLatitude();
				double lng = location.getLongitude();
				Log.d(TAG, "Received GPS request for " + String.valueOf(lat) + "," + String.valueOf(lng) + " , ready to rumble!");
		        
		        float distance[] = new float[2];
		        
		        for( int i=0 ; i < markers.length && isRunning; i++ )
		        {
			         Location.distanceBetween(markers[i].lat,
			           markers[i].lng, lat,
			                    lng, distance);
			
			         if (distance[0] <= radius)
			         {
			        	 
			        	 
			        	 NotificationCompat.Builder mBuilder =
							        new NotificationCompat.Builder(this)
							        .setContentTitle(markers[i].getType_of_event())
							        .setContentText("You are in "+radius+"m range of "+markers[i].address )
							        .setDefaults( Notification.DEFAULT_SOUND);
			        	 if(markers[i].type_of_event.compareTo("Red zone!") == 0)
			        		 mBuilder.setSmallIcon(R.drawable.red_circle);
			        	 else
			        		 if(markers[i].type_of_event.compareTo("Orange zone!") == 0)
			        			 mBuilder.setSmallIcon(R.drawable.orange_circle);
			        		 else
			        			 mBuilder.setSmallIcon(R.drawable.yellow_circle);
			        	 
							
								
			        	 NotificationManager mNotificationManager =(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			        	 mNotificationManager.notify(i, mBuilder.build());
			         }
			         
		         
		        }

			}

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		isRunning = false;
		
	}
	
	

}

