package com.app.bricenangue.timeme;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;


public class InviteFriendActivity extends AppCompatActivity implements View.OnClickListener, TextView.OnEditorActionListener, UserForRequestListAdapter.UserCheckedForRequest {
    private Button buttonaddFriend, buttonSearchForRequest,buttonSendRequest;
    private EditText editTextEmail;
    private TextInputLayout textInputLayout;
    private ListView listViewFriends;
    private UserLocalStore userLocalStore;
    private ArrayList<PublicInfos> userArrayList=new ArrayList<>();
    private UserForRequestListAdapter userFriendForRequestListAdapter;
    private boolean[] isChecked;
    private User user;
    private FirebaseAuth auth;
    private DatabaseReference root;

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

        auth=FirebaseAuth.getInstance();
        root= FirebaseDatabase.getInstance().getReference();

        if(savedInstanceState!=null){
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
       userFriendForRequestListAdapter=new UserForRequestListAdapter(this,userArrayList,this);
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
                    final DatabaseReference reference=root.child(Config.FIREBASE_APP_URL_USERS)
                            .child(Config.FIREBASE_APP_URL_USERS_PUBLIC_USER_ROOM).child(emailstr.replace(".",""));
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                String uidpartner=dataSnapshot.getValue(String.class);
                                DatabaseReference refPartner=root.child(Config.FIREBASE_APP_URL_USERS)
                                        .child(uidpartner).child(Config.FIREBASE_APP_URL_USERS_publicProfilInfos);
                                refPartner.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.hasChild("regId")){
                                            userArrayList.add(dataSnapshot.getValue(PublicInfos.class));
                                            populateListview();
                                            buttonSendRequest.setVisibility(View.VISIBLE);
                                            hideTheKeyboard(InviteFriendActivity.this,editTextEmail);

                                        }else {
                                            // no request possible
                                            //no corresponding user
                                            showErrordialog("You cannot send a friend request to this user");
                                            editTextEmail.requestFocus();
                                            editTextEmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                                            buttonSendRequest.setVisibility(View.GONE);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }else {
                               // no user found
                                //no corresponding user
                                showErrordialog("No corresponding user found. Please verify the email address");
                                editTextEmail.requestFocus();
                                editTextEmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                                buttonSendRequest.setVisibility(View.GONE);
                            }
                            reference.removeEventListener(this);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

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
                            Toast.makeText(getApplicationContext(),"Friend request sent to "+userArrayList.get(i).getFirstname()
                                    +" " + userArrayList.get(i).getLastname(),Toast.LENGTH_SHORT).show();
                            finish();
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
        outState.putBooleanArray("isChecked",isChecked);
        outState.putParcelable("user",user);
    }

    public  void sendFriendrequest(final PublicInfos user) {

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
                        data.add(new Pair<String, String>("registrationReceiverIDs", user.getRegId()));
                        data.add(new Pair<String, String>("receiver", user.getEmail()));
                        data.add(new Pair<String, String>("sender", userLocalStore.getLoggedInUser().email));

                        data.add(new Pair<String, String>("registrationSenderIDs", regid));
                        data.add(new Pair<String, String>("title","You have a new friend request." ));
                        data.add(new Pair<String, String>("chatRoom","" ));
                        data.add(new Pair<String, String>("senderuid",auth.getCurrentUser().getUid() ));
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
 class UserForRequestListAdapter extends BaseAdapter
{
    private ArrayList<PublicInfos> userlist;
    private Context context;
    private boolean[] checked;
    String senderName,mesg,status,currentusername;

    public interface UserCheckedForRequest{
        void onUserisChecked(boolean[] position);
    }

    private UserCheckedForRequest userCheckedForRequest;
    public UserForRequestListAdapter(Context context, ArrayList<PublicInfos> userlist, UserCheckedForRequest userCheckedForRequest){
        this.context=context;
        this.userlist=userlist;
        checked=new boolean[userlist.size()];
        this.userCheckedForRequest=userCheckedForRequest;

    }

    public void setUserStatus(boolean[] status){

        this.checked=status;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return userlist.size();
    }

    @Override
    public Object getItem(int position) {
        return userlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Holder holder;
        if(convertView==null){
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.user_friend_item,null);
            holder=new Holder();

            holder.email=(TextView)convertView.findViewById(R.id.usernamefriend_user_friend_item);
            holder.userPicture=(ImageView)convertView.findViewById(R.id.avatarfriend_user_friend_item);
            holder.checker=(ImageView)convertView.findViewById(R.id.checkerImageView_user_friend_item);


            convertView.setTag(holder);
        }else{

            holder=(Holder)convertView.getTag();
        }

        String usernam=userlist.get(position).getEmail();
        Bitmap picture=null;

        if(picture!=null){
            holder.userPicture.setImageBitmap(picture);
        }

        holder.email.setText(usernam);



        holder.checker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checked[position]){
                    checked[position]=false;
                    holder.checker.setImageResource(R.drawable.unchecked);
                }else {
                    checked[position]=true;
                    holder.checker.setImageResource(R.drawable.checked);
                }
                if(userCheckedForRequest!=null){
                    userCheckedForRequest.onUserisChecked(checked);
                }
            }
        });


        if(!checked[position]){
            holder.checker.setImageResource(R.drawable.unchecked);
        }else {
            holder.checker.setImageResource(R.drawable.checked);
        }
        return convertView;
    }

    static class Holder {
        public TextView email;
        public ImageView userPicture;
        public ImageView checker;

    }
}

