package com.app.bricenangue.timeme;

import java.util.ArrayList;

/**
 * Created by bricenangue on 01/02/16.
 */
interface GetEventsCallbacks {
    public abstract void done(ArrayList<CalendarCollection> returnedeventobject);
    public abstract void itemslis(ArrayList<ShoppingItem> returnedShoppingItem);
    void updated(String reponse);

}
