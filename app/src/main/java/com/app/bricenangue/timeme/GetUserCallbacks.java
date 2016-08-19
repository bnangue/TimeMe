package com.app.bricenangue.timeme;

import java.util.ArrayList;

/**
 * Created by alex on 17.01.2016.
 */
public interface GetUserCallbacks {
    public abstract void done(User returneduser);
    public abstract void serverReponse(String reponse);
    public abstract void userlist(ArrayList<User> reponse);



}
