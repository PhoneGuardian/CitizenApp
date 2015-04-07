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
	boolean show_events_in_list = false;
	boolean radius_checked = false;
	 
	ArrayList<Marker> events_in_radius;
	Geocoder geoCoder ;

    String msg;
	
	JSONParser jParser = new JSONParser();
    
    private static String URL = "http://nemanjastolic.co.nf/guardian/get_events_by_filter.php";
 
    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_EVENTS = "events";
    
    private static final String TAG_ADDRESS = "address";
    private static final String TAG_USER_PHONE = "user_phone";
    private static final String TAG_TYPE_OF_EVENT = "type_of_event";
    private static final String TAG_DESC = "description";
    
    private static final String TAG_EVENT_TIME = "event_time";
    private static final String TAG_LAT = "lat";
    private static final String TAG_LNG = "lng";

    private static final String TAG_EVENT_ID = "event_id";
    private static final String TAG_LOCATION_ACC= "location_acc";
    private static final String TAG_ANONYMOUS = "anonymous";
    
    
   
 
    // products JSONArray
    JSONArray events_response = null;
	
	protected boolean filterOnOff = false;
	
	DatePicker dt_begin;
	DatePicker dt_end;
	
	EditText et_address;
	EditText et_description;
	EditText loaction_address_for_radius;
	
	RadioGroup rg_category;
	
	RadioButton rb_fire;
	RadioButton rb_emergency;
	RadioButton rb_police;
	
	Spinner s;
	
	RadioButton rb_current_location;
	RadioButton rb_location_from_address;
	
	String address="";
	String description="";
	String type_of_event="";
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

		
		loaction_address_for_radius = (EditText)findViewById(R.id.et_loaction_address_for_radius);
		
		rg_category = (RadioGroup)findViewById(R.id.rg_category);

		rb_fire = (RadioButton)findViewById(R.id.rb_fire_filter);
		rb_emergency = (RadioButton)findViewById(R.id.rb_emergency_filter);
		rb_police = (RadioButton)findViewById(R.id.rb_police_filter);

		dt_begin = (DatePicker) findViewById(R.id.datepicker_from);
		dt_end = (DatePicker) findViewById(R.id.datepicker_to);
		
		rb_current_location = (RadioButton)findViewById(R.id.rb_current_location);
		rb_location_from_address = (RadioButton)findViewById(R.id.rb_location_from_address);
		
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
		         this, R.array.spinner, android.R.layout.simple_spinner_item );
		       adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
		     
	    s = (Spinner) findViewById( R.id.radius_near_me );
	    s.setAdapter( adapter );
	     
	    events_in_radius = new ArrayList<Marker>();
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
	            		if(rb_fire.isChecked()) this.type_of_event = "F";
	            		if(rb_emergency.isChecked()) this.type_of_event = "E";
	            		if(rb_police.isChecked()) this.type_of_event = "P";

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
    	

    	
    	cb = (CheckBox)findViewById(R.id.cb_radius);
        if(cb.isChecked())
        	this.radius= Float.parseFloat(s.getSelectedItem().toString());
        
        if(rb_location_from_address.isChecked())
        	this.location = loaction_address_for_radius.getText().toString();
    	
        switch(v.getId())
        {
         
         case R.id.btn_show_filtered_events_on_map:
            show_events_in_list=false;
            break;
         case R.id.btn_show_filtered_events:
            show_events_in_list = true;
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
	                int success = json.getInt(TAG_SUCCESS);
	 
	                if (success == 1)
	                {

	                	events_response = json.getJSONArray(TAG_EVENTS);
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
                                markers[i].setAddress(c.getString(TAG_ADDRESS));
                                markers[i].setUser_phone(c.getString(TAG_USER_PHONE));
                                markers[i].setType_of_event( c.getString(TAG_TYPE_OF_EVENT));
                                markers[i].setDescription(c.getString(TAG_DESC));
                                markers[i].setEvent_time(c.getString(TAG_EVENT_TIME));
                                markers[i].setLng(c.getDouble(TAG_LNG));
                                markers[i].setLat(c.getDouble(TAG_LAT));
                                markers[i].id = c.getString(TAG_EVENT_ID);
                                markers[i].setLocation_acc(Float.parseFloat(c.getString(TAG_LOCATION_ACC)));
                                markers[i].setAnonymous(c.getInt(TAG_ANONYMOUS));


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
            if(result==1)
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
             
            
        }
		
	}
	
	
	
	

}
