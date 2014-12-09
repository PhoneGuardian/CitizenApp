package edu.elfak.mosis.phoneguardian;


import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

@SuppressWarnings("deprecation")
public class MarkerActivity extends TabActivity {

	String username;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.marker_activity);
         
        TabHost tabHost = getTabHost();
        username = getIntent().getStringExtra("USERNAME");
        Marker m = (Marker) getIntent().getSerializableExtra("marker");
       
        TabSpec details = tabHost.newTabSpec("View details");
        details.setIndicator("View details");
        Intent i1 = new Intent(this, ViewMarkerDetailsActivity.class);
        i1.putExtra("marker", m);
        i1.putExtra("USERNAME", username);
        details.setContent(i1);
         
        TabSpec comments = tabHost.newTabSpec("View comments");
        comments.setIndicator("View comments");
        Intent i2 = new Intent(this, ViewCommentsActivity.class);
        i2.putExtra("marker", m);
        comments.setContent(i2);
         
        TabSpec add_comment = tabHost.newTabSpec("Add comment");
        add_comment.setIndicator("Add comment");
        Intent i3 = new Intent(this, AddCommentActivity.class);
        i3.putExtra("USERNAME", username);
        i3.putExtra("marker", m);
        add_comment.setContent(i3);
         
        tabHost.addTab(details); 
        tabHost.addTab(comments); 
        tabHost.addTab(add_comment); 
        
        
    }
	
	@Override
	  public void onBackPressed() {
	    Intent i = new Intent(MarkerActivity.this,PGMapActivity.class);
	    i.putExtra("USERNAME", username);
	    startActivity(i);
	  }
	
}
