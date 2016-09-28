package com.app.bricenangue.timeme;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.Firebase;
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
    private RecyclerView mChatRecyclerView;
    private TextView mUserMessageChatText;
    private MessageChatAdapter mMessageChatAdapter;

    /* Sender and Recipient status*/
    private static final int SENDER_STATUS=0;
    private static final int RECIPIENT_STATUS=1;

    /* Recipient uid */
    private String mRecipientUid;

    /* Sender uid */
    private String mSenderUid,chatRoom,chatmessage,chatSenderName;

    private User chatPartner;
    /* unique Firebase ref for this chat */
    private Firebase mFirebaseMessagesChat;

    /* Listen to change in chat in firabase-remember to remove it */
    private ChildEventListener mMessageChatListener;
    private EditText editText;
    private Button buttonSend;
    private DatabaseReference root;
    private String temp_key,chatPartnerId,chatpartnerName;
    private UserLocalStore userLocalStore;

    private ChildEventListener childEventListener;
    private List<MessageChatModel> newMessages=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_me_chat);

        messageshowed=false;
        MyFireBaseMessagingService.notificationId=0;
        Bundle extras=getIntent().getExtras();
        userLocalStore=new UserLocalStore(this);
        if(extras!=null){

            chatRoom=extras.getString("Chatroom");

            if(extras.containsKey("registrationSenderIDs")){
                chatPartnerId=extras.getString("registrationSenderIDs");
                chatpartnerName=extras.getString("chattingToName");
            }
            if(extras.containsKey("chatPartner")){
                chatPartner=extras.getParcelable("chatPartner");
            }
        }
        mSenderUid=userLocalStore.getUserRegistrationId();
        root= FirebaseDatabase.getInstance().getReference().child(chatRoom);
        root.push();
        // Reference to recyclerView and text view
        mChatRecyclerView=(RecyclerView)findViewById(R.id.chat_recycler_view);
        mUserMessageChatText=(TextView)findViewById(R.id.chat_user_message);
        buttonSend=(Button)findViewById(R.id.sendUserMessage);
        editText=(EditText) findViewById(R.id.chat_user_message);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String,Object> map=new HashMap<String, Object>();
                temp_key=root.push().getKey();
                root.updateChildren(map);
                DatabaseReference message_root=root.child(temp_key);
                if(!editText.getText().toString().isEmpty()){

                    sendNotification(userLocalStore.getUserPartnerRegId(),userLocalStore.getUserPartnerEmail());
                    Map<String,Object> map1=new HashMap<String, Object>();
                    map1.put("sender",mSenderUid);
                    if(chatPartner!=null){
                        map1.put("recipient",chatPartner.regId);
                    }else {
                        map1.put("recipient",chatPartnerId);
                    }

                    map1.put("message",editText.getText().toString());

                    message_root.updateChildren(map1);
                    editText.setText("");
                }


            }
        });

        // Set recyclerView and adapter
        mChatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mChatRecyclerView.setHasFixedSize(true);

        // Initialize adapter
       initAdapter();

        if(chatPartner!=null){
            setTitle(chatPartner.getfullname());
        }else {
            setTitle(chatpartnerName);
        }




    }

    private void initAdapter(){
        List<MessageChatModel> emptyMessageChat=new ArrayList<MessageChatModel>();
        mMessageChatAdapter=new MessageChatAdapter(emptyMessageChat);
        mMessageChatAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                mChatRecyclerView.setScrollY(mChatRecyclerView.getChildCount()-1);
            }
        });

        // Set adapter to recyclerView
        mChatRecyclerView.setAdapter(mMessageChatAdapter);
        if(mMessageChatAdapter.getItemCount()!=0){
            mMessageChatAdapter.cleanUp();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        messageshowed=false;
        initAdapter();
     //   root= FirebaseDatabase.getInstance().getReference().child(chatRoom);


       root.addChildEventListener(new com.google.firebase.database.ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists()){
                    // Log.e(TAG, "A new chat was inserted");


                    MessageChatModel newMessage=dataSnapshot.getValue(MessageChatModel.class);
                    if(newMessage.getSender().equals(mSenderUid)){
                        newMessage.setRecipientOrSenderStatus(SENDER_STATUS);
                    }else{
                        newMessage.setRecipientOrSenderStatus(RECIPIENT_STATUS);
                    }

                    newMessages.add(newMessage);


                }
                mMessageChatAdapter.refillFirsTimeAdapter(newMessages);
                mChatRecyclerView.scrollToPosition(mMessageChatAdapter.getItemCount()-1);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
        initAdapter();
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
