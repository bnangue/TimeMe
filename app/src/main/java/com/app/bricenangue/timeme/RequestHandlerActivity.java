package com.app.bricenangue.timeme;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
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

/**
 * Created by bricenangue on 17/02/16.
 */
public class RequestHandlerActivity extends ActionBarActivity implements View.OnClickListener {
    private String senderRegId, receiverername,message,sendername,myemail,mypassword;
    Button btnCancleRequest, btnAcceptRequest;
    TextView tvMessage;
    UserLocalStore userLocalStore;
    private MySQLiteHelper mySQLiteHelper;
    private Snackbar snackbar;

    private CoordinatorLayout coordinatorLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_handler);
        mySQLiteHelper=new MySQLiteHelper(this);
        userLocalStore=new UserLocalStore(this);
        Bundle extras=getIntent().getExtras();
        coordinatorLayout=(CoordinatorLayout)findViewById(R.id.coordinateLayout_request_handler_activity);
        btnAcceptRequest =(Button)findViewById(R.id.btnacceptRequest);
        btnCancleRequest =(Button)findViewById(R.id.btncancleRequest);
        tvMessage=(TextView)findViewById(R.id.textViewFriendRequestgcmMessage);

        btnAcceptRequest.setOnClickListener(this);
        btnCancleRequest.setOnClickListener(this);


        if(extras!=null){
            sendername =extras.getString("sender");
            receiverername =extras.getString("receiver");
            senderRegId=extras.getString("senderRegId");
            message=extras.getString("messagefromgcm");

        }

        if(message!=null){
            tvMessage.setText(message);
        }

    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.btnacceptRequest:
                userLocalStore.setUserPartnerEmail(sendername);
                userLocalStore.setUserPartnerRegId(senderRegId);
                updateFriendLists(userLocalStore.getLoggedInUser(),sendername);
                finish();
                break;
            case R.id.btncancleRequest:
                Toast.makeText(getApplicationContext(),"You rejected the request from "+ receiverername, Toast.LENGTH_SHORT).show();
                break;
        }
    }


    private void updateFriendLists(final User user, final String email){

        ServerRequests serverRequests=new ServerRequests(this);
        serverRequests.updateFrienListInBackgroung(user, email, new GetUserCallbacks() {
            @Override
            public void done(User returneduser) {

            }

            @Override
            public void serverReponse(String reponse) {

                if(reponse.contains("friendlist updated")){
                    sendFriendrequestAccepted();
                }else {
                    //show snakbar
                    showSnackBar(user,email);
                }
            }

            @Override
            public void userlist(ArrayList<User> reponse) {

            }
        });
    }
    public  void sendFriendrequestAccepted() {

        if (!userLocalStore.getUserRegistrationId().isEmpty()) {


            Thread thread = new Thread() {
                @Override
                public void run() {
                    HttpURLConnection conn=null;
                    try {

                        String regid = userLocalStore.getUserRegistrationId();
                        String sendertname=userLocalStore.getLoggedInUser().getfullname();
                        ArrayList<Pair<String,String>> data=new ArrayList<>();


                        data.add(new Pair<String, String>("message",sendertname +
                        " accepted your request."));
                        data.add(new Pair<String, String>("registrationReceiverIDs", senderRegId));
                        data.add(new Pair<String, String>("receiver", userLocalStore.getLoggedInUser().email));
                        data.add(new Pair<String, String>("sender", sendertname));

                        data.add(new Pair<String, String>("registrationSenderIDs", regid));
                        data.add(new Pair<String, String>("title",sendertname +" is now your friend." ));
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

    public void showSnackBar(final User user, final String email){
        snackbar = Snackbar
                .make(coordinatorLayout, "An error occured.", Snackbar.LENGTH_INDEFINITE)
                .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateFriendLists(user,email);
                    }
                });;
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(getResources().getColor(R.color.colorSnackbar));
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }
}
