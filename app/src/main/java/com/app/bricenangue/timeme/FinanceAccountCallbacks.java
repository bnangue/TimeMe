package com.app.bricenangue.timeme;

import java.util.ArrayList;

/**
 * Created by bricenangue on 03/08/16.
 */
public interface FinanceAccountCallbacks {
    public abstract void fetchDone(ArrayList<FinanceAccount> returnedAccounts);

    void setServerResponse(String serverResponse);
}
