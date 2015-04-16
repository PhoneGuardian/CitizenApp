package edu.elfak.mosis.phoneguardian;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
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
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;

import android.widget.ProgressBar;
import android.widget.RadioButton;
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

public class AddLocationActivity extends FragmentActivity implements OnClickListener
        ,GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks
{
    EventLocation inputLocation = new EventLocation();
    EventLocation currentLocation = new EventLocation();
    String inputAddr ;

	String type_of_event = "F";

	Tags t = new Tags();

	final JSONParser jParser = new JSONParser();
	String URL = "";
	String[] argss = new String[9];

	TextView time;
	LineEditText description;
    ClearableAutoCompleteTextView mAutocompleteView;

    protected GoogleApiClient mGoogleApiClient;

    private PlaceAutocompleteAdapter mAdapter;

    LatLngBounds BOUNDS_GREATER;


    int anonymous=0; //1 anonymous message is sent, 0 message is not sent anonymously, default state not checked


    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	Calendar cal = Calendar.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addlocation_activity);

        if (mGoogleApiClient == null) {
            rebuildGoogleApiClient();
        }

		time = (TextView) findViewById(R.id.label_addingtime);
		description = (LineEditText) findViewById(R.id.edit_text_descr);
        mAutocompleteView = (ClearableAutoCompleteTextView) findViewById(R.id.autocomplete_places); // here will be selected address if exists,
        // if we decide to pick location, not use current location

        mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);

		Button btnSave = (Button) findViewById(R.id.btn_report_event);
		btnSave.setOnClickListener(this);

		time.setText( new SimpleDateFormat("HH:mm dd/MM/yyyy").format(cal.getTime()));

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
        findViewById(R.id.layout_add_location).setOnTouchListener(hideKeyboardlistener);


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

          //  Toast.makeText(getApplicationContext(), "Clicked: " + item.description, Toast.LENGTH_SHORT).show();
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
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.btn_report_event:

            inputAddr = ((AutoCompleteTextView) findViewById(R.id.autocomplete_places)).getText().toString();

			if(type_of_event!="" && description.getText().toString().length()!=0)
			{
                disableReportEvent();
                argss[0] = User.getInstance().getPhone();
				argss[2] = type_of_event;
				argss[3] = description.getText().toString();
				argss[4] = dateFormat.format(cal.getTime()).toString();

                if ( inputAddr.length() == 0 && currentLocation.isValid()){
                    argss[1] = currentLocation.getAddress();
                    argss[5] = Double.toString(currentLocation.getLongitude());
                    argss[6] = Double.toString(currentLocation.getLatitude());
                    argss[7] = Float.toString(currentLocation.getAccuracy());
                    argss[8] = Integer.toString(anonymous);
                    new AddLocation().execute(argss);


                }else if (inputLocation.isValid() && inputAddr.equals(inputLocation.getAddress()) ){
                    argss[1] = inputLocation.getAddress();
                    argss[5] = Double.toString(inputLocation.getLongitude());
                    argss[6] = Double.toString(inputLocation.getLatitude());
                    argss[7] = Float.toString(inputLocation.getAccuracy());
                    argss[8] = Integer.toString(anonymous);
                    new AddLocation().execute(argss);

                }else{
                    Toast.makeText(AddLocationActivity.this, "Entered Address is not a valid location ", Toast.LENGTH_LONG).show();
                    enableReportEvent();
                }
			}
			else
				Toast.makeText(AddLocationActivity.this, "Some fields are empty", Toast.LENGTH_LONG).show();
			break;
			
			
		}
		
	}
	public void onCheckboxClicked(View view)
    {
        boolean checked;
        switch (view.getId())
        {
            case R.id.cb_anonymous:
            {
                checked = ((CheckBox) view).isChecked();
                if (checked)
                    anonymous = 1;
                else
                    anonymous = 0;
            }
            break;
        }
    }


	public void onRadioButtonClicked(View view) {
	    // Is the button now checked?
	    boolean checked = ((RadioButton) view).isChecked();
	    
	    // Check which radio button was clicked
	    switch(view.getId()) {
	        case R.id.rb_fire:
	            if (checked)
                    type_of_event="F";
	            break;
	        case R.id.rb_emergency:
	            if (checked)
                    type_of_event="E";
	            break;
	        case R.id.rb_police:
	            if (checked)
                    type_of_event="P";
	            break;
	    }
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
	            		Log.e("LocationSampleActivity","IO Exception in getFromLocation()");
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
		
		class AddLocation extends AsyncTask<String, String, String> {
		   	 
	        
			int success;
			String msg;
			/**
	         * Before starting background thread Show Progress Dialog
	         * */
	        @Override
	        protected void onPreExecute() {
	            super.onPreExecute();

	        }
	 
	        /**
	         * getting All products from url
	         * */
	        protected String doInBackground(String... argss) {
	            // Building Parameters
	            List<NameValuePair> params = new ArrayList<NameValuePair>();
	           
	            params.add(new BasicNameValuePair("address", argss[1]));
	            params.add(new BasicNameValuePair("type_of_event", argss[2]));
	            params.add(new BasicNameValuePair("description", argss[3]));
	            params.add(new BasicNameValuePair("event_time", argss[4]));
	            params.add(new BasicNameValuePair("lng", argss[5]));
	            params.add(new BasicNameValuePair("lat", argss[6]));
                params.add(new BasicNameValuePair("location_acc", argss[7]));
                params.add(new BasicNameValuePair("anonymous", argss[8]));
	            
	            params.add(new BasicNameValuePair("user_phone", argss[0]));
	            // getting JSON string from URL
                URL = "http://nemanjastolic.co.nf/guardian/add_event.php";
	            JSONObject json = jParser.makeHttpRequest(URL, "GET", params);

	 
	            try 
	            {
	                // Checking for SUCCESS TAG
	                success = json.getInt(t.TAG_SUCCESS);
	                msg = json.getString(t.TAG_MESSAGE);


                }
	            catch (JSONException e)
	            {
	                e.printStackTrace();
	            }
	 
	            return null;
	        }
	 
	        /**
	         * After completing background task Dismiss the progress dialog
	         * **/
	        protected void onPostExecute(String file_url) {
                enableReportEvent();
                if(success == 1) { // case event successfully created
                    LatLng ll;
                    if ( inputAddr.length() == 0 && currentLocation.isValid())
                        ll= new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                    else
                        ll = new LatLng(inputLocation.getLatitude(), inputLocation.getLongitude());

                    Toast.makeText(AddLocationActivity.this, msg, Toast.LENGTH_LONG).show();
                    Intent i = new Intent(getApplicationContext(), PGMapActivity.class );
                    i.putExtra("lat",ll.latitude);
                    i.putExtra("lng",ll.longitude);
                    i.setFlags(i.FLAG_ACTIVITY_NEW_TASK );
                    startActivity(i); // Launch the PGMapActivity
                    finish();         // Close down the AddLocationActivity
                }else {
                    Toast.makeText(AddLocationActivity.this, "Oops! Looks like something went wrong" , Toast.LENGTH_LONG).show();
                }

	        }
	 
	    }

    private void enableReportEvent(){
        ProgressBar spinner = (ProgressBar)findViewById(R.id.spinner_report_event);
        spinner.setVisibility(View.INVISIBLE);

        Button signupBtn = (Button) findViewById(R.id.btn_report_event);
        signupBtn.setEnabled(true);
    }
    private void disableReportEvent(){
        ProgressBar spinner = (ProgressBar)findViewById(R.id.spinner_report_event);
        spinner.setVisibility(View.VISIBLE);

        Button signupBtn = (Button) findViewById(R.id.btn_report_event);
        signupBtn.setEnabled(false);
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
