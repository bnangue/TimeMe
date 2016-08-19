package com.app.bricenangue.timeme;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by bricenangue on 21/07/16.
 */
public class ShoppingList implements Parcelable{
    private ArrayList<ShoppingItem> items=new ArrayList<>();
    private boolean listisDone=false;
    private String listCreatorName;
    private String listcreationdate;
    private String listtoUseDate;
    private int listId;


    protected ShoppingList(Parcel in) {
        listisDone = in.readByte() != 0;
        listCreatorName = in.readString();
        listcreationdate = in.readString();
        listtoUseDate = in.readString();
        listId = in.readInt();
    }

    public static final Creator<ShoppingList> CREATOR = new Creator<ShoppingList>() {
        @Override
        public ShoppingList createFromParcel(Parcel in) {
            return new ShoppingList(in);
        }

        @Override
        public ShoppingList[] newArray(int size) {
            return new ShoppingList[size];
        }
    };

    public ArrayList<ShoppingItem> getItems() {
        return items;
    }

    public void setItems(ArrayList<ShoppingItem> items) {
        this.items = items;
    }

    public void addItemsToList(ShoppingItem item){
        items.add(item);
    }
    public boolean isListisDone() {
        return listisDone;
    }

    public void setListisDone(boolean listisDone) {
        this.listisDone = listisDone;
    }

    public String getListCreatorName() {
        return listCreatorName;
    }

    public void setListCreatorName(String listCreatorName) {
        this.listCreatorName = listCreatorName;
    }

    public String getListcreationdate() {
        return listcreationdate;
    }

    public void setListcreationdate(String listcreationdate) {
        this.listcreationdate = listcreationdate;
    }

    public String getListtoUseDate() {
        return listtoUseDate;
    }

    public void setListtoUseDate(String listtoUseDate) {
        this.listtoUseDate = listtoUseDate;
    }

    public int getListId() {
        return listId;
    }

    public void setListId(int listId) {
        this.listId = listId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getListtotalCost(){
        String tPrice="0";
        int p=0;
        if(items.size()!=0){
            for(int i=0;i<items.size();i++){
                p = p + Integer.parseInt(items.get(i).getPrice());
            }
            tPrice=String.valueOf(p);
        }
        return tPrice;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (listisDone ? 1 : 0));
        dest.writeString(listCreatorName);
        dest.writeString(listcreationdate);
        dest.writeString(listtoUseDate);
        dest.writeInt(listId);
    }
}
