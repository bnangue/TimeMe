package com.app.bricenangue.timeme;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by alex on 16.01.2016.
 */
public class UserLocalStore {

    private int appVersion;
    private Context context;

    public static final String SP_NAME="userDetails";
    SharedPreferences userLocalDataBase;

    public UserLocalStore(Context context){
        this.context=context;
        userLocalDataBase=context.getSharedPreferences(SP_NAME,0);
    }
    public void storeUserData(User user){
        SharedPreferences.Editor spEditor=userLocalDataBase.edit();
        spEditor.putString("email",user.email);
        spEditor.putString("password",user.password);
        spEditor.putString("firstname",user.firstname);
        spEditor.putString("lastname",user.lastname);
        spEditor.apply();
    }

    public User getLoggedInUser(){
        String email=userLocalDataBase.getString("email", "");
        String password=userLocalDataBase.getString("password","");
        String firstname=userLocalDataBase.getString("firstname", "");
        String lastname=userLocalDataBase.getString("lastname","");

        return new User(email,password,firstname,lastname,1,null,null,null);
    }

    public void storeUserPartnerData(User userPartner){
        SharedPreferences.Editor spEditor=userLocalDataBase.edit();
        spEditor.putString("emailp",userPartner.email);
        spEditor.putString("passwordp",userPartner.password);
        spEditor.putString("firstnamep",userPartner.firstname);
        spEditor.putString("lastnamep",userPartner.lastname);
        spEditor.apply();
    }

    public User getLoggedInUserPartner(){
        String email=userLocalDataBase.getString("emailp", "");
        String password=userLocalDataBase.getString("passwordp","");
        String firstname=userLocalDataBase.getString("firstnamep", "");
        String lastname=userLocalDataBase.getString("lastnamep","");

        return new User(email,password,firstname,lastname,1,null,null,null);
    }

    //call with true if logged in
    public void setUserLoggedIn(boolean loggedIn){
        SharedPreferences.Editor spEditor=userLocalDataBase.edit();
        spEditor.putBoolean("loggedIn", loggedIn);
        spEditor.apply();

    }

    public void setUserUserfullname(String userfullname){
        SharedPreferences.Editor editor=userLocalDataBase.edit();
        editor.putString("userfullname", userfullname);

        editor.apply();

    }
    public String getUserfullname() {
        String firstname=userLocalDataBase.getString("firstname", "");
        String lastname=userLocalDataBase.getString("lastname","");

        String userfullname = firstname+" "+lastname;
        if (userfullname.isEmpty()) {
            return "";
        }
        //int registeredVersion = userLocalDataBase.getInt("appVersion", Integer.MIN_VALUE);

        return userfullname;
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

    public void setUserFinanceAccountName(String financeAccountName){
        SharedPreferences.Editor editor=userLocalDataBase.edit();
        editor.putString("financeAccountName", financeAccountName);

        editor.apply();

    }
    public String getUserfinanceAccountName() {
        String financeAccountName = userLocalDataBase.getString("financeAccountName", "");
        if (financeAccountName.isEmpty()) {
            return "";
        }
        //int registeredVersion = userLocalDataBase.getInt("appVersion", Integer.MIN_VALUE);

        return financeAccountName;
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

    public void setChatRoom(String path){

        SharedPreferences.Editor editor=userLocalDataBase.edit();
        editor.putString("chatRoom", path);
        editor.apply();

    }
    public String getChatRoom() {
        String chatRoom = userLocalDataBase.getString("chatRoom", "");
        if (chatRoom.isEmpty()) {
            return "";
        }
        return chatRoom;
    }



    public void setUserPartnerPicturePath(String path){

        SharedPreferences.Editor editor=userLocalDataBase.edit();
        editor.putString("partnerPicturePath", path);
        editor.apply();

    }
    public String getUserPartnerPicturePath() {
        String picturePath = userLocalDataBase.getString("partnerPicturePath", "");
        if (picturePath.isEmpty()) {
            return "";
        }
        return picturePath;
    }

    public void setUserAccountBalance(String balance){

        SharedPreferences.Editor editor=userLocalDataBase.edit();
        editor.putString("balance", balance);
        editor.apply();

    }
    public String getUserAccountBalance() {
        String balance = userLocalDataBase.getString("balance", "");
        if (balance.isEmpty()) {
            return "0,00";
        }
        return balance;
    }
    public void setUserAccountBalancelastChange(String lastchange){

        SharedPreferences.Editor editor=userLocalDataBase.edit();
        editor.putString("lastchange", lastchange);
        editor.apply();

    }
    public String getUserAccountBalancelastChange() {
        String lastchange = userLocalDataBase.getString("lastchange", "");
        if (lastchange.isEmpty()) {
            return "";
        }
        return lastchange;
    }

    public void setUserPartnerRegId(String gcmRegId){

        SharedPreferences.Editor editor=userLocalDataBase.edit();
        editor.putString("gcmRegId", gcmRegId);
        editor.apply();

    }
    public String getUserPartnerRegId() {
        String lastchange = userLocalDataBase.getString("gcmRegId", "");
        if (lastchange.isEmpty()) {
            return "";
        }
        return lastchange;
    }


    public void setUserPartnerEmail(String partnerEmail){

        SharedPreferences.Editor editor=userLocalDataBase.edit();
        editor.putString("partnerEmail", partnerEmail);
        editor.apply();

    }
    public String getUserPartnerEmail() {
        String lastchange = userLocalDataBase.getString("partnerEmail", "");
        if (lastchange.isEmpty()) {
            return "";
        }
        return lastchange;
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

    public String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(context);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("userProfilePicture", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,"user.jpg");

        if(mypath.exists()){
            mypath.delete();
            mypath=new File(directory,"user.jpg");
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }


    public Bitmap loadImageFromStorage(String path)
    {
        Bitmap bitmap=null;
        try {
            File f=new File(path, "user.jpg");
            bitmap = BitmapFactory.decodeStream(new FileInputStream(f));
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        return bitmap;
    }

    public String savePartnerPicToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(context);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("partnerProfilePicture", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,"partner.jpg");

        if(mypath.exists()){
            mypath.delete();
            mypath=new File(directory,"partner.jpg");
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }


    public Bitmap loadPartnerImageFromStorage(String path)
    {
        Bitmap bitmap=null;
        try {
            File f=new File(path, "partner.jpg");
            bitmap = BitmapFactory.decodeStream(new FileInputStream(f));
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        return bitmap;
    }


}
