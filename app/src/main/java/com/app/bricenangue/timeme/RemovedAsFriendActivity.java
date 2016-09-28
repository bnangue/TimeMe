package com.app.bricenangue.timeme;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class RemovedAsFriendActivity extends ActionBarActivity {

    private String senderRegId, receiverername,message,sendername,email,password,chatRoom;
    private MySQLiteHelper mySQLiteHelper;
    private UserLocalStore userLocalStore;
    private  int id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_removed_as_friend);
        Bundle extras=getIntent().getExtras();
        userLocalStore=new UserLocalStore(this);
        mySQLiteHelper=new MySQLiteHelper(this);
        if(extras!=null){
            receiverername =extras.getString("receiver");
            sendername =extras.getString("sender");
            senderRegId=extras.getString("senderRegId");
            message=extras.getString("messagefromgcm");
            chatRoom=extras.getString("chatRoom");


        }

        if(!chatRoom.isEmpty()){
            userLocalStore.setChatRoom(chatRoom);
            Toast.makeText(getApplicationContext(),userLocalStore.getChatRoom()+"\n"+
                    userLocalStore.getUserPartnerEmail(),Toast.LENGTH_SHORT).show();
        }
        TextView tv=(TextView)findViewById(R.id.removedasfriendtext);
        tv.setText(message);
    }
    public void OnbuttonRemoveAsFriendPressed(View v){
        userLocalStore.setUserPartnerRegId(senderRegId);
        userLocalStore.setUserPartnerEmail(receiverername);
        finish();
    }
}
