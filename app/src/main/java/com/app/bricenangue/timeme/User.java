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
    private String chatroom;

    public String getChatroom() {
        return chatroom;
    }

    public void setChatroom(String chatroom) {
        this.chatroom = chatroom;
    }

    public User(String email, String password){
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
        chatroom=in.readString();


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
        dest.writeString(chatroom);

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
        PrivateInfo privateInfo=userForFireBase.getPrivateProfileInfo();
        PublicInfos publicInfos=userForFireBase.getPublicProfilInfos();

        user.email=publicInfos.getEmail();
        user.password=userForFireBase.getPassword();
        user.firstname=publicInfos.getFirstname();
        user.lastname=publicInfos.getLastname();
        user.friendlist=privateInfo.getFriendlist();
        user.regId=publicInfos.getRegId();
        user.status=privateInfo.getStatus();
        user.pictureurl=publicInfos.getPicturefirebaseUrl();
        user.setChatroom(userForFireBase.getChatroom());

        return user;

    }
    public UserForFireBase getUserForFireBase(User user){

        PrivateInfo privateInfo=new PrivateInfo();
        PublicInfos publicInfos=new PublicInfos();

        UserForFireBase userForFireBase=new UserForFireBase();
        publicInfos.setEmail(user.email);
        userForFireBase.setPassword(user.password);
        publicInfos.setFirstname(user.firstname);
        publicInfos.setLastname(user.lastname);
        privateInfo.setFriendlist(user.friendlist);
        publicInfos.setRegId(user.regId);
        privateInfo.setStatus(user.status);
        publicInfos.setPicturefirebaseUrl(user.pictureurl);
        userForFireBase.setPrivateProfileInfo(privateInfo);
        userForFireBase.setPublicProfilInfos(publicInfos);
        userForFireBase.setChatroom(user.getChatroom());

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
