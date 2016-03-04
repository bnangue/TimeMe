package com.example.bricenangue.timeme;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class OpenUserProfileActivity extends AppCompatActivity implements AlertDailogChangePassword.OnPasswordChanged,AlertDialogChangeEmail.OnEmailChanged {

    private User currentuser;
    private TextView changepass;
    private EditText emailed;
    private String emailstr,firstnamestr,lastnamestr,passwordhashstr;
    public static boolean arechangesSaved;
    private Snackbar snackbar;
    private CoordinatorLayout coordinatorLayout;
    private ImageView profilepic;
   private Bitmap bitmap;
    UserLocalStore userLocalStore;
    Menu menu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        arechangesSaved=true;
        setContentView(R.layout.activity_open_user_profile);

        userLocalStore=new UserLocalStore(this);
        coordinatorLayout=(CoordinatorLayout)findViewById(R.id.coordinateLayoutopenprofile);
        Bundle extras=getIntent().getExtras();
        if(extras!=null){
            currentuser=extras.getParcelable("loggedInUser");
        }
        profilepic=(ImageView)findViewById(R.id.imageViewUserprofilepicture);
        emailed=(EditText)findViewById(R.id.editTextemailprofileview);
        emailed.setText(currentuser.email);

        emailstr=currentuser.email;
        passwordhashstr=currentuser.password;
        Toast.makeText(this,currentuser.email,Toast.LENGTH_SHORT).show();
    }

    public void OnEditEmailInProfile(View view){
        editEmail();
    }

    private void editEmail() {

        DialogFragment fragment=new AlertDialogChangeEmail();
        fragment.setCancelable(false);
        fragment.show(getSupportFragmentManager(), "CHANGE EMAIL");
    }

    public void OnChangepassword(View view){
        arechangesSaved=false;

     showOption(R.id.action_menu_save);
        changepasword();
    }

    private void changepasword() {
        DialogFragment fragment=new AlertDailogChangePassword();
        fragment.setCancelable(false);
        fragment.show(getSupportFragmentManager(), "CHANGE PASSWORD");
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu=menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_user_profile, menu);
        if(!arechangesSaved){
            showOption(R.id.action_menu_save);
        }else{
            hideOption(R.id.action_menu_save);
        }
        return true;
    }

    private void hideOption(int id){
        MenuItem item=menu.findItem(id);
        item.setVisible(false);
    }
    private void showOption(int id){
        MenuItem item=menu.findItem(id);
        item.setVisible(true);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_menu_save) {
            saveChangestoServer();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void save(final User newu){

        ServerRequests serverRequests=new ServerRequests(this);
        serverRequests.savingEmailAndPassowrdChangedUserinBackground(currentuser, newu, new GetUserCallbacks() {
            @Override
            public void done(User returneduser) {

            }

            @Override
            public void serverReponse(String reponse) {
                if(reponse.contains("User data successfully updated")){
                    arechangesSaved=true;
                    userLocalStore.storeUserData(newu);
                }else {
                    showSnackBar();
                }
            }

            @Override
            public void userlist(ArrayList<User> reponse) {

            }
        });
    }
    private void saveChangestoServer() {
        User newuser=new User(emailstr,passwordhashstr);
            save(newuser);
    }

    public void showSnackBar(){
        snackbar = Snackbar
                .make(coordinatorLayout, " An error occured during registration ", Snackbar.LENGTH_INDEFINITE)
                .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveChangestoServer();
                    }
                });
        snackbar.setActionTextColor(Color.WHITE);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(getResources().getColor(R.color.colorSnackbar));
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }
    @Override
    public void emailchange(String newEmail) {
        if(newEmail!=null){
            arechangesSaved=false;
            showOption(R.id.action_menu_save);
            emailstr=newEmail;
            if(!emailstr.isEmpty()){
                emailed.setText(emailstr);
                emailed.setTextColor(getResources().getColor(R.color.colorAccent));

            }
        }else {
            emailstr=currentuser.password;
        }

    }

    @Override
    public void passwordchange(String newPassword) {
        if(newPassword!=null){
            arechangesSaved=false;
            showOption(R.id.action_menu_save);
            passwordhashstr=newPassword;
        }else {
            passwordhashstr=currentuser.password;
        }

    }

    @Override
    public void onBackPressed() {
        if(arechangesSaved){
             onBackPressed();
        }else{

        }
    }
}
