package edu.elfak.mosis.phoneguardian;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;


public class ViewMarkerDetailsActivity extends Activity {

	String username;
	double avg_rate;
	
	static final int REQUEST_IMAGE_CAPTURE = 1;
	final String TAG_SUCCESS = "success";
	
	final String TAG_MARKER_ID = "id_marker";
	final String TAG_AVG = "avg";

	final JSONParser jParser = new JSONParser();
	String URL = "http://nikolamilica10.site90.com/get_avg_rate_from_users.php";
	
	
	String id_marker = "";
	RatingBar t5;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.viewmarker_details_activity);
		
		username = getIntent().getStringExtra("USERNAME");
		Marker m = (Marker) getIntent().getSerializableExtra("marker");
		
		id_marker = m.id;
		
		TextView t =(TextView) findViewById(R.id.username_marker);
		t.setText(m.getUsername());
		TextView t1 =(TextView) findViewById(R.id.address_marker);
		t1.setText(m.getAddress());
		TextView t2 =(TextView) findViewById(R.id.date_marker);
		t2.setText(m.getAddingTime());
		TextView t3 =(TextView) findViewById(R.id.description_marker);
		t3.setText(m.getDescription());
		TextView t4 =(TextView) findViewById(R.id.category_marker);
		t4.setText(m.getCategory());
		
		t5 =(RatingBar) findViewById(R.id.avg_rating_marker);
		new GetAvgRate().execute();
		//t5.setText(m.getCategory()); PRIBAVI REJTINGE SVIH KOMENTARA TOG MARKERA I DA SE IZRACUNA PROSEK
		
		ImageView photo = (ImageView) findViewById(R.id.img_marker_picture);
		
		 try
        {
			 URL url = new URL("http://nikolamilica10.site90.com/photos_of_markers/"+m.id+".jpg");
			 Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
			 photo.setImageBitmap(bmp);
        }
        catch(Exception e)
        {
       	 
        };
	}
	
	@Override
	  public void onBackPressed() {
	    this.getParent().onBackPressed();   
	  }
	
	class GetAvgRate extends AsyncTask<String, String, String> {
	   	 
        
		int success;
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
            //FOTOGRAFIJA NIJE DODATA!!! TO NAKNADNO!!!!!!
	            params.add(new BasicNameValuePair("id_marker", id_marker));
	            
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(URL, "GET", params);

            try 
            {
                // Checking for SUCCESS TAG
                success = json.getInt(TAG_SUCCESS);
                avg_rate = json.getDouble(TAG_AVG);
 
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
        	
        	
    		t5.setRating((float)avg_rate);
        	
        }
 
    }
}
