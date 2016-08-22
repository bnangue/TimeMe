package com.app.bricenangue.timeme;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentCategoryShoppingInteractionListener} interface
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class FragmentCategoryShopping extends Fragment implements View.OnClickListener{

    private String mParam1;
    private String mParam2;
    private TextView textBalance;
    private Button add_List;
    private boolean isExpanded = false;
    private float mCurrentRotation = 360.0f;
    private android.support.v7.app.AlertDialog alertDialog;
    private boolean[] selectedOnRows;


    private RecyclerAdapterSmallCards.MyRecyclerAdaptaterCreateShoppingListClickListener myClickListener;
    private RecyclerAdapterSmallCards.MyRecyclerAdaptaterCreateShoppingListDoneClickListener myDoneClickListener;

    private RecyclerView mRecyclerView,mRecyclerViewdone;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private MySQLiteHelper mySQLiteHelper;
    private SQLiteShoppingList sqLiteShoppingList;
    private OnFragmentCategoryShoppingInteractionListener mListener;
    private static String LOG_TAG = "RecyclerViewActivity_Fragment_Shopping";
    private Fragment fragment=this;
    private ArrayList<GroceryList> shoppingListdone = new ArrayList<>();
    private ArrayList<GroceryList> shoppingListsrecent = new ArrayList<>();

    private boolean isShown=false;
    private OnCalendarEventsChanged calendarEventsChanged;
    private UserLocalStore userLocalStore;
    private TextView textshowHide;
    private LinearLayout linearLayout;
    private SQLFinanceAccount sqlFinanceAccount;
    private String sortName;


    private void prepareRecyclerView(Context context,ArrayList<GroceryList> arrayList){
        mRecyclerView.setVisibility(View.VISIBLE);
        mAdapter = new RecyclerAdapterSmallCards(((NewCalendarActivty)getActivity()),arrayList,myClickListener,myDoneClickListener,false);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

    }

    private void prepareRecyclerViewdone(Context context,ArrayList<GroceryList> arrayList){

        mRecyclerViewdone.setVisibility(View.VISIBLE);
        mAdapter = new RecyclerAdapterSmallCards(((NewCalendarActivty)getActivity()),arrayList,myClickListener,myDoneClickListener,true);
        mRecyclerViewdone.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerViewdone.setLayoutManager(mLayoutManager);
        mRecyclerViewdone.setAdapter(mAdapter);

    }

    public FragmentCategoryShopping() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }



    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //Save the fragment's state here
        outState.putParcelableArrayList("list_recent",shoppingListsrecent);
        outState.putParcelableArrayList("list_done",shoppingListdone);
    }


    private ArrayList<GroceryList> getGroceryList(){
        return sqLiteShoppingList.getAllShoppingList()[0];
    }
    private ArrayList<GroceryList> getGroceryListDone(){
        return sqLiteShoppingList.getAllShoppingList()[1];
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
      //  getEvents(mySQLiteHelper.getAllIncomingNotification());
        // Inflate the layout for this fragment
        sqLiteShoppingList=new SQLiteShoppingList(getContext());
        userLocalStore=new UserLocalStore(getContext());
        sqlFinanceAccount=new SQLFinanceAccount(getContext());
        alertDialog = new android.support.v7.app.AlertDialog.Builder(getContext()).create();

        View v = inflater.inflate(R.layout.fragment_grocery_list, container, false);
        textBalance = (TextView) v.findViewById(R.id.grocery_fragment_balance_amount);
        textshowHide = (TextView) v.findViewById(R.id.text_grocery_fragment_show);

        add_List = (Button) v.findViewById(R.id.grocery_fragment_add_recently_button);




       // linearLayout=(LinearLayout)v.findViewById(R.id.text_grocery_fragment_done_hide_layout);
        mRecyclerViewdone = (RecyclerView) v.findViewById(R.id.shoppingrecycleViewfragment_grocery_list_done_lists);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.shoppingrecycleViewfragöment_grocery_list_recentlityl_added);


        myClickListener=new RecyclerAdapterSmallCards.MyRecyclerAdaptaterCreateShoppingListClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                startGroceryListOverview(shoppingListsrecent.get(position));
            }

        };

        myDoneClickListener=new RecyclerAdapterSmallCards.MyRecyclerAdaptaterCreateShoppingListDoneClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                startGroceryListOverview(shoppingListdone.get(position));
            }

        };


       final ImageView arrow = (ImageView) v.findViewById(R.id.arrow_grocery_fragment_done_hide_text);


        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (isExpanded) {
                    RotateAnimation anim = new RotateAnimation(mCurrentRotation, mCurrentRotation + 180.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    mCurrentRotation = (mCurrentRotation + 180.0f) % 360.0f;
                    anim.setInterpolator(new LinearInterpolator());
                    anim.setFillAfter(true);
                    anim.setFillEnabled(true);
                    anim.setDuration(300);
                    assert arrow != null;
                    arrow.startAnimation(anim);
                    isExpanded = false;
                    textshowHide.setText(getContext().getString(R.string.grocery_fragment_list_hide));
                    mRecyclerViewdone.setVisibility(View.VISIBLE);
                } else {
                    RotateAnimation anim = new RotateAnimation(mCurrentRotation, mCurrentRotation - 180.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    mCurrentRotation = (mCurrentRotation - 180.0f) % 360.0f;
                    anim.setInterpolator(new LinearInterpolator());
                    anim.setFillAfter(true);
                    anim.setFillEnabled(true);
                    anim.setDuration(300);
                    assert arrow != null;
                    arrow.startAnimation(anim);
                    isExpanded = true;
                    textshowHide.setText(getContext().getString(R.string.grocery_fragment_list_show));
                    mRecyclerViewdone.setVisibility(View.GONE);

                }

            }
        });
        add_List.setOnClickListener(this);
        if(userLocalStore.getUserAccountBalance().contains("-")){
            textBalance.setText(userLocalStore.getUserAccountBalance()+" €");
            textBalance.setTextColor(getResources().getColor(R.color.warning_color));
        }else {
            textBalance.setText(userLocalStore.getUserAccountBalance()+" €");
        }




        return v;
    }

    private void startGroceryListOverview(GroceryList item) {
        startActivity(new Intent(getActivity(),DetailsShoppingListActivity.class)
                .putExtra("GroceryList",item).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));

    }

    private ArrayList[] getShoppingListsformDB(){
        return sqLiteShoppingList.getAllShoppingList();
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mySQLiteHelper=new MySQLiteHelper(getContext());
        //prepareListview
        if(savedInstanceState!=null){
            shoppingListdone=savedInstanceState.getParcelableArrayList("list_done");
            shoppingListsrecent=savedInstanceState.getParcelableArrayList("list_recent");
        }

    }

    public void onButtonDeleteGroceryListPressed(GroceryList groceryList, int position,ArrayList<GroceryList> groceryLists) {
        if (mListener != null) {
            mListener.onFragmentCategoryShoppingInteraction(groceryList,position,groceryLists);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentCategoryShoppingInteractionListener) {
            mListener = (OnFragmentCategoryShoppingInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentCategoryShoppingInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onClick(View v) {

        int id=v.getId();
        switch (id){
            case R.id.grocery_fragment_add_recently_button:
                //Create new shopping list
               //showDialogChoosingAccount();
                startActivity(new Intent(getActivity(),CreateNewShoppingListActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                break;


        }
    }




    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentCategoryShoppingInteractionListener {
        // TODO: Update argument type and name
        void onFragmentCategoryShoppingInteraction(GroceryList groceryList,int position,ArrayList<GroceryList> groceryLists);
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        calendarEventsChanged =(OnCalendarEventsChanged)getActivity();

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
        //bing listener
        shoppingListdone=getGroceryListDone();
        shoppingListsrecent=getGroceryList();
        prepareRecyclerView(getContext(),shoppingListsrecent);
        prepareRecyclerViewdone(getContext(),shoppingListdone);


        if(userLocalStore.getUserAccountBalance().contains("-")){
            textBalance.setText(userLocalStore.getUserAccountBalance()+" €");
            textBalance.setTextColor(getResources().getColor(R.color.warning_color));
        }else {
            textBalance.setText(userLocalStore.getUserAccountBalance()+" €");
        }
    ((RecyclerAdapterSmallCards) mAdapter).setOnshoppinglistsmallClickListener(myClickListener,myDoneClickListener);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void updateUi(ArrayList<CalendarCollection> arrayList){
        ArrayList<CalendarCollection> a=new ArrayList<>();
        for(int i=0;i<arrayList.size();i++){
            if(arrayList.get(i).category.contains("Grocery")){
                a.add(arrayList.get(i));
            }
        }
    }

}
