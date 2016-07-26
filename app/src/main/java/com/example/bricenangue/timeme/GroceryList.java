package com.example.bricenangue.timeme;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by bricenangue on 25/07/16.
 */
public class GroceryList implements Parcelable {
    private String datum;
    private ArrayList<ShoppingItem> itemsOftheList=new ArrayList<>();
    private boolean isListdone=false;
    private String creatorName;
    private String list_unique_id;
    private String listcontain;
    private boolean isToListshare=false;

    public GroceryList() {
    }

    protected GroceryList(Parcel in) {
        datum = in.readString();
        itemsOftheList = in.createTypedArrayList(ShoppingItem.CREATOR);
        isListdone = in.readByte() != 0;
        creatorName = in.readString();
        listcontain = in.readString();
        list_unique_id = in.readString();
        isToListshare = in.readByte() != 0;
    }

    public static final Creator<GroceryList> CREATOR = new Creator<GroceryList>() {
        @Override
        public GroceryList createFromParcel(Parcel in) {
            return new GroceryList(in);
        }

        @Override
        public GroceryList[] newArray(int size) {
            return new GroceryList[size];
        }
    };

    public String getDatum() {
        return datum;
    }

    public void setDatum(String datum) {
        this.datum = datum;
    }

    public ArrayList<ShoppingItem> getItemsOftheList() {
        return itemsOftheList;
    }

    public void setItemsOftheList(ArrayList<ShoppingItem> itemsOftheList) {
        this.itemsOftheList = itemsOftheList;
    }

    public boolean isListdone() {
        return isListdone;
    }

    public void setListdone(boolean listdone) {
        isListdone = listdone;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getList_unique_id() {
        return list_unique_id;
    }

    public void setList_unique_id(String list_unique_id) {
        this.list_unique_id = list_unique_id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(datum);
        dest.writeTypedList(itemsOftheList);
        dest.writeByte((byte) (isListdone ? 1 : 0));
        dest.writeString(creatorName);
        dest.writeString(listcontain);
        dest.writeString(list_unique_id);
        dest.writeByte((byte) (isToListshare ? 1 : 0));

    }

    /**
     *
     * @return true if list is to be share with partner. Automatically synch with server mysql und sql
     */
    public boolean isToListshare() {
        return isToListshare;
    }

    public void setToListshare(boolean toListshare) {
        isToListshare = toListshare;
    }

    public String getListcontain() {
        String listcontain="";

        try {

            JSONObject json = new JSONObject();

            JSONArray jsonArray = new JSONArray();
            for (int i=0; i < itemsOftheList.size(); i++) {

                jsonArray.put(itemsOftheList.get(i).getShoppingItemJSONObject());
            }
            json.put("list_contain", jsonArray);

            listcontain=json.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        setListcontain(listcontain);
        return listcontain;
    }

    public ArrayList<ShoppingItem> getListItems(){
        ArrayList<ShoppingItem> items=new ArrayList<>();

        JSONObject json = null;
        try {
            json = new JSONObject(listcontain);
            JSONArray array = json.getJSONArray("list_contain");
            for(int i =0; i <array.length();i++){
                items.add( new ShoppingItem().getShoppingItemFromJSONObject(array.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        setItemsOftheList(items);
        return items;
    }


    public void setListcontain( String listcontain) {
        this.listcontain = listcontain;
    }

    public boolean allItemsbought(){
        boolean allbought =true;
        for(int i =0;i<itemsOftheList.size();i++){
            if(!itemsOftheList.get(i).isItemIsBought()){
                allbought=false;
            }
        }
        setListdone(allbought);
        return allbought;
    }
}
