package edu.elfak.mosis.phoneguardian;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class ListFilterActivity extends ListActivity {
 
 Marker[] markers;
 String[] values;
 
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_of_markers_activity);
      
        DataWrapper dw = (DataWrapper) getIntent().getSerializableExtra("markers");
        
        markers = dw.getMarkers();
        
        String[] niz = new String[markers.length];
        
        for(int i=0 ; i<markers.length; i++)
        {
         niz[i] = "address: "+markers[i].getAddress()+"\nType of event: "+markers[i].getType_of_event();
        }
        
        
        setListAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, niz));
       
    }
    
    
 
}