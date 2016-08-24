package com.app.bricenangue.timeme;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import org.apache.poi.ss.formula.functions.T;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;


public class FragmentOverview extends Fragment{

    private AndroidListAdapter list_adapter;
    private MySQLiteHelper mySQLiteHelper;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private MyRecyclerViewAdapter.MyClickListener myClickListener;
    private static String LOG_TAG = "RecyclerViewActivity";
    private static String LOG_TAGT = "RecyclerDeleteActivity";
    private Fragment fragment = this;
    private ArrayList<CalendarCollection> newItems = new ArrayList<>();
    private ArrayList<CalendarCollection> collectionArrayList = new ArrayList<>();
    private UserLocalStore userLocalStore;
    private SQLFinanceAccount sqlFinanceAccount;
    private SQLiteShoppingList sqLiteShoppingList;
    private Spinner spinnerAccounts;
    private String [] nameAccArray;
    private String [] idAccArray;


    private ArrayList<FinanceAccount> financeAccountsArrayList = new ArrayList<>();
    private ArrayList<GroceryList> groceryListArrayList = new ArrayList<>();


    private AlertDialog alertDialog;

    private ListView listViewShopping, listViewFinance;
    private boolean isShown;


    public FragmentOverview() {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        userLocalStore=new UserLocalStore(getContext());
        sqlFinanceAccount=new SQLFinanceAccount(getContext());
        sqLiteShoppingList=new SQLiteShoppingList(getContext());

        View rootView = inflater.inflate(R.layout.fragment_fragment_overview_layout, container, false);
        listViewShopping=(ListView)rootView.findViewById(R.id.listView_overview_fragment_shopping_list);
        listViewFinance=(ListView)rootView.findViewById(R.id.listView_overview_fragment_finance_records);

        spinnerAccounts=(Spinner)rootView.findViewById(R.id.spinner_overview_fragment_which_account);


        return rootView;

    }
    private void populateSpinner(){
        if(nameAccArray!=null){
            SpinnerAdapter adapter = new ArrayAdapter<>(getContext(), R.layout.spinnerlayout, nameAccArray);
            spinnerAccounts.setAdapter(adapter);
            spinnerAccounts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                        populauteFinanceList(financeAccountsArrayList.get(i).getRecords());

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    populauteFinanceList(financeAccountsArrayList.get(0).getRecords());
                }
            });
        }
    }

    void showFriendstosharewith() {
        // Intialize  readable sequence of char values
        List<CharSequence> list = new ArrayList<CharSequence>();

        for (int i=0;i<7;i++){

            list.add("Friend  " + i);  // Add the item in the list
        }
        final CharSequence[] dialogList = list.toArray(new CharSequence[list.size()]);
        final AlertDialog.Builder builderDialog = new AlertDialog.Builder(getActivity());
        builderDialog.setTitle("Select Item");
        int count = dialogList.length;
        boolean[] is_checked = new boolean[count]; // set is_checked boolean false;

        // Creating multiple selection by using setMutliChoiceItem method
        builderDialog.setMultiChoiceItems(dialogList, is_checked,
                new DialogInterface.OnMultiChoiceClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int whichButton, boolean isChecked) {
                    }
                });

        builderDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        ListView list = ((AlertDialog) dialog).getListView();
                        // make selected item in the comma seprated string
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int i = 0; i < list.getCount(); i++) {
                            boolean checked = list.isItemChecked(i);

                            if (checked) {
                                if (stringBuilder.length() > 0) stringBuilder.append(",");
                                stringBuilder.append(list.getItemAtPosition(i));

                            }
                        }

                        /*Check string builder is empty or not. If string builder is not empty.
                          It will display on the screen.
                         */
                        if (stringBuilder.toString().trim().equals("")) {

                            stringBuilder.setLength(0);

                        } else {

                        }
                    }
                });

        builderDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        AlertDialog alert = builderDialog.create();
        alert.show();
    }

    public void populauteShoppingList(final ArrayList<GroceryList> groceryLists){
        ShowShoopingListAdapter showShoopingListAdapter=new ShowShoopingListAdapter(getContext(),groceryLists);
        listViewShopping.setAdapter(showShoopingListAdapter);
        listViewShopping.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startGroceryListOverview(groceryLists.get(i));
            }
        });
    }


    private void startGroceryListOverview(GroceryList item) {
        startActivity(new Intent(getActivity(),DetailsShoppingListActivity.class)
                .putExtra("GroceryList",item).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

    }
    public void populauteFinanceList(final ArrayList<FinanceRecords> financeRecordses){
        Calendar c=new GregorianCalendar();
        Date dat=c.getTime();
        String date = (String) android.text.format.DateFormat.format("dd-MM-yyyy", dat);
        ArrayList<FinanceRecords> recordses=new ArrayList<>();
        for (int k=0;k<financeRecordses.size();k++){
            if(financeRecordses.get(k).getRecordValueDate().equals(date)){
                recordses.add(financeRecordses.get(k));
            }
        }
        ShowFinanceAdapter financeAdapter=new ShowFinanceAdapter(getContext(),recordses);
        listViewFinance.setAdapter(financeAdapter);
        listViewFinance.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startFinanceRecordOverview(financeRecordses,i);
            }
        });
    }

    private void startFinanceRecordOverview(ArrayList<FinanceRecords> financeRecordses,int position) {
        FinanceRecords financeRecords=financeRecordses.get(position);
        startActivity(new Intent(getActivity(),ViewFinanceRecordsDetailsActivity.class)
                .putExtra("financeRecord",financeRecords)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

    }

    void showDialogListCalendarEvent(CalendarCollection collectionsevent){



        LayoutInflater inflater = getLayoutInflater(null);
        View convertView = (View) inflater.inflate(R.layout.calendar_event_details_show, null);
        alertDialog.setView(convertView);
        alertDialog.setTitle("Added Events");

        TextView txt = (TextView) convertView.findViewById(R.id.titeleventcalendar);
        txt.setText(collectionsevent.description );
        Button btn = (Button) convertView.findViewById(R.id.buttonOkarlertCalendareventshow);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();

            }
        });


        alertDialog.show();
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        /**
        mySQLiteHelper=new MySQLiteHelper(getContext());
        mAdapter = new MyRecyclerViewAdapter(((NewCalendarActivty)getActivity()), collectionArrayList, myClickListener);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        alertDialog = new android.support.v7.app.AlertDialog.Builder(getActivity()).create();
**/

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getView() != null) {
            isShown = true;
            // fetchdata() contains logic to show data when page is selected mostly asynctask to fill the data

        } else {
            isShown = false;

        }
    }

    @Override
    public void onResume() {
        super.onResume();

        financeAccountsArrayList=sqlFinanceAccount.getAllFinanceAccount();
        groceryListArrayList=sqLiteShoppingList.getAllShoppingList()[0];

        if(financeAccountsArrayList!=null &&financeAccountsArrayList.size()!=0){
            nameAccArray=new String[financeAccountsArrayList.size()];
            idAccArray=new String[financeAccountsArrayList.size()];
            for(int i=0;i<financeAccountsArrayList.size();i++){
                nameAccArray[i]= getContext().getString(R.string.View_account_accountName)+"  "+financeAccountsArrayList.get(i).getAccountName();
                idAccArray[i]=financeAccountsArrayList.get(i).getAccountUniqueId();
            }
        }


        populauteShoppingList(groceryListArrayList);
        populateSpinner();



/**
            collectionArrayList=getCalendarEvents(mySQLiteHelper.getAllIncomingNotification());
                prepareRecyclerView(getContext(),collectionArrayList);

        ((MyRecyclerViewAdapter) mAdapter).setOnItemClickListener(myClickListener);
**/

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    private ArrayList<CalendarCollection> getCalendarEvents(ArrayList<IncomingNotification> incomingNotifications){

        ArrayList<CalendarCollection> a =new ArrayList<>();
        for (int i=0;i<incomingNotifications.size();i++){
            JSONObject jo_inside = null;
            if(incomingNotifications.get(i).type==1){
                try {
                    jo_inside = new JSONObject(incomingNotifications.get(i).body);

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

                    CalendarCollection  object =new CalendarCollection(titel,infotext,creator,creationTime,startingtime,endingtime,eventHash,category,alldayevent,everymonth,creationdatetime);
                    object.incomingnotifictionid = incomingNotifications.get(i).id;
                    a.add(object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }
        return a;
    }


}
 class ShowShoopingListAdapter extends BaseAdapter {

    private Context context;


    private static LayoutInflater inflater=null;
    private ArrayList<GroceryList> groceryLists;

    public ShowShoopingListAdapter(Context oldContext, ArrayList<GroceryList> groceryLists)
    {
        context = oldContext;
        this.groceryLists = groceryLists;
        if(null != groceryLists) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

    }

    @Override
    public int getCount() {
        int i = 0;
        if(groceryLists != null)
            i = groceryLists.size();

        return i;
    }


    @Override
    public Object getItem(int position) {
        return groceryLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        final holder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.custom_overview_shopping, null);
            viewHolder = new holder();


            viewHolder.sortName = (TextView) convertView.findViewById(R.id.textView_sort_options_items_overview_shopping);
            viewHolder.balance=(TextView)convertView.findViewById(R.id.textView_sort_options_items_amount_overview_shopping);
            viewHolder.status=(TextView)convertView.findViewById(R.id.textView_sort_options_items_status_overview_shopping);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (holder) convertView.getTag();
        }

        String name=context.getResources().getString(R.string.grocery_list_item_title_text ).toLowerCase() + " " +groceryLists.get(position).getDatum();
        viewHolder.sortName.setText(name);
        String balance=groceryLists.get(position).getGroceryListTotalPriceToPayString()+" €";

            viewHolder.balance.setText(balance);
            viewHolder.balance.setTextColor(context.getResources().getColor(R.color.warning_color));
        if(!groceryLists.get(position).isListdone()){
            viewHolder.status.setText(context.getString(R.string.grocery_list_status__not_done_text));
        }

        return convertView;
    }



    static class holder {
        public TextView sortName;
        public TextView balance;
        public TextView status;

    }

}

 class ShowFinanceAdapter extends BaseAdapter {

     private Context context;


    private static LayoutInflater inflater=null;
    private ArrayList<FinanceRecords> accountsRecords;

    public ShowFinanceAdapter(Context oldContext, ArrayList<FinanceRecords> accountsRecords)
    {
        context = oldContext;
        this.accountsRecords = accountsRecords;
        if(null != accountsRecords) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

    }


    @Override
    public int getCount() {
        int i = 0;
        if(accountsRecords != null)
            i = accountsRecords.size();

        return i;
    }


    @Override
    public Object getItem(int position) {
        return accountsRecords.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        final holder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.custom_overview_finance, null);
            viewHolder = new holder();


            viewHolder.sortName = (TextView) convertView.findViewById(R.id.textView_sort_options_items_overview_finance);
            viewHolder.balance=(TextView)convertView.findViewById(R.id.textView_sort_options_items_amount_overview_finance);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (holder) convertView.getTag();
        }

        viewHolder.sortName.setText(accountsRecords.get(position).getRecordNAme());
        String balance=accountsRecords.get(position).getRecordAmount()+" €";
        if(!accountsRecords.get(position).isIncome()){
            balance="-"+balance;
            viewHolder.balance.setText(balance);
            viewHolder.balance.setTextColor( context.getResources().getColor(R.color.warning_color));
        }else {
            viewHolder.balance.setText(balance);
            viewHolder.balance.setTextColor(context.getResources().getColor(R.color.color_account_balance_positive));
        }


        return convertView;
    }



    static class holder {
        public TextView sortName;
        public TextView balance;

    }

}

