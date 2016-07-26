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

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

public class LoginScreenActivity extends AppCompatActivity implements View.OnClickListener, TextView.OnEditorActionListener {

    private Button registerbtn,loginbtn;
    private EditText emailed,passworded;
    private String emailstr,passwordstr;
    private UserLocalStore userLocalStore;
    private int count=0;
    private MySQLiteHelper mySQLiteHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        userLocalStore=new UserLocalStore(this);
        mySQLiteHelper=new MySQLiteHelper(this);

        registerbtn=(Button)findViewById(R.id.buttonregisterReg);
        loginbtn=(Button)findViewById(R.id.buttonlogin);
        emailed=(EditText)findViewById(R.id.editTextemailloggin);
        passworded=(EditText)findViewById(R.id.editTextpasswordloggin);

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

    private void logCurrenUserIn() {
        ServerRequests serverRequests=new ServerRequests(this);
        serverRequests.getCalenderEventAndUserInBackgroung(new GetEventsCallbacks() {
            @Override
            public void done(ArrayList<CalendarCollection> returnedeventobject) {
                if(returnedeventobject.size()!=0){

                    saveeventtoSQl(returnedeventobject);
                   logCurrentuserIn();
                    MainActivity.eventsareloaded=true;

                }else {
                    logCurrentuserIn();
                }
            }

            @Override
            public void itemslis(ArrayList<ShoppingItem> returnedShoppingItem) {

            }

            @Override
            public void updated(String reponse) {

            }
        });
    }


    private void saveloggedInuserPreferences(User returneduser){
        userLocalStore.setUserLoggedIn(true);
        userLocalStore.storeUserData(returneduser);
        userLocalStore.setUserPicturePath(userLocalStore.saveToInternalStorage(returneduser.picture));

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
                        Intent intent=new Intent(LoginScreenActivity.this,NewCalendarActivty.class);
                        intent.putExtra("loggedInUser",returneduser);
                        startActivity(intent);
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

    private void saveeventtoSQl(ArrayList<CalendarCollection> calendarCollections) {

        if(calendarCollections.size()!=0){

        }
        for(int i=0;i<calendarCollections.size();i++){
            try {
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("title",calendarCollections.get(i).title);
                jsonObject.put("description",calendarCollections.get(i).description);
                jsonObject.put("datetime",calendarCollections.get(i).datetime);
                jsonObject.put("creator",calendarCollections.get(i).creator);
                jsonObject.put("category",calendarCollections.get(i).category);
                jsonObject.put("startingtime",calendarCollections.get(i).startingtime);
                jsonObject.put("endingtime",calendarCollections.get(i).endingtime);
                jsonObject.put("hashid",calendarCollections.get(i).hashid);
                jsonObject.put("alldayevent",calendarCollections.get(i).alldayevent);
                Calendar c=new GregorianCalendar();
                Date dat=c.getTime();
                //String day= String.valueOf(dat.getDay());
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                String date = (String) android.text.format.DateFormat.format("yyyy-MM-dd", dat);
                IncomingNotification incomingNotification=new IncomingNotification(1,0,jsonObject.toString(),date);
                int incomingNotifiId =  mySQLiteHelper.addIncomingNotification(incomingNotification);

            }catch (Exception e){
                e.printStackTrace();

            }


        }

    }
}
