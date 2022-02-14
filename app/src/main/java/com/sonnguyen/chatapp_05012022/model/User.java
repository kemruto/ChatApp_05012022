package com.sonnguyen.chatapp_05012022.model;

import java.io.Serializable;

public class User implements Serializable {
    private String userID,name,email,password,image;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public User(){

    }

    public User(String userID, String name, String email, String password, String image) {
        this.userID = userID;
        this.name = name;
        this.email = email;
        this.password = password;
        this.image = image;
    }
}
