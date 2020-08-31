package com.example.usaid_app;

import java.io.Serializable;

public class User {
    private int id, accesslevel;
    private String firstname, lastname, username, password;

    public User(int id, String firstname, String lastname, String username, String password, int accesslevel){
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.password = password;
        this.accesslevel = accesslevel;
    }

    public int getId(){
        return id;
    }

    public String getFirstname(){
        return firstname;
    }

    public String getLastname(){
        return lastname;
    }

    public String getUsername(){
        return username;
    }

    public String getPassword(){
        return password;
    }

    public int getAccesslevel() {return accesslevel; }
}
