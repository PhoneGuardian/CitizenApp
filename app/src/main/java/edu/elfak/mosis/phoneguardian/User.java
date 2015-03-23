package edu.elfak.mosis.phoneguardian;

public class User {

   static String username;
   static String phone;

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

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


}
