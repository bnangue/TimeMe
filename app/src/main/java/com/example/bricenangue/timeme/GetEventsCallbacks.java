package com.example.bricenangue.timeme;

import java.util.ArrayList;

/**
 * Created by bricenangue on 01/02/16.
 */
interface GetEventsCallbacks {
    public abstract void done(ArrayList<CalendarCollection> returnedeventobject);
    void updated(String reponse);

}
