package edu.elfak.mosis.phoneguardian;

import java.io.Serializable;

public class Marker implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String id;
	String address;
	String category;
	String description;
	String username;
	String adding_time;
	double longitude;
	double latitude;
	
	public Marker()
    {
    }
	
	
    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getCategory()
    {
    	return this.category;
    }
    
    public void setCategory(String category)
    {
    	this.category=category;
    }
    
    public String getDescription()
    {
    	return this.description;
    }
    
    public void setDescription(String description)
    {
    	this.description=description;
    }
    
    public String getAddingTime()
    {
    	return this.adding_time;
    }
    
    public void setAddingTime(String addingtime)
    {
    	this.adding_time=addingtime;
    }
    
    public double getLong()
    {
    	return this.longitude;
    }
    
    public void setLong(double longitude)
    {
    	this.longitude=longitude;
    }
    
    public double getLat()
    {
    	return this.longitude;
    }
    
    public void setLat(double lat)
    {
    	this.latitude=lat;
    }
    
    @Override
    public String toString()
    { 
      return "Marker [id="+id+", address=" + address + ", category=" + category + ", description=" + description +", username=" + username +", adding_time=" + adding_time +", longitude=" + Double.toString(longitude) +", latitude=" + Double.toString(latitude) +"]";
    }
}
