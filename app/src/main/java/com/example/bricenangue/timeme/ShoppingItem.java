package com.example.bricenangue.timeme;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by bricenangue on 21/07/16.
 */
public class ShoppingItem implements Parcelable{
    private String itemName;
    private String price;
    private String detailstoItem;
    private int numberofItemsetForList;
    private String unique_item_id;
    private String itemSpecification;
    private boolean itemIsBought=false;

    protected ShoppingItem(Parcel in) {
        itemName = in.readString();
        price = in.readString();
        detailstoItem = in.readString();
        unique_item_id = in.readString();
        itemSpecification = in.readString();
        numberofItemsetForList = in.readInt();
        itemIsBought = in.readByte() != 0;
    }



    public JSONObject getShoppingItemJSONObject() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("itemName", itemName);
            obj.put("price", price);
            if(!detailstoItem.isEmpty() && detailstoItem!=null){
                obj.put("detailstoItem", detailstoItem);
            }else{
                obj.put("detailstoItem", "no description aviable");
            }

            obj.put("unique_item_id", unique_item_id);
            obj.put("itemSpecification", itemSpecification);
            obj.put("numberofItemsetForList", numberofItemsetForList);
            obj.put("itemIsBought", itemIsBought);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public ShoppingItem getShoppingItemFromJSONObject(JSONObject obj) {
       ShoppingItem item= new ShoppingItem();
        try {
            item.setItemName(obj.getString("itemName"));
            item.setPrice(obj.getString("price"));
            item.setDetailstoItem(obj.getString("detailstoItem"));
            item.setUnique_item_id(obj.getString("unique_item_id"));
            item.setItemSpecification(obj.getString("itemSpecification"));

            item.setNumberofItemsetForList( obj.optInt("numberofItemsetForList"));
            item.setItemIsBought(obj.getBoolean("itemIsBought"));


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return item;
    }

    public String getUnique_item_id() {
        return unique_item_id;
    }

    public void setUnique_item_id(String unique_item_id) {
        this.unique_item_id = unique_item_id;
    }
    public String getItemSpecification() {
        return itemSpecification;
    }

    public void setItemSpecification(String itemSpecification) {
        this.itemSpecification = itemSpecification;
    }

    public ShoppingItem(){

    }
    public static final Creator<ShoppingItem> CREATOR = new Creator<ShoppingItem>() {
        @Override
        public ShoppingItem createFromParcel(Parcel in) {
            return new ShoppingItem(in);
        }

        @Override
        public ShoppingItem[] newArray(int size) {
            return new ShoppingItem[size];
        }
    };

    public int getNumberofItemsetForList() {
        return numberofItemsetForList;
    }

    public void setNumberofItemsetForList(int numberofItemsetForList) {
        this.numberofItemsetForList = numberofItemsetForList;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDetailstoItem() {
        return detailstoItem;
    }

    public void setDetailstoItem(String detailstoItem) {
        this.detailstoItem = detailstoItem;
    }

    public boolean isItemIsBought() {
        return itemIsBought;
    }

    public void setItemIsBought(boolean itemIsBought) {
        this.itemIsBought = itemIsBought;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(itemName);
        dest.writeString(price);
        dest.writeString(detailstoItem);
        dest.writeString(unique_item_id);
        dest.writeString(itemSpecification);

        dest.writeInt(numberofItemsetForList);
        dest.writeByte((byte) (itemIsBought ? 1 : 0));
    }
}
