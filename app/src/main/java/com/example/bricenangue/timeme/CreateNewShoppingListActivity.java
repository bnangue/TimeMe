package com.example.bricenangue.timeme;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.provider.ContactsContract;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CreateNewShoppingListActivity extends AppCompatActivity implements View.OnClickListener,DialogDeleteEventFragment.OnDeleteListener {

    private Button addItemToListbutton;

    private ArrayList<GroceryList> grocerylistSqlDB=new ArrayList<>();
    private SQLiteShoppingList sqLiteShoppingList;
    private GroceryList groceryList;

    private TextView textViewlistIsempty;
    private StateActivitiesPreference stateActivitiesPreference;


    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerAdaptaterCreateShoppingList.MyRecyclerAdaptaterCreateShoppingListClickListener myClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_shopping_list);
        stateActivitiesPreference=new StateActivitiesPreference(this);
        sqLiteShoppingList=new SQLiteShoppingList(this);
        addItemToListbutton=(Button)findViewById(R.id.grocery_create_list_add_item_button);
        textViewlistIsempty=(TextView)findViewById(R.id.textView_create_list_List_empty);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView = (RecyclerView)findViewById(R.id.shoppingrecycleViewCreateLisactivity);

        addItemToListbutton.setOnClickListener(this);

        grocerylistSqlDB=getGroceryList();

        Bundle extras=getIntent().getExtras();
        if(extras!=null && extras.containsKey("GroceryshoppingList")){
            groceryList= extras.getParcelable("GroceryshoppingList");
        }

       // initializeDatePicker();
        if(!stateActivitiesPreference.getCopyExcelFileFromAssetToInterneMemory()){
            stateActivitiesPreference.setCopyExcelFileFromAssetToInterneMemory(saveExcelXLSXFileFirstInit(this));
        }

        if(savedInstanceState!=null){

                populateRecyclerView();

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
            InputStream in=manager.open("book_shopping_item.xlsx");
            wb = new XSSFWorkbook(in);

            file = new File(fileHelper.getExcelfile("book_shopping_item"));
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

            file = new File(fileHelper.getExcelfile("book_shopping_item"));

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

    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.grocery_create_list_add_item_button:
                //add item return shopping list to show here
                startActivity(new Intent(CreateNewShoppingListActivity.this,AddItemToListActivity.class));
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


    @Override
    public void delete(int position) {
        if(sqLiteShoppingList.deleteShoppingList(grocerylistSqlDB.get(position).getList_unique_id())!=0){
            ((RecyclerAdaptaterCreateShoppingList)mAdapter).deleteItem(position);
            Toast.makeText(getApplicationContext(),"List succesffully deleted",Toast.LENGTH_SHORT).show();
        }
    }
}
