package com.app.bricenangue.timeme;

import android.graphics.Bitmap;

/**
 * Created by bricenangue on 24/09/16.
 */

public class UserForFireBase {
   private String email;
    private String picturefirebaseUrl;
   private int status;
    private String password;
    private String firstname;
    private String lastname;
    private String regId;
    private String friendlist;

    public UserForFireBase() {
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

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getRegId() {
        return regId;
    }

    public void setRegId(String regId) {
        this.regId = regId;
    }

    public String getFriendlist() {
        return friendlist;
    }

    public void setFriendlist(String friendlist) {
        this.friendlist = friendlist;
    }

    public String getPicturefirebaseUrl() {
        return picturefirebaseUrl;
    }

    public void setPicturefirebaseUrl(String picturefirebaseUrl) {
        this.picturefirebaseUrl = picturefirebaseUrl;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
