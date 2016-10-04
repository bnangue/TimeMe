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
    public static final String FIREBASE_APP_URL = "https://timeme-140013.firebaseio.com/";

    // Server Url absolute url where php files are placed.
    static final String YOUR_SERVER_URL   =  "http://timemebrice.site88.net/";

    // Google project id
    static final String GOOGLE_SENDER_ID = "883940536728";

    /**
     * Tag used on log messages.
     */
    static final String FIREBASE_APP_URL_SHARED_GROCERY = "SHAREDGROCERY";
    static final String FIREBASE_APP_URL_SHARED_GROCERY_ITEMS_NODE = "items";

    // Broadcast reciever name to show gcm registration messages on screen
    static final String DISPLAY_REGISTRATION_MESSAGE_ACTION =
            "com.app.bricenangue.timeme.DISPLAY_REGISTRATION_MESSAGE";

    // Broadcast reciever name to show user messages on screen
    static final String DISPLAY_MESSAGE_ACTION =
            "com.app.bricenangue.timeme.DISPLAY_MESSAGE";


    static final String FIREBASE_APP_URL_USERS_publicProfilInfos="publicProfilInfos";
    static final String FIREBASE_APP_URL_USERS_privateProfileInfo="privateProfileInfo";
    static final String FIREBASE_APP_URL_FINANCE_ACCOUNTS_SHARED= "Shared Accounts";
    static final String FIREBASE_APP_URL_GROCERYLISTS_SHARED = "Shared Grocery";
    static final String FIREBASE_APP_URL_CHAT_ROOMS= "Chat Rooms";

    static final String FIREBASE_APP_URL_USERS = "Users";
    static final String FIREBASE_APP_URL_GROCERYLISTS = "Grocery Lists";
    static final String FIREBASE_APP_URL_FINANCE_ACCOUNTS = "Finance Accounts";
    static final String FIREBASE_APP_URL_CALENDAR_EVENTS = "Calendar Events";
    static final String FIREBASE_APP_URL_SHOPPING_ITEMS_XSL = "Shopping Items";
    static final String FIREBASE_APP_URL_SHOPPING_ITEMS_XSL_ITEMS_NODE = "All items";
    static final String FIREBASE_APP_URL_SHOPPING_ITEMS_XSL_USER_LIST = "MY LIST";

    static final String FIREBASE_APP_URL_FINANCE_ACCOUNTS_RECORDS = "accountsRecords";
    String FIREBASE_APP_URL_USERS_password="password";
    static final String FIREBASE_APP_URL_USERS_PUBLIC_USER_ROOM = "Public_Users_Room";
}
