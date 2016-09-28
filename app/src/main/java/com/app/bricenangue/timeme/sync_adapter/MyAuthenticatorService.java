package com.app.bricenangue.timeme.sync_adapter;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.net.Authenticator;

/**
 * Created by bricenangue on 02/09/16.
 */
public class MyAuthenticatorService extends Service {

    // Instance field that stores the authenticator object
    private MyAuthenticatorClass mAuthenticator;
    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new MyAuthenticatorClass(this);
    }
    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}

