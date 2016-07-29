package com.example.bricenangue.timeme;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
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

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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
    private Context context;
    private FragmentManager manager;
    private RecyclerAdapterSmallCards RecyclerAdaptaterCreateShoppingList=this;
    private UserLocalStore userLocalStore;
    private boolean isListwithDoneList;
    private SQLiteShoppingList sqLiteShoppingList;


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


            Log.i(LOG_TAG, "Adding Listener");
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

    public RecyclerAdapterSmallCards(Context context,ArrayList<GroceryList> myDataset, MyRecyclerAdaptaterCreateShoppingListClickListener myClickListener, MyRecyclerAdaptaterCreateShoppingListDoneClickListener myDoneClickListener, boolean isListwithDoneList) {
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
        holder.listStatus.setText(groceryList.isListdone() ? R.string.grocery_list_status_done_text : R.string.grocery_list_status__not_done_text);

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

                if(sqLiteShoppingList.deleteShoppingList(mDataset.get(holder.getAdapterPosition()).getList_unique_id())!=0){
                    deleteItem(holder.getAdapterPosition());
                    Toast.makeText(context,"List succesffully deleted",Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                }else {
                    Toast.makeText(context,"Error deleting grocery list",Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                }

            }
        });

        alertDialog.setCancelable(false);
        // show it
        alertDialog.show();
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
