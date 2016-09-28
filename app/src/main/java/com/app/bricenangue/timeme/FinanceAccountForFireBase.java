package com.app.bricenangue.timeme;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by bricenangue on 25/09/16.
 */

public class FinanceAccountForFireBase {
    private String accountName;
    private String accountUniqueId;
    private double accountBalance;
    private ArrayList<FinanceRecordsForFireBase> accountsRecords=new ArrayList<>();
    private ArrayList<UserForFireBase> accountOwners=new ArrayList<>();
    private String lastchangeToAccount;
    private String accountRecordsString;
    private  String accountOwnersToString;

    public FinanceAccountForFireBase() {
    }


    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountUniqueId() {
        return accountUniqueId;
    }

    public void setAccountUniqueId(String accountUniqueId) {
        this.accountUniqueId = accountUniqueId;
    }

    public double getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(double accountBalance) {
        this.accountBalance = accountBalance;
    }

    public ArrayList<FinanceRecordsForFireBase> getAccountsRecords() {
        return accountsRecords;
    }

    public void setAccountsRecords(ArrayList<FinanceRecordsForFireBase> accountsRecords) {
        this.accountsRecords = accountsRecords;
    }

    public ArrayList<UserForFireBase> getAccountOwners() {
        return accountOwners;
    }

    public void setAccountOwners(ArrayList<UserForFireBase> accountOwners) {
        this.accountOwners = accountOwners;
    }

    public String getLastchangeToAccount() {
        return lastchangeToAccount;
    }

    public void setLastchangeToAccount(String lastchangeToAccount) {
        this.lastchangeToAccount = lastchangeToAccount;
    }

    public String getAccountRecordsString() {
        return accountRecordsString;
    }

    public void setAccountRecordsString(String accountRecordsString) {
        this.accountRecordsString = accountRecordsString;
    }

    public String getAccountOwnersToString() {
        return accountOwnersToString.trim();
    }

    public void setAccountOwnersToString(String accountOwnersToString) {
        this.accountOwnersToString = accountOwnersToString;
    }
}
