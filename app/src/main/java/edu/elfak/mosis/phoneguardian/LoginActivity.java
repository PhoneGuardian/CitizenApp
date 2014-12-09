package edu.elfak.mosis.phoneguardian;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity implements android.view.View.OnClickListener {
	
	String argss[] = new String[2];
	
	private String url_check_user = "http://nikolamilica10.site90.com/get_signin_user.php";
	
	final String TAG_SUCCESS = "success";
    public int success=0;
    
    final JSONParser jParser = new JSONParser();
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);
		
		Button btnSingIn = (Button) findViewById(R.id.btn_login_signin);
		btnSingIn.setOnClickListener(this);
		
		Button btnSignUp = (Button) findViewById(R.id.btn_login_signup);
		btnSignUp.setOnClickListener(this);
        
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
			case R.id.btn_login_signin:
				EditText etUsername = (EditText) findViewById(R.id.login_username);
		        EditText textPassword = (EditText) findViewById(R.id.login_password);
		        argss[0]=etUsername.getText().toString();
		        argss[1]=textPassword.getText().toString();
				try {
					new CheckSignInUser().execute().get();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ExecutionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if(success==1)
				{	
					Intent i1 = new Intent(this,AlertActivity.class);
					i1.putExtra("USERNAME", etUsername.getText().toString());
					startActivity(i1);
				}
				break;
            case R.id.btn_login_signup:
                Intent i2 = new Intent(this,RegisterActivity.class);
                startActivity(i2);
                break;
		}
		
	}
	
	class CheckSignInUser extends AsyncTask<String, String, String> {
	   	 
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
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
          
	        params.add(new BasicNameValuePair("password", argss[1]));
            params.add(new BasicNameValuePair("username", argss[0]));
            
            JSONObject json = jParser.makeHttpRequest(url_check_user, "POST", params);

            try 
            {
                success = json.getInt(TAG_SUCCESS); 
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
            
        	runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                	if( success == 0 ) 
                		Toast.makeText(LoginActivity.this, "Username does not exist!" , Toast.LENGTH_LONG).show();
                	else
                		if(success==2)
                			Toast.makeText(LoginActivity.this, "Incorrect password!" , Toast.LENGTH_LONG).show();
                	
                }
            });
        	
        }
 
    }

}
