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
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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

	boolean show_events_in_list = false;
	int radius_checked = 0;
    int description_checked = 0;
    int date_checked = 0;
	 
	ArrayList<Marker> events_in_radius;
	Geocoder geoCoder ;

    String msg;
	
	JSONParser jParser = new JSONParser();
    


    // JSON Node names
    Tags t = new Tags();

    JSONArray events_response = null;

	DatePicker dt_begin;
	DatePicker dt_end;
    ClearableAutoCompleteTextView mAutocompleteView;
	EditText et_description;
	
    CheckBox cb_fire;
    CheckBox cb_emergency;
    CheckBox cd_police;
    DatePicker date_from;
    DatePicker date_to;
	
	Spinner spinner_radius;

    Button btn_show_map;
    Button btn_filtered_events;
	
	String address="";
	String description="";

	 
	double latitude;
	double longitude;
    double lat_min;
    double lat_max;
    double lng_min;
    double lng_max;
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
        mAdapter.setGoogleApiClient(mGoogleApiClient);
        mAutocompleteView.setAdapter(mAdapter);

        (new GetAddressTask(this)).execute(location);
		
		btn_show_map = (Button) findViewById(R.id.btn_show_filtered_events_on_map);
		btn_show_map.setOnClickListener(this);
		
		btn_filtered_events = (Button) findViewById(R.id.btn_show_filtered_events);
		btn_filtered_events.setOnClickListener(this);

		et_description = (EditText)findViewById(R.id.et_desc_filter);
        date_from = (DatePicker) findViewById(R.id.datepicker_from);
        date_from.setSpinnersShown(false);
        date_to = (DatePicker) findViewById(R.id.datepicker_to);
        date_to.setSpinnersShown(false);
		

		cb_fire = (CheckBox)findViewById(R.id.cb_fire_filter);
		cb_emergency = (CheckBox)findViewById(R.id.cb_emergency_filter);
		cd_police = (CheckBox)findViewById(R.id.cb_police_filter);

		dt_begin = (DatePicker) findViewById(R.id.datepicker_from);
		dt_end = (DatePicker) findViewById(R.id.datepicker_to);

		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
		         this, R.array.radius_array, android.R.layout.simple_spinner_item );
		       adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
		     
	    spinner_radius = (Spinner) findViewById( R.id.radius_filter );
	    spinner_radius.setAdapter( adapter );
	     
	    events_in_radius = new ArrayList<Marker>();
	    geoCoder = new Geocoder(FilterActivity.this);

        findViewById(R.id.filter_layout).setOnTouchListener(hideKeyboardlistener);

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

	        case R.id.cb_desc_filter:
	            if (checked)
	            {
                    description_checked = 1;
	            	et_description.setEnabled(true);
	            }
	            else
	            {
	            	et_description.setEnabled(false);
	            	description_checked = 0;
	            }
	            break;
	        case R.id.cb_radius:
	             if (checked)
	             {
	              spinner_radius.setEnabled(true);
	              radius_checked = 1;
	             }
	             else
	             {
	              spinner_radius.setEnabled(false);
	              radius_checked = 0;
	   
	             }
	             break;
            case R.id.cb_date_filter:
                if (checked)
                {
                    date_from.setSpinnersShown(true);
                    date_to.setSpinnersShown(true);
                    date_checked = 1;
                }
                else
                {
                    date_from.setSpinnersShown(false);
                    date_to.setSpinnersShown(false);
                    spinner_radius.setEnabled(false);
                    date_checked = 0;

                }
                break;

	    }
	}
	

	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
        if (v.getId() == R.id.btn_show_filtered_events_on_map || v.getId() == R.id.btn_show_filtered_events)
        {
            show_events_in_list = v.getId() == R.id.btn_show_filtered_events ? true : false;

            if (description_checked == 1)
                this.description = et_description.getText().toString();
            if (radius_checked == 1)
                this.radius = Float.parseFloat(spinner_radius.getSelectedItem().toString());

            String inputAddr = mAutocompleteView.getText().toString();
            if (inputAddr.length() == 0 && currentLocation.isValid()) {
                this.address = currentLocation.getAddress();
                this.latitude = currentLocation.getLatitude();
                this.longitude = currentLocation.getLongitude();
                new GetMarkersBySearch().execute();

            } else if (inputLocation.isValid() && inputAddr.equals(inputLocation.getAddress())) {
                this.address = inputLocation.getAddress();
                this.latitude = inputLocation.getLatitude();
                this.longitude = inputLocation.getLongitude();
                new GetMarkersBySearch().execute();

            } else {
                Toast.makeText(FilterActivity.this, "Entered Address is not a valid location ", Toast.LENGTH_LONG).show();
            }


        }

/*        switch(v.getId())
        {
         
         case R.id.btn_show_filtered_events_on_map:
             show_events_in_list=false;
             if(radius_checked==0 && description_checked==0 && type_of_event_checked==0 && date_checked==0)
                Toast.makeText(this,"Filter not chosen!",Toast.LENGTH_SHORT).show();
             else
                new GetMarkersBySearch().execute();
             break;
         case R.id.btn_show_filtered_events:
            show_events_in_list = true;
            if(radius_checked==0 && description_checked==0 && type_of_event_checked==0 && date_checked==0)
                Toast.makeText(this,"Filter not chosen!",Toast.LENGTH_SHORT).show();
            else
                new GetMarkersBySearch().execute();
            break;

        }
        */
		
	}
	

    private double  toRad(double val) {
        /** Converts numeric degrees to radians */
        return val * Math.PI / 180;
    }

    private double  toDeg(double val) {
        /** Converts numeric degrees to radians */
        return val * 180 / Math.PI;
    }
	
	class GetMarkersBySearch extends AsyncTask<Void, Void, Integer>
	{

		@Override
		protected Integer doInBackground(Void... paramss) {
			// TODO Auto-generated method stub

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            String URL1 = "http://nemanjastolic.co.nf/guardian/get_events_by_filter.php";
            if(radius_checked==1) {
                double R = 6371; //in km
                double r = radius / R; //d has to be in km

                double lat_rad = toRad(latitude);
                double lng_rad = toRad(longitude);

                double lat_min_rad = lat_rad - r;
                double lat_max_rad = lat_rad + r;

                double delta_lot = Math.asin(Math.sin(r) / Math.cos(r));
                double lng_min_rad = lng_rad - delta_lot;
                double lng_max_rad = lng_rad + delta_lot;

                lat_min = toDeg(lat_min_rad);
                lat_max = toDeg(lat_max_rad);
                lng_min = toDeg(lng_min_rad);
                lng_max = toDeg(lng_max_rad);

                params.add(new BasicNameValuePair("lng_min",Double.toString(lng_min)));
                params.add(new BasicNameValuePair("lng_max",Double.toString(lng_max)));
                params.add(new BasicNameValuePair("lat_min",Double.toString(lat_min)));
                params.add(new BasicNameValuePair("lat_max",Double.toString(lat_max)));
            }

            params.add(new BasicNameValuePair("fire_event_checked", cb_fire.isChecked()? "1" : "0"));
            params.add(new BasicNameValuePair("police_event_checked", cd_police.isChecked()? "1" : "0"));
            params.add(new BasicNameValuePair("emergency_event_checked", cb_emergency.isChecked()? "1" : "0"));

            params.add(new BasicNameValuePair("description", description));

            params.add(new BasicNameValuePair("radius_checked",Integer.toString(radius_checked)));
            params.add(new BasicNameValuePair("description_checked",Integer.toString(description_checked)));
            params.add(new BasicNameValuePair("date_checked",Integer.toString(date_checked)));
	        

        	String begin_day, end_day;
 	        
 	        if(dt_begin.getDayOfMonth()>0 && dt_begin.getDayOfMonth()<9) {
                begin_day = '0' + Integer.toString(dt_begin.getDayOfMonth() + 1);
            }else{
                begin_day = Integer.toString(dt_begin.getDayOfMonth()+1);
            }
 	        
 	        if(dt_end.getDayOfMonth()>0 && dt_end.getDayOfMonth()<9) {
                end_day = '0' + Integer.toString(dt_end.getDayOfMonth() + 1);
            }else {
                end_day = Integer.toString(dt_end.getDayOfMonth() + 1);
            }
 	        
	        String begin_month, end_month;
	        
	        if(dt_begin.getMonth()>0 && dt_begin.getMonth()<9) {
                begin_month = '0' + Integer.toString(dt_begin.getMonth() + 1);
            }else{
                begin_month = Integer.toString(dt_begin.getMonth()+1);
            }
	        
	        if(dt_end.getMonth()>0 && dt_end.getMonth()<9) {
                end_month = '0' + Integer.toString(dt_end.getMonth() + 1);
            }else{
                end_month = Integer.toString(dt_end.getMonth()+1);
            }
	        
	        params.add(new BasicNameValuePair("begin_time", dt_begin.getYear()+"-"+begin_month+"-"+begin_day+" 00:00:00"));
	        params.add(new BasicNameValuePair("end_time",dt_end.getYear()+"-"+end_month+"-"+end_day+" 23:59:59"));
        
	       
        	JSONObject json = jParser.makeHttpRequest(URL1, "GET", params);
 
            try {
                // Checking for SUCCESS TAG
	                int success = json.getInt(t.TAG_SUCCESS);
	 
	                if (success == 1){
	                	events_response = json.getJSONArray(t.TAG_EVENTS);

	                	if(events_response==null) {
                            msg = "No markers found!";
                        }
	                	else{
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
	                else{
	                	msg = "No markers found!";
	                }
            	}
            	catch (JSONException e){
            		msg = "GRESKA JSON";
            	}

            return 0;
		}
		@Override
		protected void onPostExecute(Integer result)
		{
            	if(show_events_in_list == true){
                 Intent i = new Intent(FilterActivity.this,ListFilterActivity.class);
                 i.putExtra("markers", new DataWrapper(markers));
                 startActivity(i);
                }
                else{
                  Intent returnIntent = new Intent();
                  returnIntent.putExtra("markers", new DataWrapper(markers));
                  setResult(RESULT_OK, returnIntent);
                    
                  Toast.makeText(FilterActivity.this,"USPEH",Toast.LENGTH_LONG).show();
                  FilterActivity.this.finish();
                }
        }
	}

    private View.OnTouchListener hideKeyboardlistener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent ev) {
            hideKeyboard(view);
            return false;
        }
        protected void hideKeyboard(View view)
        {
            InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }

    };
}
