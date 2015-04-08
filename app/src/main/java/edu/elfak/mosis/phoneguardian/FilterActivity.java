package edu.elfak.mosis.phoneguardian;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class FilterActivity extends FragmentActivity implements android.view.View.OnClickListener,GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks{


    EventLocation inputLocation = new EventLocation();
    EventLocation currentLocation = new EventLocation();

    protected GoogleApiClient mGoogleApiClient;

    private PlaceAutocompleteAdapter mAdapter;

    LatLngBounds BOUNDS_GREATER;


    Marker markers[];
	int finishedTask = 0;
	boolean show_events_in_list = false;
	boolean radius_checked = false;
	 
	ArrayList<Marker> events_in_radius;
	Geocoder geoCoder ;

    String msg;
	
	JSONParser jParser = new JSONParser();
    
    private static String URL = "http://nemanjastolic.co.nf/guardian/get_events_by_filter.php";
 
    // JSON Node names
    Tags t;

    JSONArray events_response = null;

	DatePicker dt_begin;
	DatePicker dt_end;
    ClearableAutoCompleteTextView mAutocompleteView;
	EditText et_description;
	
	RadioGroup rg_type_of_event;
	RadioButton rb_fire;
	RadioButton rb_emergency;
	RadioButton rb_police;
	
	Spinner spinner_radius;

    Button btn_show_map;
    Button btn_filtered_events;
	
	String address="";
	String description="";
	String type_of_event="";

	 
	double latitude;
	double longitude;
	float radius;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_activity);

        if (mGoogleApiClient == null) {
            rebuildGoogleApiClient();
        }

        mAutocompleteView = (ClearableAutoCompleteTextView)findViewById(R.id.autocomplete_places_filter);
        mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);

        Location location = getlocation();
        currentLocation.setLatitude(location.getLatitude());
        currentLocation.setLongitude(location.getLongitude());
        currentLocation.setAccuracy(location.getAccuracy());

        BOUNDS_GREATER = new LatLngBounds(new LatLng(location.getLatitude()-0.5, location.getLongitude()-0.5),
                new LatLng(location.getLatitude()+0.5, location.getLongitude()+0.5));

        mAdapter = new PlaceAutocompleteAdapter(this, R.layout.single_location_search_item,BOUNDS_GREATER, null);
        mAutocompleteView.setAdapter(mAdapter);

        (new GetAddressTask(this)).execute(location);
		
		btn_show_map = (Button) findViewById(R.id.btn_show_filtered_events_on_map);
		btn_show_map.setOnClickListener(this);
		
		btn_filtered_events = (Button) findViewById(R.id.btn_show_filtered_events);
		btn_filtered_events.setOnClickListener(this);
		

		et_description = (EditText)findViewById(R.id.et_desc_filter);
		
		rg_type_of_event = (RadioGroup)findViewById(R.id.rg_type_of_event);

		rb_fire = (RadioButton)findViewById(R.id.rb_fire_filter);
		rb_emergency = (RadioButton)findViewById(R.id.rb_emergency_filter);
		rb_police = (RadioButton)findViewById(R.id.rb_police_filter);

		dt_begin = (DatePicker) findViewById(R.id.datepicker_from);
		dt_end = (DatePicker) findViewById(R.id.datepicker_to);

		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
		         this, R.array.spinner, android.R.layout.simple_spinner_item );
		       adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
		     
	    spinner_radius = (Spinner) findViewById( R.id.radius_filter );
	    spinner_radius.setAdapter( adapter );
	     
	    events_in_radius = new ArrayList<Marker>();
	    geoCoder = new Geocoder(FilterActivity.this);
	}

    public Location getlocation() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = lm.getProviders(true);

        Location l = null;
        for (int i = 0; i < providers.size(); i++) {
            l = lm.getLastKnownLocation(providers.get(i));
            if (l != null)
                break;
        }

        return l;
    }

    private class GetAddressTask extends AsyncTask<Location, Void, String>
    {
        Context mContext;
        public GetAddressTask(Context context) {
            super();
            mContext = context;
        }

        @Override
        protected String doInBackground(Location... params)
        {
            Geocoder geocoder =
                    new Geocoder(mContext, Locale.getDefault());
            // Get the current location from the input parameter list
            Location loc = params[0];
            // Create a list to contain the result address
            List<Address> addresses = null;
            try {
	                /*
	                 * Return 1 address.
	                 */
                addresses = geocoder.getFromLocation(loc.getLatitude(),
                        loc.getLongitude(), 1);

            }
            catch (IOException e1)
            {
                Log.e("LocationSampleActivity", "IO Exception in getFromLocation()");
                e1.printStackTrace();
                return ("IO Exception trying to get address");
            }
            catch (IllegalArgumentException e2)
            {
                // Error message to post in the log
                String errorString = "Illegal arguments " +
                        Double.toString(loc.getLatitude()) +
                        " , " +
                        Double.toString(loc.getLongitude()) +
                        " passed to address service";
                Log.e("LocationSampleActivity", errorString);
                e2.printStackTrace();
                return errorString;
            }
            // If the reverse geocode returned an address
            if (addresses != null && addresses.size() > 0)
            {
                // Get the first address
                Address address = addresses.get(0);
	                /*
	                 * Format the first line of address (if available),
	                 * city, and country name.
	                 */
                String addressText = String.format(
                        "%s, %s",
                        // If there's a street address, add it
                        address.getMaxAddressLineIndex() > 0 ?
                                address.getAddressLine(0) : "",
                        // Locality is usually a city
                        address.getLocality());
                // Return the text
                return addressText;
            }
            else
            {
                return "No address found";
            }
        }

        @Override
        protected void onPostExecute(String addr) {
            currentLocation.setAddress(addr);
        }

    }


    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a PlaceAutocomplete object from which we
             read the place ID.
              */
            final PlaceAutocompleteAdapter.PlaceAutocomplete item = mAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
              details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

            Toast.makeText(getApplicationContext(), "Clicked: " + item.description,
                    Toast.LENGTH_SHORT).show();
        }
    };
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully


                return;
            }
            // Get the Place object from the buffer.
            final Place place = places.get(0);

            // Format details of the place for display and show it in a TextView.
            mAutocompleteView.setText(place.getAddress());
            inputLocation.setAddress(place.getAddress().toString());
            inputLocation.setLatitude(place.getLatLng().latitude);
            inputLocation.setLongitude(place.getLatLng().longitude);
            inputLocation.setAccuracy(10);
        }
    };




    /**
     * Construct a GoogleApiClient for the {@link Places#GEO_DATA_API} using AutoManage
     * functionality.
     * This automatically sets up the API client to handle Activity lifecycle events.
     */
    protected synchronized void rebuildGoogleApiClient() {
        // When we build the GoogleApiClient we specify where connected and connection failed
        // callbacks should be returned, which Google APIs our app uses and which OAuth 2.0
        // scopes our app requests.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addConnectionCallbacks(this)
                .addApi(Places.GEO_DATA_API)
                .build();
    }

    /**
     * Called when the Activity could not connect to Google Play services and the auto manager
     * could resolve the error automatically.
     * In this case the API is not available and notify the user.
     *
     * @param connectionResult can be inspected to determine the cause of the failure
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {


        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(this,
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();

        // Disable API access in the adapter because the client was not initialised correctly.
        mAdapter.setGoogleApiClient(null);

    }


    @Override
    public void onConnected(Bundle bundle) {
        // Successfully connected to the API client. Pass it to the adapter to enable API access.
        mAdapter.setGoogleApiClient(mGoogleApiClient);


    }

    @Override
    public void onConnectionSuspended(int i) {
        // Connection to the API client has been suspended. Disable API access in the client.
        mAdapter.setGoogleApiClient(null);

    }

	public void onCheckboxClicked(View view) {
	    // Is the view now checked?
	    boolean checked = ((CheckBox) view).isChecked();
	    
	    // Check which checkbox was clicked
	    switch(view.getId()) {
	        case R.id.cb_type_of_event_filter:
	            if (checked)
	            	{
	            		/*if(rb_fire.isChecked()) this.type_of_event = "F";
	            		if(rb_emergency.isChecked()) this.type_of_event = "E";
	            		if(rb_police.isChecked()) this.type_of_event = "P";*/

                        rb_fire.setEnabled(true);
                        rb_emergency.setEnabled(true);
                        rb_police.setEnabled(true);
	            	}
	            else
		            {
                        rb_fire.setEnabled(false);
                        rb_emergency.setEnabled(false);
                        rb_police.setEnabled(false);
		            	this.type_of_event = "";
		            }
	            break;
	        case R.id.cb_desc_filter:
	            if (checked)
	            {
	            	et_description.setEnabled(true);
	            }
	            else
	            {
	            	et_description.setEnabled(false);
	            	this.description = "";
	            }
	            break;
	        case R.id.cb_radius:
	             if (checked)
	             {
	              spinner_radius.setEnabled(true);
	              radius_checked = true;
	             }
	             else
	             {
	              spinner_radius.setEnabled(false);
	              radius_checked = false;
	   
	             }
	             break;

	    }
	}
	
	public void onRadioButtonClicked(View view) {
	    // Is the button now checked?
	    boolean checked = ((RadioButton) view).isChecked();
	    
	    // Check which radio button was clicked
	    switch(view.getId()) {
	        case R.id.rb_fire_filter:
	            if (checked)
                    type_of_event="F";
	            break;
	        case R.id.rb_emergency_filter:
	            if (checked)
                    type_of_event="E";
	            break;
	        case R.id.rb_police_filter:
	            if (checked)
                    type_of_event="P";
	            break;
	    }
	}

	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		CheckBox cb;

    	this.address = mAutocompleteView.getText().toString();
    	
    	cb = (CheckBox)findViewById(R.id.cb_desc_filter);
    	if(cb.isChecked())
    		this.description = et_description.getText().toString();

    	cb = (CheckBox)findViewById(R.id.cb_radius);
        if(cb.isChecked())
        	this.radius= Float.parseFloat(spinner_radius.getSelectedItem().toString());
        

        switch(v.getId())
        {
         
         case R.id.btn_show_filtered_events_on_map:
            show_events_in_list=false;
            break;
         case R.id.btn_show_filtered_events:
            show_events_in_list = true;
            break;

        }
        
        
         new GetMarkersBySearch().execute();
          
         
		
	}
	
	/*@Override
	  public void onBackPressed() {
	    this.getParent().onBackPressed();   
	  }*/
	
	
	class GetMarkersBySearch extends AsyncTask<Void, Void, Integer>
	{

		@Override
		protected Integer doInBackground(Void... paramss) {
			// TODO Auto-generated method stub
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			
			params.add(new BasicNameValuePair("address", address));
	        params.add(new BasicNameValuePair("type_of_event", type_of_event));
	        params.add(new BasicNameValuePair("description", description));
	       
	        
	        CheckBox cb = (CheckBox) findViewById(R.id.cb_date_filter);
	         
	        if(cb.isChecked()) params.add(new BasicNameValuePair("date_checked", "1"));
	        else params.add(new BasicNameValuePair("date_checked", "0"));

        	String begin_day, end_day;
 	        
 	        if(dt_begin.getDayOfMonth()>0 && dt_begin.getDayOfMonth()<9)
 	        	begin_day = '0'+ Integer.toString(dt_begin.getDayOfMonth()+1);
 	        else begin_day = Integer.toString(dt_begin.getDayOfMonth()+1);
 	        
 	        if(dt_end.getDayOfMonth()>0 && dt_end.getDayOfMonth()<9)
 	        	end_day = '0'+ Integer.toString(dt_end.getDayOfMonth()+1);
 	        else end_day = Integer.toString(dt_end.getDayOfMonth()+1);
 	        
	        String begin_month, end_month;
	        
	        if(dt_begin.getMonth()>0 && dt_begin.getMonth()<9)
	        	begin_month = '0'+ Integer.toString(dt_begin.getMonth()+1);
	        else begin_month = Integer.toString(dt_begin.getMonth()+1);
	        
	        if(dt_end.getMonth()>0 && dt_end.getMonth()<9)
	        	end_month = '0'+ Integer.toString(dt_end.getMonth()+1);
	        else end_month = Integer.toString(dt_end.getMonth()+1);
	        
	        
	        params.add(new BasicNameValuePair("begin_time", dt_begin.getYear()+"/"+begin_month+"/"+begin_day));
	        params.add(new BasicNameValuePair("end_time",dt_end.getYear()+"/"+end_month+"/"+end_day));
        
	       
        	JSONObject json = jParser.makeHttpRequest(URL, "GET", params);
 
            try {
                // Checking for SUCCESS TAG
	                int success = json.getInt(t.TAG_SUCCESS);
	 
	                if (success == 1)
	                {

	                	events_response = json.getJSONArray(t.TAG_EVENTS);
	                	if(events_response==null)
	                		msg =  "No markers found!";
	                	else
	                	{
	                		markers = new Marker[events_response.length()];
	 

                            for (int i = 0; i < events_response.length(); i++)
                            {
                                JSONObject c = events_response.getJSONObject(i);

                                markers[i] = new Marker();
                                // Storing each json item in variable
                                markers[i].setAddress(c.getString(t.TAG_ADDRESS));
                                markers[i].setUser_phone(c.getString(t.TAG_USER_PHONE));
                                markers[i].setType_of_event( c.getString(t.TAG_TYPE_OF_EVENT));
                                markers[i].setDescription(c.getString(t.TAG_DESC));
                                markers[i].setEvent_time(c.getString(t.TAG_EVENT_TIME));
                                markers[i].setLng(c.getDouble(t.TAG_LNG));
                                markers[i].setLat(c.getDouble(t.TAG_LAT));
                                markers[i].id = c.getString(t.TAG_EVENT_ID);
                                markers[i].setLocation_acc(Float.parseFloat(c.getString(t.TAG_LOCATION_ACC)));
                                markers[i].setAnonymous(c.getInt(t.TAG_ANONYMOUS));


                            }
	                	}
	                }
	                else
	                {
	                	msg = "No markers found!";
	                }
            	}
            	catch (JSONException e)
            	{
            		msg = "GRESKA JSON";
            	}
            finishedTask=1;
            return finishedTask;
		}
		@Override
		protected void onPostExecute(Integer result)
		{
           /* if(result==1)
            {
            	if(radius_checked)
                {
                 
                 if(rb_location_from_address.isChecked()) 
                  convertAddress();
                  
                 
                 float distance[] = new float[2];
                 
                 for( int i=0 ; i < markers.length; i++ )
                 {
	                  Location.distanceBetween(markers[i].lat,
	                    markers[i].lng, latitude,
	                             longitude, distance);
	    
	                  if (distance[0] <= radius)
	                  {
	                    events_in_radius.add(markers[i]);
	                  }
                 }
                 
                 markers=null;
                 markers=new Marker[events_in_radius.size()];
                 for( int i=0 ; i < markers.length; i++ )
                 {
	                  markers[i] = new Marker();
	                  markers[i]=events_in_radius.get(i);
                 }
                 
                }
            	if(show_events_in_list == true)
                {
                
                 Intent i = new Intent(FilterActivity.this,ListFilterActivity.class);
                 i.putExtra("markers", new DataWrapper(markers));
                 startActivity(i);
                }
                else
                {
                  Intent returnIntent = new Intent();
                  returnIntent.putExtra("markers", new DataWrapper(markers));
                  setResult(RESULT_OK, returnIntent);
                    
                  Toast.makeText(FilterActivity.this,"USPEH",Toast.LENGTH_LONG).show();
                }
            	
            }
            else
            	Toast.makeText(FilterActivity.this,"finishedTask is 0",
	                    Toast.LENGTH_LONG).show();
            
          
            FilterActivity.this.finish();
             
            */
        }
		
	}
	
	
	
	

}
