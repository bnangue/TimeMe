package com.app.bricenangue.timeme;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by bricenangue on 27/07/16.
 */
public class RecyclerAdapterSmallCards  extends RecyclerView
        .Adapter<RecyclerAdapterSmallCards
        .DataObjectHolder>  {
    private static String LOG_TAG = "RecyclerAdaptaterCreateShoppingList";
    private static LayoutInflater inflater=null;
    private ArrayList<GroceryList> mDataset;
    private  MyRecyclerAdaptaterCreateShoppingListClickListener myClickListener;
    private  MyRecyclerAdaptaterCreateShoppingListDoneClickListener myDoneClickListener;
    private MySQLiteHelper mySQLiteHelper;
    private AppCompatActivity context;
    private FragmentManager manager;
    private RecyclerAdapterSmallCards RecyclerAdaptaterCreateShoppingList=this;
    private UserLocalStore userLocalStore;
    private boolean isListwithDoneList;
    private SQLiteShoppingList sqLiteShoppingList;
    private SQLFinanceAccount sqlFinanceAccount;


    public class OnExpandDetailsClickListener implements View.OnClickListener {

        int position;
        View v;
        public OnExpandDetailsClickListener(int position, View v){
            this.position=position;
            this.v=v;
        }
        @Override
        public void onClick(View v) {

        }
    }

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener{
        TextView listname,listcreator,listStatus;
        Button share,delete;

        RelativeLayout relativeLayout;
        LinearLayout mcard;
        private MyRecyclerAdaptaterCreateShoppingListClickListener myClickListener;
        private  MyRecyclerAdaptaterCreateShoppingListDoneClickListener myDoneClickListener;
        private  boolean isListwithDoneList;

        public DataObjectHolder(View itemView, MyRecyclerAdaptaterCreateShoppingListClickListener myClickListener1,
                                MyRecyclerAdaptaterCreateShoppingListDoneClickListener myDoneClickListener1, boolean isListwithDoneList1) {
            super(itemView);
            myClickListener=myClickListener1;
            myDoneClickListener=myDoneClickListener1;
            isListwithDoneList=isListwithDoneList1;
            mcard=(LinearLayout)itemView.findViewById(R.id.lin_create_shopping_list_small_card);

            listname = (TextView) itemView.findViewById(R.id.textView_Grocery_listname_create_shopping_list_small_card);
            listStatus = (TextView) itemView.findViewById(R.id.textView_LisStatus_create_shopping_list_small_card);

            delete = (Button) itemView.findViewById(R.id.buttondeletecardview_create_shopping_list_small_card);


            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if(isListwithDoneList){
                myDoneClickListener.onItemClick(getAdapterPosition(), v);
            }else{
                myClickListener.onItemClick(getAdapterPosition(), v);
            }



        }


    }

    public void setOnshoppinglistsmallClickListener(MyRecyclerAdaptaterCreateShoppingListClickListener myClickListener,  MyRecyclerAdaptaterCreateShoppingListDoneClickListener myDoneClickListener) {
        this.myClickListener = myClickListener;
        this.myDoneClickListener=myDoneClickListener;
    }

    public RecyclerAdapterSmallCards(AppCompatActivity context,ArrayList<GroceryList> myDataset, MyRecyclerAdaptaterCreateShoppingListClickListener myClickListener, MyRecyclerAdaptaterCreateShoppingListDoneClickListener myDoneClickListener, boolean isListwithDoneList) {
        this.context=context;
        this.isListwithDoneList=isListwithDoneList;
        mDataset = myDataset;
        sqLiteShoppingList=new SQLiteShoppingList(context);
       inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.myClickListener=myClickListener;
        this.myDoneClickListener=myDoneClickListener;
        mySQLiteHelper=new MySQLiteHelper(context);
        manager=((Activity)context).getFragmentManager();
        userLocalStore=new UserLocalStore(context);
        sqlFinanceAccount=new SQLFinanceAccount(context);

    }

    public RecyclerAdapterSmallCards (ArrayList<GroceryList> myDataset){

        mDataset = myDataset;
    }
    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_shop_list_small, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view,myClickListener,myDoneClickListener,isListwithDoneList);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(final DataObjectHolder holder, final int position) {
        GroceryList groceryList=mDataset.get(position);


        String name=context.getResources().getString(R.string.grocery_list_item_title_text ).toLowerCase() + " " + groceryList.getDatum();
        holder.listname.setText(name);
        holder.listStatus.setText(groceryList.isListdone() ? groceryList.getGroceryListTotalPriceString() : context.getString(R.string.grocery_list_status__not_done_text));
        holder.listStatus.setTextColor(groceryList.isListdone() ? context.getResources().getColor(R.color.warning_color) : context.getResources().getColor(R.color.grey_light));

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isListwithDoneList){
                    alertDialogDelete(holder);
                }else{
                    alertDialogDelete(holder);
                }
            }
        });
    }



    public void alertDialogDelete(final DataObjectHolder holder){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);
        View dialoglayout = inflater.inflate(R.layout.dialog_warning_delete_event, null);

        final android.support.v7.app.AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.setView(dialoglayout);
        Button delete= (Button)dialoglayout.findViewById(R.id.buttonDeleteaccount);
        Button cancel= (Button)dialoglayout.findViewById(R.id.buttonCancelaccount);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();

            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=holder.getAdapterPosition();
                GroceryList groceryList=mDataset.get(position);
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd, HH:mm");
                formatter.setLenient(false);

                Date currentDate = new Date();


                String currentTime = formatter.format(currentDate);
                CalendarCollection    calendarCollection=new CalendarCollection(groceryList.getDatum(),groceryList.getListcontain(),
                        groceryList.getCreatorName(),groceryList.getDatum(), groceryList.getDatum() + " 17:00",
                        groceryList.getDatum() +" 20:00",groceryList.getList_unique_id(),context.getString(R.string.Event_Category_Category_Shopping),"0","0",currentTime);

                ArrayList<FinanceAccount> financeAccountArrayList= sqlFinanceAccount.getAllFinanceAccount();
                if(financeAccountArrayList.size()!=0) {
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
                    recordsArrayList = financeAccount.getRecords(context);
                    for (int i = 0; i < recordsArrayList.size(); i++) {
                        if (!groceryList.allItemsbought() && recordsArrayList.get(i).getRecordUniquesId().equals(groceryList.getList_unique_id())) {
                            recordsArrayList.remove(recordsArrayList.get(i));
                        }
                    }
                    financeAccount.setAccountsRecord(recordsArrayList);
                    financeAccount.getAccountrecordsAmountUpdateBalance(context);

                    financeAccount.getAccountRecordsString();
                    financeAccount.setLastchangeToAccount();

                    deleteGroceryOnServer(groceryList,position,calendarCollection,financeAccount);
                }else {

                }


                alertDialog.dismiss();
            }
        });

        alertDialog.setCancelable(false);
        // show it
        alertDialog.show();
    }


    private void deleteGroceryOnServer(final GroceryList groceryList, final int position, final CalendarCollection calendarCollection, final FinanceAccount financeAccount){
        ServerRequests serverRequests=new ServerRequests(context);
        serverRequests.deleteGroceryListInBackgroung(groceryList,financeAccount, calendarCollection,new GroceryListCallBacks() {
            @Override
            public void fetchDone(ArrayList<GroceryList> returnedGroceryLists) {

            }

            @Override
            public void setServerResponse(String serverResponse) {
                if (serverResponse.contains("Grocery list successfully deleted, account updated,Event deleted")){

                    mySQLiteHelper.deleteIncomingNotification(calendarCollection.incomingnotifictionid);

                        Firebase shoopinglistItem=new Firebase(Config.FIREBASE_APP_URL).
                        child(Config.FIREBASE_APP_URL_SHARED_GROCERY).child(groceryList.getList_unique_id());
                        shoopinglistItem.removeValue();


                    deleteGroceryListLocally(groceryList,position,financeAccount);
                    deleteGroceryListLocally(groceryList,position,financeAccount);


                }else{
                    Toast.makeText(context,"An error occured during the connection to the server",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void deleteFromSQLITEAndSERver(final CalendarCollection collection, final GroceryList groceryList, final int position, final FinanceAccount financeAccount){
        ServerRequests serverRequests= new ServerRequests(context);
        serverRequests.deleteCalenderEventInBackgroung(collection, new GetEventsCallbacks() {
            @Override
            public void done(ArrayList<CalendarCollection> returnedeventobject) {

            }

            @Override
            public void itemslis(ArrayList<ShoppingItem> returnedShoppingItem) {

            }

            @Override
            public void updated(String reponse) {
                if (reponse.contains("Event successfully deleted")) {
                    mySQLiteHelper.deleteIncomingNotification(collection.incomingnotifictionid);
                    deleteGroceryListLocally(groceryList,position,financeAccount);

                }else{
                    Toast.makeText(context,"An error ont event occured during the connection to the server",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void deleteGroceryListLocally(GroceryList groceryList, int position,FinanceAccount financeAccount){

        if (sqlFinanceAccount.updateFinanceAccount(financeAccount)!=0){
            if(sqLiteShoppingList.deleteShoppingList(groceryList.getList_unique_id())!=0){
                deleteItem(position);
                Toast.makeText(context,"List succesffully deleted",Toast.LENGTH_SHORT).show();

            }else {
                Toast.makeText(context,"Error deleting grocery list",Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(context,"Error updating account locally",Toast.LENGTH_SHORT).show();
        }


    }

    public void addItem(GroceryList dataObj) {

        mDataset.add(dataObj);

        notifyItemInserted(mDataset.size()-1);
    }


    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface MyRecyclerAdaptaterCreateShoppingListClickListener {
        public void onItemClick(int position, View v);

    }

    public interface MyRecyclerAdaptaterCreateShoppingListDoneClickListener {
        public void onItemClick(int position, View v);

    }




    public void deleteItem(int index) {

        mDataset.remove(index);
        notifyItemRemoved(index);
    }


}
