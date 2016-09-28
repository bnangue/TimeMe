package com.app.bricenangue.timeme;

import android.app.ProgressDialog;
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
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegisterUserActivity extends AppCompatActivity implements TextView.OnEditorActionListener {
    private String emailstr,passwordstr,confirmpasswordstr,firstnamestr,lastnamestr;
    private EditText emailed,passworded,confirmpassworded,firstnameed,lastnameed;
    private Button registerbtn;
    private TextView gotologgintv;
    UserLocalStore userLocalStore;
    private Snackbar snackbar;
    private CoordinatorLayout coordinatorLayout;
    private  String fireBaseuniqueId="";
    String regid;
    private FirebaseAuth mAuth;
    private ProgressDialog progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        userLocalStore=new UserLocalStore(this);

        mAuth=FirebaseAuth.getInstance();
        coordinatorLayout=(CoordinatorLayout)findViewById(R.id.coordinateLayout);
        registerbtn=(Button)findViewById(R.id.buttonregisterReg);
        emailed=(EditText)findViewById(R.id.editTextregemail);
        passworded=(EditText)findViewById(R.id.editTextregpassword);
        firstnameed=(EditText)findViewById(R.id.editTextregisterfirstname);
        lastnameed=(EditText)findViewById(R.id.editTextregisterlastname);

        confirmpassworded=(EditText)findViewById(R.id.editTextregconfirmpassword);
        confirmpassworded.setOnEditorActionListener(this);

        fireBaseuniqueId=registerDevice();

    }

    public void Onregisterclicked(View view){
       registerNewUser();
    }

    private void registerNewUser(){
        emailstr = emailed.getText().toString();
        passwordstr = passworded.getText().toString();
        confirmpasswordstr = confirmpassworded.getText().toString();
        firstnamestr = firstnameed.getText().toString();
        lastnamestr = lastnameed.getText().toString();

        if(TextUtils.isEmpty(emailstr)){
            emailed.setError("this field cannot be empty");
        }else if(TextUtils.isEmpty(passwordstr)){
            passworded.setError("this field cannot be empty");

        }else if(TextUtils.isEmpty(confirmpasswordstr)){
            confirmpassworded.setError("this field cannot be empty");
        }else if(!TextUtils.isEmpty(emailstr) && !TextUtils.isEmpty(passwordstr) && !TextUtils.isEmpty(confirmpasswordstr)){
            if(passwordstr.equals(confirmpasswordstr)){
                progressBar = new ProgressDialog(this);
                progressBar.setCancelable(false);
                progressBar.setTitle("Creating your Account");
                progressBar.setMessage("in progress ...");
                progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressBar.show();
                final int passHash=passwordstr.hashCode();
                mAuth.createUserWithEmailAndPassword(emailstr,String.valueOf(passHash))
                        .addOnCompleteListener(RegisterUserActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                   new RegisterToFireDBAsyncTask().execute();

                                }else {
                                    showSnackBar();
                                    Toast.makeText(getApplicationContext(),
                                             task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                    if(progressBar!=null)
                                        progressBar.dismiss();

                                }
                            }
                        });

            }else {
                Toast.makeText(getApplicationContext(),"password doesn't match. Please check your entry",Toast.LENGTH_SHORT).show();
            }
        }

    }
    public void showSnackBar(){
        snackbar = Snackbar
                .make(coordinatorLayout, " An error occured during registration ", Snackbar.LENGTH_INDEFINITE)
                .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       registerNewUser();
                    }
                });
        snackbar.setActionTextColor(Color.WHITE);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(getResources().getColor(R.color.colorSnackbar));
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }
    public void OngotoLogginclicked(View view){

        startActivity(new Intent(RegisterUserActivity.this,LoginScreenActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

        if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
           registerNewUser();
        }
        return false;
    }



    class RegisterToFireDBAsyncTask extends AsyncTask<Void ,Void, Void> {


        public RegisterToFireDBAsyncTask() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //start progressBar
        }

        @Override
        protected Void doInBackground(Void... params) {
            final int passHash=passwordstr.hashCode();
            mAuth.signInWithEmailAndPassword(emailstr,String.valueOf(passHash))
                    .addOnCompleteListener(RegisterUserActivity.this,
                            new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        UserForFireBase userForFireBase=new UserForFireBase();
                                        userForFireBase.setEmail(emailstr);
                                        userForFireBase.setFirstname(firstnamestr);
                                        userForFireBase.setLastname(lastnamestr);
                                        userForFireBase.setPassword(String.valueOf(passHash));
                                        userForFireBase.setFriendlist(" ");
                                        userForFireBase.setRegId(fireBaseuniqueId);
                                        userForFireBase.setPicturefirebaseUrl(" ");
                                        userForFireBase.setStatus(0);
                                        DatabaseReference firebase = FirebaseDatabase.
                                                getInstance().getReference()
                                                .child(Config.FIREBASE_APP_URL_USERS)
                                                .child(mAuth.getCurrentUser().getUid())
                                                ;
                                        firebase.setValue(userForFireBase).addOnCompleteListener(
                                                new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            mAuth.signOut();
                                                            if(progressBar!=null)
                                                                progressBar.dismiss();
                                                            userLocalStore.setUserGCMregId(fireBaseuniqueId,0);
                                                            startActivity(new Intent
                                                                    (RegisterUserActivity.this,LoginScreenActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

                                                        }else {
                                                            Toast.makeText(getApplicationContext(),
                                                                   task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                                            if(progressBar!=null)
                                                                progressBar.dismiss();
                                                        }
                                                    }
                                                }
                                        );


                                    }else{
                                        Toast.makeText(getApplicationContext(),
                                                 task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                        if(progressBar!=null)
                                            progressBar.dismiss();
                                    }
                                }
                            });

            return null;
        }

        @Override
        protected void onPostExecute(Void adapter) {
            //end progressBar


        }
    }

     private String registerDevice() {
     //Creating a firebase object
     FirebaseMessaging.getInstance().subscribeToTopic("timeMe");
     return FirebaseInstanceId.getInstance().getToken();


     }


}
