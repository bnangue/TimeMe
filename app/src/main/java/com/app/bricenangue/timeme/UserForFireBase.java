package com.app.bricenangue.timeme;

import android.graphics.Bitmap;

/**
 * Created by bricenangue on 24/09/16.
 */

public class UserForFireBase {
   private PrivateInfo privateProfileInfo;
    private PublicInfos publicProfilInfos;
    private String password;
    private String chatroom;


    public UserForFireBase() {
    }

    public PrivateInfo getPrivateProfileInfo() {
        return privateProfileInfo;
    }

    public void setPrivateProfileInfo(PrivateInfo privateProfileInfo) {
        this.privateProfileInfo = privateProfileInfo;
    }

    public PublicInfos getPublicProfilInfos() {
        return publicProfilInfos;
    }

    public void setPublicProfilInfos(PublicInfos publicProfilInfos) {
        this.publicProfilInfos = publicProfilInfos;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getChatroom() {
        return chatroom;
    }

    public void setChatroom(String chatroom) {
        this.chatroom = chatroom;
    }
}
