package com.app.bricenangue.timeme;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by bricenangue on 02/08/16.
 */
public class RecyclerViewAdapterCreateAccount  extends RecyclerView
        .Adapter<RecyclerViewAdapterCreateAccount
        .DataObjectHolder> {
    private static String LOG_TAG = "RecyclerViewAdapterCreateAccount";
    private ArrayList<FinanceAccount> mDataset;
    private MyRecyclerAdaptaterCreateAccountClickListener myClickListener;
    private MySQLiteHelper mySQLiteHelper;
    private Context context;
    private FragmentManager manager;
    private RecyclerViewAdapterCreateAccount recyclerViewAdapterCreateAccount=this;
    private UserLocalStore userLocalStore;




    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener, View.OnLongClickListener {
        TextView accountnametv,accountOwnertv,lastCHangetv,balancetv,pendingtv,accountidtv;

        private MyRecyclerAdaptaterCreateAccountClickListener myClickListener;

        public DataObjectHolder(View itemView, MyRecyclerAdaptaterCreateAccountClickListener myClickListener1) {
            super(itemView);
            myClickListener=myClickListener1;

            accountnametv = (TextView) itemView.findViewById(R.id.textView_AccountName_create_account_finance_card);
            accountOwnertv = (TextView) itemView.findViewById(R.id.textView_AccountOwner_create_account_finance_card);
            lastCHangetv = (TextView) itemView.findViewById(R.id.textView_last_changeOnAccount_create_account_finance_card);
            balancetv = (TextView) itemView.findViewById(R.id.textView_AccountBalance_create_account_finance_card);
            pendingtv = (TextView) itemView.findViewById(R.id.textView_pending_expenses_create_account_finance_card);
            accountidtv=(TextView) itemView.findViewById(R.id.textView_AccountID_create_account_finance_card);


            //Log.i(LOG_TAG, "Adding Listener");
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        }

        @Override
        public void onClick(View v) {

                myClickListener.onItemClick(getAdapterPosition(), v);

        }

        @Override
        public boolean onLongClick(View view) {
            myClickListener.onLongClick(getAdapterPosition(),view);
            return false;
        }
    }

    public void setOnCreateAccountlistClickListener(MyRecyclerAdaptaterCreateAccountClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public RecyclerViewAdapterCreateAccount(Context context, ArrayList<FinanceAccount> myDataset, MyRecyclerAdaptaterCreateAccountClickListener myClickListener) {
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
                .inflate(R.layout.create_account_card, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view,myClickListener);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(final DataObjectHolder holder, final int position) {
        FinanceAccount financeAccount=mDataset.get(position);

        String name=context.getResources().getString(R.string.Account_list_item_title_text ).toLowerCase()  +" " + financeAccount.getAccountName();


        holder.accountnametv.setText(name);

        holder.accountidtv.setText(financeAccount.getAccountUniqueId().replace("-","A"));
        String p=financeAccount.getAccountBlanceTostring() +" €";
        if(p.contains("-")){
            holder.balancetv.setText(p);
            holder.balancetv.setTextColor(context.getResources().getColor(R.color.warning_color));
        }else {
            holder.balancetv.setText(p);
            holder.balancetv.setTextColor(context.getResources().getColor(R.color.color_account_balance_positive));
        }

        if(financeAccount.getAccountOwnersToString().startsWith(" ")){
            String owner=financeAccount.getAccountOwnersToString().substring(1);
            holder.accountOwnertv.setText(owner);
        }

        holder.lastCHangetv.setText(financeAccount.getLastChangeDateToAccount());
       // holder.pendingtv.setVisibility(View.VISIBLE);
       // holder.pendingtv.setText(context.getString(R.string.account_finance_card_pending_expenses)+ "..."+ + " €");
       // holder.pendingtv.setTextColor(context.getResources().getColor(R.color.grey_light));

    }

    public void addItem(FinanceAccount dataObj) {

        mDataset.add(dataObj);

        notifyItemInserted(mDataset.size()-1);
    }


    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface MyRecyclerAdaptaterCreateAccountClickListener {
        public void onItemClick(int position, View v);
        public void onLongClick(int position, View v);
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
