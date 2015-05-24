package it.polimi.guardian.citizenapp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;


public class ViewMarkerDetailsActivity extends Activity {

	String id_marker = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.viewmarker_details_activity);

		Marker m = (Marker) getIntent().getSerializableExtra("marker");
        String type_of_event = m.getType_of_event();
		id_marker = m.id;
		
		TextView t =(TextView) findViewById(R.id.event_time);
		t.setText(m.getEvent_time());
		TextView t1 =(TextView) findViewById(R.id.event_address);
		t1.setText(m.getAddress());
        TextView t2 =(TextView) findViewById(R.id.type_of_event);

		switch (type_of_event)
        {
            case "E":
                t2.setText("Emergency");
                break;
            case "F":
                t2.setText("Fire");
                break;
            case "P":
                t2.setText("Police");
                break;
        }

		TextView t3 =(TextView) findViewById(R.id.description_of_event);
		t3.setText(m.getDescription());
		TextView t4 =(TextView) findViewById(R.id.event_acc);
		t4.setText(Float.toString(m.getLocation_acc()));
        TextView t5 =(TextView) findViewById(R.id.anonymous);
        if(m.getAnonymous() == 1)
            t5.setText("YES");
        else
            t5.setText("NO");
	}

}
