package edu.elfak.mosis.phoneguardian;


import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;

import java.io.IOException;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class AddLocationActivity extends Activity implements OnClickListener {
	
	String category = "Red zone!";
	boolean alertClicked = false;
	
	static final int REQUEST_IMAGE_CAPTURE = 1;
	final String TAG_SUCCESS = "success";
	final String TAG_MESSAGE = "message";
	final String TAG_MARKER_ID = "id";
	final JSONParser jParser = new JSONParser();
	String URL = "";
	String[] argss = new String[7];
	String img_marker_id ="";
	
	TextView user;
	TextView time;
	TextView longitude;
	TextView latitude;
	TextView address;
	EditText description;
	
	File photo;
	int serverResponseCode = 0;
	ProgressDialog dialog = null;


    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");


	Calendar cal = Calendar.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addlocation_activity);

		user = (TextView) findViewById(R.id.label_username);
		time = (TextView) findViewById(R.id.label_addingtime);
		longitude = (TextView) findViewById(R.id.label_long);
		latitude = (TextView) findViewById(R.id.label_lat);
		address = (TextView) findViewById(R.id.label_address);
		description = (EditText) findViewById(R.id.edit_text_descr);

		Button btnSave = (Button) findViewById(R.id.btn_save_location);

		btnSave.setOnClickListener(this);

		Calendar cal = Calendar.getInstance();
		time.setText(dateFormat.format(cal.getTime()));


		Location location = getlocation();
		latitude.setText(Double.toString(location.getLatitude()));
		longitude.setText(Double.toString(location.getLongitude()));

		(new GetAddressTask(this)).execute(location);

		user.setText(getIntent().getStringExtra("USERNAME"));

	}

	
	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.btn_save_location:
			
			if(category!="" && description.getText().toString()!="")
			{
				URL = "http://nikolamilica10.site90.com/add_marker.php";
				
				
				argss[1] = address.getText().toString();
				argss[2] = category;
				argss[3] = description.getText().toString();
				argss[4] = time.getText().toString();
				argss[5] = longitude.getText().toString();
				argss[6] = latitude.getText().toString();
				
				new AddLocation().execute(argss);
			}
			else
				Toast.makeText(AddLocationActivity.this, "Some fields are empty!", Toast.LENGTH_LONG).show();
			break;
			
			
		}
		
	}
	
	public void onRadioButtonClicked(View view) {
	    // Is the button now checked?
	    boolean checked = ((RadioButton) view).isChecked();
	    
	    // Check which radio button was clicked
	    switch(view.getId()) {
	        case R.id.rb_redzone:
	            if (checked)
	                category="Red zone!";
	            break;
	        case R.id.rb_orangezone:
	            if (checked)
	            	category="Orange zone!";
	            break;
	        case R.id.rb_yellowzone:
	            if (checked)
	            	category="Yellow zone!";
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

			    address.setText(addr);
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
	            params.add(new BasicNameValuePair("category", argss[2]));
	            params.add(new BasicNameValuePair("description", argss[3]));
	            params.add(new BasicNameValuePair("time", argss[4]));
	            params.add(new BasicNameValuePair("longitude", argss[5]));
	            params.add(new BasicNameValuePair("latitude", argss[6]));
	            
	            params.add(new BasicNameValuePair("username", argss[0]));
	            // getting JSON string from URL
	            JSONObject json = jParser.makeHttpRequest(URL, "GET", params);

	 
	            try 
	            {
	                // Checking for SUCCESS TAG
	                success = json.getInt(TAG_SUCCESS);
	                msg = json.getString(TAG_MESSAGE);
	                img_marker_id = json.getString(TAG_MARKER_ID);
	 
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

	        	
	        }
	 
	    }
	
}
