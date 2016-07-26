package com.example.bricenangue.timeme;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by bricenangue on 26/07/16.
 */
public class RecyclerAdaptaterCreateShoppingList  extends RecyclerView
        .Adapter<RecyclerAdaptaterCreateShoppingList
        .DataObjectHolder> {
    private static String LOG_TAG = "RecyclerAdaptaterCreateShoppingList";
    private ArrayList<GroceryList> mDataset;
    private  MyRecyclerAdaptaterCreateShoppingListClickListener myClickListener;
    private MySQLiteHelper mySQLiteHelper;
    private Context context;
    private FragmentManager manager;
    private RecyclerAdaptaterCreateShoppingList RecyclerAdaptaterCreateShoppingList=this;
    private UserLocalStore userLocalStore;


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
            .OnClickListener {
        TextView listname,listcreator,listStatus;
        Button share,delete;

        RelativeLayout relativeLayout;
        LinearLayout mcard;
        private MyRecyclerAdaptaterCreateShoppingListClickListener myClickListener;

        public DataObjectHolder(View itemView, MyRecyclerAdaptaterCreateShoppingListClickListener myClickListener1) {
            super(itemView);
            myClickListener=myClickListener1;
            mcard=(LinearLayout)itemView.findViewById(R.id.lin_create_shopping_list_card);

            listname = (TextView) itemView.findViewById(R.id.textView_Grocery_listname_create_shopping_list_card);
            listcreator = (TextView) itemView.findViewById(R.id.textView_Listcreator_create_shopping_list_card);
            listStatus = (TextView) itemView.findViewById(R.id.textView_LisStatus_create_shopping_list_card);
            share = (Button) itemView.findViewById(R.id.buttonsharecardview_create_shopping_list_card);
            delete = (Button) itemView.findViewById(R.id.buttondeletecardview_create_shopping_list_card);


            Log.i(LOG_TAG, "Adding Listener");
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);

        }
    }

    public void setOnshoppinglistClickListener(MyRecyclerAdaptaterCreateShoppingListClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public RecyclerAdaptaterCreateShoppingList(Context context,ArrayList<GroceryList> myDataset, MyRecyclerAdaptaterCreateShoppingListClickListener myClickListener) {
        this.context=context;
        mDataset = myDataset;
        this.myClickListener=myClickListener;
        mySQLiteHelper=new MySQLiteHelper(context);
        manager=((Activity)context).getFragmentManager();
        userLocalStore=new UserLocalStore(context);

    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.create_shopping_list_card, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view,myClickListener);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(final DataObjectHolder holder, final int position) {
        GroceryList groceryList=mDataset.get(position);


        holder.listname.setText(groceryList.getDatum());
        holder.listcreator.setText(groceryList.getCreatorName());
        holder.listStatus.setText(groceryList.isListdone() ? R.string.grocery_list_status_done_text : R.string.grocery_list_status__not_done_text);

        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myClickListener.onButtonClick(holder.getAdapterPosition(), v);
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id=v.getId();
                // deleteItem(holder.getAdapterPosition());
                myClickListener.onButtonClick(holder.getAdapterPosition(), v);


            }
        });
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
        public void onButtonClick(int position, View v);
    }



    private IncomingNotification getIncomingNotificationFromEvent(CalendarCollection calendarCollection){
        IncomingNotification incomingNotification=null;
        try {
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("title",calendarCollection.title);
            jsonObject.put("description",calendarCollection.description);
            jsonObject.put("datetime",calendarCollection.datetime);
            jsonObject.put("creator",calendarCollection.creator);
            jsonObject.put("category",calendarCollection.category);
            jsonObject.put("startingtime",calendarCollection.startingtime);
            jsonObject.put("endingtime",calendarCollection.endingtime);
            jsonObject.put("hashid",calendarCollection.hashid);
            jsonObject.put("alldayevent",calendarCollection.alldayevent);
            Calendar c=new GregorianCalendar();
            Date dat=c.getTime();
            //String day= String.valueOf(dat.getDay());
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            String date = (String) android.text.format.DateFormat.format("dd-MM-yyyy", dat);
            incomingNotification=new IncomingNotification(1,0,jsonObject.toString(),date);
            incomingNotification.id=calendarCollection.incomingnotifictionid;
        }catch (Exception e){
            e.printStackTrace();
        }

        return incomingNotification;
    }

    public void deleteItem(int index) {
       // deleteFromSQLITEAndSERver(index);

        mDataset.remove(index);
        notifyItemRemoved(index);
    }


    /**

    private void deleteFromSQLITEAndSERver(final int index){
        ServerRequests serverRequests= new ServerRequests(context);
        serverRequests.deleteCalenderEventInBackgroung(mDataset.get(index), new GetEventsCallbacks() {
            @Override
            public void done(ArrayList<CalendarCollection> returnedeventobject) {

            }

            @Override
            public void itemslis(ArrayList<ShoppingItem> returnedShoppingItem) {

            }

            @Override
            public void updated(String reponse) {
                if (reponse.contains("Event successfully deleted")) {
                    mySQLiteHelper.deleteIncomingNotification(mDataset.get(index).incomingnotifictionid);
                    mDataset.remove(index);
                    notifyItemRemoved(index);
                    //getEvents(mySQLiteHelper.getAllIncomingNotification());

                }
            }
        });
    }
     **/
}
