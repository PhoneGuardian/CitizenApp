package edu.elfak.mosis.phoneguardian;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

public class FilterActivity extends Activity implements android.view.View.OnClickListener{
	
	Marker markers[];
	int finishedTask = 0;
	boolean show_markers_in_list = false;
	boolean radius_checked = false;
	 
	ArrayList<Marker> markers_in_radius;
	Geocoder geoCoder ;
	
	
	JSONParser jParser = new JSONParser();
    
    private static String URL = "http://nikolamilica10.site90.com/get_markers_by_search.php";
 
    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MARKERS = "markers";
    
    private static final String TAG_ADDRESS = "address";
    private static final String TAG_USERNAME = "username";
    private static final String TAG_CATEGORY = "category";
    private static final String TAG_DESC = "description";
    
    private static final String TAG_TIME = "time";
    private static final String TAG_LAT = "latitude";
    private static final String TAG_LONG = "longitude";
    
    
   
 
    // products JSONArray
    JSONArray markers_response = null;
	
	protected boolean filterOnOff = false;
	
	DatePicker dt_begin;
	DatePicker dt_end;
	
	EditText et_address;
	EditText et_description;
	EditText et_username;
	EditText et_comment;
	EditText et_comment_user;
	EditText loaction_address_for_radius;
	
	RadioGroup rg_category;
	
	RadioButton rb_red;
	RadioButton rb_orange;
	RadioButton rb_yellow;
	
	Spinner s;
	
	RadioButton rb_current_location;
	RadioButton rb_location_from_address;
	
	String address="";
	String description="";
	String username="";
	String comment="";
	String comment_user="";
	String category="";
	
	String location="";
	 
	double latitude;
	double longitude;
	float radius;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_activity);
		
		Button btn_show_map = (Button) findViewById(R.id.btn_show_filltered_markers_on_map);
		btn_show_map.setOnClickListener(this);
		
		Button btn_filtered_markers = (Button) findViewById(R.id.btn_show_filltered_markers);
		btn_filtered_markers.setOnClickListener(this);
		
		et_address = (EditText)findViewById(R.id.et_address_filter);
		et_description = (EditText)findViewById(R.id.et_desc_filter);
		et_username = (EditText)findViewById(R.id.et_user_filter);
		et_comment = (EditText)findViewById(R.id.et_comment_filter);
		et_comment_user = (EditText)findViewById(R.id.et_comment_user_filter);
		
		loaction_address_for_radius = (EditText)findViewById(R.id.et_loaction_address_for_radius);
		
		rg_category = (RadioGroup)findViewById(R.id.rg_category);
		rb_orange = (RadioButton)findViewById(R.id.rb_orangezone_filter);
		rb_red = (RadioButton)findViewById(R.id.rb_redzone_filter);
		rb_yellow = (RadioButton)findViewById(R.id.rb_yellowzone_filter);
		dt_begin = (DatePicker) findViewById(R.id.datepicker_from);
		dt_end = (DatePicker) findViewById(R.id.datepicker_to);
		
		rb_current_location = (RadioButton)findViewById(R.id.rb_current_location);
		rb_location_from_address = (RadioButton)findViewById(R.id.rb_location_from_address);
		
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
		         this, R.array.spinner, android.R.layout.simple_spinner_item );
		       adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
		     
	    s = (Spinner) findViewById( R.id.radius_near_me );
	    s.setAdapter( adapter );
	     
	    markers_in_radius = new ArrayList<Marker>();
	    geoCoder = new Geocoder(FilterActivity.this);
	}
	
	public void onToggleClicked(View v)
	{
		filterOnOff = ((ToggleButton) v).isChecked();
		LinearLayout l = (LinearLayout) findViewById(R.id.filter_layout);
		
		if(filterOnOff)
		{
			l.setVisibility(View.VISIBLE);
		}
		else
		{
			l.setVisibility(View.INVISIBLE);
		}
	}
	
	public void onCheckboxClicked(View view) {
	    // Is the view now checked?
	    boolean checked = ((CheckBox) view).isChecked();
	    
	    // Check which checkbox was clicked
	    switch(view.getId()) {
	        case R.id.cb_address_filter:
	            if (checked)
	            {
	                et_address.setEnabled(true);
	            }
	            else
	            {
	            	et_address.setEnabled(false);
	            	this.address = "";
	            }
	            break;
	        case R.id.cb_category_filter:
	            if (checked)
	            	{
	            		if(rb_red.isChecked()) this.category = "Red zone!";
	            		if(rb_orange.isChecked()) this.category = "Orange zone!";
	            		if(rb_yellow.isChecked()) this.category = "Yellow zone!";
	            		
	            		rb_red.setEnabled(true);
	            		rb_orange.setEnabled(true);
	            		rb_yellow.setEnabled(true);
	            	}
	            else
		            {
		            	rb_red.setEnabled(false);
		            	rb_orange.setEnabled(false);
		            	rb_yellow.setEnabled(false);
		            	this.category = "";
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
	        case R.id.cb_user_filter:
	            if (checked)
	            {
	            	et_username.setEnabled(true);
	            }
	            else
	            {
	            	et_username.setEnabled(false);
	            	this.username = "";
	            }
	            break;
	        case R.id.cb_comment_filter:
	            if (checked)
	            {
	            	et_comment.setEnabled(true);
	            }
	            else
	            {
	            	et_comment.setEnabled(false);
	            	this.comment = "";
	            }
	            break;
	        case R.id.cb_comment_username_filter:
	            if (checked)
	            {
	            	et_comment_user.setEnabled(true);
	            }
	            else
	            {
	            	et_comment_user.setEnabled(false);
	            	this.comment_user = "";
	            }
	            break;
	        case R.id.cb_radius:
	             if (checked)
	             {
	              s.setEnabled(true);
	              radius_checked = true;
	             }
	             else
	             {
	              s.setEnabled(false);
	              radius_checked = false;
	   
	             }
	             break;
	        case R.id.cb_location_filter:
	             if (checked)
	             {
	            	 if(rb_current_location.isChecked())
	            	 {
	            		 	double[] d = getlocation();
	            		 	latitude = d[0];
	            		 	longitude = d[1];
	        
	            	 }
	            	 if(rb_location_from_address.isChecked())
	            	 {
	            		 loaction_address_for_radius.setEnabled(true);
	            	 }
	              
	            	 rb_current_location.setEnabled(true);
	            	 rb_location_from_address.setEnabled(true);
	             }
	             else
	             {
		              rb_current_location.setEnabled(false);
		              rb_location_from_address.setEnabled(false);
		              
		              loaction_address_for_radius.setEnabled(false);
	             }
	             break;
	    }
	}
	
	public void onRadioButtonClicked(View view) {
	    // Is the button now checked?
	    boolean checked = ((RadioButton) view).isChecked();
	    
	    // Check which radio button was clicked
	    switch(view.getId()) {
	        case R.id.rb_redzone_filter:
	            if (checked)
	                category="Red zone!";
	
	            break;
	        case R.id.rb_orangezone_filter:
	            if (checked)
	            	category="Orange zone!";

	            break;
	        case R.id.rb_yellowzone_filter:
	            if (checked)
	            	category="Yellow zone!";
	           
	            break;
	        case R.id.rb_current_location:
	            if(checked)
	            {
	               double[] d = getlocation();
	               latitude = d[0];
	               longitude = d[1];
	               loaction_address_for_radius.setEnabled(false);
	            }
	            break;
	        case R.id.rb_location_from_address:
	            loaction_address_for_radius.setEnabled(true);
	            break;
	    }
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
	
	 public void convertAddress() {
		  //this.location = "Копитарева, Niš";
		     if (this.location != null ) {
		         try {
		             List<Address> addressList = geoCoder.getFromLocationName(this.location, 1);
		             if (addressList != null && addressList.size() > 0) {
		                 latitude = addressList.get(0).getLatitude();
		                 longitude = addressList.get(0).getLongitude();
		             }
		         } catch (Exception e) {
		             e.printStackTrace();
		         } // end catch
		     } // end if
		 } // end convertAddress
		
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		CheckBox cb= (CheckBox)findViewById(R.id.cb_address_filter);
    	if(cb.isChecked())
    		this.address = et_address.getText().toString();
    	
    	cb = (CheckBox)findViewById(R.id.cb_desc_filter);
    	if(cb.isChecked())
    		this.description = et_description.getText().toString();
    	
    	cb = (CheckBox)findViewById(R.id.cb_user_filter);
    	if(cb.isChecked())
    		this.username = et_username.getText().toString();
    	
    	
    	cb = (CheckBox)findViewById(R.id.cb_comment_filter);
    	if(cb.isChecked())
    		this.comment = et_comment.getText().toString();
    	
    	
    	cb = (CheckBox)findViewById(R.id.cb_comment_username_filter);
    	if(cb.isChecked())
    		this.comment_user = et_comment_user.getText().toString();
    	
    	cb = (CheckBox)findViewById(R.id.cb_radius);
        if(cb.isChecked())
        	this.radius= Float.parseFloat(s.getSelectedItem().toString());
        
        if(rb_location_from_address.isChecked())
        	this.location = loaction_address_for_radius.getText().toString();
    	
        switch(v.getId())
        {
         
         case R.id.btn_show_filltered_markers_on_map:
          show_markers_in_list=false;
          break;
                  case R.id.btn_show_filltered_markers:
                   show_markers_in_list = true;
                   break;

        }
        
        
        CheckBox cb2= (CheckBox)findViewById(R.id.cb_category_filter);
           CheckBox cb3= (CheckBox)findViewById(R.id.cb_location_filter);
           if(cb.isChecked())
           {
            if(!cb2.isChecked() || !cb3.isChecked())
             Toast.makeText(FilterActivity.this, "Check if you selected category or location", Toast.LENGTH_LONG).show();
            else
             new GetMarkersBySearch().execute(); 
           }
           else if(cb3.isChecked())
           {
            if(!cb2.isChecked() || !cb.isChecked())
             Toast.makeText(FilterActivity.this, "Check if you selected category or radius", Toast.LENGTH_LONG).show();
            else
             new GetMarkersBySearch().execute();
           }
           else new GetMarkersBySearch().execute();
          
         
		
	}
	
	@Override
	  public void onBackPressed() {
	    this.getParent().onBackPressed();   
	  }
	
	
	class GetMarkersBySearch extends AsyncTask<Void, Void, Integer>
	{

		@Override
		protected Integer doInBackground(Void... paramss) {
			// TODO Auto-generated method stub
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			
			params.add(new BasicNameValuePair("address", address));
	        params.add(new BasicNameValuePair("category", category));
	        params.add(new BasicNameValuePair("description", description));
	        params.add(new BasicNameValuePair("username", username));
	        params.add(new BasicNameValuePair("comment", comment));
	        params.add(new BasicNameValuePair("username_comment", comment_user));
	       
	        
	        CheckBox cb = (CheckBox) findViewById(R.id.cb_date_filter);
	         
	        if(cb.isChecked()) params.add(new BasicNameValuePair("date_checked", "1"));
	        else params.add(new BasicNameValuePair("date_checked", "0"));
	        
	        CheckBox cb1 = (CheckBox) findViewById(R.id.cb_comment_filter);
	        CheckBox cb2 = (CheckBox) findViewById(R.id.cb_comment_username_filter);
	        
	        if(cb1.isChecked() || cb2.isChecked()) params.add(new BasicNameValuePair("comment_checked", "1"));
	        else params.add(new BasicNameValuePair("comment_checked", "0"));
	        
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
	        
	        
	        params.add(new BasicNameValuePair("begintime", dt_begin.getYear()+"/"+begin_month+"/"+begin_day));
	        params.add(new BasicNameValuePair("endtime",dt_end.getYear()+"/"+end_month+"/"+end_day));
        
	       
        	JSONObject json = jParser.makeHttpRequest(URL, "GET", params);
 
            try {
                // Checking for SUCCESS TAG
	                int success = json.getInt(TAG_SUCCESS);
	 
	                if (success == 1)
	                {
	                    // products found
	                    // Getting Array of Products
	                	markers_response = json.getJSONArray(TAG_MARKERS);
	                	if(markers_response==null)
	                		Toast.makeText(FilterActivity.this, "No markers found!", Toast.LENGTH_LONG).show();
	                	else
	                	{
	                		markers = new Marker[markers_response.length()];
	 
	                    // looping through All Products
	                    for (int i = 0; i < markers_response.length(); i++)
	                    {
	                        JSONObject c = markers_response.getJSONObject(i);
	 
	                        markers[i] = new Marker();
	                        // Storing each json item in variable
	                        markers[i].setAddress(c.getString(TAG_ADDRESS));
	                        markers[i].setUsername(c.getString(TAG_USERNAME));
	                        markers[i].setCategory( c.getString(TAG_CATEGORY));
	                        markers[i].setDescription(c.getString(TAG_DESC));
	                        markers[i].setAddingTime(c.getString(TAG_TIME));
	                        markers[i].setLong(c.getDouble(TAG_LONG));
	                        markers[i].setLat(c.getDouble(TAG_LAT));
	 
	                   
	                    }
	                	}
	                }
	                else
	                {
	                	Toast.makeText(FilterActivity.this, "No markers found!", Toast.LENGTH_LONG).show();
	                }
            	}
            	catch (JSONException e)
            	{
            		Toast.makeText(FilterActivity.this, "GRESKA JSON", Toast.LENGTH_LONG).show();
            	}
            finishedTask=1;
            return finishedTask;
		}
		@Override
		protected void onPostExecute(Integer result)
		{
            if(result==1)
            {
            	if(radius_checked)
                {
                 
                 if(rb_location_from_address.isChecked()) 
                  convertAddress();
                  
                 
                 float distance[] = new float[2];
                 
                 for( int i=0 ; i < markers.length; i++ )
                 {
	                  Location.distanceBetween(markers[i].latitude,
	                    markers[i].longitude, latitude,
	                             longitude, distance);
	    
	                  if (distance[0] <= radius)
	                  {
	                    markers_in_radius.add(markers[i]);
	                  }
                 }
                 
                 markers=null;
                 markers=new Marker[markers_in_radius.size()];
                 for( int i=0 ; i < markers.length; i++ )
                 {
	                  markers[i] = new Marker();
	                  markers[i]=markers_in_radius.get(i);
                 }
                 
                }
            	if(show_markers_in_list == true)
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
             
            
        }
		
	}
	
	
	
	

}
