package com.example.bricenangue.timeme;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CreateNewShoppingListActivity extends AppCompatActivity implements View.OnClickListener,DialogFragmentDatePicker.OnDateGet,DialogDeleteEventFragment.OnDeleteListener {

    private Button addItemToListbutton,setDateButtuon;

    private ArrayList<GroceryList> grocerylistSqlDB=new ArrayList<>();
    private SQLiteShoppingList sqLiteShoppingList;
    private GroceryList groceryList;

    private TextView textViewlistIsempty;


    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerAdaptaterCreateShoppingList.MyRecyclerAdaptaterCreateShoppingListClickListener myClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_shopping_list);

        sqLiteShoppingList=new SQLiteShoppingList(this);
        addItemToListbutton=(Button)findViewById(R.id.grocery_create_list_add_item_button);
        setDateButtuon=(Button)findViewById(R.id.dateaddeventstart_creatList);
        textViewlistIsempty=(TextView)findViewById(R.id.textView_create_list_List_empty);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView = (RecyclerView)findViewById(R.id.shoppingrecycleViewCreateLisactivity);
        setDateButtuon.setOnClickListener(this);
        addItemToListbutton.setOnClickListener(this);

        grocerylistSqlDB=getGroceryList();

        Bundle extras=getIntent().getExtras();
        if(extras!=null && extras.containsKey("GroceryshoppingList")){
            groceryList= extras.getParcelable("GroceryshoppingList");
        }

        initializeDatePicker();

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
        mAdapter = new RecyclerAdaptaterCreateShoppingList(this, grocerylistSqlDB, myClickListener);
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
        return sqLiteShoppingList.getAllShoppingList();
    }

    private void initializeDatePicker(){
        final SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        formatter.setLenient(false);

        Date curDate = new Date();
        String curTime = formatter.format(curDate);
        setDateButtuon.setText(curTime);
    }
    public void onDatePickercliced(boolean bol){
        android.support.v4.app.FragmentManager manager=getSupportFragmentManager();
        DialogFragment fragmentDatePicker=DialogFragmentDatePicker.newInstance(bol);

        fragmentDatePicker.show(manager,"datePickerfr");
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
                startActivity(new Intent(CreateNewShoppingListActivity.this,AddItemToListActivity.class).putExtra("listName",setDateButtuon.getText().toString()));
                break;
            case R.id.dateaddeventstart_creatList:
                onDatePickercliced(true);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        grocerylistSqlDB=getGroceryList();
        populateRecyclerView();
        ((RecyclerAdaptaterCreateShoppingList) mAdapter).setOnshoppinglistClickListener(myClickListener);

    }
    @Override
    public void dateSet(String date, boolean isstart) {
        setDateButtuon.setText(date);
    }


    @Override
    public void delete(int position) {
        if(sqLiteShoppingList.deleteShoppingList(grocerylistSqlDB.get(position).getList_unique_id())!=0){
            ((RecyclerAdaptaterCreateShoppingList)mAdapter).deleteItem(position);
            Toast.makeText(getApplicationContext(),"List succesffully deleted",Toast.LENGTH_SHORT).show();
        }
    }
}
