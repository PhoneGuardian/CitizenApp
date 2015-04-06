package edu.elfak.mosis.phoneguardian;

/**
 * Created by Mirjamsk on 6.4.2015..
 */
public class Location {
        private Double latitude;
        private Double longitude;
        private String address;
        private float accuracy;
        private boolean isValid;

    public Location(Double latitude, Double longitude, String address, boolean isValid, float accuracy) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.accuracy = accuracy;
        this.isValid = isValid;
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
        return isValid;
    }

    public void setValid(boolean isValid) {
        this.isValid = isValid;
    }
}
