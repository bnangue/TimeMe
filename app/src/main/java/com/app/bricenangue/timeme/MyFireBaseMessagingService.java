package com.app.bricenangue.timeme;


import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

/**
 * Created by bricenangue on 12/08/16.
 */
public class MyFireBaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private final static String GROUP_KEY_MESSAGES = "group_key_messages";
    public static final int NOTIFICATION_ID = 1;

    private NotificationManager mNotificationManager;

    private IncomingNotification incomingNotification;
    private MySQLiteHelper mySQLiteHelper;
   // private SQLPictureHelper sqlPictureHelper;
  //  private SQLITELastMessageReceive sqliteLastMessageReceive;
    private int incomingNotifiId;
    public static int notificationId=0;
    public static ArrayList<String> incomingmessages=new ArrayList<>();
    private UserLocalStore userLocalStore;


    public MyFireBaseMessagingService() {

    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Displaying data in log
        //It is optional
        remoteMessage.getData();

        Log.d(TAG, "From: " + remoteMessage.getFrom());
        //Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());


        Map<String,String > maps=remoteMessage.getData();
        String title=maps.get("title");

        Intent i = new Intent("com.app.bricenangue.timeme.CHAT_MESSAGE_RECEIVED");
        i.putExtra("message", maps.get("message"));
        i.putExtra("sender", maps.get("sender"));
        i.putExtra("registrationSenderIDs", maps.get("registrationSenderIDs"));

        if(title.contains("You have a new friend request")){
            sendNotificationFriendRequest(maps);
        }else if(title.contains(" is now your friend")){
            sendNotificationFriendRequestAccepted(maps);
        }else if(title.contains("New grocery list")){
            sendNotification(maps);
        }else if(title.contains("New chat message")){
           // sendChatNotification(maps);
            if(TimeMeChatActivity.messageshowed ){
                sendChatNotification(maps);
            }
        }
        /**
        else{
            if(!LiveChatActivity.messageshowed && maps.get("sender").equals(LiveChatActivity.receiverName)){
                sendOrderedBroadcast(i, null);
            }else if(LiveChatActivity.messageshowed){
                sendChatNotification(maps);
               // LiveChatBroadcastReceiver.completeWakefulIntent(intent);
            }else if(!LiveChatActivity.messageshowed && !maps.get("sender").equals(LiveChatActivity.receiverName)){
                sendChatNotification(maps);
               // LiveChatBroadcastReceiver.completeWakefulIntent(intent);
            }

        }
        **/
        //Calling method to generate notification

    }

    @Override
    public void onCreate() {
        super.onCreate();
        mySQLiteHelper=new MySQLiteHelper(this);

        userLocalStore=new UserLocalStore(this);

    }

    //This method is only generating push notification
    //It is same as we did in earlier posts
    private void sendNotification(Map<String,String> stringMap) {
        Map<String,String> map=stringMap;

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcherclock)
                .setContentTitle(map.get("title"))
                .setContentText(map.get("message"))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }

    private void sendNotificationFriendRequest(Map<String,String> stringMap){
        Map<String,String> map =stringMap;


        //String chattingFrom = extras.getString("chattingFrom");
        String sender = map.get("sender");// will be user as receiver name in current Device getting the notifiction
        String senderRegId = map.get("registrationSenderIDs");
        String message = map.get("message");
        String receiver = map.get("receiver");// will be user as sender name in current Device getting the notifiction
        String title = map.get("title");
        String senderuid = map.get("senderuid");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                this).setSmallIcon(R.drawable.ic_add_user_notification_icon)
                .setContentTitle("New friend request")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(""))
                .setContentText(title);
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setAutoCancel(true);
        mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this,RequestHandlerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        //intent.putExtra("chattingFrom", chattingFrom);
        intent.putExtra("sender", sender);
        intent.putExtra("receiver", receiver);
        intent.putExtra("senderRegId", senderRegId);
        intent.putExtra("messagefromgcm", message);
        intent.putExtra("senderuid", senderuid);
        intent.putExtra("request", true);

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentText(message);
        builder.setContentIntent(resultPendingIntent)
                .setGroup(GROUP_KEY_MESSAGES)
                .setGroupSummary(true);

        mNotificationManager.notify(NOTIFICATION_ID, builder.build());
    }


    private void sendChatNotification(Map<String,String> stringMap) {

        Map<String,String> map =stringMap;
        notificationId++;
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_chat_icon);

        //String chattingFrom = extras.getString("chattingFrom");
        String chattingToName = map.get("sender");// will be user as receiver name in current Device getting the notifiction
        String chattingToDeviceID = map.get("registrationSenderIDs");
        String msg = map.get("message");
        String title=map.get("title");
        String chatRoom=map.get("chatRoom");

        String message=chattingToName+": " +msg;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                this).setSmallIcon(R.mipmap.ic_chat_icon)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(""))
                .setContentText( getString(R.string.Chat_Message_recieved_from)+" " +chattingToName);
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setAutoCancel(true);
        mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this,TimeMeChatActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        //intent.putExtra("chattingFrom", chattingFrom);
        intent.putExtra("chattingToName",chattingToName);
        intent.putExtra("Chatroom", chatRoom);
        intent.putExtra("registrationSenderIDs",chattingToDeviceID);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(TimeMeChatActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT

                );
        //PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
        //		new Intent(this, ChatActivity.class), 0);

        incomingmessages.add(message);
        NotificationCompat.InboxStyle style=new NotificationCompat.InboxStyle();
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentText(message);
        for(int i=0;i<incomingmessages.size();i++){
            style.addLine(incomingmessages.get(i));
            style.setBigContentTitle(notificationId + " new messages");
            style.setSummaryText("messages");
        }
        if(notificationId>=2){
            builder.setContentIntent(resultPendingIntent)
                    .setStyle(style)
                    .setGroup(GROUP_KEY_MESSAGES)
                    .setGroupSummary(true);
        }
        builder.setContentIntent(resultPendingIntent);
        mNotificationManager.cancelAll();
        mNotificationManager.notify(notificationId, builder.build());
        if(!TimeMeChatActivity.messageshowed){
            mNotificationManager.cancelAll();
        }
    }


    private void sendNotificationFriendRequestAccepted(Map<String,String> stringMap){
        Map<String,String> map =stringMap;


        //String chattingFrom = extras.getString("chattingFrom");
        String sender = map.get("sender");// will be user as receiver name in current Device getting the notifiction
        String senderRegId = map.get("registrationSenderIDs");
        String message = map.get("message");
        String receiver = map.get("receiver");// will be user as sender name in current Device getting the notifiction
        String title = map.get("title");
        String chatRoom = map.get("chatRoom");
        String senderuid=map.get("senderuid");
        //to automaticly log in an dupdate friend list in mysql in device receiving notification

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                this).setSmallIcon(R.drawable.ic_add_user_notification_icon)
                .setContentTitle("New friend request")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(""))
                .setContentText(title);
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setAutoCancel(true);
        mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this,RemovedAsFriendActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        //intent.putExtra("chattingFrom", chattingFrom);
        intent.putExtra("sender", sender);
        intent.putExtra("receiver", receiver);
        intent.putExtra("senderRegId", senderRegId);
        intent.putExtra("messagefromgcm", message);
        intent.putExtra("chatRoom",chatRoom);
        intent.putExtra("senderuid",senderuid);
        intent.putExtra("request", true);

        userLocalStore.setUserPartnerRegId(senderRegId);
        userLocalStore.setUserPartnerEmail(receiver);
        if(!chatRoom.isEmpty()){
            userLocalStore.setChatRoom(chatRoom);
        }

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentText(message);
        builder.setContentIntent(resultPendingIntent)
                .setGroup(GROUP_KEY_MESSAGES)
                .setGroupSummary(true);

        mNotificationManager.notify(NOTIFICATION_ID, builder.build());
    }

}
