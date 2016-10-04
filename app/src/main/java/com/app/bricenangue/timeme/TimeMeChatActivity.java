package com.app.bricenangue.timeme;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.Firebase;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TimeMeChatActivity extends AppCompatActivity {

    public static boolean messageshowed= true;
    private TextView mUserMessageChatText;
    private ListView listView_chat;

    /* Sender and Recipient status*/
    private static final int SENDER_STATUS=0;
    private static final int RECIPIENT_STATUS=1;

    /* Recipient uid */
    private String mRecipientUid;

    /* Sender uid */
    private String mSenderUid,chatRoom,chatmessage,chatPartneruid,chatPartnerName,chatPartnerPicURL;

    /* unique Firebase ref for this chat */
    private Firebase mFirebaseMessagesChat;

    /* Listen to change in chat in firabase-remember to remove it */
    private ChildEventListener mMessageChatListener;
    private EditText editText;
    private Button buttonSend;
    private DatabaseReference root;
    private FirebaseAuth auth;
    private String temp_key,chatPartnerId,chatpartnerName;
    private UserLocalStore userLocalStore;

    private ChildEventListener childEventListener;
    private List<MessageChatModel> newMessages=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_me_chat);

        messageshowed=false;
        auth=FirebaseAuth.getInstance();
        MyFireBaseMessagingService.notificationId=0;
        Bundle extras=getIntent().getExtras();
        userLocalStore=new UserLocalStore(this);
        if(extras!=null){

            chatRoom=extras.getString("Chatroom");

            if(extras.containsKey("registrationSenderIDs")){
                chatPartnerId=extras.getString("registrationSenderIDs");
                chatpartnerName=extras.getString("chattingToName");
            }
            if(extras.containsKey("chatPartnerName")){
                chatPartnerName=extras.getString("chatPartnerName");
                chatPartnerPicURL=extras.getString("chatPartnerPicURL");
            }
        }
        assert auth.getCurrentUser()!=null;
        mSenderUid=auth.getCurrentUser().getUid();
        chatPartneruid=userLocalStore.getUserfriendliststring().trim();

        if(chatPartneruid.isEmpty()){
            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(Config.FIREBASE_APP_URL_USERS)
                    .child(auth.getCurrentUser().getUid()).child(Config.FIREBASE_APP_URL_USERS_privateProfileInfo)
                    .child("friendlist");
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()&& !dataSnapshot.getValue(String.class).isEmpty()){
                        userLocalStore.setUserfriendliststring(dataSnapshot.getValue(String.class));
                        ref.removeEventListener(this);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
        chatPartneruid=userLocalStore.getUserfriendliststring().trim();

        root= FirebaseDatabase.getInstance().getReference().child(Config.FIREBASE_APP_URL_CHAT_ROOMS);

        // Reference to recyclerView and text view
        listView_chat=(ListView) findViewById(R.id.chat_listview);
        mUserMessageChatText=(TextView)findViewById(R.id.chat_user_message);
        buttonSend=(Button)findViewById(R.id.sendUserMessage);
        editText=(EditText) findViewById(R.id.chat_user_message);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String,Object> map=new HashMap<String, Object>();
                temp_key=root.push().getKey();
                root.updateChildren(map);
                DatabaseReference message_root=root.child(chatRoom).child(temp_key);
                if(!editText.getText().toString().isEmpty()){

                    sendNotification(userLocalStore.getUserPartnerRegId(),userLocalStore.getUserPartnerEmail());
                    Map<String,Object> map1=new HashMap<String, Object>();
                    map1.put("sender",mSenderUid);
                    if(chatPartneruid!=null){
                        map1.put("recipient",chatPartneruid);
                    }else {
                        map1.put("recipient",chatPartneruid);
                    }

                    map1.put("message",editText.getText().toString());

                    message_root.updateChildren(map1);
                    editText.setText("");
                }


            }
        });


        if(chatPartnerName!=null &&!chatPartnerName.isEmpty()){
            setTitle(chatPartnerName);
        }else {
            setTitle("unkowned user");
        }




    }


    @Override
    protected void onStart() {
        super.onStart();
        messageshowed=false;
      DatabaseReference ref=root.child(chatRoom);
        FirebaseListAdapter<MessageChatModel> adapter=new FirebaseListAdapter<MessageChatModel>(
                TimeMeChatActivity.this,
                MessageChatModel.class,
                R.layout.chat,
                ref
        ) {
            @Override
            protected void populateView(View v, MessageChatModel model, int position) {
                boolean left;
                if(model.getSender().equals(mSenderUid)){
                    model.setRecipientOrSenderStatus(SENDER_STATUS);
                }else{
                    model.setRecipientOrSenderStatus(RECIPIENT_STATUS);
                }

                LinearLayout layout=(LinearLayout)v.findViewById(R.id.message1);

                if(model.getRecipientOrSenderStatus()==RECIPIENT_STATUS){
                    left=true;
                }else{
                    left=false;
                }
                TextView chattext=(TextView)v.findViewById(R.id.singlemessage);
                chattext.setText(model.getMessage());
                chattext.setBackgroundResource(left ? R.drawable.out_message_bg : R.drawable.in_message_bg);
                layout.setGravity(left ? Gravity.LEFT : Gravity.RIGHT);
            }
        };
        listView_chat.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView_chat.setStackFromBottom(true);
        listView_chat.setAdapter(adapter);
        listView_chat.setScrollY(adapter.getCount()-1);


    }

    @Override
    protected void onStop() {
        super.onStop();
        messageshowed=true;

    }

    @Override
    protected void onPause() {
        super.onPause();
        messageshowed=true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        messageshowed=false;

    }

    public  void sendNotification(final String regId, final String email) {

        if (!userLocalStore.getUserRegistrationId().isEmpty()) {


            Thread thread = new Thread() {
                @Override
                public void run() {
                    HttpURLConnection conn=null;
                    try {

                        String regid = userLocalStore.getUserRegistrationId();
                        String sendertname=userLocalStore.getLoggedInUser().getfullname();
                        ArrayList<Pair<String,String>> data=new ArrayList<>();


                        String title=getString(R.string.fcm_Notification_title_message_sent);

                        data.add(new Pair<String, String>("message", editText.getText().toString() ));
                        data.add(new Pair<String, String>("registrationReceiverIDs",regId));
                        data.add(new Pair<String, String>("receiver",email));
                        data.add(new Pair<String, String>("sender", userLocalStore.getLoggedInUser().getfullname()));

                        data.add(new Pair<String, String>("registrationSenderIDs", regid));
                        data.add(new Pair<String, String>("title",title ));
                        data.add(new Pair<String, String>("chatRoom",chatRoom));
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
}
