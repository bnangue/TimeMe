package com.example.bricenangue.timeme;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Pair;
import android.view.Surface;
import android.view.WindowManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by bricenangue on 03/03/16.
 */
public class ServerRequests {

    private FragmentProgressBarLoading progressDialog;
    public static final int CONNECTION_TIMEOUT=1000*15;
   // public static final String SERVER_ADDRESS="http://time-tracker.comlu.com/";
    public static final String SERVER_ADDRESS="http://timemebrice.site88.net/";
    AppCompatActivity activity;

    public ServerRequests(AppCompatActivity appCompatActivity) {
        activity=appCompatActivity;
        progressDialog = new FragmentProgressBarLoading();
        progressDialog.setCancelable(false);


    }

    public void registerUserinBackground(User user, GetUserCallbacks callbacks){
        progressDialog.setCancelable(false);
        progressDialog.setTitle(activity.getSupportFragmentManager(),activity.getString(R.string.AsyncTsk_Register_User_In_Backgroung));
        progressDialog.show(activity.getSupportFragmentManager(), "task_progress");
        lockScreenOrientation(activity);
        new RegisterUserAsyncTask(user,callbacks).execute();
    }

    public void loggingUserinBackground(User user, GetUserCallbacks callbacks){
        progressDialog.setCancelable(false);
        progressDialog.setTitle(activity.getSupportFragmentManager(),activity.getString(R.string.AsyncTsk_LOGGING_User_In_Backgroung));

        progressDialog.show(activity.getSupportFragmentManager(), "task_progress");
        lockScreenOrientation(activity);
        new LoggingUserAsynckTacks(user,callbacks).execute();
    }

    public void savingEmailAndPassowrdChangedUserinBackground(User currentuser, User newuser,GetUserCallbacks callbacks){

        progressDialog.setCancelable(false);
        progressDialog.setTitle(activity.getSupportFragmentManager(),activity.getString(R.string.AsyncTsk_Saving_Email_Password_Changed_User_In_Backgroung));

        progressDialog.show(activity.getSupportFragmentManager(), "task_progress");
        lockScreenOrientation(activity);
        new UpdateUserPasswordAndEmailListAsynckTacks(currentuser,newuser,callbacks).execute();
    }

    public void saveCalenderEventInBackgroung(CalendarCollection calendarCollection,GetEventsCallbacks callbacks){
        progressDialog.setCancelable(false);
        progressDialog.setTitle(activity.getSupportFragmentManager(),activity.getString(R.string.AsyncTsk_Creating_Event_In_Backgroung));

        progressDialog.show(activity.getSupportFragmentManager(), "task_progress");
        lockScreenOrientation(activity);
        new StoreCalenderEventsAsynckTacks(calendarCollection,callbacks).execute();
    }

    public void saveGroceryListInBackgroung(GroceryList groceryList,GroceryListCallBacks callbacks){
        progressDialog.setCancelable(false);
        progressDialog.setTitle(activity.getSupportFragmentManager(),activity.getString(R.string.AsyncTsk_Creating_Grocery_List_In_Backgroung));

        progressDialog.show(activity.getSupportFragmentManager(), "task_progress");
        lockScreenOrientation(activity);
        new StoreGroceryListAsynckTacks(groceryList,callbacks).execute();
    }



    public void saveFinanceAccountInBackgroung(FinanceAccount financeAccount,FinanceAccountCallbacks callbacks){

        progressDialog.show(activity.getSupportFragmentManager(), "task_progress");
        lockScreenOrientation(activity);
        new StoreFinanceAccountAsynckTacks(financeAccount,callbacks).execute();
    }


    public void saveItemInBackgroung(ShoppingItem item,GetEventsCallbacks callbacks){
        progressDialog.setCancelable(false);
        progressDialog.setTitle(activity.getSupportFragmentManager(),activity.getString(R.string.AsyncTsk_Creating_Shopping_Item_In_Backgroung));

        progressDialog.show(activity.getSupportFragmentManager(), "task_progress");
        lockScreenOrientation(activity);
        new StoreItemsAsynckTacks(item,callbacks).execute();
    }


    public void getCalenderEventInBackgroung(String username,GetEventsCallbacks callbacks){
        lockScreenOrientation(activity);
        new FetchAllEventsAsynckTacks(username,callbacks).execute();
    }

    public void getGroceryListsInBackgroung(String username, GroceryListCallBacks callbacks){
        lockScreenOrientation(activity);
        new FetchAllGroceryListsAsynckTacks(username, callbacks).execute();
    }
    public void getItemsInBackgroung(GetEventsCallbacks callbacks){
        lockScreenOrientation(activity);
        new FetchAllShoppingItemsAsynckTacks(callbacks).execute();
    }
    public void getCalenderEventAndUserInBackgroung(String username,GetEventsCallbacks callbacks){
        progressDialog.setCancelable(false);
        progressDialog.show(activity.getSupportFragmentManager(), "task_progress");
        lockScreenOrientation(activity);
        new FetchAllEventsAsynckTacks(username,callbacks).execute();
    }

    public void getFinanceAccountsAndUserInBackgroung(String username, FinanceAccountCallbacks callbacks){
        lockScreenOrientation(activity);
        new FetchAllFinanceAccountsAsynckTacks(username,callbacks).execute();
    }


    public void deleteCalenderEventInBackgroung(CalendarCollection calendarCollection,GetEventsCallbacks callbacks){
        progressDialog.setCancelable(false);
        progressDialog.setTitle(activity.getSupportFragmentManager(),activity.getString(R.string.AsyncTsk_Deleting_Event_In_Backgroung));

        progressDialog.show(activity.getSupportFragmentManager(), "task_progress");
        lockScreenOrientation(activity);
        new DeleteEventsAsynckTasks(calendarCollection,callbacks).execute();
    }

    public void deleteGroceryListInBackgroung(GroceryList groceryList,GroceryListCallBacks callbacks){
        progressDialog.setCancelable(false);
        progressDialog.setTitle(activity.getSupportFragmentManager(),activity.getString(R.string.AsyncTsk_Deleting_Grocery_List_In_Backgroung));

        progressDialog.show(activity.getSupportFragmentManager(), "task_progress");
        lockScreenOrientation(activity);
        new DeleteGroceryListAsynckTasks(groceryList,callbacks).execute();
    }


    public void deleteFinanceAccountInBackgroung(FinanceAccount financeAccount,FinanceAccountCallbacks callbacks){

        progressDialog.show(activity.getSupportFragmentManager(), "task_progress");
        lockScreenOrientation(activity);
        new DeleteFinanceAccountAsynckTasks(financeAccount,callbacks).execute();
    }

    public void updateGroceryListInBackgroung(GroceryList groceryList,GroceryListCallBacks callbacks){
        progressDialog.setCancelable(false);
        progressDialog.setTitle(activity.getSupportFragmentManager(),activity.getString(R.string.AsyncTsk_Upadte_Grocery_List_In_Backgroung));

        progressDialog.show(activity.getSupportFragmentManager(), "task_progress");
        lockScreenOrientation(activity);
        new UpdateGroceryListAsynckTacks(groceryList,callbacks).execute();
    }

    public void updateFinanceAccountInBackgroung(FinanceAccount financeAccount,FinanceAccountCallbacks callbacks){

        progressDialog.show(activity.getSupportFragmentManager(), "task_progress");
        lockScreenOrientation(activity);
        new UpdateFinanceAccountAsynckTacks(financeAccount,callbacks).execute();
    }



    public void updateGroceryAndFinanceAccountInBackgroung(GroceryList groceryList, FinanceAccount financeAccount,FinanceAccountCallbacks callbacks){

        progressDialog.show(activity.getSupportFragmentManager(), "task_progress");
        lockScreenOrientation(activity);
        new UpdateGroceryAndFinanceAccountAsynckTacks(groceryList,financeAccount,callbacks).execute();
    }

    public void createGroceryAndUpdateFinanceAccountInBackgroung(GroceryList groceryList, FinanceAccount financeAccount,FinanceAccountCallbacks callbacks){

        progressDialog.show(activity.getSupportFragmentManager(), "task_progress");
        lockScreenOrientation(activity);
        new createGroceryAndUpdateFinanceAccountAsynckTacks(groceryList,financeAccount,callbacks).execute();
    }


    public void logginguserOutInBackgroung(User user,GetUserCallbacks callbacks){
        progressDialog.setCancelable(false);
        progressDialog.setTitle(activity.getSupportFragmentManager(),activity.getString(R.string.AsyncTsk_Logging_USER_Out_In_Backgroung));

        progressDialog.show(activity.getSupportFragmentManager(), "task_progress");
        lockScreenOrientation(activity);
        new UpdateUserStatusAsynckTacks(user,callbacks).execute();
    }








    public class RegisterUserAsyncTask extends AsyncTask<Void,Void,String> {

        User user;
        GetUserCallbacks userCallbacks;

        public RegisterUserAsyncTask(User user, GetUserCallbacks callbacks){
            this.user=user;
            this.userCallbacks=callbacks;
        }
        @Override
        protected void onPostExecute(String reponse) {
            unlockScreenOrientation(activity);
            progressDialog.dismiss(activity.getSupportFragmentManager());
            userCallbacks.serverReponse(reponse);
            super.onPostExecute(reponse);
        }

        @Override
        protected String doInBackground(Void... params) {
            String response="";
            URL url;
            HttpURLConnection urlConnection=null;
            try {
                url=new URL(SERVER_ADDRESS + "RegisterUser.php");
                urlConnection=(HttpURLConnection)url.openConnection();
                urlConnection.setReadTimeout(CONNECTION_TIMEOUT);
               urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);

                OutputStream out=urlConnection.getOutputStream();
                BufferedWriter buff=new BufferedWriter(new OutputStreamWriter(out,"UTF-8"));
                String data =URLEncoder.encode("email","UTF-8")+"="+URLEncoder.encode(user.email,"UTF-8")+"&"+
                        URLEncoder.encode("password","UTF-8")+"="+URLEncoder.encode(String.valueOf(user.password),"UTF-8")
                        +"&"+
                        URLEncoder.encode("firstname","UTF-8")+"="+URLEncoder.encode(String.valueOf(user.firstname),"UTF-8")
                        +"&"+
                        URLEncoder.encode("lastname","UTF-8")+"="+URLEncoder.encode(String.valueOf(user.lastname),"UTF-8");
                buff.write(data);
                buff.flush();
                buff.close();
                out.close();

                InputStream in =urlConnection.getInputStream();

                StringBuilder bi=new StringBuilder();
                BufferedReader reader=new BufferedReader(new InputStreamReader(in));
                String line;
                while((line=reader.readLine())!=null){
                    bi.append(line);
                }
                reader.close();
                in.close();

                response =bi.toString();
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                assert urlConnection != null;
                urlConnection.disconnect();
            }
            return response;
        }
    }


    public class LoggingUserAsynckTacks extends AsyncTask<Void,Void,User> {
        User user;
        GetUserCallbacks userCallbacks;

        public LoggingUserAsynckTacks(User user, GetUserCallbacks callbacks) {
            this.user = user;
            this.userCallbacks = callbacks;
        }

        @Override
        protected void onPostExecute(User returneduser) {

            if(MainActivity.eventsareloaded=true){
                unlockScreenOrientation(activity);
                progressDialog.dismiss(activity.getSupportFragmentManager());

                userCallbacks.done(returneduser);
                super.onPostExecute(returneduser);
            }
        }

        @Override
        protected User doInBackground(Void... params) {

            User returneduser=null;
            URL url;
            HttpURLConnection urlConnection=null;
            try {
                url=new URL(SERVER_ADDRESS + "LogUserIn.php");
                urlConnection=(HttpURLConnection)url.openConnection();
                urlConnection.setReadTimeout(CONNECTION_TIMEOUT);
                urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);

                OutputStream out=urlConnection.getOutputStream();
                BufferedWriter buff=new BufferedWriter(new OutputStreamWriter(out,"UTF-8"));
                String data =URLEncoder.encode("email","UTF-8")+"="+URLEncoder.encode(user.email,"UTF-8")+"&"+
                        URLEncoder.encode("password","UTF-8")+"="+URLEncoder.encode(String.valueOf(user.password),"UTF-8")
                        +"&"+
                        URLEncoder.encode("onlinestatus","UTF-8")+"="+URLEncoder.encode(String.valueOf(user.status),"UTF-8");
                buff.write(data);
                buff.flush();
                buff.close();
                out.close();

                InputStream in =urlConnection.getInputStream();
                String respons="";
                StringBuilder bi=new StringBuilder();
                BufferedReader reader=new BufferedReader(new InputStreamReader(in));
                String line;
                while((line=reader.readLine())!=null){
                    bi.append(line).append("\n");
                }
                reader.close();
                in.close();

                respons =bi.toString();

                JSONArray jsonArray= new JSONArray(respons);

                JSONObject jsonObject= jsonArray.getJSONObject(0);
                if(jsonObject.length()==0){
                    returneduser=null;
                }else {
                    String email=null;
                    if(jsonObject.has("email")){
                        String regId=jsonObject.getString("gcmregid");
                        String firstname=jsonObject.getString("firstname");
                        String lastname=jsonObject.getString("lastname");
                        email=jsonObject.getString("email");
                        String imgString=jsonObject.getString("picture");
                        Bitmap bitmap=decodeBase64(imgString);
                        String friendlist=jsonObject.getString("friendlist");
                        int stat=jsonObject.getInt("onlinestatus");

                        returneduser=new User(email,user.password,firstname,lastname,stat,regId,bitmap,friendlist);
                    }

                }

            }catch (Exception e){
                e.printStackTrace();
            }finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            return returneduser;
        }


    }





    public ArrayList<User> getDetails(JSONArray jsonArray){
        ArrayList<User> events=new ArrayList<>();

        try {


            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jo_inside = jsonArray.getJSONObject(i);

                String email = jo_inside.getString("email");
                String password = jo_inside.getString("password");
                String firstname = jo_inside.getString("firstname");
                String lastname = jo_inside.getString("lastname");
                int  status = jo_inside.getInt("onlinestatus");
                String regId = jo_inside.getString("gcmregid");
                String imgString=jo_inside.getString("picture");
                Bitmap bitmap=decodeBase64(imgString);
                String friendlist=jo_inside.getString("friendlist");


                User  object =new User(email, password, firstname,
                        lastname,status,regId,bitmap,friendlist);

                events.add(object);


            }



        } catch (Exception e) {
            e.printStackTrace();
        }
        return events;

    }



    public class UpdateUserPasswordAndEmailListAsynckTacks extends AsyncTask<Void,Void,String>
    {

        User newuser;
        User currentuser;
        GetUserCallbacks getUserCallbacks;

        public UpdateUserPasswordAndEmailListAsynckTacks(User currentuser,User newuser, GetUserCallbacks callbacks){
            this.getUserCallbacks=callbacks;
            this.newuser=newuser;
            this.currentuser=currentuser;
        }
        @Override
        protected void onPostExecute(String reponse) {
            unlockScreenOrientation(activity);
            progressDialog.dismiss(activity.getSupportFragmentManager());
            getUserCallbacks.serverReponse(reponse);
            super.onPostExecute(reponse);
        }

        @Override
        protected String doInBackground(Void... params) {

            String line="";
            String uploadimage;
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                if(newuser.picture!=null){
                    uploadimage=getStringImage(newuser.picture);
                }else {
                    uploadimage="";
                }


                url=new URL(SERVER_ADDRESS + "UpdateEmailAndPassword.php");
                urlConnection=(HttpURLConnection)url.openConnection();
                urlConnection.setReadTimeout(CONNECTION_TIMEOUT);
                urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);

                OutputStream out=urlConnection.getOutputStream();
                BufferedWriter buff=new BufferedWriter(new OutputStreamWriter(out,"UTF-8"));
                String data =URLEncoder.encode("email","UTF-8")+"="+URLEncoder.encode(newuser.email,"UTF-8")+"&"+
                        URLEncoder.encode("password","UTF-8")+"="+URLEncoder.encode(newuser.password,"UTF-8")
                        +"&"+
                        URLEncoder.encode("currentEmail","UTF-8")+"="+URLEncoder.encode(currentuser.email,"UTF-8")
                        +"&"+
                        URLEncoder.encode("currentPassword","UTF-8")+"="+URLEncoder.encode(currentuser.password,"UTF-8")
                        +"&"+
                        URLEncoder.encode("picture","UTF-8")+"="+URLEncoder.encode(uploadimage,"UTF-8")
                        +"&"+
                        URLEncoder.encode("firstname","UTF-8")+"="+URLEncoder.encode(newuser.firstname,"UTF-8")
                        +"&"+
                        URLEncoder.encode("lastname","UTF-8")+"="+URLEncoder.encode(newuser.lastname,"UTF-8");
                buff.write(data);
                buff.flush();
                buff.close();
                out.close();

                int responsecode=urlConnection.getResponseCode();
                if(responsecode==HttpURLConnection.HTTP_OK){
                    InputStream in =urlConnection.getInputStream();

                    BufferedReader reader= new BufferedReader(new InputStreamReader(in));
                    StringBuilder bld =new StringBuilder();
                    String il;
                    while((il=reader.readLine())!=null){
                        bld.append(il);
                    }
                    line=bld.toString();
                }else{
                    line="Error";
                }



            }catch (Exception e){
                e.printStackTrace();
            }finally {
                assert urlConnection != null;
                urlConnection.disconnect();
            }
            return line;
        }
    }



    public class StoreCalenderEventsAsynckTacks extends AsyncTask<Void,Void,String>{

        CalendarCollection eventObject;
        GetEventsCallbacks eventsCallbacks;

        public StoreCalenderEventsAsynckTacks(CalendarCollection eventObject, GetEventsCallbacks callbacks){
            this.eventsCallbacks=callbacks;
            this.eventObject=eventObject;
        }
        @Override
        protected void onPostExecute(String aVoid) {
            unlockScreenOrientation(activity);
            progressDialog.dismiss(activity.getSupportFragmentManager());
            eventsCallbacks.updated(aVoid);
            super.onPostExecute(aVoid);
        }

        @Override
        protected String doInBackground(Void... params) {

            String reponse=null;
            ArrayList<Pair<String,String>> data=new ArrayList<>();
            String d=eventObject.title;
            data.add(new Pair<String, String>("title", d));
            data.add(new Pair<String, String>("description",eventObject.description));
            data.add(new Pair<String, String>("datetime", eventObject.datetime));
            data.add(new Pair<String, String>("creator", eventObject.creator));
            data.add(new Pair<String, String>("category", eventObject.category));
            data.add(new Pair<String, String>("startingtime",eventObject.startingtime));
            data.add(new Pair<String, String>("endingtime",eventObject.endingtime));
            data.add(new Pair<String, String>("hashid",eventObject.hashid));
            data.add(new Pair<String, String>("alldayevent",eventObject.alldayevent));
            data.add(new Pair<String, String>("everymonth",eventObject.everymonth));
            data.add(new Pair<String, String>("defaulttime",eventObject.creationdatetime));


            URL url;
            HttpURLConnection urlConnection=null;
            try {

                byte[] postData= getData(data).getBytes("UTF-8");
                url=new URL(SERVER_ADDRESS + "CreateCalenderEvents.php");
                urlConnection=(HttpURLConnection)url.openConnection();
                urlConnection.setReadTimeout(CONNECTION_TIMEOUT);
                urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("Content-Length", String.valueOf(postData.length));
                urlConnection.setDoOutput(true);
                urlConnection.getOutputStream().write(postData);

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(urlConnection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                reponse=response.toString();

                in.close();
            }catch (Exception e){
                e.printStackTrace();
            }
            return reponse;
        }
    }



    public class StoreGroceryListAsynckTacks extends AsyncTask<Void,Void,String>{

        GroceryList groceryList;
        GroceryListCallBacks groceryListCallBacks;

        public StoreGroceryListAsynckTacks( GroceryList groceryList,  GroceryListCallBacks groceryListCallBacks){
            this.groceryList=groceryList;
            this.groceryListCallBacks=groceryListCallBacks;
        }
        @Override
        protected void onPostExecute(String aVoid) {
            unlockScreenOrientation(activity);
            progressDialog.dismiss(activity.getSupportFragmentManager());
           groceryListCallBacks.setServerResponse(aVoid);
            super.onPostExecute(aVoid);
        }

        @Override
        protected String doInBackground(Void... params) {

            String reponse=null;
            ArrayList<Pair<String,String>> data=new ArrayList<>();
            int status=(groceryList.isListdone())? 1 : 0;
            int shareStatus=(groceryList.isToListshare())? 1 : 0;

            data.add(new Pair<String, String>("list_name", groceryList.getDatum()));
            data.add(new Pair<String, String>("list_creator",groceryList.getCreatorName()));
            data.add(new Pair<String, String>("list_status", String.valueOf(status)));
            data.add(new Pair<String, String>("list_uniqueId", groceryList.getList_unique_id()));
            data.add(new Pair<String, String>("list_contain", groceryList.getListcontain()));
            data.add(new Pair<String, String>("list_isShareStatus",String.valueOf(shareStatus)));
            data.add(new Pair<String, String>("list_note","nothing specify"));



            URL url;
            HttpURLConnection urlConnection=null;
            try {

                byte[] postData= getData(data).getBytes("UTF-8");
                url=new URL(SERVER_ADDRESS + "CreateGroceryList.php");
                urlConnection=(HttpURLConnection)url.openConnection();
                urlConnection.setReadTimeout(CONNECTION_TIMEOUT);
                urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("Content-Length", String.valueOf(postData.length));
                urlConnection.setDoOutput(true);
                urlConnection.getOutputStream().write(postData);

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(urlConnection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                reponse=response.toString();

                in.close();
            }catch (Exception e){
                e.printStackTrace();
            }
            return reponse;
        }
    }



    public class StoreFinanceAccountAsynckTacks extends AsyncTask<Void,Void,String>{

        FinanceAccount financeAccount;
        FinanceAccountCallbacks callbacks;

        public StoreFinanceAccountAsynckTacks(FinanceAccount financeAccount, FinanceAccountCallbacks callbacks){
            this.financeAccount=financeAccount;
            this.callbacks=callbacks;
        }
        @Override
        protected void onPostExecute(String aVoid) {
            unlockScreenOrientation(activity);
            progressDialog.dismiss(activity.getSupportFragmentManager());
            callbacks.setServerResponse(aVoid);
            super.onPostExecute(aVoid);
        }

        @Override
        protected String doInBackground(Void... params) {

            String reponse=null;
            ArrayList<Pair<String,String>> data=new ArrayList<>();

            data.add(new Pair<String, String>("account_name", financeAccount.getAccountName()));
            data.add(new Pair<String, String>("account_owner",financeAccount.getAccountOwnersToString()));
            data.add(new Pair<String, String>("account_balance", financeAccount.getAccountBlanceTostring()));
            data.add(new Pair<String, String>("account_uniqueId", financeAccount.getAccountUniqueId()));
            data.add(new Pair<String, String>("account_lastchange", financeAccount.getLastChangeDateToAccount()));
            data.add(new Pair<String, String>("account_records",financeAccount.getAccountRecordsString()));




            URL url;
            HttpURLConnection urlConnection=null;
            try {

                byte[] postData= getData(data).getBytes("UTF-8");
                url=new URL(SERVER_ADDRESS + "CreateFinanceAccount.php");
                urlConnection=(HttpURLConnection)url.openConnection();
                urlConnection.setReadTimeout(CONNECTION_TIMEOUT);
                urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("Content-Length", String.valueOf(postData.length));
                urlConnection.setDoOutput(true);
                urlConnection.getOutputStream().write(postData);

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(urlConnection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                reponse=response.toString();

                in.close();
            }catch (Exception e){
                e.printStackTrace();
            }
            return reponse;
        }
    }



    public class UpdateGroceryListAsynckTacks extends AsyncTask<Void,Void,String>{

        GroceryList groceryList;
        GroceryListCallBacks groceryListCallBacks;

        public UpdateGroceryListAsynckTacks(GroceryList groceryList, GroceryListCallBacks groceryListCallBacks){
            this.groceryList=groceryList;
            this.groceryListCallBacks=groceryListCallBacks;
        }
        @Override
        protected void onPostExecute(String aVoid) {
            unlockScreenOrientation(activity);
            progressDialog.dismiss(activity.getSupportFragmentManager());
            groceryListCallBacks.setServerResponse(aVoid);
            super.onPostExecute(aVoid);
        }

        @Override
        protected String doInBackground(Void... params) {

            String reponse=null;
            ArrayList<Pair<String,String>> data=new ArrayList<>();
            int status=(groceryList.isListdone())? 1 : 0;
            int shareStatus=(groceryList.isToListshare())? 1 : 0;

            data.add(new Pair<String, String>("list_name", groceryList.getDatum()));
            data.add(new Pair<String, String>("list_creator",groceryList.getCreatorName()));
            data.add(new Pair<String, String>("list_status", String.valueOf(status)));
            data.add(new Pair<String, String>("list_uniqueId", groceryList.getList_unique_id()));
            data.add(new Pair<String, String>("list_contain", groceryList.getListcontain()));
            data.add(new Pair<String, String>("list_isShareStatus",String.valueOf(shareStatus)));
            data.add(new Pair<String, String>("list_note","nothing specify"));



            URL url;
            HttpURLConnection urlConnection=null;
            try {

                byte[] postData= getData(data).getBytes("UTF-8");
                url=new URL(SERVER_ADDRESS + "UpdateGroceryList.php");
                urlConnection=(HttpURLConnection)url.openConnection();
                urlConnection.setReadTimeout(CONNECTION_TIMEOUT);
                urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("Content-Length", String.valueOf(postData.length));
                urlConnection.setDoOutput(true);
                urlConnection.getOutputStream().write(postData);

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(urlConnection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                reponse=response.toString();

                in.close();
            }catch (Exception e){
                e.printStackTrace();
            }
            return reponse;
        }
    }


    public class UpdateFinanceAccountAsynckTacks extends AsyncTask<Void,Void,String>{

        FinanceAccount financeAccount;
        FinanceAccountCallbacks callbacks;

        public UpdateFinanceAccountAsynckTacks(FinanceAccount financeAccount, FinanceAccountCallbacks callbacks){
            this.financeAccount=financeAccount;
            this.callbacks=callbacks;
        }
        @Override
        protected void onPostExecute(String aVoid) {
            unlockScreenOrientation(activity);
            progressDialog.dismiss(activity.getSupportFragmentManager());
            callbacks.setServerResponse(aVoid);
            super.onPostExecute(aVoid);
        }

        @Override
        protected String doInBackground(Void... params) {

            String reponse=null;
            ArrayList<Pair<String,String>> data=new ArrayList<>();


            data.add(new Pair<String, String>("account_owner", financeAccount.getAccountOwnersToString()));
            data.add(new Pair<String, String>("account_balance",financeAccount.getAccountBlanceTostring()));
            data.add(new Pair<String, String>("account_uniqueId", financeAccount.getAccountUniqueId()));
            data.add(new Pair<String, String>("account_lastchange", financeAccount.getLastChangeDateToAccount()));
            data.add(new Pair<String, String>("account_records", financeAccount.getAccountRecordsString()));




            URL url;
            HttpURLConnection urlConnection=null;
            try {

                byte[] postData= getData(data).getBytes("UTF-8");
                url=new URL(SERVER_ADDRESS + "UpdateFinanceAccount.php");
                urlConnection=(HttpURLConnection)url.openConnection();
                urlConnection.setReadTimeout(CONNECTION_TIMEOUT);
                urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("Content-Length", String.valueOf(postData.length));
                urlConnection.setDoOutput(true);
                urlConnection.getOutputStream().write(postData);

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(urlConnection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                reponse=response.toString();

                in.close();
            }catch (Exception e){
                e.printStackTrace();
            }
            return reponse;
        }
    }




    public class UpdateGroceryAndFinanceAccountAsynckTacks extends AsyncTask<Void,Void,String>{

        FinanceAccount financeAccount;
        GroceryList groceryList;
        FinanceAccountCallbacks callbacks;

        public UpdateGroceryAndFinanceAccountAsynckTacks(GroceryList groceryList, FinanceAccount financeAccount, FinanceAccountCallbacks callbacks){
            this.financeAccount=financeAccount;
            this.groceryList=groceryList;
            this.callbacks=callbacks;
        }
        @Override
        protected void onPostExecute(String aVoid) {
            unlockScreenOrientation(activity);
            progressDialog.dismiss(activity.getSupportFragmentManager());
            callbacks.setServerResponse(aVoid);
            super.onPostExecute(aVoid);
        }

        @Override
        protected String doInBackground(Void... params) {

            String reponse=null;
            ArrayList<Pair<String,String>> data=new ArrayList<>();


            data.add(new Pair<String, String>("account_owner", financeAccount.getAccountOwnersToString()));
            data.add(new Pair<String, String>("account_balance",financeAccount.getAccountBlanceTostring()));
            data.add(new Pair<String, String>("account_uniqueId", financeAccount.getAccountUniqueId()));
            data.add(new Pair<String, String>("account_lastchange", financeAccount.getLastChangeDateToAccount()));
            data.add(new Pair<String, String>("account_records", financeAccount.getAccountRecordsString()));

            int status=(groceryList.isListdone())? 1 : 0;
            int shareStatus=(groceryList.isToListshare())? 1 : 0;

            data.add(new Pair<String, String>("list_name", groceryList.getDatum()));
            data.add(new Pair<String, String>("list_creator",groceryList.getCreatorName()));
            data.add(new Pair<String, String>("list_status", String.valueOf(status)));
            data.add(new Pair<String, String>("list_uniqueId", groceryList.getList_unique_id()));
            data.add(new Pair<String, String>("list_contain", groceryList.getListcontain()));
            data.add(new Pair<String, String>("list_isShareStatus",String.valueOf(shareStatus)));
            data.add(new Pair<String, String>("list_note","nothing specify"));




            URL url;
            HttpURLConnection urlConnection=null;
            try {

                byte[] postData= getData(data).getBytes("UTF-8");
                url=new URL(SERVER_ADDRESS + "UpdateGroceryAndFinance.php");
                urlConnection=(HttpURLConnection)url.openConnection();
                urlConnection.setReadTimeout(CONNECTION_TIMEOUT);
                urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("Content-Length", String.valueOf(postData.length));
                urlConnection.setDoOutput(true);
                urlConnection.getOutputStream().write(postData);

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(urlConnection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                reponse=response.toString();

                in.close();
            }catch (Exception e){
                e.printStackTrace();
            }
            return reponse;
        }
    }

    public class createGroceryAndUpdateFinanceAccountAsynckTacks extends AsyncTask<Void,Void,String>{

        FinanceAccount financeAccount;
        GroceryList groceryList;
        FinanceAccountCallbacks callbacks;

        public createGroceryAndUpdateFinanceAccountAsynckTacks(GroceryList groceryList, FinanceAccount financeAccount, FinanceAccountCallbacks callbacks){
            this.financeAccount=financeAccount;
            this.groceryList=groceryList;
            this.callbacks=callbacks;
        }
        @Override
        protected void onPostExecute(String aVoid) {
            unlockScreenOrientation(activity);
            progressDialog.dismiss(activity.getSupportFragmentManager());
            callbacks.setServerResponse(aVoid);
            super.onPostExecute(aVoid);
        }

        @Override
        protected String doInBackground(Void... params) {

            String reponse=null;
            ArrayList<Pair<String,String>> data=new ArrayList<>();


            data.add(new Pair<String, String>("account_owner", financeAccount.getAccountOwnersToString()));
            data.add(new Pair<String, String>("account_balance",financeAccount.getAccountBlanceTostring()));
            data.add(new Pair<String, String>("account_uniqueId", financeAccount.getAccountUniqueId()));
            data.add(new Pair<String, String>("account_lastchange", financeAccount.getLastChangeDateToAccount()));
            data.add(new Pair<String, String>("account_records", financeAccount.getAccountRecordsString()));

            int status=(groceryList.isListdone())? 1 : 0;
            int shareStatus=(groceryList.isToListshare())? 1 : 0;

            data.add(new Pair<String, String>("list_name", groceryList.getDatum()));
            data.add(new Pair<String, String>("list_creator",groceryList.getCreatorName()));
            data.add(new Pair<String, String>("list_status", String.valueOf(status)));
            data.add(new Pair<String, String>("list_uniqueId", groceryList.getList_unique_id()));
            data.add(new Pair<String, String>("list_contain", groceryList.getListcontain()));
            data.add(new Pair<String, String>("list_isShareStatus",String.valueOf(shareStatus)));
            data.add(new Pair<String, String>("list_note","nothing specify"));




            URL url;
            HttpURLConnection urlConnection=null;
            try {

                byte[] postData= getData(data).getBytes("UTF-8");
                url=new URL(SERVER_ADDRESS + "CreateGroceryListAndUpdateFinanceAccount.php");
                urlConnection=(HttpURLConnection)url.openConnection();
                urlConnection.setReadTimeout(CONNECTION_TIMEOUT);
                urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("Content-Length", String.valueOf(postData.length));
                urlConnection.setDoOutput(true);
                urlConnection.getOutputStream().write(postData);

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(urlConnection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                reponse=response.toString();

                in.close();
            }catch (Exception e){
                e.printStackTrace();
            }
            return reponse;
        }
    }


    public class StoreItemsAsynckTacks extends AsyncTask<Void,Void,String>{

        ShoppingItem item;
        GetEventsCallbacks eventsCallbacks;

        public StoreItemsAsynckTacks(ShoppingItem item, GetEventsCallbacks callbacks){
            this.eventsCallbacks=callbacks;
            this.item=item;
        }
        @Override
        protected void onPostExecute(String aVoid) {
            unlockScreenOrientation(activity);
            progressDialog.dismiss(activity.getSupportFragmentManager());
            eventsCallbacks.updated(aVoid);
            super.onPostExecute(aVoid);
        }

        @Override
        protected String doInBackground(Void... params) {

            String reponse=null;
            ArrayList<Pair<String,String>> data=new ArrayList<>();

            data.add(new Pair<String, String>("description",item.getDetailstoItem()));
            data.add(new Pair<String, String>("name", item.getItemName()));
            data.add(new Pair<String, String>("price", item.getPrice()));
            data.add(new Pair<String, String>("specification", item.getItemSpecification()));
            data.add(new Pair<String, String>("unique_id", item.getUnique_item_id()));


            URL url;
            HttpURLConnection urlConnection=null;
            try {

                byte[] postData= getData(data).getBytes("UTF-8");
                url=new URL(SERVER_ADDRESS + "CreateShoppingItem.php");
                urlConnection=(HttpURLConnection)url.openConnection();
                urlConnection.setReadTimeout(CONNECTION_TIMEOUT);
                urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("Content-Length", String.valueOf(postData.length));
                urlConnection.setDoOutput(true);
                urlConnection.getOutputStream().write(postData);

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(urlConnection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                reponse=response.toString();

                in.close();
            }catch (Exception e){
                e.printStackTrace();
            }
            return reponse;
        }
    }


    public class FetchAllEventsAsynckTacks extends AsyncTask<Void,Void,ArrayList<CalendarCollection>> {

        GetEventsCallbacks eventsCallbacks;
        String username;


        public FetchAllEventsAsynckTacks( String username, GetEventsCallbacks callbacks) {
            this.eventsCallbacks = callbacks;
            this.username=username;
        }

        @Override
        protected void onPostExecute(ArrayList<CalendarCollection> returnedevents) {
            unlockScreenOrientation(activity);
            progressDialog.dismiss(activity.getSupportFragmentManager());
            eventsCallbacks.done(returnedevents);
            super.onPostExecute(returnedevents);
        }

        @Override
        protected ArrayList<CalendarCollection> doInBackground(Void... params) {

            ArrayList<CalendarCollection> returnedEvents=new ArrayList<>();
            URL url;
            HttpURLConnection urlConnection=null;
            ArrayList<Pair<String,String>> data=new ArrayList<>();

            data.add(new Pair<String, String>("creator",username));

            try {
                byte[] postData= getData(data).getBytes("UTF-8");

                url=new URL(SERVER_ADDRESS + "FetchAllCalenderEvents.php");
                urlConnection=(HttpURLConnection)url.openConnection();
                urlConnection.setReadTimeout(CONNECTION_TIMEOUT);
                urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("Content-Length", String.valueOf(postData.length));
                urlConnection.setDoOutput(true);
                urlConnection.getOutputStream().write(postData);


                InputStream in =urlConnection.getInputStream();
                String respons="";
                StringBuilder bi=new StringBuilder();
                BufferedReader reader=new BufferedReader(new InputStreamReader(in));
                String line;
                while((line=reader.readLine())!=null){
                    bi.append(line).append("\n");
                }
                reader.close();
                in.close();

                respons =bi.toString();
                JSONArray jsonArray= new JSONArray(respons);
                returnedEvents= getDetailsEvents(  jsonArray);


                // fetch data to a jason object
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                assert urlConnection != null;
                urlConnection.disconnect();
            }

            return returnedEvents;
        }


    }



    public class FetchAllFinanceAccountsAsynckTacks extends AsyncTask<Void,Void,ArrayList<FinanceAccount>> {

        FinanceAccountCallbacks callbacks;
        String username;


        public FetchAllFinanceAccountsAsynckTacks(String username, FinanceAccountCallbacks callbacks) {
            this.callbacks = callbacks;
            this.username=username;
        }

        @Override
        protected void onPostExecute(ArrayList<FinanceAccount> returnedevents) {
            unlockScreenOrientation(activity);

            callbacks.fetchDone(returnedevents);
            super.onPostExecute(returnedevents);
        }

        @Override
        protected ArrayList<FinanceAccount> doInBackground(Void... params) {

            ArrayList<FinanceAccount> returnedAccounts=new ArrayList<>();
            URL url;
            HttpURLConnection urlConnection=null;

            ArrayList<Pair<String,String>> data=new ArrayList<>();

            data.add(new Pair<String, String>("account_owner",username));

            try {
                byte[] postData= getData(data).getBytes("UTF-8");

                url=new URL(SERVER_ADDRESS + "FetchAllFinanceAccounts.php");
                urlConnection=(HttpURLConnection)url.openConnection();
                urlConnection.setReadTimeout(CONNECTION_TIMEOUT);
                urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("Content-Length", String.valueOf(postData.length));
                urlConnection.getOutputStream().write(postData);


                InputStream in =urlConnection.getInputStream();
                String respons="";
                StringBuilder bi=new StringBuilder();
                BufferedReader reader=new BufferedReader(new InputStreamReader(in));
                String line;
                while((line=reader.readLine())!=null){
                    bi.append(line).append("\n");
                }
                reader.close();
                in.close();

                respons =bi.toString();
                JSONArray jsonArray= new JSONArray(respons);
                returnedAccounts= getDetailsFinancesAccounts(  jsonArray);


                // fetch data to a jason object
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                assert urlConnection != null;
                urlConnection.disconnect();
            }

            return returnedAccounts;
        }


    }





    public class FetchAllGroceryListsAsynckTacks extends AsyncTask<Void,Void,ArrayList<GroceryList>> {

        GroceryListCallBacks eventsCallbacks;
        String username;


        public FetchAllGroceryListsAsynckTacks(String username, GroceryListCallBacks callbacks) {
            this.eventsCallbacks = callbacks;
            this.username=username;
        }

        @Override
        protected void onPostExecute(ArrayList<GroceryList> groceryLists) {
            unlockScreenOrientation(activity);
            progressDialog.dismiss(activity.getSupportFragmentManager());
            eventsCallbacks.fetchDone(groceryLists);
            super.onPostExecute(groceryLists);
        }

        @Override
        protected ArrayList<GroceryList> doInBackground(Void... params) {

            ArrayList<GroceryList> returnedEvents=new ArrayList<>();
            URL url;
            HttpURLConnection urlConnection=null;
            ArrayList<Pair<String,String>> data=new ArrayList<>();

            data.add(new Pair<String, String>("list_creator",username));

            try {
                byte[] postData= getData(data).getBytes("UTF-8");

                url=new URL(SERVER_ADDRESS + "FetchAllGroceryLists.php");
                urlConnection=(HttpURLConnection)url.openConnection();
                urlConnection.setReadTimeout(CONNECTION_TIMEOUT);
                urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("Content-Length", String.valueOf(postData.length));
                urlConnection.getOutputStream().write(postData);




                InputStream in =urlConnection.getInputStream();
                String respons="";
                StringBuilder bi=new StringBuilder();
                BufferedReader reader=new BufferedReader(new InputStreamReader(in));
                String line;
                while((line=reader.readLine())!=null){
                    bi.append(line).append("\n");
                }
                reader.close();
                in.close();

                respons =bi.toString();
                JSONArray jsonArray= new JSONArray(respons);

                returnedEvents= getGroceryListdetails(jsonArray);


                // fetch data to a jason object
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                assert urlConnection != null;
                urlConnection.disconnect();
            }

            return returnedEvents;
        }


    }



    public class FetchAllShoppingItemsAsynckTacks extends AsyncTask<Void,Void,ArrayList<ShoppingItem>> {

        GetEventsCallbacks eventsCallbacks;


        public FetchAllShoppingItemsAsynckTacks(GetEventsCallbacks callbacks) {
            this.eventsCallbacks = callbacks;
        }

        @Override
        protected void onPostExecute(ArrayList<ShoppingItem> returnedevents) {
            unlockScreenOrientation(activity);
            progressDialog.dismiss(activity.getSupportFragmentManager());
            eventsCallbacks.itemslis(returnedevents);
            super.onPostExecute(returnedevents);
        }

        @Override
        protected ArrayList<ShoppingItem> doInBackground(Void... params) {

            ArrayList<ShoppingItem> returnedItems=new ArrayList<>();
            URL url;
            HttpURLConnection urlConnection=null;
            try {
                url=new URL(SERVER_ADDRESS + "FetchAllCalenderEvents.php");
                urlConnection=(HttpURLConnection)url.openConnection();
                urlConnection.setReadTimeout(CONNECTION_TIMEOUT);
                urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);


                InputStream in =urlConnection.getInputStream();
                String respons="";
                StringBuilder bi=new StringBuilder();
                BufferedReader reader=new BufferedReader(new InputStreamReader(in));
                String line;
                while((line=reader.readLine())!=null){
                    bi.append(line).append("\n");
                }
                reader.close();
                in.close();

                respons =bi.toString();
                JSONArray jsonArray= new JSONArray(respons);
                returnedItems= getDetailsItems(jsonArray);


                // fetch data to a jason object
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                assert urlConnection != null;
                urlConnection.disconnect();
            }

            return returnedItems;
        }


    }
    public ArrayList<CalendarCollection> getDetailsEvents(JSONArray jsonArray){
        ArrayList<CalendarCollection> events=new ArrayList<>();

        try {


            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jo_inside = jsonArray.getJSONObject(i);

                String titel = jo_inside.getString("title");
                String infotext = jo_inside.getString("description");
                String creator = jo_inside.getString("creator");
                String creationTime = jo_inside.getString("datetime");
                String category = jo_inside.getString("category");
                String startingtime = jo_inside.getString("startingtime");
                String endingtime = jo_inside.getString("endingtime");
                String alldayevent = jo_inside.getString("alldayevent");
                String eventHash = jo_inside.getString("hashid");
                String everymonth = jo_inside.getString("everymonth");
                String creationdatetime = jo_inside.getString("defaulttime");


                String[] creationtime=creationTime.split(" ");

                CalendarCollection  object =new CalendarCollection(titel,infotext,creator,creationTime,startingtime,endingtime,eventHash,category,alldayevent,everymonth,creationdatetime);

                events.add(object);


            }



        } catch (Exception e) {
            e.printStackTrace();
        }
        return events;

    }

    public ArrayList<FinanceAccount> getDetailsFinancesAccounts(JSONArray jsonArray){
        ArrayList<FinanceAccount> events=new ArrayList<>();

        try {


            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jo_inside = jsonArray.getJSONObject(i);

                String accountname = jo_inside.getString("account_name");
                String accountowner = jo_inside.getString("account_owner");
                String accountbalance = jo_inside.getString("account_balance");
                String accountuniqueId = jo_inside.getString("account_uniqueId");
                String accountlastchange = jo_inside.getString("account_lastchange");
                String accountrecords = jo_inside.getString("account_records");

                FinanceAccount object=new FinanceAccount(activity);
                object.setAccountName(accountname);
                object.setAccountOwnersToString(accountowner);
                object.setAccountBalanceToString(accountbalance);
                object.setAccountUniqueId(accountuniqueId);
                object.setLastchangeToAccount(accountlastchange);
                object.setAccountRecordsString(accountrecords);
                object.getRecords();


                events.add(object);


            }



        } catch (Exception e) {
            e.printStackTrace();
        }
        return events;

    }



    public ArrayList<GroceryList> getGroceryListdetails(JSONArray jsonArray){
        ArrayList<GroceryList> events=new ArrayList<>();

        try {


            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jo_inside = jsonArray.getJSONObject(i);

                String listname = jo_inside.getString("list_name");
                String listcreator = jo_inside.getString("list_creator");

                String liststatus = jo_inside.getString("list_status");

                String listuniqueId = jo_inside.getString("list_uniqueId");
                String listcontain = jo_inside.getString("list_contain");

                String listisShareStatus = jo_inside.getString("list_isShareStatus");

                String listnote = jo_inside.getString("list_note");

                boolean status= (Integer.parseInt(liststatus) == 1);
                boolean shareStatus= (Integer.parseInt(listisShareStatus) == 1);

                GroceryList  groceryList =new GroceryList();

                groceryList.setDatum(listname);
                groceryList.setCreatorName(listcreator);
                groceryList.setListdone(status);
                groceryList.setList_unique_id(listuniqueId);
                groceryList.setListcontain(listcontain);
                groceryList.setToListshare(shareStatus);

               // groceryList.setDatum(listname);

                events.add(groceryList);


            }



        } catch (Exception e) {
            e.printStackTrace();
        }
        return events;

    }


    public ArrayList<ShoppingItem> getDetailsItems(JSONArray jsonArray){
        ArrayList<ShoppingItem> items=new ArrayList<>();

        try {


            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jo_inside = jsonArray.getJSONObject(i);

                String name = jo_inside.getString("name");
                String description = jo_inside.getString("description");
                String price = jo_inside.getString("price");
                String specification = jo_inside.getString("specification");
                String unique_id = jo_inside.getString("unique_id");




                ShoppingItem  object =new ShoppingItem();
                object.setUnique_item_id(unique_id);
                object.setItemName(name);
                object.setPrice(price);
                object.setDetailstoItem(description);
                object.setItemSpecification(specification);


                items.add(object);


            }



        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;

    }



    public class DeleteEventsAsynckTasks extends AsyncTask<Void,Void,String>{

        CalendarCollection eventObject;
        GetEventsCallbacks eventsCallbacks;

        public DeleteEventsAsynckTasks(CalendarCollection eventObject, GetEventsCallbacks callbacks){
            this.eventsCallbacks=callbacks;
            this.eventObject=eventObject;

        }
        @Override
        protected void onPostExecute(String aVoid) {
            progressDialog.dismiss();
            eventsCallbacks.updated(aVoid);
            super.onPostExecute(aVoid);
        }

        @Override
        protected String doInBackground(Void... params) {

            ArrayList<Pair<String,String>> data=new ArrayList<>();

            data.add(new Pair<String, String>("creator", eventObject.creator));
            data.add(new Pair<String, String>("hashid",eventObject.hashid));



            URL url;
            String line=null;
            HttpURLConnection urlConnection=null;
            try {

                byte[] postData= getData(data).getBytes("UTF-8");
                url=new URL(SERVER_ADDRESS + "DeleteCalenderEvent.php");
                urlConnection=(HttpURLConnection)url.openConnection();
                urlConnection.setReadTimeout(CONNECTION_TIMEOUT);
                urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("Content-Length", String.valueOf(postData.length));
                urlConnection.setDoOutput(true);
                urlConnection.getOutputStream().write(postData);

                urlConnection.getOutputStream().close();
                int responsecode=urlConnection.getResponseCode();
                if(responsecode==HttpURLConnection.HTTP_OK){
                    InputStream in =urlConnection.getInputStream();

                    BufferedReader reader= new BufferedReader(new InputStreamReader(in));
                    StringBuilder bld =new StringBuilder();
                    String il;
                    while((il=reader.readLine())!=null){
                        bld.append(il);
                    }
                    line=bld.toString();
                }else{
                    line="Error";
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return line;
        }
    }


    public class DeleteGroceryListAsynckTasks extends AsyncTask<Void,Void,String>{

        GroceryList groceryList;
        GroceryListCallBacks groceryListCallBacks;

        public DeleteGroceryListAsynckTasks(GroceryList groceryList, GroceryListCallBacks groceryListCallBacks){
            this.groceryList=groceryList;
            this.groceryListCallBacks=groceryListCallBacks;

        }
        @Override
        protected void onPostExecute(String aVoid) {
            unlockScreenOrientation(activity);
            progressDialog.dismiss(activity.getSupportFragmentManager());
            groceryListCallBacks.setServerResponse(aVoid);
            super.onPostExecute(aVoid);
        }

        @Override
        protected String doInBackground(Void... params) {

            ArrayList<Pair<String,String>> data=new ArrayList<>();

            data.add(new Pair<String, String>("list_creator", groceryList.getCreatorName()));
            data.add(new Pair<String, String>("list_uniqueId",groceryList.getList_unique_id()));



            URL url;
            String line=null;
            HttpURLConnection urlConnection=null;
            try {

                byte[] postData= getData(data).getBytes("UTF-8");
                url=new URL(SERVER_ADDRESS + "DeleteGroceryList.php");
                urlConnection=(HttpURLConnection)url.openConnection();
                urlConnection.setReadTimeout(CONNECTION_TIMEOUT);
                urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("Content-Length", String.valueOf(postData.length));
                urlConnection.setDoOutput(true);
                urlConnection.getOutputStream().write(postData);

                urlConnection.getOutputStream().close();
                int responsecode=urlConnection.getResponseCode();
                if(responsecode==HttpURLConnection.HTTP_OK){
                    InputStream in =urlConnection.getInputStream();

                    BufferedReader reader= new BufferedReader(new InputStreamReader(in));
                    StringBuilder bld =new StringBuilder();
                    String il;
                    while((il=reader.readLine())!=null){
                        bld.append(il);
                    }
                    line=bld.toString();
                }else{
                    line="Error";
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return line;
        }
    }


    public class DeleteFinanceAccountAsynckTasks extends AsyncTask<Void,Void,String>{

        FinanceAccount financeAccount;
        FinanceAccountCallbacks callbacks;

        public DeleteFinanceAccountAsynckTasks(FinanceAccount financeAccount, FinanceAccountCallbacks callbacks){
            this.financeAccount=financeAccount;
            this.callbacks=callbacks;

        }
        @Override
        protected void onPostExecute(String aVoid) {
            unlockScreenOrientation(activity);
            progressDialog.dismiss(activity.getSupportFragmentManager());
            callbacks.setServerResponse(aVoid);
            super.onPostExecute(aVoid);
        }

        @Override
        protected String doInBackground(Void... params) {

            ArrayList<Pair<String,String>> data=new ArrayList<>();

            data.add(new Pair<String, String>("account_uniqueId", financeAccount.getAccountUniqueId()));


            URL url;
            String line=null;
            HttpURLConnection urlConnection=null;
            try {

                byte[] postData= getData(data).getBytes("UTF-8");
                url=new URL(SERVER_ADDRESS + "DeleteFinanceAccount.php");
                urlConnection=(HttpURLConnection)url.openConnection();
                urlConnection.setReadTimeout(CONNECTION_TIMEOUT);
                urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("Content-Length", String.valueOf(postData.length));
                urlConnection.setDoOutput(true);
                urlConnection.getOutputStream().write(postData);

                urlConnection.getOutputStream().close();
                int responsecode=urlConnection.getResponseCode();
                if(responsecode==HttpURLConnection.HTTP_OK){
                    InputStream in =urlConnection.getInputStream();

                    BufferedReader reader= new BufferedReader(new InputStreamReader(in));
                    StringBuilder bld =new StringBuilder();
                    String il;
                    while((il=reader.readLine())!=null){
                        bld.append(il);
                    }
                    line=bld.toString();
                }else{
                    line="Error";
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return line;
        }
    }




    public class UpdateUserStatusAsynckTacks extends AsyncTask<Void,Void,String>
    {

        User user;
        GetUserCallbacks getUserCallbacks;

        public UpdateUserStatusAsynckTacks(User user, GetUserCallbacks callbacks){
            this.getUserCallbacks=callbacks;
            this.user=user;
        }
        @Override
        protected void onPostExecute(String reponse) {
            unlockScreenOrientation(activity);
            progressDialog.dismiss(activity.getSupportFragmentManager());
            getUserCallbacks.serverReponse(reponse);
            super.onPostExecute(reponse);
        }

        @Override
        protected String doInBackground(Void... params) {

            String line="";
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url=new URL(SERVER_ADDRESS + "UpdateUserOnlineStatus.php");
                urlConnection=(HttpURLConnection)url.openConnection();
                urlConnection.setReadTimeout(CONNECTION_TIMEOUT);
                urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);

                OutputStream out=urlConnection.getOutputStream();
                BufferedWriter buff=new BufferedWriter(new OutputStreamWriter(out,"UTF-8"));
                String data =URLEncoder.encode("email","UTF-8")+"="+URLEncoder.encode(user.email,"UTF-8")+"&"+
                        URLEncoder.encode("password","UTF-8")+"="+URLEncoder.encode(String.valueOf(user.password.hashCode()),"UTF-8")
                        +"&"+
                        URLEncoder.encode("onlinestatus","UTF-8")+"="+URLEncoder.encode(String.valueOf(user.status),"UTF-8");
                buff.write(data);
                buff.flush();
                buff.close();
                out.close();

                int responsecode=urlConnection.getResponseCode();
                if(responsecode==HttpURLConnection.HTTP_OK){
                    InputStream in =urlConnection.getInputStream();

                    BufferedReader reader= new BufferedReader(new InputStreamReader(in));
                    StringBuilder bld =new StringBuilder();
                    String il;
                    while((il=reader.readLine())!=null){
                        bld.append(il);
                    }
                    line=bld.toString();
                    if(line.contains("Status successfully updated")){
                       // mySQLiteHelper.reInitializeSqliteTable();
                    }

                }else{
                    line="Error";
                }




            }catch (Exception e){
                e.printStackTrace();
            }finally {
                assert urlConnection != null;
                urlConnection.disconnect();
            }
            return line;
        }
    }


    public static void lockScreenOrientation(Activity activity)
    {
        WindowManager windowManager =  (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        Configuration configuration = activity.getResources().getConfiguration();
        int rotation = windowManager.getDefaultDisplay().getRotation();

        // Search for the natural position of the device
        if(configuration.orientation == Configuration.ORIENTATION_LANDSCAPE &&
                (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) ||
                configuration.orientation == Configuration.ORIENTATION_PORTRAIT &&
                        (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270))
        {
            // Natural position is Landscape
            switch (rotation)
            {
                case Surface.ROTATION_0:
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    break;
                case Surface.ROTATION_90:
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                    break;
                case Surface.ROTATION_180:
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                    break;
                case Surface.ROTATION_270:
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    break;
            }
        }
        else
        {
            // Natural position is Portrait
            switch (rotation)
            {
                case Surface.ROTATION_0:
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    break;
                case Surface.ROTATION_90:
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    break;
                case Surface.ROTATION_180:
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                    break;
                case Surface.ROTATION_270:
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                    break;
            }
        }
    }

    public static void unlockScreenOrientation(Activity activity)
    {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }




    public String getStringImage(Bitmap bmp){
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            String temp=Base64.encodeToString(imageBytes, Base64.DEFAULT);
            return temp;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }
    private String getData(ArrayList<Pair<String,String>> values) throws UnsupportedEncodingException {
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

    public boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if ( "WIFI".equals(ni.getTypeName()))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if ("MOBILE".equals(ni.getTypeName()))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
}
