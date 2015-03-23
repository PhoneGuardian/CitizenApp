package edu.elfak.mosis.phoneguardian;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

public class AlertActivity extends Activity implements OnClickListener
{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alert_activity);
		
		ImageButton map = (ImageButton) findViewById(R.id.btn_map);
		map.setOnClickListener(this);
		
		ImageButton btnAlert = (ImageButton) findViewById(R.id.btn_alert);
		btnAlert.setOnClickListener(this);
		
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
			case R.id.btn_alert:
				Intent i2 = new Intent(this,AddLocationActivity.class);
				startActivity(i2);
				break;
			case R.id.btn_map:
				Intent i3 = new Intent(this,PGMapActivity.class);
				startActivity(i3);
				break;
				
		}
	}
}
