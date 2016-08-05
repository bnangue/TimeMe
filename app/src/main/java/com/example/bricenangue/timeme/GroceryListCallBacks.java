package com.example.bricenangue.timeme;

import java.util.ArrayList;

/**
 * Created by bricenangue on 30/07/16.
 */
public interface GroceryListCallBacks {

    public abstract void fetchDone(ArrayList<GroceryList> returnedGroceryLists);

    void setServerResponse(String serverResponse);
}
