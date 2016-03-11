package com.example.bricenangue.timeme;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class LoginScreenActivity extends AppCompatActivity implements View.OnClickListener, TextView.OnEditorActionListener {

    private Button registerbtn,loginbtn;
    private EditText emailed,passworded;
    private String emailstr,passwordstr;
    private UserLocalStore userLocalStore;
    private int count=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        userLocalStore=new UserLocalStore(this);

        registerbtn=(Button)findViewById(R.id.buttonregisterReg);
        loginbtn=(Button)findViewById(R.id.buttonlogin);
        emailed=(EditText)findViewById(R.id.editTextemailloggin);
        passworded=(EditText)findViewById(R.id.editTextpasswordloggin);

        loginbtn.setOnClickListener(this);
        registerbtn.setOnClickListener(this);
        passworded.setOnEditorActionListener(this);



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

        Intent intent=new Intent(LoginScreenActivity.this,NewCalendarActivty.class);
        intent.putExtra("loggedInUser",returneduser);
        startActivity(intent);
    }
    public void logCurrentuserIn(){
        emailstr = emailed.getText().toString();
        passwordstr = passworded.getText().toString();

        if(TextUtils.isEmpty(emailstr)){
            emailed.setError("this field cannot be empty");
        }else if(TextUtils.isEmpty(passwordstr)){
            passworded.setError("this field cannot be empty");

        }else if(!TextUtils.isEmpty(emailstr) && !TextUtils.isEmpty(passwordstr)){
            int passHash=passwordstr.hashCode();
            User usertologIn=new User(emailstr,String.valueOf(passHash),1,null);

            ServerRequests serverRequests=new ServerRequests(this);
            serverRequests.loggingUserinBackground(usertologIn, new GetUserCallbacks() {
                @Override
                public void done(User returneduser) {
                    if(returneduser !=null){
                        saveloggedInuserPreferences(returneduser);
                    }else {
                        showErrordialog("Could not log in.  Wrong email or password");
                    }
                }

                @Override
                public void serverReponse(String reponse) {

                }

                @Override
                public void userlist(ArrayList<User> reponse) {

                }
            });
        }


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
    }

    @Override
    protected void onStart() {
        super.onStart();
        count=0;
        if (authenticate()) {
            displayUserdetails();

        }

    }

    private  boolean internetConnection(){
        return new ServerRequests(this).haveNetworkConnection();
    }
    private void displayUserdetails() {
        User user = userLocalStore.getLoggedInUser();
        emailed.setText(user.email);
    }

    //true if user logged in
    private boolean authenticate() {
        return userLocalStore.getUserLoggedIn();
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
}
