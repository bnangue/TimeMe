package com.example.bricenangue.timeme;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentCategoryShopping.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentCategoryShopping#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentCategoryShopping extends Fragment implements DialogDeleteEventFragment.OnDeleteListener ,FragmentLife, View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TextView textBalance;
    private Button add_List;

    private RecyclerAdapterSmallCards.MyRecyclerAdaptaterCreateShoppingListClickListener myClickListener;
    private RecyclerAdapterSmallCards.MyRecyclerAdaptaterCreateShoppingListDoneClickListener myDoneClickListener;

    private RecyclerView mRecyclerView,mRecyclerViewdone;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private MySQLiteHelper mySQLiteHelper;
    private SQLiteShoppingList sqLiteShoppingList;
    private OnFragmentInteractionListener mListener;
    private static String LOG_TAG = "RecyclerViewActivity_Fragment_Shopping";
    private Fragment fragment=this;
    private ArrayList<GroceryList> shoppingListdone = new ArrayList<>();
    private ArrayList<GroceryList> shoppingListsrecent = new ArrayList<>();

    private boolean isShown=false;
    private OnCalendarEventsChanged calendarEventsChanged;

    private void prepareRecyclerView(Context context,ArrayList<GroceryList> arrayList){
        mRecyclerView.setVisibility(View.VISIBLE);
        mAdapter = new RecyclerAdapterSmallCards(context,arrayList,myClickListener,myDoneClickListener,false);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

    }

    private void prepareRecyclerViewdone(Context context,ArrayList<GroceryList> arrayList){

        mRecyclerViewdone.setVisibility(View.VISIBLE);
        mAdapter = new RecyclerAdapterSmallCards(context,arrayList,myClickListener,myDoneClickListener,true);
        mRecyclerViewdone.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerViewdone.setLayoutManager(mLayoutManager);
        mRecyclerViewdone.setAdapter(mAdapter);

    }

    public FragmentCategoryShopping() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentCategoryShopping.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentCategoryShopping newInstance(String param1, String param2) {
        FragmentCategoryShopping fragment = new FragmentCategoryShopping();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

        }
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
        View v = inflater.inflate(R.layout.fragment_grocery_list, container, false);
        textBalance = (TextView) v.findViewById(R.id.grocery_fragment_balance_amount);
        add_List = (Button) v.findViewById(R.id.grocery_fragment_add_recently_button);

        mRecyclerViewdone = (RecyclerView) v.findViewById(R.id.shoppingrecycleViewfragment_grocery_list_done_lists);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.shoppingrecycleViewfragöment_grocery_list_recentlityl_added);


        myClickListener=new RecyclerAdapterSmallCards.MyRecyclerAdaptaterCreateShoppingListClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                startGroceryListOverview(shoppingListsrecent.get(position));
            }

            @Override
            public void onButtonClick(int position, View v) {
                int iD = v.getId();
                switch (iD) {
                    case R.id.buttondeletecardview_create_shopping_list_small_card:
                        DialogFragment dialogFragment = DialogDeleteEventFragment.newInstance(position);
                        dialogFragment.setCancelable(false);
                        dialogFragment.setTargetFragment(fragment, 0);
                        dialogFragment.show(getActivity().getSupportFragmentManager(), "DELETESHOPPINGFRAGLISTFRAGMENT");

                        break;

                }
            }
        };

        myDoneClickListener=new RecyclerAdapterSmallCards.MyRecyclerAdaptaterCreateShoppingListDoneClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                startGroceryListOverview(shoppingListdone.get(position));
            }

            @Override
            public void onButtonClick(int position, View v) {
                int iD = v.getId();
                switch (iD) {
                    case R.id.buttondeletecardview_create_shopping_list_small_card:
                        DialogFragment dialogFragment = DialogDeleteEventFragment.newInstance(position);
                        dialogFragment.setCancelable(false);
                        dialogFragment.setTargetFragment(fragment, 2);
                        dialogFragment.show(getActivity().getSupportFragmentManager(), "DELETESHOPPINGFRAGLISTDONEFRAGMENT");

                        break;

                }
            }
        };


        add_List.setOnClickListener(this);

        return v;
    }

    private void startGroceryListOverview(GroceryList item) {
        startActivity(new Intent(getActivity(),DetailsShoppingListActivity.class).putExtra("GroceryList",item).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));

    }

    private ArrayList[] getShoppingListsformDB(){
        return sqLiteShoppingList.getAllShoppingList();
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mySQLiteHelper=new MySQLiteHelper(getContext());
        //prepareListview

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onUpdateUi(ArrayList<CalendarCollection> arrayList,String uName) {
        updateUi(arrayList);
    }

    @Override
    public void onClick(View v) {

        int id=v.getId();
        switch (id){
            case R.id.grocery_fragment_add_recently_button:
                //Create new shopping list
                startActivity(new Intent(getActivity(),CreateNewShoppingListActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                break;
        }
    }

    @Override
    public void delete(int position) {

        Toast.makeText(getContext(),"from fragment",Toast.LENGTH_SHORT).show();
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
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
