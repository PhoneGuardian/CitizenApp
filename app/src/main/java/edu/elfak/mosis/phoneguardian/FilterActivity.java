package edu.elfak.mosis.phoneguardian;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;
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

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FilterActivity extends FragmentActivity implements android.view.View.OnClickListener,GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks{



    EventLocation inputLocation = new EventLocation();
    EventLocation currentLocation = new EventLocation();

    protected GoogleApiClient mGoogleApiClient;

    private PlaceAutocompleteAdapter mAdapter;

    LatLngBounds BOUNDS_GREATER;

    Marker markers[];

	boolean show_events_in_list = false;

	ArrayList<Marker> events_in_radius;
	Geocoder geoCoder ;

    String msg;
	
	JSONParser jParser = new JSONParser();
    


    // JSON Node names
    Tags t = new Tags();

    JSONArray events_response = null;

    ClearableAutoCompleteTextView mAutocompleteView;

    CheckBox cb_filterByType;
    CheckBox cb_filterByDescription;
    CheckBox cb_filterByDate;
    FilterTypeDialog filterTypeDialog;
    FilterDescriptionDialog filterDescriptionDialog;
    FilterDateDialog filterDateDialog;


    SeekBar seekBar;
    TextView tvSeekBarProgress;
    boolean filterByRadius = false;

    Button btn_show_map;
    Button btn_filtered_events;
	
	String address="";

	 
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

        findViewById(R.id.filter_type_dialog).setOnClickListener(this);
        cb_filterByType = (CheckBox) findViewById(R.id.filter_type_checkbox);
        findViewById(R.id.filter_description_dialog).setOnClickListener(this);
        cb_filterByDescription = (CheckBox) findViewById(R.id.filter_description_checkbox);
        findViewById(R.id.filter_date_dialog).setOnClickListener(this);
        cb_filterByDate = (CheckBox) findViewById(R.id.filter_date_checkbox);


        tvSeekBarProgress = (TextView) findViewById(R.id.tv_filter_seekbar_progress);
        seekBar = (SeekBar) findViewById(R.id.filter_seekBar);
        seekBar.setOnSeekBarChangeListener(radiusSeekBarChangeListener);

	     
	    events_in_radius = new ArrayList<Marker>();
	    geoCoder = new Geocoder(FilterActivity.this);

        findViewById(R.id.filter_layout).setOnTouchListener(hideKeyboardlistener);
        ///
        filterTypeDialog = new FilterTypeDialog(this, (TextView) findViewById(R.id.filter_type));
        filterDescriptionDialog = new FilterDescriptionDialog(this, (TextView) findViewById(R.id.filter_description_summary));
        filterDateDialog = new FilterDateDialog(this, (TextView) findViewById(R.id.filter_date_summary));


    }



    private SeekBar.OnSeekBarChangeListener radiusSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            tvSeekBarProgress.setText( (progress == 0 ? "∞": progress) + " km");
            filterByRadius = progress != 0;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) { }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) { }
    };

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


	@Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch(v.getId()) {

            case R.id.btn_show_filtered_events_on_map:
            case R.id.btn_show_filtered_events:
                boolean inputDataIsValid = true;
                show_events_in_list = v.getId() == R.id.btn_show_filtered_events ;

                if (filterByRadius) {
                    this.radius = seekBar.getProgress();

                    String inputAddr = mAutocompleteView.getText().toString();
                    if (inputAddr.length() == 0 && currentLocation.isValid()) {
                        this.address = currentLocation.getAddress();
                        this.latitude = currentLocation.getLatitude();
                        this.longitude = currentLocation.getLongitude();

                    } else if (inputLocation.isValid() && inputAddr.equals(inputLocation.getAddress())) {
                        this.address = inputLocation.getAddress();
                        this.latitude = inputLocation.getLatitude();
                        this.longitude = inputLocation.getLongitude();

                    } else {
                        inputDataIsValid = false;
                        Toast.makeText(FilterActivity.this, "Entered Address is not a valid location ", Toast.LENGTH_LONG).show();
                    }
                }
                if(inputDataIsValid)
                    new GetMarkersBySearch().execute();

                break;

            case R.id.filter_type_dialog:
                if(cb_filterByType.isChecked())
                    filterTypeDialog.show();
                break;
            case R.id.filter_description_dialog:
                if(cb_filterByDescription.isChecked())
                    filterDescriptionDialog.show();
                break;
            case R.id.filter_date_dialog:
                if(cb_filterByDate.isChecked())
                    filterDateDialog.show();
                break;
        }
    }

    private double  toRad(double val) {  /** Converts numeric degrees to radians */
        return val * Math.PI / 180;
    }

    private double  toDeg(double val) { /** Converts numeric degrees to radians */
        return val * 180 / Math.PI;
    }
	
	class GetMarkersBySearch extends AsyncTask<Void, Void, Integer>
	{
		@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
        @Override
		protected Integer doInBackground(Void... paramss) {
			// TODO Auto-generated method stub

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            String URL1 = "http://nemanjastolic.co.nf/guardian/get_events_by_filter.php";
            if(filterByRadius) {
                double R = 6371.0; //in km
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

            if( cb_filterByType.isChecked()) {
                params.add(new BasicNameValuePair("fire_event_checked", filterTypeDialog.isFireChecked() ? "1" : "0"));
                params.add(new BasicNameValuePair("police_event_checked", filterTypeDialog.isPoliceChecked() ? "1" : "0"));
                params.add(new BasicNameValuePair("emergency_event_checked", filterTypeDialog.isEmergencyChecked() ? "1" : "0"));
            }


            params.add(new BasicNameValuePair("radius_checked", filterByRadius ? "1" :"0"));

            params.add(new BasicNameValuePair("description_checked", cb_filterByDescription.isChecked()? "1" : "0"));
            params.add(new BasicNameValuePair("description", filterDescriptionDialog.getDescription()));

            params.add(new BasicNameValuePair("date_checked", cb_filterByDate.isChecked()? "1": "0"));
            if( cb_filterByDate.isChecked()) {
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                if (filterDateDialog.isFilteringByFromDate()) {
                    params.add(new BasicNameValuePair("from_date_checked", "1"));
                    params.add(new BasicNameValuePair("begin_time", String.format("%s 00:00:00", dateFormat.format(filterDateDialog.getFromDate()))));
                }
                if (filterDateDialog.isFilteringByToDate()) {
                    params.add(new BasicNameValuePair("to_date_checked", "1"));
                    params.add(new BasicNameValuePair("end_time", String.format("%s 23:59:59", dateFormat.format(filterDateDialog.getToDate()))));
                }
            }
	       
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
	 
                            for (int i = 0; i < events_response.length(); i++){
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
                        markers = null;
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
            	if(show_events_in_list){
                 Intent i = new Intent(FilterActivity.this,ListFilterActivity.class);
                 i.putExtra("markers", new DataWrapper(markers));
                 startActivity(i);
                }
                else{
                  Intent returnIntent = new Intent();
                  returnIntent.putExtra("markers", new DataWrapper(markers));
                  setResult(RESULT_OK, returnIntent);
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
