package com.app.bricenangue.timeme;

import android.app.AlertDialog;
import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;


public class InviteFriendActivity extends AppCompatActivity implements View.OnClickListener, TextView.OnEditorActionListener, UserFriendForRequestListAdapter.UserCheckedForRequest {
    private Button buttonaddFriend, buttonSearchForRequest,buttonSendRequest;
    private EditText editTextEmail;
    private TextInputLayout textInputLayout;
    private ListView listViewFriends;
    private UserLocalStore userLocalStore;
    private ArrayList<User> userArrayList=new ArrayList<>();
    private UserFriendForRequestListAdapter userFriendForRequestListAdapter;
    private boolean[] isChecked;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friend);

        userLocalStore=new UserLocalStore(this);
        buttonaddFriend=(Button)findViewById(R.id.button_invite_friend_activity_add_a_new_friend);
        buttonSearchForRequest =(Button)findViewById(R.id.button_search_for_request_to_partner);
        buttonSendRequest =(Button)findViewById(R.id.button_send_request_to_partner);
        editTextEmail=(EditText) findViewById(R.id.editText_enter_friend_email_address);
        textInputLayout=(TextInputLayout) findViewById(R.id.editText_enter_friend_email_address_layout);
        listViewFriends=(ListView)findViewById(R.id.activity_invite_friend_listView);


        if(savedInstanceState!=null){
            userArrayList=savedInstanceState.getParcelableArrayList("userArrayList");
            isChecked=savedInstanceState.getBooleanArray("isChecked");
            user=savedInstanceState.getParcelable("user");

            prepareorientationChanged();
        }

        buttonaddFriend.setOnClickListener(this);
        buttonSearchForRequest.setOnClickListener(this);
        buttonSendRequest.setOnClickListener(this);
        editTextEmail.setOnEditorActionListener(this);

        if(userArrayList!=null && userArrayList.size()!=0){
            buttonaddFriend.setVisibility(View.GONE);
            buttonSearchForRequest.setVisibility(View.VISIBLE);
            textInputLayout.setVisibility(View.VISIBLE);
            buttonSendRequest.setVisibility(View.VISIBLE);
            hideTheKeyboard(this,editTextEmail);
        }
    }

    private void prepareorientationChanged(){
        if(isChecked!=null){
            populateListview();
            userFriendForRequestListAdapter.setUserStatus(isChecked);

        }

    }

   private void populateListview(){

       listViewFriends.setVisibility(View.VISIBLE);
       userFriendForRequestListAdapter=new UserFriendForRequestListAdapter(this,userArrayList,this);
       if(isChecked==null){
           isChecked=new boolean[userArrayList.size()];

       }else {
           userFriendForRequestListAdapter.setUserStatus(isChecked);
       }
       listViewFriends.setAdapter(userFriendForRequestListAdapter);
       listViewFriends.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
       listViewFriends.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           @Override
           public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
               if(isChecked[i]){
                   isChecked[i]=false;
                   userFriendForRequestListAdapter.setUserStatus(isChecked);
               }else {
                   isChecked[i]=true;
                   userFriendForRequestListAdapter.setUserStatus(isChecked);
               }
           }

           @Override
           public void onNothingSelected(AdapterView<?> adapterView) {

           }
       });



   }
    @Override
    public void onClick(View view) {
        int id=view.getId();
        switch (id){
            case R.id.button_search_for_request_to_partner:

                userArrayList.clear();
                isChecked=null;
                String emailstr=editTextEmail.getText().toString();

                if(TextUtils.isEmpty(emailstr)){
                    editTextEmail.setError("this field cannot be empty");
                    editTextEmail.requestFocus();
                }else if (emailstr.equals(userLocalStore.getLoggedInUser().email)){
                    editTextEmail.setError("Please enter a different email address");
                    editTextEmail.requestFocus();
                }else {
                    ServerRequests serverRequests=new ServerRequests(this);
                    serverRequests.fetchUserForRequestinBackground(emailstr, new GetUserCallbacks() {
                        @Override
                        public void done(User returneduser) {
                            if(returneduser!=null && !returneduser.regId.isEmpty()){
                                userArrayList.add(returneduser);
                                populateListview();
                                buttonSendRequest.setVisibility(View.VISIBLE);
                                hideTheKeyboard(InviteFriendActivity.this,editTextEmail);

                            }else {
                                //no corresponding user
                                showErrordialog("No corresponding user found. Please verify the email address");
                                editTextEmail.requestFocus();
                                editTextEmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                                buttonSendRequest.setVisibility(View.GONE);
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

                break;
            case R.id.button_invite_friend_activity_add_a_new_friend:
                //open edittext and set button visible
                buttonaddFriend.setVisibility(View.GONE);
                buttonSearchForRequest.setVisibility(View.VISIBLE);
                textInputLayout.setVisibility(View.VISIBLE);


                break;
            case R.id.button_send_request_to_partner:
                if(isChecked!=null){
                    for (int i=0;i<isChecked.length;i++){
                        if(isChecked[i]){
                            sendFriendrequest(userArrayList.get(i));
                        }
                    }
                }else {

                    Toast.makeText(getApplicationContext(),"Please select a user to add to your friend list",Toast.LENGTH_SHORT).show();

                }

                break;
        }

    }


    /**
     * Method for hiding the Keyboard
     * @param context The context of the activity
     * @param editText The edit text for which we want to hide the keyboard
     */
    public void hideTheKeyboard(Context context, EditText editText){
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }
    /**
     * Another method to hide the keyboard if the above method is not working.
     */
    public void hideTheKeyboardSecond(EditText editText){
        editText.setInputType(InputType.TYPE_NULL);
    }
    /**
     * Method for showing the Keyboard
     * @param context The context of the activity
     * @param editText The edit text for which we want to show the keyboard
     */
    public void showTheKeyboard(Context context, EditText editText){
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    /**
     * Method for showing the Keyboard when a QWERTY (physical keyboard is enabled)
     * @param context The context of the activity
     * @param editText The edit text for which we want to show the keyboard
     */
    public void showTheKeyboardWhenQWERTY(Context context, EditText editText){
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.RESULT_UNCHANGED_SHOWN, 0);
    }



    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if ((keyEvent != null && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (i == EditorInfo.IME_ACTION_DONE)) {

        }
        return false;
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("userArrayList",userArrayList);
        outState.putBooleanArray("isChecked",isChecked);
        outState.putParcelable("user",user);
    }

    public  void sendFriendrequest(final User user) {

        if (!userLocalStore.getUserRegistrationId().isEmpty()) {


            Thread thread = new Thread() {
                @Override
                public void run() {
                    HttpURLConnection conn=null;
                    try {

                        String regid = userLocalStore.getUserRegistrationId();
                        String sendertname=userLocalStore.getLoggedInUser().getfullname();
                        ArrayList<Pair<String,String>> data=new ArrayList<>();


                        data.add(new Pair<String, String>("message", "Do you want to be friend with " +sendertname ));
                        data.add(new Pair<String, String>("registrationReceiverIDs", user.regId));
                        data.add(new Pair<String, String>("receiver", user.email));
                        data.add(new Pair<String, String>("sender", userLocalStore.getLoggedInUser().email));

                        data.add(new Pair<String, String>("registrationSenderIDs", regid));
                        data.add(new Pair<String, String>("title","You have a new friend request." ));
                        data.add(new Pair<String, String>("apiKey", Config.FIREBASESERVER_KEY));

                        byte[] bytes = getData(data).getBytes("UTF-8");


                        URL url=new URL(Config.YOUR_SERVER_URL+ "FireBaseConnection.php");
                        conn=(HttpURLConnection)url.openConnection();
                        conn.setDoOutput(true);
                        conn.setUseCaches(false);
                        conn.setFixedLengthStreamingMode(bytes.length);
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Content-Type",
                                "application/x-www-form-urlencoded;charset=UTF-8");
                        // post the request
                        OutputStream out = conn.getOutputStream();
                        out.write(bytes);
                        out.close();

                        BufferedReader in = new BufferedReader(
                                new InputStreamReader(conn.getInputStream()));
                        String inputLine;
                        StringBuffer reponse = new StringBuffer();

                        while ((inputLine = in.readLine()) != null) {
                            reponse.append(inputLine);
                        }
                        final String response =reponse.toString();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }finally {
                        if(conn!=null){
                            conn.disconnect();
                        }
                    }
                }
            };

            thread.start();

        }

    }

    private static String getData(ArrayList<Pair<String, String>> values) throws UnsupportedEncodingException {
        StringBuilder result=new StringBuilder();
        for(Pair<String,String> pair : values){

            if(result.length()!=0)

                result.append("&");
            result.append(URLEncoder.encode(pair.first, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.second, "UTF-8"));

        }
        return result.toString();
    }

    private void showErrordialog(String message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage(message);
        alert.setPositiveButton("OK", null);
        alert.show();
    }

    @Override
    public void onUserisChecked(boolean[] position) {
        isChecked=position;
        userFriendForRequestListAdapter.setUserStatus(isChecked);
        for(int i=0;i<position.length;i++){
            if(position[i]){

            }
        }
    }
}
