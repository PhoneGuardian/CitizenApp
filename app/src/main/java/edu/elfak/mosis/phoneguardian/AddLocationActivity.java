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
	int brojac;
	
	String photo_dest; //destination of photo in phone
	Uri selectedImage; 
	
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
		
		brojac=0;
		new AlertPlayer().execute();
		
		user = (TextView) findViewById(R.id.label_username);
		time = (TextView) findViewById(R.id.label_addingtime);
		longitude = (TextView) findViewById(R.id.label_long);
		latitude = (TextView) findViewById(R.id.label_lat);
		address = (TextView) findViewById(R.id.label_address);
		description = (EditText) findViewById(R.id.edit_text_descr);
		Button btnTakePic = (Button) findViewById(R.id.btn_take_picture);
		Button btnSave = (Button) findViewById(R.id.btn_save_location);
		
		
		btnTakePic.setOnClickListener(this);
		btnSave.setOnClickListener(this);
		
		Calendar cal = Calendar.getInstance();
		time.setText(dateFormat.format(cal.getTime()));
		
		
		Location location = getlocation();
		latitude.setText(Double.toString(location.getLatitude()));
		longitude.setText(Double.toString(location.getLongitude()));
		
		(new GetAddressTask(this)).execute(location);
		
		user.setText(getIntent().getStringExtra("USERNAME"));
		
	}
	
	private class AlertPlayer extends AsyncTask<Void, Void, Void>
	{

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			
			alertClicked=!alertClicked;
			Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
			RingtoneManager.getRingtone(getApplicationContext(), notification);
			MediaPlayer mp = MediaPlayer.create(AddLocationActivity.this, R.raw.siren);
			
			AudioManager audioManager =
					(AudioManager)AddLocationActivity.this.getSystemService(Context.AUDIO_SERVICE);
					// Set the volume of played media to maximum.
					audioManager.setStreamVolume (
					AudioManager.STREAM_MUSIC,
					audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
					0);
					
			mp.setOnCompletionListener(new OnCompletionListener() {
				
				@Override
				public void onCompletion(MediaPlayer mp) {
					// TODO Auto-generated method stub
					if(alertClicked && brojac<4)
					{
						brojac++;
						mp.start();
					}
					else
						if(brojac==4 && alertClicked)
						{
							brojac=0;
							Toast.makeText(AddLocationActivity.this, "Proslo 16sec", Toast.LENGTH_LONG).show();
			                mp.release();
			               
						}
					
				}
			});
		
			if(alertClicked)
			{
				brojac++;
				mp.start();	
			}
			else
				mp.stop();
			
			return null;
		}
		
		
		
	}
	
	
	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.btn_take_picture:
			argss[0] = user.getText().toString();
			
			String fileName = argss[0];
			ContentValues values = new ContentValues();
			values.put(MediaStore.Images.Media.TITLE, fileName);
			values.put(MediaStore.Images.Media.DESCRIPTION,"Image capture by camera");
			selectedImage = getContentResolver().insert(
			        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, selectedImage);

			startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);

			   break;
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
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{

		ImageView  img = (ImageView) findViewById(R.id.img_location);

        photo_dest= getRealPathFromURI(selectedImage);
        
        img.setImageURI(selectedImage);
        
	}


	public String getRealPathFromURI(Uri contentUri) {
	    String [] proj={MediaColumns.DATA};
	    android.database.Cursor cursor = managedQuery( contentUri,proj,null, null, null);     // Order-by clause (ascending by name)
	    int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
	    cursor.moveToFirst();
	    return cursor.getString(column_index);
	    
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
	        	
	        	dialog = ProgressDialog.show(AddLocationActivity.this, "", "Uploading information of location...", true);
	        	new Thread(new Runnable() {
	                public void run() {
	                     runOnUiThread(new Runnable() {
	                            public void run() {
	                                
	                            }
	                        });                     
	                    uploadFile(photo_dest);                   
	                }
	              }).start(); 
				
	        	runOnUiThread(new Runnable() {
	                public void run() {
	                    /**
	                     * Updating parsed JSON data into ListView
	                     * */
	                	if( success == 0 ) 
	                		Toast.makeText(AddLocationActivity.this, "GRESKA NEKA - asinhroni task!" , Toast.LENGTH_LONG).show();
	                	else
	                		Toast.makeText(AddLocationActivity.this, "Proslo je - addlocation activity asinhroni task" , Toast.LENGTH_LONG).show();
	                	
	                }
	            });
	        	
	        }
	 
	    }
		
		
		
		
		 public void uploadFile(String sourceFileUri) {
	         String upLoadServerUri = "http://nikolamilica10.site90.com/upload_photo_of_marker.php";
	         String fileName = sourceFileUri;

	         HttpURLConnection conn = null;
	         DataOutputStream dos = null; 
	         String lineEnd = "\r\n";
	         String twoHyphens = "--";
	         String boundary = "*****";
	         int bytesRead, bytesAvailable, bufferSize;
	         byte[] buffer;
	         int maxBufferSize = 1 * 1024 * 1024;
	         File sourceFile = new File(sourceFileUri);
	         if (!sourceFile.isFile()) {
	          Log.e("uploadFile", "Source File Does not exist");
	         }
	             try { // open a URL connection to the Servlet
	              FileInputStream fileInputStream = new FileInputStream(sourceFile);
	              URL url = new URL(upLoadServerUri);
	              conn = (HttpURLConnection) url.openConnection(); // Open a HTTP  connection to  the URL
	              conn.setDoInput(true); // Allow Inputs
	              conn.setDoOutput(true); // Allow Outputs
	              conn.setUseCaches(false); // Don't use a Cached Copy
	              conn.setRequestMethod("POST");
	              conn.setRequestProperty("Connection", "Keep-Alive");
	              conn.setRequestProperty("ENCTYPE", "multipart/form-data");
	              conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
	              conn.setRequestProperty("uploaded_file", fileName);
	              dos = new DataOutputStream(conn.getOutputStream());
	    
	              dos.writeBytes(twoHyphens + boundary + lineEnd);
	              dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""+ img_marker_id+".jpg" + "\"" + lineEnd);
	              dos.writeBytes(lineEnd);
	    
	              bytesAvailable = fileInputStream.available(); // create a buffer of  maximum size
	    
	              bufferSize = Math.min(bytesAvailable, maxBufferSize);
	              buffer = new byte[bufferSize];
	    
	              // read file and write it into form...
	              bytesRead = fileInputStream.read(buffer, 0, bufferSize); 
	                
	              while (bytesRead > 0) {
	                dos.write(buffer, 0, bufferSize);
	                bytesAvailable = fileInputStream.available();
	                bufferSize = Math.min(bytesAvailable, maxBufferSize);
	                bytesRead = fileInputStream.read(buffer, 0, bufferSize);              
	               }
	    
	              // send multipart form data necesssary after file data...
	              dos.writeBytes(lineEnd);
	              dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
	    
	              // Responses from the server (code and message)
	              serverResponseCode = conn.getResponseCode();
	              String serverResponseMessage = conn.getResponseMessage();
	               
	              Log.i("uploadFile", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);
	              if(serverResponseCode == 200){
	                  runOnUiThread(new Runnable() {
	                       public void run() {
	                           Toast.makeText(AddLocationActivity.this, "Successfully upload information of location!", Toast.LENGTH_SHORT).show();
	                       }
	                   });               
	              }   
	              
	              //close the streams //
	              fileInputStream.close();
	              dos.flush();
	              dos.close();
	               
	         } catch (MalformedURLException ex) { 
	             dialog.dismiss(); 
	             ex.printStackTrace();
	             Toast.makeText(AddLocationActivity.this, "MalformedURLException", Toast.LENGTH_SHORT).show();
	             Log.e("Upload file to server", "error: " + ex.getMessage(), ex); 
	         } catch (Exception e) {
	             dialog.dismiss(); 
	             e.printStackTrace();
	             Toast.makeText(AddLocationActivity.this, "Exception : " + e.getMessage(), Toast.LENGTH_SHORT).show();
	             Log.e("Upload file to server Exception", "Exception : " + e.getMessage(), e); 
	         }
	         dialog.dismiss();      
	        }
	
}
