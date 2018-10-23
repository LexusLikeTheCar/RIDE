package edu.osu.ride;


public class User {

    public String name;
    public String email;
    public String home;
    public String dummyEmail;
    public String password;


    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public User() {
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}



