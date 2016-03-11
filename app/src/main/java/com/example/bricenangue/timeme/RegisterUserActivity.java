package com.example.bricenangue.timeme;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
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

public class RegisterUserActivity extends AppCompatActivity implements TextView.OnEditorActionListener {
    private String emailstr,passwordstr,confirmpasswordstr,firstnamestr,lastnamestr;
    private EditText emailed,passworded,confirmpassworded,firstnameed,lastnameed;
    private Button registerbtn;
    private TextView gotologgintv;
    UserLocalStore userLocalStore;
    private Snackbar snackbar;
    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        userLocalStore=new UserLocalStore(this);

        coordinatorLayout=(CoordinatorLayout)findViewById(R.id.coordinateLayout);
        registerbtn=(Button)findViewById(R.id.buttonregisterReg);
        emailed=(EditText)findViewById(R.id.editTextregemail);
        passworded=(EditText)findViewById(R.id.editTextregpassword);
        firstnameed=(EditText)findViewById(R.id.editTextregisterfirstname);
        lastnameed=(EditText)findViewById(R.id.editTextregisterlastname);

        confirmpassworded=(EditText)findViewById(R.id.editTextregconfirmpassword);
        confirmpassworded.setOnEditorActionListener(this);



    }

    public void Onregisterclicked(View view){
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
        }else if(!TextUtils.isEmpty(emailstr) && !TextUtils.isEmpty(emailstr) && !TextUtils.isEmpty(emailstr)){
            if(passwordstr.equals(confirmpasswordstr)){
                int passHash=passwordstr.hashCode();
                User usertoRegister=new User(emailstr,String.valueOf(passHash));
                usertoRegister.firstname=firstnamestr;
                usertoRegister.lastname=lastnamestr;
                registerUser(usertoRegister);
            }else {
                Toast.makeText(getApplicationContext(),"password doesn't match. Please check your entry",Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void registerUser(final User user) {
        ServerRequests serverRequests=new ServerRequests(this);
        serverRequests.registerUserinBackground(user, new GetUserCallbacks() {
            @Override
            public void done(User returneduser) {

            }

            @Override
            public void serverReponse(String reponse) {
                if(reponse.contains("User registered successfully")){
                    startActivity(new Intent(RegisterUserActivity.this,LoginScreenActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                }else {
                    showSnackBar(user);
                }
            }

            @Override
            public void userlist(ArrayList<User> reponse) {

            }
        });
    }

    public void showSnackBar(final User user){
        snackbar = Snackbar
                .make(coordinatorLayout, " An error occured during registration ", Snackbar.LENGTH_INDEFINITE)
                .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       registerUser(user);
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
                    int passHash=passwordstr.hashCode();
                    User usertoRegister=new User(emailstr,String.valueOf(passHash));
                    usertoRegister.firstname=firstnamestr;
                    usertoRegister.lastname=lastnamestr;
                    registerUser(usertoRegister);
                }else {
                    Toast.makeText(getApplicationContext(),"password doesn't match. Please check your entry",Toast.LENGTH_SHORT).show();
                }
            }
        }
        return false;
    }
}
