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

	ProgressDialog dialog = null;
	int serverResponseCode = 0;
	String photo_dest; //destination of photo in phone
	Uri selectedImage; 
	int pom = 0;

	final JSONParser jParser = new JSONParser();
	private String url_get_all_users = "http://nikolamilica10.site90.com/webaplication.php";
	
	

    final String TAG_SUCCESS = "success";

    public int success=0;
    String argss[] = new String[4];
	Bitmap bmap;
	byte[] b;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_actvity);
        
        Button btnFinish = (Button) findViewById(R.id.btn_finished);
        btnFinish.setOnClickListener(this);
        
        Button btnSelectPic = (Button) findViewById(R.id.btn_select_picture);
        btnSelectPic.setOnClickListener(this);
        
        EditText etPhone = (EditText) findViewById(R.id.et_register_phone_num);
		etPhone.setText(this.GetCountryZipCode());

    }
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) 
    {

        ImageView  img = (ImageView) findViewById(R.id.img_selected_picture);
 
        if (resultCode == RESULT_OK) 
        {
        	if(data.getData() != null)
	        {
	        	selectedImage = data.getData();
	        }
        	else
        	{
		        Toast.makeText(getApplicationContext(), "Failed to get Image!", Toast.LENGTH_LONG).show();
        	}
            if (requestCode == 1)
            {
 
            		selectedImage = data.getData();
		            photo_dest= getRealPathFromURI(selectedImage);
		            img.setImageURI(selectedImage);
            }
         }
    }
   
    public String getRealPathFromURI(Uri contentUri) {
	    String [] proj={MediaColumns.DATA};
	    android.database.Cursor cursor = managedQuery( contentUri,proj,null, null, null); 
	    int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
	    cursor.moveToFirst();
	    return cursor.getString(column_index);
	    
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
			case R.id.btn_select_picture:
				
				Intent intent = new Intent();
    			intent.setType("image/*");
    			intent.setAction(Intent.ACTION_GET_CONTENT);
    			startActivityForResult(Intent.createChooser(intent, "Select Picture"),1);
				break;
			case R.id.btn_finished:
				EditText etUsername = (EditText) findViewById(R.id.et_login_username);
				String userNm =etUsername.getText().toString();
				EditText etPass = (EditText) findViewById(R.id.et_register_password1);
				String pass =etPass.getText().toString();
				EditText etRepPass = (EditText) findViewById(R.id.et_register_password2);
				String repeatPass =etRepPass.getText().toString();
				EditText etName = (EditText) findViewById(R.id.et_register_name_lastname);
				String name =etName.getText().toString();
				EditText etPhone = (EditText) findViewById(R.id.et_register_phone_num);
				String phone =etPhone.getText().toString();
				
				
				if(userNm.compareTo("")==0 || pass.compareTo("")==0 || name.compareTo("")==0 || phone.compareTo("")==0)
					Toast.makeText(this, "Invalid input data!", Toast.LENGTH_LONG).show();
				else
					if(pass.compareTo(repeatPass) != 0)
					{
						Toast.makeText(this, "Password and repeated password are not the same!", Toast.LENGTH_LONG).show();
					}
					else
					{
						
						try
						{
							url_get_all_users = "http://nikolamilica10.site90.com/webaplication.php";
							argss[0]= userNm;
							new CheckUser().execute().get();
							
						} catch (InterruptedException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ExecutionException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						if( success==1 )
						{
							url_get_all_users = "http://nikolamilica10.site90.com/adduser.php";
							
							dialog = ProgressDialog.show(RegisterActivity.this, "", "Uploading account information...", true);
		                    
							new Thread(new Runnable() {
		                        public void run() {
		                             runOnUiThread(new Runnable() {
		                                    public void run() {
		                                        
		                                    }
		                                });                     
		                            uploadFile(photo_dest);                   
		                        }
		                      }).start(); 
							
							
							
							argss[1]=pass;
							argss[2]=name;
							argss[3]=phone;
							try
							{
								new CheckUser().execute().get();
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
         * getting All products from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            if(success==1)
            {

	            params.add(new BasicNameValuePair("password", argss[1]));
	            params.add(new BasicNameValuePair("name", argss[2]));
	            params.add(new BasicNameValuePair("phone", argss[3]));
	            pom=1;
	            
            }
            params.add(new BasicNameValuePair("username", argss[0]));
            JSONObject json = jParser.makeHttpRequest(url_get_all_users, "POST", params);

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
        @Override
        protected void onPostExecute(String file_url) {
        	
        	
        	runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                	if( success == 0 ) 
                		Toast.makeText(RegisterActivity.this, "Username already exist!" , Toast.LENGTH_LONG).show();
                	
                	
                }
            });
        	
        }
 
    }
	
	
	//////slika
	 public void uploadFile(String sourceFileUri)
	 {
         String upLoadServerUri = "http://nikolamilica10.site90.com/upload_photo_of_user.php";
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
         
         try
         { 
	          FileInputStream fileInputStream = new FileInputStream(sourceFile);
	          URL url = new URL(upLoadServerUri);
	          conn = (HttpURLConnection) url.openConnection();
	          conn.setDoInput(true); 
	          conn.setDoOutput(true); 
	          conn.setUseCaches(false); 
	          conn.setRequestMethod("POST");
	          conn.setRequestProperty("Connection", "Keep-Alive");
	          conn.setRequestProperty("ENCTYPE", "multipart/form-data");
	          conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
	          conn.setRequestProperty("uploaded_file", fileName);
	          dos = new DataOutputStream(conn.getOutputStream());
	
	          dos.writeBytes(twoHyphens + boundary + lineEnd);
	          dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""+ argss[0]+".jpg" + "\"" + lineEnd);
	          dos.writeBytes(lineEnd);
	
	          bytesAvailable = fileInputStream.available(); // create a buffer of  maximum size
	
	          bufferSize = Math.min(bytesAvailable, maxBufferSize);
	          buffer = new byte[bufferSize];
	
	          bytesRead = fileInputStream.read(buffer, 0, bufferSize); 
	            
	          while (bytesRead > 0) {
	            dos.write(buffer, 0, bufferSize);
	            bytesAvailable = fileInputStream.available();
	            bufferSize = Math.min(bytesAvailable, maxBufferSize);
	            bytesRead = fileInputStream.read(buffer, 0, bufferSize);              
	           }
	
	
	          dos.writeBytes(lineEnd);
	          dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
	
	          serverResponseCode = conn.getResponseCode();
	          String serverResponseMessage = conn.getResponseMessage();
	           
	          Log.i("uploadFile", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);
	          if(serverResponseCode == 200){
	              runOnUiThread(new Runnable() {
	                   public void run() {
	                       Toast.makeText(RegisterActivity.this, "Successfully created account!", Toast.LENGTH_SHORT).show();
	                   }
	               });               
	          }   
	
	              fileInputStream.close();
	              dos.flush();
	              dos.close();
               
         }
         catch (MalformedURLException ex)
         { 
             dialog.dismiss(); 
             ex.printStackTrace();
             Toast.makeText(RegisterActivity.this, "MalformedURLException", Toast.LENGTH_SHORT).show();
             Log.e("Upload file to server", "error: " + ex.getMessage(), ex); 
         }
         catch (Exception e)
         {
             dialog.dismiss(); 
             e.printStackTrace();
             Toast.makeText(RegisterActivity.this, "Exception : " + e.getMessage(), Toast.LENGTH_SHORT).show();
             Log.e("Upload file to server Exception", "Exception : " + e.getMessage(), e); 
         }
         dialog.dismiss();      
        }

}
