package edu.elfak.mosis.phoneguardian;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

public class PGMapActivity extends FragmentActivity implements OnMarkerClickListener, OnItemSelectedListener 
{
	
	public GoogleMap mapa;
	MarkerOptions markerOptions;
	Marker markers[];
	ArrayList<Marker> markers_in_radius;
	int finishedTask = 0;
	Spinner s;

	
	int flag_red = 1;
	int flag_orange = 1;
	int flag_yellow = 1;
	
	int refres= 1;
	
	float radius=20;
	
	JSONParser jParser = new JSONParser();
	
	Intent service_intent;
	
    private static String URL = "http://nikolamilica10.site90.com/get_markers_by_category.php";
 
    
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MARKERS = "markers";
    
    private static final String TAG_ADDRESS = "address";
    private static final String TAG_USER_PHONE = "user_phone";
    private static final String TAG_TYPE_OF_EVENT = "type_of_event";
    private static final String TAG_DESC = "description";
    private static final String TAG_EVENT_TIME = "event_time";
    private static final String TAG_LAT = "latitude";
    private static final String TAG_LONG = "longitude";
    private static final String TAG_ACCURACY = "location_acc";
    private static final String TAG_ANONYMOUS = "anonymous";
    private static final String TAG_ID = "id";

    JSONArray markers_response = null;
    
    protected boolean filterOnOff = false;

	
	@Override
	protected void onCreate(Bundle savedInstanceBundle) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceBundle);
		setContentView(R.layout.pgmap_activity);
		
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				  this, R.array.spinner, android.R.layout.simple_spinner_item );
				adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
		
		s = (Spinner) findViewById( R.id.radius_near_me );
		s.setAdapter( adapter );
				
		GoogleMap googleMap;
	    googleMap = ((SupportMapFragment)(getSupportFragmentManager().findFragmentById(R.id.mapf))).getMap();
		googleMap.setOnMarkerClickListener(this);
			
		finishedTask=0;
		markers_in_radius = new ArrayList<Marker>();
		s.setOnItemSelectedListener(this);
		

		new GetMarkersByCategory().execute();
		
		service_intent = new Intent(this,NotificationService.class);
		

	}
	

	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,long id) {
		
		if(filterOnOff && markers!=null)
		{
			
			/*if(flag_orange==0 && flag_red==0 && flag_yellow==0) 
	        	Toast.makeText(PGMapActivity.this, "No category selected!", Toast.LENGTH_LONG).show();
	        else
	        	onRestart();*/
			
			radius = Float.parseFloat(s.getSelectedItem().toString());
		
			stopService(new Intent(service_intent));
			service_intent.putExtra("markers", new DataWrapper(markers));
			service_intent.putExtra("radius", radius);
			startService(service_intent);
			
		}
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		
		//new GetMarkersByCategory().execute();
	}
	
	public void onToggleClicked(View v)
	{
		filterOnOff = ((ToggleButton) v).isChecked();
		Spinner s = (Spinner) findViewById(R.id.radius_near_me);
		
		if(filterOnOff)
		{
			
			
			
			s.setVisibility(View.VISIBLE);
			
			new GetMarkersByCategory().execute();
			
		}
		else
		{
			
			stopService(new Intent(service_intent));
			s.setVisibility(View.INVISIBLE);
			
			
		}
	}
	
		@Override
		public boolean onMarkerClick(com.google.android.gms.maps.model.Marker marker) {
			
			Intent i = new Intent(PGMapActivity.this,MarkerActivity.class);
			Marker m = new Marker();
			
			String[] niz = new String[5];
			String snip = marker.getSnippet();
			niz = snip.split("&");
			
			m.id = niz[4];
			m.setType_of_event(marker.getTitle());
			m.setUser_phone(niz[0]);
			m.setAddress(niz[1]);
			m.setEvent_time(niz[2]);
			m.setDescription(niz[3]);
			m.setLat(marker.getPosition().latitude);
			m.setLng(marker.getPosition().longitude);

			i.putExtra("marker", m);
			startActivity(i);
			// TODO Auto-generated method stub
			return true;// vraca true da bi se ukinulo defaultno ponasanje markera... da se ne bi prikazao info prozor markera
		}
	
	@Override 
	protected void onRestart()
	{
		super.onRestart();
		if(refres==1)
		{
			new GetMarkersByCategory().execute();
		}
		
		
		
		
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(0,1,1,"Refresh");
		menu.add(0,2,2,"Search");
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId())
		{
			case 1:
				refres=1;
				flag_red = 1;
				flag_orange = 1;
				flag_yellow = 1;
				CheckBox cb_red = (CheckBox) PGMapActivity.this.findViewById(R.id.cb_redzone);
				cb_red.setChecked(true);
				CheckBox cb_orange = (CheckBox) PGMapActivity.this.findViewById(R.id.cb_orangezone);
				cb_orange.setChecked(true);
				CheckBox cb_yellow = (CheckBox) PGMapActivity.this.findViewById(R.id.cb_yellowzone);
				cb_yellow.setChecked(true);
				onRestart();
				break;
			case 2:
				Intent i = new Intent(PGMapActivity.this,FilterActivity.class);
				startActivityForResult(i, 1);
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==RESULT_OK)
		{
			refres=0;
			DataWrapper dw = (DataWrapper) data.getSerializableExtra("markers");
			
	        markers = dw.getMarkers();
	        //onResume();
		}
	}
	
	
	
	
	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		
		
	}


	public void onCheckboxClicked(View v) {
	    // Is the view now checked?
	    boolean checked = ((CheckBox) v).isChecked();
	    refres=1;
	    // Check which checkbox was clicked
	    switch(v.getId()) {
	        case R.id.cb_redzone:
	            if (checked)
	                flag_red=1;
	            else
	            	flag_red=0;
	            break;
	        case R.id.cb_orangezone:
	            if (checked)
	                flag_orange=1;
	            else
	            	flag_orange=0;
	            break;
	        case R.id.cb_yellowzone:
	        	if (checked)
	                flag_yellow=1;
	            else
	            	flag_yellow=0;
	            break;
	        // TODO: Veggie sandwich
	            
	       
	    }
	    
	   
	    
	    if(filterOnOff)
	    {
	    	stopService(new Intent(service_intent));
	    }
	    onRestart();
	    
	}

	private void DrawMarkers() {
		// TODO Auto-generated method stub
		
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
				CircleOptions circleOptions = new CircleOptions()
		        .center(new LatLng(markers[i].lat,markers[i].lng))
		        .radius(100)
		        .strokeColor(Color.alpha(255))
		        ;
				
				
				if(markers[i].type_of_event.equals("F"))
				{
					circleOptions.fillColor(Color.argb(128, 255, 0, 0));
					
					com.google.android.gms.maps.model.Marker m = mapa
			        .addMarker(new MarkerOptions()
			                .position(new LatLng(markers[i].lat,markers[i].lng))
			                .title(title)
			                .snippet(snippet)
			                .icon(BitmapDescriptorFactory.fromResource(R.drawable.fire)));
					m.hideInfoWindow();


				}
				else
					if(markers[i].type_of_event.equals("E"))
					{
						circleOptions.fillColor(Color.argb(128, 255, 165, 0));
						
						com.google.android.gms.maps.model.Marker m = mapa
				        .addMarker(new MarkerOptions()
				                .position(new LatLng(markers[i].lat,markers[i].lng))
				                .title(title)
				                .snippet(snippet)
				                .icon(BitmapDescriptorFactory.defaultMarker(R.drawable.emergency)));
						m.hideInfoWindow();
					}
					else
					{
						circleOptions.fillColor(Color.argb(128, 255, 255, 0));
						
						com.google.android.gms.maps.model.Marker m = mapa
				        .addMarker(new MarkerOptions()
				                .position(new LatLng(markers[i].lat,markers[i].lng))
				                .title(title)
				                .snippet(snippet)
				                .icon(BitmapDescriptorFactory.defaultMarker(R.drawable.police)));
						m.hideInfoWindow();
					}
				
				mapa.addCircle(circleOptions);
			}
		}
		 // In meters
		 
	    
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
			
			refres = 0;
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			
			params.add(new BasicNameValuePair("category_red", Integer.toString(flag_red)));
	        params.add(new BasicNameValuePair("category_orange", Integer.toString(flag_orange)));
	        params.add(new BasicNameValuePair("category_yellow", Integer.toString(flag_yellow)));
			
        	JSONObject json = jParser.makeHttpRequest(URL, "POST", params);
 
            try {
                // Checking for SUCCESS TAG
	                int success = json.getInt(TAG_SUCCESS);
	 
	                if (success == 1)
	                {
	                    // products found
	                    // Getting Array of Products
	                	markers_response = json.getJSONArray(TAG_MARKERS);
	                	if(markers_response==null)
	                		Toast.makeText(PGMapActivity.this, "No markers found!", Toast.LENGTH_LONG).show();
	                	else
	                	{
	                		markers = null;
	                		
	                		markers = new Marker[markers_response.length()];
	 
	                    // looping through All Products
	                    for (int i = 0; i < markers_response.length(); i++)
	                    {
	                        JSONObject c = markers_response.getJSONObject(i);
	 
	                        markers[i] = new Marker();
	                        // Storing each json item in variable
	                        markers[i].id = c.getString(TAG_ID);
	                        markers[i].setAddress(c.getString(TAG_ADDRESS));
	                        markers[i].setUser_phone(c.getString(TAG_USER_PHONE));
	                        markers[i].setType_of_event( c.getString(TAG_TYPE_OF_EVENT));
	                        markers[i].setDescription(c.getString(TAG_DESC));
	                        markers[i].setEvent_time(c.getString(TAG_EVENT_TIME));
	                        markers[i].setLng(c.getDouble(TAG_LONG));
	                        markers[i].setLat(c.getDouble(TAG_LAT));
                            markers[i].setAnonymous(c.getInt(TAG_ANONYMOUS));
                            markers[i].setLocation_acc(c.getLong(TAG_ACCURACY));
	 
	                   
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
            
            	
            if(filterOnOff && markers!=null)
            {
            	service_intent.putExtra("markers", new DataWrapper(markers));
    			service_intent.putExtra("radius", radius);
    			startService(service_intent);
            }
            
            DrawMarkers();
    		GetCurrentLocation();
           
        }
	}
	
	

	private void GetCurrentLocation()
	{

	    double[] d = getlocation();
	    double lat = d[0];
	    double lng = d[1];

	    mapa.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lng), 15));
	    mapa.setMyLocationEnabled(true);

	}

	public double[] getlocation()
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

	





	

}
