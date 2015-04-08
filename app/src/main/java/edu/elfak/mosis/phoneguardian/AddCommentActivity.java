package edu.elfak.mosis.phoneguardian;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;

import android.os.AsyncTask;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.TextView;
import android.widget.Toast;

public class AddCommentActivity extends Activity implements android.view.View.OnClickListener {

    Tags t;

    final JSONParser jParser = new JSONParser();

	String username = User.getInstance().getUsername();
	String id;
	String id_comment;

    String URL = "http://nemanjastolic.co.nf/guardian/add_comment.php";

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	TextView t_username;
	TextView t_date;
	EditText et_comment;
	Button btn_add_comment;

	String[] args = new String[4];
	
	Marker m;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addcomment_activity);

		m = (Marker) getIntent().getSerializableExtra("marker");
		
		t_username =(TextView) findViewById(R.id.label_username_comment);
		t_username.setText(username);
		
		t_date =(TextView) findViewById(R.id.label_date_comment);
		Calendar cal = Calendar.getInstance();
		t_date.setText(dateFormat.format(cal.getTime()));

		et_comment = (EditText)findViewById(R.id.et_comment);

		btn_add_comment = (Button) findViewById(R.id.btn_add_comment);
		btn_add_comment.setOnClickListener(this);

		
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
			case R.id.btn_add_comment:
				if(et_comment.getText().toString()!="")
				{

					args[0] = m.id; //event_id
					args[1] = username; //username
					args[2] = t_date.getText().toString(); //date of adding a comment
					args[3] = et_comment.getText().toString(); //text of the comment


					new AddComment().execute(args);
				}
				else
					Toast.makeText(AddCommentActivity.this, "Field comment is empty!", Toast.LENGTH_LONG).show();
				break;

					
				
			}
		
	}

	
	class AddComment extends AsyncTask<String, String, String> {
	   	 
        
		int success;
		String msg;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        protected String doInBackground(String... argss) {

            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair("id_event", argss[0]));
	        params.add(new BasicNameValuePair("username", argss[1]));
	        params.add(new BasicNameValuePair("comment_date", argss[2]));
	        params.add(new BasicNameValuePair("comment_text", argss[3]));

            JSONObject json = jParser.makeHttpRequest(URL, "GET", params);
 
            try 
            {
                success = json.getInt(t.TAG_SUCCESS);
                msg = json.getString(t.TAG_MESSAGE);
                id_comment = json.getString(t.TAG_ID);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
 
            return null;
        }
 

        protected void onPostExecute(String file_url)
        {
            Toast.makeText(AddCommentActivity.this,msg,Toast.LENGTH_SHORT).show();
        }

    }


}
