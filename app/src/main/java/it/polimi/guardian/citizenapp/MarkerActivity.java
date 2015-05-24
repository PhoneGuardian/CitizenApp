package it.polimi.guardian.citizenapp;


import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

@SuppressWarnings("deprecation")
public class MarkerActivity extends TabActivity {


	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.marker_activity);
         
        TabHost tabHost = getTabHost();
        Marker m = (Marker) getIntent().getSerializableExtra("marker");
       
        TabSpec details = tabHost.newTabSpec("View details");
        details.setIndicator("View details");
        Intent i1 = new Intent(this, ViewMarkerDetailsActivity.class);
        i1.putExtra("marker", m);

        details.setContent(i1);
         
        TabSpec comments = tabHost.newTabSpec("View comments");
        comments.setIndicator("View comments");
        Intent i2 = new Intent(this, ViewCommentsActivity.class);
        i2.putExtra("marker", m);
        comments.setContent(i2);
         
        TabSpec add_comment = tabHost.newTabSpec("Add comment");
        add_comment.setIndicator("Add comment");
        Intent i3 = new Intent(this, AddCommentActivity.class);
        i3.putExtra("marker", m);
        add_comment.setContent(i3);
         
        tabHost.addTab(details); 
        tabHost.addTab(comments); 
        tabHost.addTab(add_comment); 
        
        
    }

	
}
