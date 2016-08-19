package com.app.bricenangue.timeme;



import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by bricenangue on 12/08/16.
 */
public class TimeMeApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //Initializing firebase
        Firebase.setAndroidContext(getApplicationContext());
    }
}
