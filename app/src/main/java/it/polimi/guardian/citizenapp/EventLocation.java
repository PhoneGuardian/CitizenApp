package it.polimi.guardian.citizenapp;

/**
 * Created by Mirjamsk on 6.4.2015..
 */
public class
        EventLocation {
        private Double latitude;
        private Double longitude;
        private String address;
        private float accuracy = -1;

    public EventLocation() {    }

    public EventLocation(Double latitude, Double longitude, String address, float accuracy) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.accuracy = accuracy;
    }


    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }


    public boolean isValid() {

        return  address != null && !address.equals("IO Exception trying to get address") && latitude != null && longitude != null && accuracy != -1;
    }

}
