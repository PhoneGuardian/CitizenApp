package edu.elfak.mosis.phoneguardian;



/**
 * Created by ntasic on 5/8/14.
 */
public class User {

    String username;
    String password;
    String name_lastname;
    String phone;
    //byte[] photo;

    public User(String uname)
    {
        this.username = uname;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName_lastname() {
        return this.name_lastname;
    }

    public void setName_lastname(String name_lastname) {
        this.name_lastname = name_lastname;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    /*public byte[] getPhoto() {
        return this.photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }*/
    
    @Override
	public String toString()
	{
		return this.name_lastname;
	}
}
