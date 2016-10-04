package com.app.bricenangue.timeme;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.apache.poi.util.PngUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class LoginScreenActivity extends AppCompatActivity implements View.OnClickListener, TextView.OnEditorActionListener {

    private Button registerbtn,loginbtn;
    private EditText emailed,passworded;
    private String emailstr,passwordstr;
    private UserLocalStore userLocalStore;
    public static boolean eventsareloaded=false;
    private Snackbar snackbar;
    private CoordinatorLayout coordinatorLayout;

    private int count=0;

    private FragmentProgressBarLoading progressDialog;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private ValueEventListener valueEventListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        userLocalStore=new UserLocalStore(this);


        auth=FirebaseAuth.getInstance();
        firebaseUser=auth.getCurrentUser();

        databaseReference=FirebaseDatabase.getInstance().getReference().child(Config.FIREBASE_APP_URL_USERS);

        registerbtn=(Button)findViewById(R.id.buttonregisterReg);
        loginbtn=(Button)findViewById(R.id.buttonlogin);
        emailed=(EditText)findViewById(R.id.editTextemailloggin);
        passworded=(EditText)findViewById(R.id.editTextpasswordloggin);
        coordinatorLayout=(CoordinatorLayout)findViewById(R.id.coordinateLayout_login_screen);

        loginbtn.setOnClickListener(this);
        registerbtn.setOnClickListener(this);
        passworded.setOnEditorActionListener(this);



    }

    private void dialg(String message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage(message);
        alert.setPositiveButton("OK", null);
        alert.show();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonlogin:
                if(internetConnection()){
                  logCurrentuserIn();


                }else {
                    showErrordialog("No internet connection detected");
                }
                break;
            case R.id.buttonregisterReg:
                startActivity(new Intent(LoginScreenActivity.this,RegisterUserActivity.class));
                break;
        }
    }


    private void saveloggedInuserPreferences(User returneduser){
        userLocalStore.setUserLoggedIn(true);
        userLocalStore.storeUserData(returneduser);
        userLocalStore.setUserPartnerEmail(returneduser.friendlist);

       userLocalStore.setChatRoom(returneduser.getChatroom());


        Intent intent=new Intent(LoginScreenActivity.this,NewCalendarActivty.class);

        startActivity(intent);
        progressDialog.dismiss(getSupportFragmentManager());
    }



    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

        if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
            if(internetConnection()){
               logCurrentuserIn();
            }else {
                showErrordialog("No internet connection detected");

            }

        }

        return false;
    }

    private void showErrordialog(String message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage(message);
        alert.setPositiveButton("OK", null);
        alert.show();
        if(progressDialog!=null){
            progressDialog.dismiss(getSupportFragmentManager());

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        count=0;
        if(firebaseUser!=null){
            //direct loging
            Intent intent=new Intent(LoginScreenActivity.this,NewCalendarActivty.class);

            startActivity(intent);
        }

    }

    private  boolean internetConnection(){
        return new ServerRequests(this).haveNetworkConnection();
    }
    @Override
    public void onBackPressed() {
       count++;

        if(count==2){
            this.finishAffinity();
            System.exit(0);
        }
        Toast.makeText(getApplicationContext(),"Click a second time to exit application",Toast.LENGTH_SHORT).show();

    }


    public void logCurrentuserIn(){

        ServerRequests.lockScreenOrientation(LoginScreenActivity.this);
        progressDialog = new FragmentProgressBarLoading();
        progressDialog.setCancelable(false);
        progressDialog.show(getSupportFragmentManager(), "logging_progress");


        emailstr = emailed.getText().toString();
        passwordstr = passworded.getText().toString();

        if(TextUtils.isEmpty(emailstr)){
            emailed.setError("this field cannot be empty");
        }else if(TextUtils.isEmpty(passwordstr)){
            passworded.setError("this field cannot be empty");

        }else if(!TextUtils.isEmpty(emailstr) && !TextUtils.isEmpty(passwordstr)) {
            int passHash = passwordstr.hashCode();
            User usertologIn = new User(emailstr, String.valueOf(passHash), 1, null);

            if (userLocalStore.getUserRegistrationId().isEmpty() || userLocalStore.getUserRegistrationId()==null) {
                userLocalStore.setUserGCMregId(registerDevice(), 0);
                usertologIn.regId=userLocalStore.getUserRegistrationId();
            } else {
                usertologIn.regId = userLocalStore.getUserRegistrationId();
            }


            final UserForFireBase userForFireBase=new UserForFireBase();
            auth.signInWithEmailAndPassword(emailstr,String.valueOf(passHash))
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                firebaseUser=auth.getCurrentUser();
                                assert firebaseUser != null;
                                final String uid=firebaseUser.getUid();

                                final DatabaseReference refPrivate=databaseReference.child(firebaseUser.getUid())
                                        .child(Config.FIREBASE_APP_URL_USERS_privateProfileInfo);
                                final DatabaseReference refPublic=databaseReference.child(firebaseUser.getUid())
                                        .child(Config.FIREBASE_APP_URL_USERS_publicProfilInfos);
                                final DatabaseReference refPassword=databaseReference.child(firebaseUser.getUid())
                                        .child(Config.FIREBASE_APP_URL_USERS_password);
                                final DatabaseReference refchatRoom=databaseReference.child(firebaseUser.getUid())
                                        .child("chatroom");
                                 final PrivateInfo privateInfo=new PrivateInfo();
                                PublicInfos publicInfos=new PublicInfos();
                                final String password;
                               refPrivate.addValueEventListener(new ValueEventListener() {
                                   @Override
                                   public void onDataChange(DataSnapshot dataSnapshot) {
                                     PrivateInfo  privateInfo=dataSnapshot.getValue(PrivateInfo.class);
                                       userForFireBase.setPrivateProfileInfo(privateInfo);
                                       refPrivate.removeEventListener(this);
                                       refPublic.addValueEventListener(new ValueEventListener() {
                                           @Override
                                           public void onDataChange(DataSnapshot dataSnapshot) {
                                               PublicInfos  publicInfos=dataSnapshot.getValue(PublicInfos.class);
                                               userForFireBase.setPublicProfilInfos(publicInfos);

                                               refPublic.removeEventListener(this);
                                               refPassword.addValueEventListener(new ValueEventListener() {
                                                   @Override
                                                   public void onDataChange(DataSnapshot dataSnapshot) {

                                                       String  password=dataSnapshot.getValue(String.class);
                                                       userForFireBase.setPassword(password);
                                                       refPassword.removeEventListener(this);

                                                       refchatRoom.addValueEventListener(new ValueEventListener() {
                                                           @Override
                                                           public void onDataChange(DataSnapshot dataSnapshot) {
                                                               String chatroom=dataSnapshot.getValue(String.class);
                                                               userForFireBase.setChatroom(chatroom);
                                                               refchatRoom.removeEventListener(this);

                                                               final User user=new User().getUserFromFireBase(userForFireBase);

                                                               DatabaseReference data= databaseReference.child(uid).child(Config.FIREBASE_APP_URL_USERS_privateProfileInfo);
                                                               data.child("status").setValue(1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                   @Override
                                                                   public void onComplete(@NonNull Task<Void> task) {
                                                                       if(task.isSuccessful()){
                                                                           saveloggedInuserPreferences(user);

                                                                       }else {
                                                                           Toast.makeText(getApplicationContext(), task.getException().getMessage()
                                                                                   ,Toast.LENGTH_SHORT).show();
                                                                       }
                                                                   }
                                                               });
                                                           }

                                                           @Override
                                                           public void onCancelled(DatabaseError databaseError) {

                                                           }
                                                       });

                                                   }

                                                   @Override
                                                   public void onCancelled(DatabaseError databaseError) {

                                                   }
                                               });
                                           }

                                           @Override
                                           public void onCancelled(DatabaseError databaseError) {

                                           }
                                       });
                                   }

                                   @Override
                                   public void onCancelled(DatabaseError databaseError) {

                                   }
                               });


                            }else {
                                progressDialog.dismiss(getSupportFragmentManager());
                                Toast.makeText(getApplicationContext(),
                                         task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }


    }


    private String registerDevice() {
        //Creating a firebase object
        FirebaseMessaging.getInstance().subscribeToTopic("timeMe");
        return FirebaseInstanceId.getInstance().getToken();


    }


    public void showSnackBar(){
        snackbar = Snackbar
                .make(coordinatorLayout, "No connection internet detected.", Snackbar.LENGTH_INDEFINITE)
                .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        logCurrentuserIn();
                    }
                });;
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(getResources().getColor(R.color.colorSnackbar));
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

}
