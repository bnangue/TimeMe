package com.example.bricenangue.timeme;

import android.app.Activity;
import android.content.Context;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
        import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by bricenangue on 14/03/16.
 */
public class MyRecyclerViewAdapter extends RecyclerView
        .Adapter<MyRecyclerViewAdapter
        .DataObjectHolder> {
    private static String LOG_TAG = "MyRecyclerViewAdapter";
    private ArrayList<CalendarCollection> mDataset;
    private static LayoutInflater inflater=null;
    private  MyClickListener myClickListener;
    private MySQLiteHelper mySQLiteHelper;
    private AppCompatActivity context;
    private FragmentManager manager;
    private MyRecyclerViewAdapter myRecyclerViewAdapter=this;
    private UserLocalStore userLocalStore;
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
            .OnClickListener {
        TextView label,category,edescription;
        TextView dateTime;
        Button share,delete;

        RelativeLayout relativeLayout;
        LinearLayout mcard;
        private MyClickListener myClickListener;

        public DataObjectHolder(View itemView, MyClickListener myClickListener1) {
            super(itemView);
            myClickListener=myClickListener1;
            mcard=(LinearLayout)itemView.findViewById(R.id.lin);

            label = (TextView) itemView.findViewById(R.id.textView);
            dateTime = (TextView) itemView.findViewById(R.id.textView2);
           // edescription = (TextView) itemView.findViewById(R.id.textView3);
            category = (TextView) itemView.findViewById(R.id.textViewcategory);
            share = (Button) itemView.findViewById(R.id.buttonsharecardview);
            delete = (Button) itemView.findViewById(R.id.buttondeletecardview);


            Log.i(LOG_TAG, "Adding Listener");
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);

            relativeLayout=(RelativeLayout)v.findViewById(R.id.reexp);

            if (relativeLayout.getVisibility() == View.GONE) {
                relativeLayout.setVisibility(View.VISIBLE);
                share.setVisibility(View.GONE);
                delete.setVisibility(View.GONE);
                mcard.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,150));
               // category.setGravity(Gravity.CENTER | Gravity.BOTTOM);
               // label.setGravity(Gravity.CENTER | Gravity.BOTTOM);

            } else if (relativeLayout.getVisibility() == View.VISIBLE){
                relativeLayout.setVisibility(View.GONE);
                mcard.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                share.setVisibility(View.VISIBLE);
                delete.setVisibility(View.VISIBLE);

            }
            //edescription.setVisibility(View.VISIBLE);
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public MyRecyclerViewAdapter(AppCompatActivity context,ArrayList<CalendarCollection> myDataset, MyClickListener myClickListener) {
        this.context=context;
        mDataset = myDataset;
        this.myClickListener=myClickListener;
        inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mySQLiteHelper=new MySQLiteHelper(context);
        sqLiteShoppingList=new SQLiteShoppingList(context);
        manager=((Activity)context).getFragmentManager();
        userLocalStore=new UserLocalStore(context);

    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_item, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view,myClickListener);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(final DataObjectHolder holder, final int position) {
        CalendarCollection collection=mDataset.get(position);


        holder.label.setText(collection.title);
        holder.dateTime.setText(collection.datetime);

        switch (collection.category){
            case "Normal":
                holder.category.setText(collection.category);
                holder.category.setTextColor(context.getResources().getColor(R.color.normal));

                break;
            case "Business":
                holder.category.setText(collection.category);
                holder.category.setTextColor(context.getResources().getColor(R.color.business));

                break;
            case "Birthdays":
                holder.category.setText(collection.category);
                holder.category.setTextColor(context.getResources().getColor(R.color.birthdays));

                break;
            case "Grocery":
                holder.category.setText(collection.category);
                holder.category.setTextColor(context.getResources().getColor(R.color.grocery));

                break;
            case "Work Plans":
                holder.category.setText(collection.category);
                holder.category.setTextColor(context.getResources().getColor(R.color.workPlans));

                break;
        }
        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogDelete(holder);


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
                deleteItem(position);

                alertDialog.dismiss();
            }
        });

        alertDialog.setCancelable(false);
        // show it
        alertDialog.show();
    }
    public void addItem(CalendarCollection dataObj) {

        mDataset.add(dataObj);

        notifyItemInserted(mDataset.size()-1);
    }


    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);

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

            String date = (String) android.text.format.DateFormat.format("yyyy-MM-dd", dat);
            incomingNotification=new IncomingNotification(1,0,jsonObject.toString(),date);
            incomingNotification.id=calendarCollection.incomingnotifictionid;
        }catch (Exception e){
            e.printStackTrace();
        }

        return incomingNotification;
    }

    public void deleteItem(int index) {
        deleteFromSQLITEAndSERver(index);


    }


    private void deleteFromSQLITEAndSERver(final int index){
        final ServerRequests serverRequests= new ServerRequests(context);
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
                        Toast.makeText(context,"Event deleted",Toast.LENGTH_SHORT).show();

                    //getEvents(mySQLiteHelper.getAllIncomingNotification());

                }
            }
        });
    }

}
