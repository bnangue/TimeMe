package com.app.bricenangue.timeme;

import java.util.ArrayList;

/**
 * Created by bricenangue on 06/09/16.
 */
public class GroceryListForFireBase {
    private String datum;
    private boolean isListdone=false;
    private String creatorName;
    private String list_unique_id;
    private String listcontain;
    private boolean isToListshare=false;
    private String accountid;
    private ArrayList<ShoppingItemForFireBase> items=new ArrayList<>();


    public GroceryListForFireBase() {

    }

    public String getDatum() {
        return datum;
    }

    public void setDatum(String datum) {
        this.datum = datum;
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

    public String getListcontain() {
        return listcontain;
    }

    public void setListcontain(String listcontain) {
        this.listcontain = listcontain;
    }

    public String getAccountid() {
        return accountid;
    }

    public void setAccountid(String accountid) {
        this.accountid = accountid;
    }

    public boolean isToListshare() {
        return isToListshare;
    }

    public void setToListshare(boolean toListshare) {
        isToListshare = toListshare;
    }

    public ArrayList<ShoppingItemForFireBase> getItems() {
        return items;
    }

    public void setItems(ArrayList<ShoppingItemForFireBase> items) {
        this.items = items;
    }
}
