package com.app.bricenangue.timeme;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CreateNewShoppingListActivity extends AppCompatActivity implements View.OnClickListener,DialogDeleteEventFragment.OnDeleteListener {

    private Button addItemToListbutton;

    private ArrayList<GroceryList> grocerylistSqlDB=new ArrayList<>();
    private SQLiteShoppingList sqLiteShoppingList;
    private GroceryList groceryList;
    private FinanceAccount financeAccount;

    private TextView textViewlistIsempty;
    private StateActivitiesPreference stateActivitiesPreference;


    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerAdaptaterCreateShoppingList.MyRecyclerAdaptaterCreateShoppingListClickListener myClickListener;
    private MySQLiteHelper mySQLiteHelper;
    private SQLFinanceAccount sqlFinanceAccount;
    private FileHelper fileHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_shopping_list);
        stateActivitiesPreference=new StateActivitiesPreference(this);
        sqLiteShoppingList=new SQLiteShoppingList(this);
        sqlFinanceAccount=new SQLFinanceAccount(this);
        mySQLiteHelper=new MySQLiteHelper(this);
        fileHelper=new FileHelper(this);
        addItemToListbutton=(Button)findViewById(R.id.grocery_create_list_add_item_button);
        textViewlistIsempty=(TextView)findViewById(R.id.textView_create_list_List_empty);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView = (RecyclerView)findViewById(R.id.shoppingrecycleViewCreateLisactivity);

        addItemToListbutton.setOnClickListener(this);

        grocerylistSqlDB=getGroceryList();

        Bundle extras=getIntent().getExtras();
        if(extras!=null && extras.containsKey("GroceryshoppingList")){
            groceryList= extras.getParcelable("GroceryshoppingList");
            financeAccount=extras.getParcelable("account");
        }else if(extras!=null && extras.containsKey("account")){
            financeAccount=extras.getParcelable("account");
        }

/**
        File file = new File(fileHelper.getExcelfile("shopping_list_items"));
        try {
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int read;
            while ((read = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, read);
            }

            byte[] bytes = bos.toByteArray();
            new StoreFileAsynckTacks(getStringFilebyte(bytes)).execute();

        } catch (Exception e) {
            e.printStackTrace();
        }

**/
        if(!stateActivitiesPreference.getCopyExcelFileFromAssetToInterneMemory()){
            new InitItemToDBAsyncTask(this).execute();
        }

        if(savedInstanceState!=null){

                populateRecyclerView();
            financeAccount=savedInstanceState.getParcelable("account");

        }else{


                populateRecyclerView();

        }

        myClickListener=new RecyclerAdaptaterCreateShoppingList.MyRecyclerAdaptaterCreateShoppingListClickListener(){
            @Override
            public void onItemClick(int position, View v) {
                startGroceryListOverview(grocerylistSqlDB.get(position));
            }

            @Override
            public void onButtonClick(int position, View v) {
                int iD = v.getId();
                switch (iD) {
                    case R.id.buttondeletecardview_create_shopping_list_card:
                        DialogFragment dialogFragment = DialogDeleteEventFragment.newInstance(position);
                        dialogFragment.setCancelable(false);
                        dialogFragment.show(getSupportFragmentManager(), "DELETEListFRAGMENT");


                        break;
                    case R.id.buttonsharecardview_create_shopping_list_card:

                        break;
                }
            }
        };






    }

    private  boolean saveExcelXLSXFileFirstInit(Context context) {
        boolean success= false;

        FileHelper fileHelper=new FileHelper(context);
        XSSFWorkbook wb = null;
        String workbooksFolderpath= fileHelper.getWorkbooksFolder();
        String filesDirectorypath= fileHelper.getFilesDirectory();
        File filedirectory=new File(filesDirectorypath);

        if (!filedirectory.exists()){
            filedirectory.mkdirs();
        }
        File workbookfolder=new File(workbooksFolderpath);
        if(!workbookfolder.exists()){
            workbookfolder.mkdir();
        }
        // Create a path where we will place our List of objects on external storage
        File file=null;
        FileOutputStream os = null;

        try {
            AssetManager manager=getAssets();
            InputStream in=manager.open("shopping_list_items.xlsx");
            wb = new XSSFWorkbook(in);

            file = new File(fileHelper.getExcelfile("shopping_list_items"));
            if(!file.exists()){
                file.createNewFile();

            }

            os = new FileOutputStream(file);
            wb.write(os);
            Log.w("FileUtils", "Writing file" + file);
            success = true;
        } catch (IOException e) {
            Log.w("FileUtils", "Error writing " + file, e);
        } catch (Exception e) {
            Log.w("FileUtils", "Failed to save file", e);
        } finally {
            try {
                if (null != os)
                    os.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return success;
    }

    private  boolean addExcelXLSXFileToWoorkbook(Context context,String filename) {
        boolean success= false;

        FileHelper fileHelper=new FileHelper(context);
        XSSFWorkbook wb = null;

        File file=null;
        FileOutputStream os = null;

        try {
            AssetManager manager=getAssets();
            InputStream in=manager.open(filename+ ".xlsx");
            wb = new XSSFWorkbook(in);

            file = new File(fileHelper.getExcelfile("shopping_list_items"));

            os = new FileOutputStream(file);
            wb.write(os);
            Log.w("FileUtils", "Writing file" + file);
            success = true;
        } catch (IOException e) {
            Log.w("FileUtils", "Error writing " + file, e);
        } catch (Exception e) {
            Log.w("FileUtils", "Failed to save file", e);
        } finally {
            try {
                if (null != os)
                    os.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return success;
    }

    private void startGroceryListOverview(GroceryList item) {
        startActivity(new Intent(this,DetailsShoppingListActivity.class).putExtra("GroceryList",item).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));

    }


    void populateRecyclerView(){
        if(grocerylistSqlDB.size()==0){
            textViewlistIsempty.setText(R.string.text_create_list_List_empty);
        }else{

            textViewlistIsempty.setText(R.string.text_create_list_List_not_empty_more_than_one);
        }


        mRecyclerView.setVisibility(View.VISIBLE);
        mAdapter = new RecyclerAdaptaterCreateShoppingList(this, grocerylistSqlDB, myClickListener,null,false);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }


    private ArrayList<ShoppingItem> getItems(ShoppingItem[] itemstoShoparray) {
        ArrayList<ShoppingItem> itemstoShopList=new ArrayList<>();

        for(ShoppingItem s:itemstoShoparray){
            if(s!=null){
               itemstoShopList.add(s);
            }
        }

        return itemstoShopList;
    }

    private ArrayList<GroceryList> getGroceryList(){
        return sqLiteShoppingList.getAllShoppingList()[0];
    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(financeAccount!=null){
            outState.putParcelable("account",financeAccount);
        }


    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.grocery_create_list_add_item_button:
                //add item return shopping list to show here
                if(financeAccount!=null){
                    startActivity(new Intent(CreateNewShoppingListActivity.this,AddItemToListActivity.class).putExtra("account",financeAccount));
                }else{
                    startActivity(new Intent(CreateNewShoppingListActivity.this,AddItemToListActivity.class));
                }

                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        grocerylistSqlDB=getGroceryList();
        populateRecyclerView();
        ((RecyclerAdaptaterCreateShoppingList) mAdapter).setOnshoppinglistClickListener(myClickListener,null);

    }

    class InitItemToDBAsyncTask extends AsyncTask<Void ,Void, Boolean> {

        private FragmentProgressBarLoading progressDialog;
        private Context context;
        private boolean success=false;
        public InitItemToDBAsyncTask(Context context){
            this.context=context;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //start progressBar
            progressDialog = new FragmentProgressBarLoading();
            progressDialog.setCancelable(false);
            progressDialog.show(getSupportFragmentManager(), "task_progress");
        }

        @Override
        protected Boolean doInBackground(Void... params) {


                success=saveExcelXLSXFileFirstInit(context);


            return success;

        }

        @Override
        protected void onPostExecute(Boolean params) {
            //end progressBar
            progressDialog.dismiss(getSupportFragmentManager());
            if(params){
                // initializeDatePicker();

                    stateActivitiesPreference.setCopyExcelFileFromAssetToInterneMemory(true);
                Toast.makeText(getApplicationContext(),"Database loaded",Toast.LENGTH_SHORT).show();
    /**
                File file = new File(fileHelper.getExcelfile("shopping_list_items"));
                try {
                    FileInputStream fis = new FileInputStream(file);
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    byte[] buf = new byte[1024];

                        for (int readNum; (readNum = fis.read(buf)) != -1;) {
                            bos.write(buf, 0, readNum); //no doubt here is 0

                        }
                    byte[] bytes = bos.toByteArray();
                    new StoreFileAsynckTacks(getStringFilebyte(bytes)).execute();

                } catch (Exception e) {
                    e.printStackTrace();
                }

**/

            }else{
                Toast.makeText(getApplicationContext(),"Error initializing database",Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public void delete(int position) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd, HH:mm");
        formatter.setLenient(false);

        Date currentDate = new Date();


        String currentTime = formatter.format(currentDate);
        CalendarCollection calendarCollection = new CalendarCollection(groceryList.getDatum(), groceryList.getListcontain(),
                groceryList.getCreatorName(), groceryList.getDatum(), groceryList.getDatum() + " 17:00",
                groceryList.getDatum() + " 20:00", groceryList.getList_unique_id(), getString(R.string.Event_Category_Category_Shopping), "0", "0", currentTime);

        ArrayList<FinanceAccount> financeAccountArrayList = sqlFinanceAccount.getAllFinanceAccount();
        if (financeAccountArrayList.size() != 0) {
            FinanceAccount financeAccount = null;
            for (int i=0;i<financeAccountArrayList.size();i++){
                if(financeAccountArrayList.get(i).getAccountUniqueId().equals(groceryList.getAccountid())){
                    financeAccount=financeAccountArrayList.get(i);
                    break;
                }else if(groceryList.getAccountid().isEmpty()){
                    financeAccount=financeAccountArrayList.get(0);
                    break;
                }
            }
            ArrayList<FinanceRecords> recordsArrayList = new ArrayList<>();
            recordsArrayList = financeAccount.getRecords();
            for (int i = 0; i < recordsArrayList.size(); i++) {
                if (!groceryList.allItemsbought() && recordsArrayList.get(i).getRecordUniquesId().equals(groceryList.getList_unique_id())) {
                    recordsArrayList.remove(recordsArrayList.get(i));
                }
            }
            financeAccount.setAccountsRecord(recordsArrayList);
            financeAccount.getAccountrecordsAmountUpdateBalance();

            financeAccount.getAccountRecordsString();
            financeAccount.setLastchangeToAccount();
            deleteGroceryOnServer(grocerylistSqlDB.get(position), position, calendarCollection, financeAccount);

        }
    }


    public String getStringFilebyte(byte[ ] file){
        try {

            byte[] imageBytes =file;
            String temp= Base64.encodeToString(imageBytes, Base64.DEFAULT);
            return temp;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    public static byte[] decodeBase64File(String input)
    {
        byte[] decodedByte = Base64.decode(input, 0);
        return decodedByte;
    }
    public class StoreFileAsynckTacks extends AsyncTask<Void,Void,String>{

        private String file;
        public StoreFileAsynckTacks(String file){
            this.file=file;

        }
        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);
            if(aVoid.contains("file added successfully")){
                Toast.makeText(getApplicationContext(),"Database loaded",Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(Void... params) {

            String reponse=null;
            ArrayList<Pair<String,String>> data=new ArrayList<>();
            data.add(new Pair<String, String>("file", file ));

            URL url;
            HttpURLConnection urlConnection=null;
            try {

                byte[] postData= getData(data).getBytes("UTF-8");
                url=new URL(ServerRequests.SERVER_ADDRESS + "SaveFileToServer.php");
                urlConnection=(HttpURLConnection)url.openConnection();
                urlConnection.setReadTimeout(ServerRequests.CONNECTION_TIMEOUT);
                urlConnection.setConnectTimeout(ServerRequests.CONNECTION_TIMEOUT);
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

    private void deleteGroceryOnServer(final GroceryList groceryList, final int position, final CalendarCollection calendarCollection, final FinanceAccount financeAccount){
        ServerRequests serverRequests=new ServerRequests(this);
        serverRequests.deleteGroceryListInBackgroung(groceryList,financeAccount, calendarCollection,new GroceryListCallBacks() {
            @Override
            public void fetchDone(ArrayList<GroceryList> returnedGroceryLists) {

            }

            @Override
            public void setServerResponse(String serverResponse) {
                if (serverResponse.contains("Grocery list successfully deleted, account updated,Event deleted")){

                    mySQLiteHelper.deleteIncomingNotification(calendarCollection.incomingnotifictionid);
                    deleteGroceryListLocally(groceryList,position,financeAccount);


                }else{
                    Toast.makeText(getApplicationContext(),"An error occured during the connection to the server",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private void deleteGroceryListLocally(GroceryList groceryList, int position,FinanceAccount financeAccount){

        if (sqlFinanceAccount.updateFinanceAccount(financeAccount)!=0){
            if(sqLiteShoppingList.deleteShoppingList(groceryList.getList_unique_id())!=0){
                ((RecyclerAdaptaterCreateShoppingList)mAdapter).deleteItem(position);
                Toast.makeText(getApplicationContext(),"List succesffully deleted",Toast.LENGTH_SHORT).show();

            }else {
                Toast.makeText(getApplicationContext(),"Error deleting grocery list locally",Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(getApplicationContext(),"Error updating account locally",Toast.LENGTH_SHORT).show();
        }


    }
}
