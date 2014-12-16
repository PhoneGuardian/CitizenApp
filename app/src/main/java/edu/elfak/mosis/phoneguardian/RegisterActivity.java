package edu.elfak.mosis.phoneguardian;


import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore.MediaColumns;
import android.telephony.TelephonyManager;
import android.util.Log;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class RegisterActivity extends Activity implements OnClickListener {


	int serverResponseCode = 0;

	int pom = 0;

	final JSONParser jParser = new JSONParser();
	private String url_check_user = "http://nemanjastolic.co.nf/guardian/check_user.php";
    private String url_add_user = "http://nemanjastolic.co.nf/guardian/add_user.php";

    final String TAG_SUCCESS = "success";

    public int success=0;
    String argss[] = new String[2];

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_actvity);
        
        Button btnFinish = (Button) findViewById(R.id.btn_finished);
        btnFinish.setOnClickListener(this);
        
        EditText etPhone = (EditText) findViewById(R.id.et_register_phone_num);
		etPhone.setText(this.GetCountryZipCode());

    }
    

    
    String GetCountryZipCode()
    {

        String CountryID="";
        String CountryZipCode="+";

        TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        CountryID= manager.getSimCountryIso().toUpperCase();
        String[] rl=this.getResources().getStringArray(R.array.CountryCodes);
        for(int i=0;i<rl.length;i++)
        {
              String[] g=rl[i].split(",");
              if(g[1].trim().equals(CountryID.trim()))
              {
                    CountryZipCode+=g[0];
                    break;
              }
        }
        return CountryZipCode;
    }
    

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{

			case R.id.btn_finished:
				EditText etUsername = (EditText) findViewById(R.id.et_login_username);
				String userNm =etUsername.getText().toString();
				EditText etPhone = (EditText) findViewById(R.id.et_register_phone_num);
				String phone =etPhone.getText().toString();

				
				if(userNm.compareTo("")==0 ||  phone.compareTo("")==0)
					Toast.makeText(this, "Invalid input data!", Toast.LENGTH_LONG).show();
				else
					{

						try
						{
							argss[0]= phone;

							new CheckUser().execute().get();//on the top of the class success is initialized to 0 when thread is executed
						} catch (InterruptedException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ExecutionException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						if( success==1 )//if the thread returned 1 we will start it once more to execute adding of user
						{
							argss[1]=userNm;

							try
							{
								new CheckUser().execute().get();//starting of thread
							}
							catch (InterruptedException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							catch (ExecutionException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						
						}
					}
				
				
		}
		
	}

	class CheckUser extends AsyncTask<String, String, String>
	{
	   	 
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
 
        /**
         * getting All users from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
            JSONObject json;
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            if(success==1)
            {

	            params.add(new BasicNameValuePair("username", argss[1]));
                params.add(new BasicNameValuePair("phone_number", argss[0]));

                json = jParser.makeHttpRequest(url_add_user, "GET", params);
	            pom=1;

            }
            else {//so when the thread start success will be 0, and we need to check if the user already exists in db
                params.add(new BasicNameValuePair("phone_number", argss[0]));
                json = jParser.makeHttpRequest(url_check_user, "POST", params);//here we send user's phone and check if he is in the database.
                                                                                // php will return 1 if he doesn't exist, 0 otherwise
            }

            try 
            {
                success = json.getInt(TAG_SUCCESS); //1 if he doesn't exist, 0 otherwise
                //now return to  the line 127
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
        @Override
        protected void onPostExecute(String file_url) {
        	
        	
        	runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                	if( success == 0 ) 
                		Toast.makeText(RegisterActivity.this, "Phone number already exists in our system!" , Toast.LENGTH_LONG).show();
                    else
                    {
                        Toast.makeText(RegisterActivity.this, "User created!" , Toast.LENGTH_LONG).show();
                        Intent i = new Intent(getApplicationContext(),AlertActivity.class );
                        startActivity(i);
                    }

                	
                	
                }
            });
        	
        }
 
    }

}
