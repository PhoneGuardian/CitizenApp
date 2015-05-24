package it.polimi.guardian.citizenapp;

import android.app.ListActivity;
import android.os.Bundle;

public class ListFilterActivity extends ListActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_of_markers_activity);

        DataWrapper dw = (DataWrapper) getIntent().getSerializableExtra("markers");
        Marker[] markers = dw.getMarkers();

        String[] eventType = new String[0];
        String[] eventDate = new String[0];
        String[] eventAddress = new String[0];
        String[] eventDescription = new String[0];

        if(markers != null) {    //if there's no internet connection on the phone markers will be null
            eventType = new String[markers.length];
            eventDate = new String[markers.length];
            eventAddress = new String[markers.length];
            eventDescription = new String[markers.length];

            for (int i = 0; i < markers.length; i++) {
                eventType[i] = markers[i].getType_of_event();
                eventDate[i] = markers[i].getEvent_time();
                eventAddress[i] = markers[i].getAddress();
                eventDescription[i] = markers[i].getDescription();
            }
        }
        setListAdapter(new FilterListAdapter(this, eventType, eventDate, eventAddress, eventDescription));
    }
}