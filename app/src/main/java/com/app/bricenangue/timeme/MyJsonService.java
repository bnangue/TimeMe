package com.app.bricenangue.timeme;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
/**
 * Created by bricenangue on 14/03/16.
 */
public interface MyJsonService {
    @GET("/1kpjf")
    void listEvents(Callback<List<Event>> eventsCallback) ;
}
