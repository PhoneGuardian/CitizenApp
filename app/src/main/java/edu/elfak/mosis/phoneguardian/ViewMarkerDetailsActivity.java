package edu.elfak.mosis.phoneguardian;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.RatingBar;
import android.widget.TextView;


public class ViewMarkerDetailsActivity extends Activity {

	String username;
	double avg_rate;

	final String TAG_SUCCESS = "success";
	
	final String TAG_MARKER_ID = "id_marker";
	final String TAG_AVG = "avg";

	final JSONParser jParser = new JSONParser();
	String URL = "http://nikolamilica10.site90.com/get_avg_rate_from_users.php";
	
	
	String id_marker = "";
	RatingBar t6;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.viewmarker_details_activity);
		
		username = getIntent().getStringExtra("USERNAME");
		Marker m = (Marker) getIntent().getSerializableExtra("marker");

        String type_of_event = m.getType_of_event();
		
		id_marker = m.id;
		
		TextView t =(TextView) findViewById(R.id.event_time);
		t.setText(m.getEvent_time());
		TextView t1 =(TextView) findViewById(R.id.event_address);
		t1.setText(m.getAddress());
        TextView t2 =(TextView) findViewById(R.id.type_of_event);

		switch (type_of_event)
        {
            case "E":
                t2.setText("Emergency");
                break;
            case "F":
                t2.setText("Fire");
                break;
            case "P":
                t2.setText("Police");
                break;
        }

		TextView t3 =(TextView) findViewById(R.id.description_of_event);
		t3.setText(m.getDescription());
		TextView t4 =(TextView) findViewById(R.id.event_acc);
		t4.setText(Float.toString(m.getLocation_acc()));
        TextView t5 =(TextView) findViewById(R.id.anonymous);
        if(m.getAnonymous() == 1)
            t5.setText("YES");
        else
            t5.setText("NO");
		
		t6 =(RatingBar) findViewById(R.id.avg_rating_marker);
		new GetAvgRate().execute();
		//t5.setText(m.getCategory()); PRIBAVI REJTINGE SVIH KOMENTARA TOG MARKERA I DA SE IZRACUNA PROSEK


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
	        params.add(new BasicNameValuePair("id_marker", id_marker));

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
        	
        	
    		t6.setRating((float)avg_rate);
        	
        }
 
    }
}
