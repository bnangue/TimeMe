package com.example.bricenangue.timeme;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by bricenangue on 10/03/16.
 */
public class StateActivitiesPreference {

    public static final String SP_NAME="activityState";
    SharedPreferences userLocalDataBase;

    public StateActivitiesPreference(Context context){
        userLocalDataBase=context.getSharedPreferences(SP_NAME,0);
    }

    //call with true if logged in
    public void setHasSignUPWithQRCODE(boolean loggedIn){
        SharedPreferences.Editor spEditor=userLocalDataBase.edit();
        spEditor.putBoolean("loggedIn", loggedIn);
        spEditor.apply();

    }

    public void setHasalreadysetPIN(boolean pinset){
        SharedPreferences.Editor editor=userLocalDataBase.edit();
        editor.putBoolean("pinset", pinset);

        editor.apply();

    }
    public boolean getHasalreadysetPIN() {
        if(userLocalDataBase.getBoolean("pinset", false)){
            return true;
        }else {
            return false;
        }
    }


    public void setCopyExcelFileFromAssetToInterneMemory(boolean excelfilecopied){
        SharedPreferences.Editor editor=userLocalDataBase.edit();
        editor.putBoolean("excel_file_copied", excelfilecopied);

        editor.apply();

    }
    public boolean getCopyExcelFileFromAssetToInterneMemory() {
        if(userLocalDataBase.getBoolean("excel_file_copied", false)){
            return true;
        }else {
            return false;
        }
    }

    public void setIsfirstimedownload(boolean firstdownload){
        SharedPreferences.Editor editor=userLocalDataBase.edit();
        editor.putBoolean("firstdownload", firstdownload);
        editor.apply();

    }
    public boolean getIsfirstimedownload() {
        if(userLocalDataBase.getBoolean("firstdownload", false)){
            return true;
        }else {
            return false;
        }
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

    public boolean getHasSignUpWithQRCODE(){
        if(userLocalDataBase.getBoolean("loggedIn", false)){
            return true;
        }else {
            return false;
        }
    }

}
