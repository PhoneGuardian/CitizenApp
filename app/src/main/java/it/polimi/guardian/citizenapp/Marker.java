package it.polimi.guardian.citizenapp;

import java.io.Serializable;

public class Marker implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String id;
	String address;
	String type_of_event;
	String description;
	String user_phone;
	String event_time;
	double lng;
	double lat;
    int anonymous;
    float location_acc;

	public Marker()
    {
    }
	
	
    public String getUser_phone() {
        return this.user_phone;
    }

    public void setUser_phone(String username) {
        this.user_phone = username;
    }
    
    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getType_of_event()
    {
    	return this.type_of_event;
    }
    
    public void setType_of_event(String category)
    {
    	this.type_of_event=category;
    }
    
    public String getDescription()
    {
    	return this.description;
    }
    
    public void setDescription(String description)
    {
    	this.description=description;
    }
    
    public String getEvent_time()
    {
    	return this.event_time;
    }
    
    public void setEvent_time(String time)
    {
    	this.event_time=time;
    }
    
    public double getLng()
    {
    	return this.lng;
    }
    
    public void setLng(double longitude)
    {
    	this.lng=longitude;
    }
    
    public double getLat()
    {
    	return this.lat;
    }
    
    public void setLat(double lat)
    {
    	this.lat=lat;
    }

    public float getLocation_acc() {return this.location_acc;}

    public void setLocation_acc(float acc) { this.location_acc=acc;}

    public int getAnonymous() { return this.anonymous;}

    public void setAnonymous(int anonymous) { this.anonymous = anonymous;}

    
    @Override
    public String toString()
    { 
      return "Marker [id="+id+", address=" + address + ", type_of_event=" + type_of_event + ", description=" + description +
              ", user_phone=" + user_phone +", event_time=" + event_time +", longitude=" + Double.toString(lng) +", latitude=" + Double.toString(lat) +
              ", location_acc=" + Float.toString(location_acc) +", anonymous=" + Integer.toString(anonymous) +"]";
    }
}
