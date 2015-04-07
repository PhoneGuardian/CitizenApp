package edu.elfak.mosis.phoneguardian;


import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.app.ListActivity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import android.widget.TextView;
import android.widget.Toast;

public class ViewCommentsActivity extends ListActivity {
	
	String username;
	
	static final int REQUEST_IMAGE_CAPTURE = 1;
	final String TAG_SUCCESS = "success";
	final String TAG_MESSAGE = "message";
	ArrayList<Comment> list = new ArrayList<Comment>();
	final String TAG_MARKER_ID = "id_marker";
	final String TAG_ID = "id";
	final String TAG_USERNAME_COMMENT = "username_comment";
	final String TAG_TIME = "time";
	//ListView listview = (ListView) findViewById(R.id.listview_comment);
	final String TAG_RATE = "rate";
	final String TAG_COMMENT = "comment";
	final String TAG_COMMENTS = "comments";
	final JSONParser jParser = new JSONParser();
	String URL = "http://nikolamilica10.site90.com/get_all_comments_for_marker.php";
	Context ctx;
	Comment comments[];
	Marker m;
	JSONArray comments_response = null;
	
	TextView t;
	
	String id_marker = "";
	
	int success;
	
	public CommentArrayAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.viewcomment_activity);
		ctx = getApplicationContext();

		m = (Marker) getIntent().getSerializableExtra("marker");
		id_marker = m.id;
		
		t= (TextView) findViewById(R.id.tv_empty);
	    t.setVisibility(View.GONE);
	   
	    new GetAllComments().execute();

	   
	}
	
	@Override
	  public void onBackPressed() {
	    this.getParent().onBackPressed();   
	  }
	
	class GetAllComments extends AsyncTask<String, String, String> {
	   	 
        
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
            //FOTOGRAFIJA NIJE DODATA!!! TO NAKNADNO!!!!!!
	            params.add(new BasicNameValuePair("id_marker", id_marker));
	            
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(URL, "GET", params);

            try {
                // Checking for SUCCESS TAG
	                success = json.getInt(TAG_SUCCESS);
	 
	                if (success == 1)
	                {
	                    // products found
	                    // Getting Array of Products
	                	comments_response = json.getJSONArray(TAG_COMMENTS);
	                	if(comments_response==null)
	                		Toast.makeText(ViewCommentsActivity.this, "No comments found!", Toast.LENGTH_LONG).show();
	                	else
	                	{
	                		comments = new Comment[comments_response.length()];
	 
	                    // looping through All Products
	                    for (int i = 0; i < comments_response.length(); i++)
	                    {
	                        JSONObject c = comments_response.getJSONObject(i);
	 
	                        comments[i] = new Comment();
	                        // Storing each json item in variable
	                        comments[i].id = c.getString(TAG_ID);
	                        comments[i].id_event = c.getString(TAG_MARKER_ID);
	                        comments[i].username = c.getString(TAG_USERNAME_COMMENT);
	                        comments[i].comment_date = c.getString(TAG_TIME);
	                        comments[i].comment_text = c.getString(TAG_COMMENT);
	                   
	                    }
	                	}
	                }
	                else
	                {
	                	//Toast.makeText(ViewCommentsActivity.this, "No comments found!", Toast.LENGTH_LONG).show();
	                }
            	}
            	catch (JSONException e)
            	{
            		//Toast.makeText(ViewCommentsActivity.this, "GRESKA JSON", Toast.LENGTH_LONG).show();
            	}
 
 
            return null;
        }
 
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
        	
        	
     	    /*for (int i = 0; i < comments.length; ++i) {
     	      list.add(comments[i]);
     	    }*/
     	    
     	    //adapter = new CommentArrayAdapter(ViewCommentsActivity.this,R.layout.view_comments_list_item, list);
     	    //listview.setAdapter(adapter);
        	
        	if( success == 0 ) t.setVisibility(View.VISIBLE);
        	else setListAdapter(new CommentArrayAdapter(ctx, R.layout.view_comments_list_item, comments));
        	
        }
 
    }

}
