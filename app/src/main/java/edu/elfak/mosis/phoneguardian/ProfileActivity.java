package edu.elfak.mosis.phoneguardian;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
import android.widget.TextView;

public class ProfileActivity extends Activity {
	
	String username;
	User user;

	final JSONParser jParser = new JSONParser();
	private String url_get_user = "http://nikolamilica10.site90.com/get_user.php";
	
    final String TAG_SUCCESS = "success";
    final String TAG_USERNAME = "username";
    final String TAG_NAME = "name";
    final String TAG_PHONE = "phone";
    
    public int success=0;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile_activity);
		
		username = getIntent().getStringExtra("USERNAME");
		user = new User(username);
		
		ImageView photo = (ImageView) findViewById(R.id.img_profile_picture);
		
		 try
         {
			 URL url = new URL("http://nikolamilica10.site90.com/photos_of_users/"+username+".jpg");
			 Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
			 photo.setImageBitmap(bmp);
         }
         catch(Exception e)
         {
        	 
         };
		
         
         new GetUser().execute();
         
         
        
	}
	
	class GetUser extends AsyncTask<String, String, String> {
	   	 
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
        protected String doInBackground(String... args)
        {

            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair("username", username));
            JSONObject json = jParser.makeHttpRequest(url_get_user, "GET", params);
            try 
            {
                user.name_lastname = json.getString(TAG_NAME);
                user.phone = json.getString(TAG_PHONE);
                user.username = json.getString(TAG_USERNAME); 
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
            
        	TextView usernameTextView = (TextView) findViewById(R.id.username);
            usernameTextView.setText(user.getUsername());
            TextView nameTextView = (TextView) findViewById(R.id.name);
            nameTextView.setText(user.getName_lastname());
            TextView phoneTextView = (TextView) findViewById(R.id.phone_num);
            phoneTextView.setText(user.getPhone());
        }
 
    }

}