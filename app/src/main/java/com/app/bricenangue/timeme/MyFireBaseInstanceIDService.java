package com.app.bricenangue.timeme;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
/**
 * Created by bricenangue on 12/08/16.
 */
public class MyFireBaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";
    private FirebaseAuth auth;

    @Override
    public void onTokenRefresh() {

        //Getting registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        //Displaying token on logcat
        Log.d(TAG, "Refreshed token: " + refreshedToken);
     //   sendRegistrationToServer(refreshedToken);

    }

    @Override
    public void onCreate() {
        super.onCreate();
        auth=FirebaseAuth.getInstance();
    }

    private void sendRegistrationToServer(String token) {

        assert auth.getCurrentUser()!=null;
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child(Config.FIREBASE_APP_URL_USERS)
                .child(auth.getCurrentUser().getUid())
                .child(Config.FIREBASE_APP_URL_USERS_publicProfilInfos);
        ref.child("regId").setValue(token);
        new UserLocalStore(getApplicationContext()).setUserGCMregId(token,0);
    }
}
