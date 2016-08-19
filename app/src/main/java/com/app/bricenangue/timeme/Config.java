package com.app.bricenangue.timeme;

/**
 * Created by bricenangue on 06/02/16.
 */
public interface Config {

    public static String PREF_GCM_REG_ID = "PREF_GCM_REG_ID";
    public static String FIREBASESERVER_KEY = "AIzaSyAtFzzGrJlW0gdRw0PT584ixWYo5k3yBoA";

    public static String WEB_SERVER_URL = "http://timemebrice.site88.net/reg.php";
    public static final String SERVER_SUCCESS="server_success";

   // public static String API_KEY = "AIzaSyB3dU-Imgf7SWrlk8z8aJ9qudw_wjjpz_g";

    static final boolean SECOND_SIMULATOR = false;
    public static final String FIREBASE_APP = "YOUR FIREBASE URL";

    // Server Url absolute url where php files are placed.
    static final String YOUR_SERVER_URL   =  "http://timemebrice.site88.net/";

    // Google project id
    static final String GOOGLE_SENDER_ID = "883940536728";

    /**
     * Tag used on log messages.
     */
    static final String TAG = "GCM Android Example";

    // Broadcast reciever name to show gcm registration messages on screen
    static final String DISPLAY_REGISTRATION_MESSAGE_ACTION =
            "com.app.bricenangue.timeme.DISPLAY_REGISTRATION_MESSAGE";

    // Broadcast reciever name to show user messages on screen
    static final String DISPLAY_MESSAGE_ACTION =
            "com.app.bricenangue.timeme.DISPLAY_MESSAGE";

    // Parse server message with this name
    static final String EXTRA_MESSAGE = "message";

}
