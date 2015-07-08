package it.polimi.guardian.citizenapp;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.matesnetwork.callverification.Cognalys;
import com.matesnetwork.interfaces.VerificationListner;

import static android.view.View.OnTouchListener;
import static android.widget.Toast.LENGTH_LONG;

public class RegisterActivity extends Activity implements OnClickListener {


    int serverResponseCode = 0;

    int pom = 0;

    final JSONParser jParser = new JSONParser();
    private String url_check_user = "http://nemanjastolic.co.nf/guardian/check_user.php";
    private String url_add_user = "http://nemanjastolic.co.nf/guardian/add_user.php";

    final String TAG_SUCCESS = "success";
    User u;
    public int success=0;
    String argss[] = new String[2];
    Credentials myCredentials = new Credentials();
    protected Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_actvity);

        Button btnFinish = (Button) findViewById(R.id.btn_signup);
        btnFinish.setOnClickListener(this);

        TextView tvPhoneCountryCode = (TextView) findViewById(R.id.tv_register_mcc);
        tvPhoneCountryCode.setText(Cognalys.getCountryCode(this));

        context = this;
        if(credentialsAlreadySaved()){
            myCredentials = ReadFromCredentialsFile();
            argss[1] = myCredentials.getUsername();
            argss[0] = myCredentials.getPhoneNumber();
            u = User.getInstance();
            u.setPhone(argss[0]);
            u.setUsername(argss[1]);

            Intent i = new Intent(getApplicationContext(),AlertActivity.class );
            i.addCategory(Intent.CATEGORY_HOME);
            i.setFlags(i.FLAG_ACTIVITY_NEW_TASK | i.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i); // Launch the AlertActivity
            finish();         // Close down the RegistersActivity
        }
        else {

            Log.d("RegisterAcitivity - creating file credentials with content {}: ","proceed to registration");
        }

        // hide keyboard on oudside press
        ((RelativeLayout) findViewById(R.id.layout_register)).setOnTouchListener(hideKeyboardlistener);
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

            case R.id.btn_signup:
                EditText etUsername = (EditText) findViewById(R.id.et_login_username);
                final String userNm =etUsername.getText().toString();
                EditText etPhone = (EditText) findViewById(R.id.et_register_phone_num);
                final String phone =etPhone.getText().toString();


                if(userNm.compareTo("")==0 ||  phone.compareTo("")==0)
                    Toast.makeText(this, "Invalid input data!", LENGTH_LONG).show();
                else
                {
                    disableSignUp();
                    Cognalys.verifyMobileNumber(context, "a521b10743611d028e178c577a0bf87daca7963c", "0634cad489ec4b7fb3287d7", phone, new VerificationListner() {
                        @Override
                        public void onVerificationStarted() {

                        }

                        @Override
                        public void onVerificationSucess() {
                            enableSignUp();
                            checkAndSaveUser(userNm, Cognalys.getCountryCode(RegisterActivity.this)+ phone);
                        }

//                        @Override
//                        public void onVerificationSuccess() {
//                            enableSignUp();
//                            checkAndSaveUser(userNm, Cognalys.getCountryCode(RegisterActivity.this)+ phone);
//
//                        }

                        @Override
                        public void onVerificationFailed(ArrayList<String> errorList) {
                            enableSignUp();
                            Toast.makeText(RegisterActivity.this, "You have inserted an incorrect number", LENGTH_LONG).show();
                        }
                    });
                }
        }

    }
    private void enableSignUp(){
        ProgressBar spinner = (ProgressBar)findViewById(R.id.spinner_signup);
        spinner.setVisibility(View.GONE);

        Button signupBtn = (Button) findViewById(R.id.btn_signup);
        signupBtn.setEnabled(true);
        signupBtn.setVisibility(View.VISIBLE);

    }
    private void disableSignUp(){
        ProgressBar spinner = (ProgressBar)findViewById(R.id.spinner_signup);
        spinner.setVisibility(View.VISIBLE);

        Button signupBtn = (Button) findViewById(R.id.btn_signup);
        signupBtn.setEnabled(false);
        signupBtn.setVisibility(View.GONE);
    }

    private void checkAndSaveUser(String userNm, String phone) {
        try
        {
            argss[0]= phone;
            argss[1]= userNm;

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

    protected Boolean credentialsAlreadySaved()
    {
        EditText et_login_username = (EditText) findViewById(R.id.et_login_username);
        String username =et_login_username.getText().toString();
        EditText et_register_phone_num = (EditText) findViewById(R.id.et_register_phone_num);
        String phoneNumber =et_register_phone_num.getText().toString();

        String jsonStringCredentials="";
        // credentials from file
        try{
            FileHelper fileHelper = new FileHelper();
            jsonStringCredentials = fileHelper.readFromFile("credentials.txt");
            if(jsonStringCredentials.equals("") || jsonStringCredentials.equals("{}")){//if file didn't exist

                return false;
            }
            else {
                return true;
            }
        } catch(Exception e){
            e.printStackTrace();
            Log.d("RegisterAcitivity - Error: ",e.getMessage());
            Log.d("RegisterAcitivity - jsonStringCredentials: ",jsonStringCredentials);
        }
        Toast.makeText(context, "File reading failed.", LENGTH_LONG).show();
        return false;
    }
    protected Credentials CreateCredentialsFile(String username, String phoneNumber){

        String jsonStringCredentials="";
        // credentials from file
        try{
            FileHelper fileHelper = new FileHelper();
            //make credentials object and put it inside JSONObject
            JSONObject jobj = new JSONObject();
            Credentials cred = new Credentials();
            cred.setUsername(username);
            cred.setPhoneNumber(phoneNumber);
            jobj.put("Username", username);
            jobj.put("PhoneNumber", phoneNumber);

            jsonStringCredentials = jobj.toString();
            //write stringCredentials into credentials file
            fileHelper.writeToFile(jsonStringCredentials, "credentials.txt");
            Toast.makeText(context, "Credentials are added.", LENGTH_LONG).show();
            //return created credentials object
            return cred;

        } catch(Exception e){
            e.printStackTrace();
            Log.d("RegisterAcitivity - Error: ",e.getMessage());
            Log.d("RegisterAcitivity - jsonStringCredentials: ",jsonStringCredentials);
        }
        return null;
    }
    protected Credentials ReadFromCredentialsFile(){

        String jsonStringCredentials="";
        // credentials from file
        try{
            FileHelper fileHelper = new FileHelper();
            jsonStringCredentials = fileHelper.readFromFile("credentials.txt");
            if(!(jsonStringCredentials.equals("") || jsonStringCredentials.equals("{}"))) {//there are other credentials data  already in the file
                JSONObject jObj;
                Credentials cred = new Credentials();
                // try parse the string to a JSON object
                try {
                    jObj = new JSONObject(jsonStringCredentials);
                } catch (JSONException e) {
                    Log.e("JSON Parser", "Error parsing json data from credentials.txt" + e.toString());
                    jObj = null;
                }
                if(jObj != null) {
                    cred.setUsername(jObj.getString("Username"));
                    cred.setPhoneNumber(jObj.getString("PhoneNumber"));
                    return cred;
                } else {
                    Toast.makeText(context, "File exists, but couldn't parse.", LENGTH_LONG).show();
                    return null;
                }
            }
        } catch(Exception e){
            e.printStackTrace();
            Log.d("RegisterAcitivity - Error: ",e.getMessage());
            Log.d("RegisterAcitivity - jsonStringCredentials: ",jsonStringCredentials);
        }
        return null;
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
                params.add(new BasicNameValuePair("username", argss[1]));
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
                        Toast.makeText(RegisterActivity.this, "Phone number/username combination already exist in our system!" , LENGTH_LONG).show();
                    else
                    {
                        Toast.makeText(RegisterActivity.this, "User created!" , LENGTH_LONG).show();
                        myCredentials.setUsername(argss[1]);
                        myCredentials.setPhoneNumber(argss[0]);
                        u = User.getInstance();
                        u.setPhone(argss[0]);
                        u.setUsername(argss[1]);

                        CreateCredentialsFile(myCredentials.getUsername(), myCredentials.getPhoneNumber());

                        Intent i = new Intent(getApplicationContext(),AlertActivity.class );
                        i.addCategory(Intent.CATEGORY_HOME);
                        i.setFlags(i.FLAG_ACTIVITY_NEW_TASK | i.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i); // Launch the AlertActivity
                        finish();         // Close down the RegisterActivity

                    }


                }
            });

        }

    }
    private OnTouchListener hideKeyboardlistener = new OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent ev) {
            hideKeyboard(view);
            return false;
        }
        protected void hideKeyboard(View view)
        {
            InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }

    };

}
