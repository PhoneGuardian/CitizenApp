package it.polimi.guardian.citizenapp;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PGMapActivity extends FragmentActivity implements OnMarkerClickListener,GoogleMap.OnCameraChangeListener
{


	GoogleMap mapa;
    boolean asyncTaskInProgress = false;

	Marker markers[];
	int finishedTask = 0;
    int refresh= 1;

	double lat;
    double lng;
    double lng_min;
    double lat_max;
    double lng_max;
    double lat_min;

    CheckBox cb_fire;
    CheckBox cb_police;
    CheckBox cb_emergency;


    JSONArray markers_response = null;
	JSONParser jParser = new JSONParser();

    Tags t = new Tags();

    @Override
	protected void onCreate(Bundle savedInstanceBundle) {

        // TODO Auto-generated method stub
        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.pgmap_activity);

        mapa = ((SupportMapFragment) (getSupportFragmentManager().findFragmentById(R.id.mapf))).getMap();
        mapa.setOnMarkerClickListener(this);
        mapa.setOnCameraChangeListener(this);

        cb_fire = (CheckBox) findViewById(R.id.cb_fire);
        cb_police = (CheckBox) findViewById(R.id.cb_police);
        cb_emergency = (CheckBox) findViewById(R.id.cb_emergency);

        lat = getIntent().getDoubleExtra("lat", 0);
        lng = getIntent().getDoubleExtra("lng", 0);

        finishedTask = 0;
        PostionOnMap();
    }

    public void PostionOnMap()
    {
        if (lat == 0 && lng == 0)
            GetCurrentLocation();

        LatLng ll = new LatLng(lat,lng);
        mapa.animateCamera(CameraUpdateFactory.newLatLngZoom(ll, 15));
        mapa.setMyLocationEnabled(true);
    }
		@Override
		public boolean onMarkerClick(com.google.android.gms.maps.model.Marker marker) {
			
			Intent i = new Intent(PGMapActivity.this,MarkerActivity.class);
			Marker m = new Marker();
			
			String[] niz = new String[7];
			String snip = marker.getSnippet();
			niz = snip.split("&");
			
			m.id = niz[6];
			m.setType_of_event(marker.getTitle());
			m.setUser_phone(niz[0]);
			m.setAddress(niz[1]);
			m.setEvent_time(niz[2]);
            m.setAnonymous(Integer.parseInt(niz[3]));
            m.setLocation_acc(Float.parseFloat(niz[4]));
			m.setDescription(niz[5]);
			m.setLat(marker.getPosition().latitude);
			m.setLng(marker.getPosition().longitude);

			i.putExtra("marker", m);
			startActivity(i);
			return true;// vraca true da bi se ukinulo defaultno ponasanje markera... da se ne bi prikazao info prozor markera
		}
	
	@Override 
	protected void onRestart()
	{
		super.onRestart();
		if(refresh==1)
		{
			new GetMarkersByCategory().execute();
		}
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.pgmap_menu, menu);



        new GetMarkersByCategory().execute();
        return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch(item.getItemId())
		{
			case R.id.refresh_events:
				refresh = 1;
                cb_fire.setChecked(true);
                cb_police.setChecked(true);
                cb_emergency.setChecked(true);
                onRestart();
				break;
			case R.id.filter_events:
				Intent i = new Intent(PGMapActivity.this,FilterActivity.class);
				startActivityForResult(i, 1);
				break;

		}
		return super.onOptionsItemSelected(item);
	}

    public void onCheckboxClicked(View v) {
        // Check which checkbox was clicked
        switch (v.getId()) {
            case R.id.cb_fire:
            case R.id.cb_police:
            case R.id.cb_emergency:
                refresh = 1;
                onRestart();
                break;
        }

    }


        @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==RESULT_OK)
		{
			refresh=0;
			DataWrapper dw = (DataWrapper) data.getSerializableExtra("markers");
	        markers = dw.getMarkers();
            DrawMarkers();
		}
	}
	
	
	
	
	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();

	}



	private void DrawMarkers() {

		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapf); 
		setMapa(mapFragment.getMap());
		if (getMapa()== null)
	    {
	            Toast.makeText(PGMapActivity.this,"Google Maps not Available",
	                    Toast.LENGTH_LONG).show();
	    }
		
		mapa.clear();
		if(markers!=null)
		{
			for(int i=0;i<markers.length;i++)
			{
				String title = markers[i].type_of_event;
				String snippet = markers[i].user_phone+"&"
						+markers[i].address+"&"
						+markers[i].event_time+"&"
                        +markers[i].anonymous+"&"
                        +markers[i].location_acc+"&"
						+markers[i].description+"&"+markers[i].id;

				if(markers[i].type_of_event.equals("F"))
				{
					com.google.android.gms.maps.model.Marker m = mapa
			        .addMarker(new MarkerOptions()
			                .position(new LatLng(markers[i].lat,markers[i].lng))
			                .title(title)
			                .snippet(snippet)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.flame_pin)));
					m.hideInfoWindow();
				}
				else
					if(markers[i].type_of_event.equals("E"))
					{
						com.google.android.gms.maps.model.Marker m = mapa
				        .addMarker(new MarkerOptions()
				                .position(new LatLng(markers[i].lat,markers[i].lng))
				                .title(title)
				                .snippet(snippet)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.emergency_pin)));
						m.hideInfoWindow();
					}
					else
					{
						com.google.android.gms.maps.model.Marker m = mapa
				        .addMarker(new MarkerOptions()
				                .position(new LatLng(markers[i].lat,markers[i].lng))
				                .title(title)
				                .snippet(snippet)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.police_pin)));
						m.hideInfoWindow();
					}

			}
		}
	    
	}

	public GoogleMap getMapa() {
		return mapa;
	}

	public void setMapa(GoogleMap mapa) {
		this.mapa = mapa;
	}

	class GetMarkersByCategory extends AsyncTask<Void, Void, Integer>
	{

		@Override
		protected Integer doInBackground(Void... paramss) {
			// TODO Auto-generated method stub
            if (asyncTaskInProgress) return 0;
            asyncTaskInProgress = true ;
            String URL = "http://nemanjastolic.co.nf/guardian/get_all_events.php";
			refresh = 0;

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			
			params.add(new BasicNameValuePair("category_fire", cb_fire.isChecked() ? "1" : "0"));
	        params.add(new BasicNameValuePair("category_police", cb_police.isChecked() ? "1" : "0"));
	        params.add(new BasicNameValuePair("category_emergency", cb_emergency.isChecked() ? "1" : "0"));


            params.add(new BasicNameValuePair("lng_min",Double.toString(lng_min)));
            params.add(new BasicNameValuePair("lng_max",Double.toString(lng_max)));
            params.add(new BasicNameValuePair("lat_min",Double.toString(lat_min)));
            params.add(new BasicNameValuePair("lat_max",Double.toString(lat_max)));
			
        	JSONObject json = jParser.makeHttpRequest(URL, "GET", params);
 
            try {
                // Checking for SUCCESS TAG
	                int success = json.getInt(t.TAG_SUCCESS);
	 
	                if (success == 1)
	                {
	                	markers_response = json.getJSONArray(t.TAG_EVENTS);
	                	if(markers_response==null)
	                		Toast.makeText(PGMapActivity.this, "No markers found!", Toast.LENGTH_LONG).show();
	                	else
	                	{
	                		markers = null;
	                		markers = new Marker[markers_response.length()];

                            for (int i = 0; i < markers_response.length(); i++)
                            {
                                JSONObject c = markers_response.getJSONObject(i);

                                markers[i] = new Marker();
                                markers[i].id = c.getString(t.TAG_EVENT_ID);
                                markers[i].setAddress(c.getString(t.TAG_ADDRESS));
                                markers[i].setUser_phone(c.getString(t.TAG_USER_PHONE));
                                markers[i].setType_of_event( c.getString(t.TAG_TYPE_OF_EVENT));
                                markers[i].setDescription(c.getString(t.TAG_DESC));
                                markers[i].setEvent_time(c.getString(t.TAG_EVENT_TIME));
                                markers[i].setLng(c.getDouble(t.TAG_LNG));
                                markers[i].setLat(c.getDouble(t.TAG_LAT));
                                markers[i].setAnonymous(c.getInt(t.TAG_ANONYMOUS));
                                markers[i].setLocation_acc(c.getLong(t.TAG_LOCATION_ACC));
                            }
	                	}
	                }
	                else
	                {
	                	markers = null;
	                }
	               
            	}
            	catch (JSONException e)
            	{
            		
            	}
            
            return 0;
		}
		@Override
		protected void onPostExecute(Integer result)
		{
            DrawMarkers();
            asyncTaskInProgress = false ;
        }
	}

	private void GetCurrentLocation()
	{
	    double[] a = getLocation();
	    lat = a[0];
	    lng = a[1];
	}

	public double[] getLocation()
	{
	    LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	    List<String> providers = lm.getProviders(true);

	    Location l = null;
	    for (int i = 0; i < providers.size(); i++) {
	        l = lm.getLastKnownLocation(providers.get(i));
	        if (l != null)
	            break;
	    }
	    double[] gps = new double[2];

	    if (l != null) {
	        gps[0] = l.getLatitude();
	        gps[1] = l.getLongitude();
	    }
	    return gps;
	}

    @Override
    public void onCameraChange(CameraPosition cameraPosition)
    {
        VisibleRegion vr = getMapa().getProjection().getVisibleRegion();

        if(Math.abs(lng_max-vr.latLngBounds.northeast.longitude)>0.005 || Math.abs(lat_max-vr.latLngBounds.northeast.latitude)>0.002)
        {
            lng_min = vr.latLngBounds.southwest.longitude;
            lat_max = vr.latLngBounds.northeast.latitude;
            lng_max = vr.latLngBounds.northeast.longitude;
            lat_min = vr.latLngBounds.southwest.latitude;

            new GetMarkersByCategory().execute();
        }
    }










}
