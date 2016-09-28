package com.app.bricenangue.timeme;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by alex on 16.01.2016.
 */
public class User implements Parcelable {
    String email,
            password,//hashcode of entered password
            firstname,lastname,regId,friendlist;
    Bitmap picture;
    int status;
     String pictureurl;

    public User( String email, String password){
        this.email=email;
        this.password=password;
    }
    public User( String email, String password, int status, String regId){
        this.email=email;
        this.password=password;
        this.status=status;
        this.regId=regId;
    }

    public User( Bitmap picture, int status){
        this.picture=picture;
        this.status=status;
    }
    public User(String regId, Bitmap picture){
        this.picture=picture;
        this.regId=regId;
    }
    public User(String regId, String email, String password){
        this.regId=regId;
        this.email=email;
        this.password=password;
    }
    public User( String email, String password, String firstname, String lastname, int status, String regId, Bitmap picture, String friendlist) {
        this.email=email;
        this.password=password;
        this.firstname=firstname;
        this.lastname=lastname;
        this.status=status;
        this.regId=regId;
        this.picture=picture;
        this.friendlist=friendlist;

    }
    public User(String email, String password, String friendlist, int status){
        this.friendlist=friendlist;
        this.email=email;
        this.password=password;
        this.status=status;
    }

    public User() {

    }

    public ArrayList<String> getuserfriendlist(String ufriendlist){
        ArrayList<String> list =new ArrayList<>();
        String[]friends = null;
        if(ufriendlist!=null){
          friends=ufriendlist.split(",");

        }
        assert friends != null;
        for(int i=0;i<friends.length;i++){
            if(!friends[i].isEmpty()){
                list.add(friends[i]);
            }

        }
        return list;
    }

    public String getfullname(){
        String fullname=this.firstname+" "+this.lastname;
        return fullname;
    }

    private User(Parcel in){

        email = in.readString();
        password = in.readString();
        firstname=in.readString();
        lastname=in.readString();
        status=in.readInt();
        regId=in.readString();
        friendlist=in.readString();
        picture=in.readParcelable(getClass().getClassLoader());
        pictureurl=in.readString();


    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(email);
        dest.writeString(password);
        dest.writeString(firstname);
        dest.writeString(lastname);
        dest.writeInt(status);
        dest.writeString(regId);
        dest.writeString(friendlist);
        dest.writeParcelable(picture,flags);
        dest.writeString(pictureurl);

    }
    public static final Creator<User> CREATOR = new Creator<User>() {
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };


    public User getUserFromFireBase(UserForFireBase userForFireBase){
        User user=new User();
        user.email=userForFireBase.getEmail();
        user.password=userForFireBase.getPassword();
        user.firstname=userForFireBase.getFirstname();
        user.lastname=userForFireBase.getLastname();
        user.friendlist=userForFireBase.getFriendlist();
        user.regId=userForFireBase.getRegId();
        user.status=userForFireBase.getStatus();
        user.pictureurl=userForFireBase.getPicturefirebaseUrl();

        return user;

    }
    public UserForFireBase getUserForFireBase(User user){
        UserForFireBase userForFireBase=new UserForFireBase();
        userForFireBase.setEmail(user.email);
        userForFireBase.setPassword(user.password);
        userForFireBase.setFirstname(user.firstname);
        userForFireBase.setLastname(user.lastname);
        userForFireBase.setFriendlist(user.friendlist);
        userForFireBase.setRegId(user.regId);
        userForFireBase.setStatus(user.status);
        userForFireBase.setPicturefirebaseUrl(user.pictureurl);

        return userForFireBase;

    }

public String  genaretnumber(){
    Random r = new Random();
    int Low = 1;
    int High = 51;

    StringBuilder builder=new StringBuilder();
    for(int j=1;j<20 ;j++){
        for(int i = 0;i<5;i++){
            int result = r.nextInt(High-Low) + Low;
            builder.append(result).append(",");
            if(i==4){
                builder.append("|||").append(" ");
            }
        }
    }
    String s =builder.toString();
    return s;
}


}
