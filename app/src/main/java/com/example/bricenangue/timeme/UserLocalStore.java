package com.example.bricenangue.timeme;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by alex on 16.01.2016.
 */
public class UserLocalStore {

    private int appVersion;

    public static final String SP_NAME="userDetails";
    SharedPreferences userLocalDataBase;

    public UserLocalStore(Context context){
        userLocalDataBase=context.getSharedPreferences(SP_NAME,0);
    }
    public void storeUserData(User user){
        SharedPreferences.Editor spEditor=userLocalDataBase.edit();
        spEditor.putString("email",user.email);
        spEditor.putString("password",user.password);
        spEditor.apply();
    }

    public User getLoggedInUser(){
        String email=userLocalDataBase.getString("email", "");
        String password=userLocalDataBase.getString("password","");

        return new User(email,password);
    }
    //call with true if logged in
    public void setUserLoggedIn(boolean loggedIn){
        SharedPreferences.Editor spEditor=userLocalDataBase.edit();
        spEditor.putBoolean("loggedIn", loggedIn);
        spEditor.apply();

    }

    public void setUserUserfriendliststring(String friendsname){
        SharedPreferences.Editor editor=userLocalDataBase.edit();
        editor.putString("friendsname", friendsname);

        editor.apply();

    }
    public String getUserfriendliststring() {
        String friendsname = userLocalDataBase.getString("friendsname", "");
        if (friendsname.isEmpty()) {
            return "";
        }
        //int registeredVersion = userLocalDataBase.getInt("appVersion", Integer.MIN_VALUE);

        return friendsname;
    }
    public void setUserGCMregId(String regId,int appversion){
        appVersion=appversion;
        SharedPreferences.Editor editor=userLocalDataBase.edit();
        editor.putString("registration_id", regId);
        editor.putInt("appVersion", appVersion);
        editor.apply();

    }
    public String getUserRegistrationId() {
        String registrationId = userLocalDataBase.getString("registration_id", "");
        if (registrationId.isEmpty()) {
            return "";
        }
        //int registeredVersion = userLocalDataBase.getInt("appVersion", Integer.MIN_VALUE);

        return registrationId;
    }


    public void setUserPicturePath(String path){

        SharedPreferences.Editor editor=userLocalDataBase.edit();
        editor.putString("picturePath", path);
        editor.apply();

    }
    public String getUserPicturePath() {
        String picturePath = userLocalDataBase.getString("picturePath", "");
        if (picturePath.isEmpty()) {
            return "";
        }
        return picturePath;
    }


    public void clearUserData(){
        SharedPreferences.Editor spEditor=userLocalDataBase.edit();
        spEditor.clear();
        spEditor.apply();
    }

    public boolean getUserLoggedIn(){
        if(userLocalDataBase.getBoolean("loggedIn", false)){
            return true;
        }else {
            return false;
        }
    }


}
