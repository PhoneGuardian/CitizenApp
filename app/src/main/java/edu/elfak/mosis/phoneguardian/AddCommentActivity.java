package edu.elfak.mosis.phoneguardian;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class AddCommentActivity extends Activity implements android.view.View.OnClickListener {
	
	int camera_gallery = 0;
	int i = 0;
	String username;
	String id;
	String id_comment;
	
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
	
	TextView t_username;
	TextView t_date;
	RatingBar rate;
	EditText et_comment;
	ImageView img_comment;
	Button btn_add_comment_pic;
	Button btn_add_comment;
	
	String[] argss = new String[7];
	
	String photo_dest; //destination of photo in phone
	Uri selectedImage; 
	
	Marker m;
	
	static final int REQUEST_IMAGE_CAPTURE = 1;
	final String TAG_SUCCESS = "success";
	final String TAG_MESSAGE = "message";
	final String TAG_ID = "id";
	final JSONParser jParser = new JSONParser();
	String URL = "";
	
	File photo;
	int serverResponseCode = 0;
	ProgressDialog dialog = null;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addcomment_activity);
		
		username = getIntent().getStringExtra("USERNAME");
		m = (Marker) getIntent().getSerializableExtra("marker");
		
		t_username =(TextView) findViewById(R.id.label_username_comment);
		t_username.setText(username);
		
		t_date =(TextView) findViewById(R.id.label_date_comment);
		Calendar cal = Calendar.getInstance();
		t_date.setText(dateFormat.format(cal.getTime()));
		
		rate = (RatingBar) findViewById(R.id.rating_bar);
		
		et_comment = (EditText)findViewById(R.id.et_comment);
		img_comment = (ImageView) findViewById(R.id.img_comment_picture);
		
		btn_add_comment = (Button) findViewById(R.id.btn_add_comment);
		btn_add_comment.setOnClickListener(this);
		
		btn_add_comment_pic = (Button) findViewById(R.id.btn_comment_picture);
		btn_add_comment_pic.setOnClickListener(this);
		
		
		
	}
	
	@Override
	  public void onBackPressed() {
	    this.getParent().onBackPressed();   
	  }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
			case R.id.btn_add_comment:
				if(et_comment.getText().toString()!="")
				{
					//////////////// KAZI TASICU KOJI JE URL  //////////////////
					URL = "http://nikolamilica10.site90.com/add_comment.php";
					
					argss[0] = m.id;
					argss[1] = t_username.getText().toString();
					argss[2] = t_date.getText().toString();
					argss[3] = Float.toString(rate.getRating());
					argss[4] = et_comment.getText().toString();
					
					
					
					new AddComment().execute(argss);
				}
				else
					Toast.makeText(AddCommentActivity.this, "Field comment is empty!", Toast.LENGTH_LONG).show();
				break;
			case R.id.btn_comment_picture:
				String[] addPhoto=new String[]{ "Camera" , "Gallery" };
	            AlertDialog.Builder dialog=new AlertDialog.Builder(this);
	            dialog.setTitle("Chooser");

	            dialog.setItems(addPhoto,new DialogInterface.OnClickListener(){
	                @Override
	                public void onClick(DialogInterface dialog, int id) {

	                    if(id==0){
	                    	
	                    	i++;
	            			//create parameters for Intent with filename
	                    	camera_gallery=0;
	                    	String fileName = "glupost"+Integer.toString(i);
	            			ContentValues values = new ContentValues();
	            			values.put(MediaStore.Images.Media.TITLE, fileName);
	            			values.put(MediaStore.Images.Media.DESCRIPTION,"Image capture by camera");
	            			//imageUri is the current activity attribute, define and save it for later usage (also in onSaveInstanceState)
	            			selectedImage = getContentResolver().insert(
	            			        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
	            			//create new Intent
	            			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	            			intent.putExtra(MediaStore.EXTRA_OUTPUT, selectedImage);
	            			startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
	                    }
	                    if(id==1)
	                    {
	                    	camera_gallery=1;
	                    	Intent intent = new Intent();
	            			intent.setType("image/*");
	            			intent.setAction(Intent.ACTION_GET_CONTENT);
	            			startActivityForResult(Intent.createChooser(intent, "Select Picture"),1);
	                    }
	                }
	            });     

	            dialog.setNeutralButton("cancel",new android.content.DialogInterface.OnClickListener(){
	                @Override
	                public void onClick(DialogInterface dialog, int which) {

	                    dialog.dismiss();               
	                }});
	            dialog.show();
			
				break;
					
				
			}
		
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{

		ImageView  img = (ImageView) findViewById(R.id.img_comment_picture);
		if(camera_gallery==0)
		{
			
	        photo_dest= getRealPathFromURI(selectedImage);
	        img.setImageURI(selectedImage);
		}
		else
		{
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
        
	}


	public String getRealPathFromURI(Uri contentUri) {
	    String [] proj={MediaColumns.DATA};
	    android.database.Cursor cursor = managedQuery( contentUri,proj,null, null, null);     // Order-by clause (ascending by name)
	    int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
	    cursor.moveToFirst();
	    return cursor.getString(column_index);
	    
	}
	
	class AddComment extends AsyncTask<String, String, String> {
	   	 
        
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

            List<NameValuePair> params = new ArrayList<NameValuePair>();
          
	            params.add(new BasicNameValuePair("username_comment", argss[1]));
	            params.add(new BasicNameValuePair("time", argss[2]));
	            params.add(new BasicNameValuePair("rate", argss[3]));
	            params.add(new BasicNameValuePair("comment", argss[4]));
	            
	            params.add(new BasicNameValuePair("id_marker", argss[0]));

            JSONObject json = jParser.makeHttpRequest(URL, "GET", params);

 
            try 
            {
              
                success = json.getInt(TAG_SUCCESS);
                msg = json.getString(TAG_MESSAGE);
                id_comment = json.getString(TAG_ID);
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
                		Toast.makeText(AddCommentActivity.this, "GRESKA - asinhroni task!" , Toast.LENGTH_LONG).show();
                	else
                		Toast.makeText(AddCommentActivity.this, "Proslo je - addlocation activity asinhroni task" , Toast.LENGTH_LONG).show();
                	
                }
            });
        	
        	dialog = ProgressDialog.show(AddCommentActivity.this, "", "Uploading information of location...", true);
			new Thread(new Runnable() {
                public void run() {
                     runOnUiThread(new Runnable() {
                            public void run() {
                                
                            }
                        });                     
                    uploadFile(photo_dest);                   
                }
              }).start(); 
        	
        }
 
    }
	
	
	
	
	 public void uploadFile(String sourceFileUri)
	 {
		 //////////////// KAZI TASICU KOJI JE URL  //////////////////
         String upLoadServerUri = "http://nikolamilica10.site90.com/upload_photo_of_comment.php"; 
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
              dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""+argss[0]+"_"+id_comment+".jpg" + "\"" + lineEnd);
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

              dos.writeBytes(lineEnd);
              dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

              serverResponseCode = conn.getResponseCode();
              String serverResponseMessage = conn.getResponseMessage();
               
              Log.i("uploadFile", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);
              if(serverResponseCode == 200){
                  runOnUiThread(new Runnable() {
                       public void run() {
                           Toast.makeText(AddCommentActivity.this, "Successfully upload information of location!", Toast.LENGTH_SHORT).show();
                       }
                   });               
              }   

              fileInputStream.close();
              dos.flush();
              dos.close();
               
         } catch (MalformedURLException ex) { 
             dialog.dismiss(); 
             ex.printStackTrace();
             Toast.makeText(AddCommentActivity.this, "MalformedURLException", Toast.LENGTH_SHORT).show();
             Log.e("Upload file to server", "error: " + ex.getMessage(), ex); 
         } catch (Exception e) {
             dialog.dismiss(); 
             e.printStackTrace();
             Toast.makeText(AddCommentActivity.this, "Exception : " + e.getMessage(), Toast.LENGTH_SHORT).show();
             Log.e("Upload file to server Exception", "Exception : " + e.getMessage(), e); 
         }
         dialog.dismiss();      
        }

}
