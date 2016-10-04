package com.app.bricenangue.timeme;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RemovedAsFriendActivity extends ActionBarActivity {

    private String senderRegId, receiverername,message,sendername,email,password,chatRoom,senderuid;
    private MySQLiteHelper mySQLiteHelper;
    private UserLocalStore userLocalStore;
    private  int id;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_removed_as_friend);
        Bundle extras=getIntent().getExtras();
        userLocalStore=new UserLocalStore(this);
        mySQLiteHelper=new MySQLiteHelper(this);
        auth=FirebaseAuth.getInstance();
        if (auth.getCurrentUser()==null){
            auth.signInWithEmailAndPassword(userLocalStore.getLoggedInUser().email,userLocalStore.getLoggedInUser().password);
        }
        if(extras!=null){
            receiverername =extras.getString("receiver");
            sendername =extras.getString("sender");
            senderRegId=extras.getString("senderRegId");
            message=extras.getString("messagefromgcm");
            chatRoom=extras.getString("chatRoom");
            senderuid=extras.getString("senderuid");


        }

        if(!chatRoom.isEmpty()){
            final DatabaseReference refUser= FirebaseDatabase.getInstance().getReference().child(Config.FIREBASE_APP_URL_USERS)
                    .child(auth.getCurrentUser().getUid())
                    ;
            refUser.child(Config.FIREBASE_APP_URL_USERS_privateProfileInfo).child("friendlist").setValue(senderuid).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        refUser.child("chatroom").setValue(chatRoom).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    userLocalStore.setUserfriendliststring(senderuid);
                                    userLocalStore.setChatRoom(chatRoom);
                                    userLocalStore.setUserPartnerRegId(senderRegId);
                                    userLocalStore.setUserPartnerEmail(receiverername);
                                    Toast.makeText(getApplicationContext(),userLocalStore.getChatRoom()+"\n"+
                                            userLocalStore.getUserPartnerEmail(),Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }else {
                        Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            });

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
