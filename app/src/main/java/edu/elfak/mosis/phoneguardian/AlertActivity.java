package edu.elfak.mosis.phoneguardian;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class AlertActivity extends Activity implements OnClickListener
{
	String user;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alert_activity);
		
		Button mapa = (Button) findViewById(R.id.btn_map);
		mapa.setOnClickListener(this);
		
		Button btnAlert = (Button) findViewById(R.id.btn_alert);
		btnAlert.setOnClickListener(this);
		
		Button btnProfle = (Button) findViewById(R.id.btn_profile);
		btnProfle.setOnClickListener(this);
	
		
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		user = getIntent().getStringExtra("USERNAME");
		switch(v.getId())
		{

			case R.id.btn_profile:
				Intent i1 = new Intent(this,ProfileActivity.class);
				i1.putExtra("USERNAME", user);
				startActivity(i1);
				break;
			case R.id.btn_alert:
				Intent i2 = new Intent(this,AddLocationActivity.class);
				i2.putExtra("USERNAME", user);
				startActivity(i2);
				break;
			case R.id.btn_map:
				Intent i3 = new Intent(this,PGMapActivity.class);
				i3.putExtra("USERNAME", user);
				startActivity(i3);
				break;
				
		}
	}
}
