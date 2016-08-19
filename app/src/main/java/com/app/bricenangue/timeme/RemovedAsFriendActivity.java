package com.app.bricenangue.timeme;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class RemovedAsFriendActivity extends ActionBarActivity {

    private String senderRegId, receiverername,message,sendername,email,password;
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
            receiverername =extras.getString("reciever");
            sendername =extras.getString("sender");
            senderRegId=extras.getString("senderRegId");
            message=extras.getString("messagefromgcm");


        }


        userLocalStore.setUserPartnerRegId(senderRegId);
        userLocalStore.setUserPartnerEmail(receiverername);
        TextView tv=(TextView)findViewById(R.id.removedasfriendtext);
        tv.setText(message);
    }
    public void OnbuttonRemoveAsFriendPressed(View v){
        userLocalStore.setUserPartnerRegId(senderRegId);
        userLocalStore.setUserPartnerEmail(receiverername);
        finish();
    }
}
