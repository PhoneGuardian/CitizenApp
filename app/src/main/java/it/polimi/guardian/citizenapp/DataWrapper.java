package it.polimi.guardian.citizenapp;

import java.io.Serializable;

public class DataWrapper implements Serializable {

    /**
  * 
  */
 private static final long serialVersionUID = 1L;
 
 private Marker[] markers;

    public DataWrapper(Marker[] markers2) {
       this.markers = markers2;
    }

    public Marker[] getMarkers() {
       return this.markers;
    }
}