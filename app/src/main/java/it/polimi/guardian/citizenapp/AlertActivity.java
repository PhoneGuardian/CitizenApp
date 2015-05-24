package it.polimi.guardian.citizenapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class AlertActivity extends Activity implements OnClickListener
{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alert_activity);
		
		Button map = (Button) findViewById(R.id.btn_browse_map);
		map.setOnClickListener(this);
		
		Button btnAlert = (Button) findViewById(R.id.btn_report_event);
		btnAlert.setOnClickListener(this);
		
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
			case R.id.btn_report_event:
				Intent i2 = new Intent(this,AddLocationActivity.class);
				startActivity(i2);
				break;
			case R.id.btn_browse_map:
				Intent i3 = new Intent(this,PGMapActivity.class);
				startActivity(i3);
				break;
				
		}
	}

}
