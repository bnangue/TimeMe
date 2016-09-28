package com.app.bricenangue.timeme;

/**
 * Created by bricenangue on 20/09/16.
 */
public class ShoppingItemForFireBase {
    private String itemName;
    private String price;
    private String detailstoItem;
    private int numberofItemsetForList;
    private int numberoftimeAddedAnyToList;
    private String unique_item_id;
    private String itemSpecification;
    private String itemcategory;
    private String itemmarket;
    private boolean itemIsBought=false;

    public ShoppingItemForFireBase() {

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

    public int getNumberofItemsetForList() {
        return numberofItemsetForList;
    }

    public void setNumberofItemsetForList(int numberofItemsetForList) {
        this.numberofItemsetForList = numberofItemsetForList;
    }

    public int getNumberoftimeAddedAnyToList() {
        return numberoftimeAddedAnyToList;
    }

    public void setNumberoftimeAddedAnyToList(int numberoftimeAddedAnyToList) {
        this.numberoftimeAddedAnyToList = numberoftimeAddedAnyToList;
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

    public String getItemcategory() {
        return itemcategory;
    }

    public void setItemcategory(String itemcategory) {
        this.itemcategory = itemcategory;
    }

    public String getItemmarket() {
        return itemmarket;
    }

    public void setItemmarket(String itemmarket) {
        this.itemmarket = itemmarket;
    }

    public boolean isItemIsBought() {
        return itemIsBought;
    }

    public void setItemIsBought(boolean itemIsBought) {
        this.itemIsBought = itemIsBought;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
}
