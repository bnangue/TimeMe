package com.example.bricenangue.timeme;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Switch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by bricenangue on 02/08/16.
 */
public class FinanceAccount implements Parcelable {
    private  Context context;
    private String accountName, accountUniqueId;
    private double accountBalance;
    private ArrayList<FinanceRecords> accountsRecords=new ArrayList<>();
    private ArrayList<String> accountOwners=new ArrayList<>();
    private ArrayList<FinanceAccount> myAccounts=new ArrayList<>();
    private String lastchangeToAccount,accountRecordsString,accountOwnersToString;


    protected FinanceAccount(Parcel in) {
        accountName = in.readString();
        accountUniqueId = in.readString();
        accountBalance = in.readDouble();
        accountsRecords = in.createTypedArrayList(FinanceRecords.CREATOR);
        accountOwners = in.createStringArrayList();
        myAccounts = in.createTypedArrayList(FinanceAccount.CREATOR);
        lastchangeToAccount = in.readString();
        accountRecordsString = in.readString();
        accountOwnersToString = in.readString();
    }

    public static final Creator<FinanceAccount> CREATOR = new Creator<FinanceAccount>() {
        @Override
        public FinanceAccount createFromParcel(Parcel in) {
            return new FinanceAccount(in);
        }

        @Override
        public FinanceAccount[] newArray(int size) {
            return new FinanceAccount[size];
        }
    };

    public void setAccountRecordsString(String accountRecordsString) {
        this.accountRecordsString = accountRecordsString;
        getRecords();
    }



    public String getLastChangeDateToAccount(){
        return lastchangeToAccount;
    }
    public String getLastchangeToAccount() {
        String lastChangeSince="";
        Calendar c = Calendar.getInstance();

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        formatter.setLenient(false);

        Date currentDate = new Date();


        String currentTime = formatter.format(currentDate);

        String[] datePartnow = currentTime.split("-");
        if(lastchangeToAccount==null || lastchangeToAccount.isEmpty()){
            return "";
        }
        String[] datePartlast = lastchangeToAccount.split("-");

        if(datePartnow[1].equals(datePartlast[1])){
            int daynow=Integer.parseInt(datePartnow[0]);
            int daylast=Integer.parseInt(datePartlast[0]);
            int difference=daynow -daylast;
            switch(difference){
                case 0:
                    lastChangeSince= context.getString(R.string.Today_Text);
                    break;
                case 1:
                    lastChangeSince= context.getString(R.string.A_Day_Ago_Text);

                    break;
                case 2:
                    lastChangeSince=String.valueOf(2)+" "+ context.getString(R.string.Days_Ago_Text);
                    break;
                case 3:
                    lastChangeSince=String.valueOf(3)+" "+ context.getString(R.string.Days_Ago_Text);

                    break;
                case 4:
                    lastChangeSince=String.valueOf(4)+" "+ context.getString(R.string.Days_Ago_Text);

                    break;
                case 5:
                    lastChangeSince=String.valueOf(5)+" "+ context.getString(R.string.Days_Ago_Text);

                    break;
                case 6:
                    lastChangeSince=String.valueOf(6)+" "+ context.getString(R.string.Days_Ago_Text);

                    break;
                case 7-10:
                    lastChangeSince=context.getString(R.string.A_Week_Ago_Text);

                    break;

                default:
                    lastChangeSince=context.getString(R.string.Weeks_Ago_Text);
                    break;
            }
        }else {
            lastChangeSince= context.getString(R.string.A_Month_Ago_Text);
        }

        return lastChangeSince;
    }


    public void setLastchangeToAccount() {
        Calendar c=new GregorianCalendar();
        Date dat=c.getTime();
        String date = (String) android.text.format.DateFormat.format("dd-MM-yyyy", dat);

        this.lastchangeToAccount = date;
    }


    public void setLastchangeToAccount(String lastchangeToAccount) {
        this.lastchangeToAccount = lastchangeToAccount;
    }
    public FinanceAccount(Context context){

        this.context=context;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public ArrayList<FinanceAccount> getMyAccounts() {
        return myAccounts;
    }

    public void setMyAccounts(ArrayList<FinanceAccount> myAccounts) {
        this.myAccounts = myAccounts;
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

    public ArrayList<FinanceRecords> getAccountsRecord() {
        return accountsRecords;
    }

    public void setAccountsRecord(ArrayList<FinanceRecords> accountsRecords) {
        this.accountsRecords = accountsRecords;
    }
    public void setAccountOwnersToString(String accountOwnersToString){
        this.accountOwnersToString=accountOwnersToString;
    }

    public String getAccountOwnersToString() {
        StringBuilder builder=new StringBuilder();
        if(accountOwners.size()!=0 ){
           for(String u :accountOwners){
               builder.append(u).append(" ");

           }
            accountOwnersToString=builder.toString();
            return accountOwnersToString;

        }else if(accountRecordsString!=null && !accountOwnersToString.isEmpty()){

            return this.accountOwnersToString;
        }
        return "No owner";
    }

    public String getAccountBlanceTostring(){
        DecimalFormat df = new DecimalFormat("0.00");
        df.setMaximumFractionDigits(2);

        String priceStr = df.format(accountBalance);

        return priceStr;
    }

    public void setAccountBalanceToString(String balance){
        DecimalFormat df = new DecimalFormat("0.00");
        df.setMaximumFractionDigits(2);

        String priceStr = balance;

        try {
            Number nm=df.parse(priceStr);

            accountBalance=nm.doubleValue();
            setAccountBalance(accountBalance);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    public ArrayList<String> getAccountOwners(){
        return this.accountOwners;
    }
    public void setAccountOwners(ArrayList<String> accountOwners) {
        this.accountOwners = accountOwners;
    }
    public void addRecordToAccount(FinanceRecords records){
        accountsRecords.add(records);

        if(records.isIncome()){
            if(checkBookingDate(records.getRecordBookingDate())){
                accountBalance = accountBalance + toDouble(records.getRecordAmount());
                getAccountBlanceTostring();
            }

        }else {
            if(records.getRecordNAme().equals("Grocery list")){
                if(records.isSecured()){
                    double d=toDouble(records.getRecordAmount());
                    accountBalance = accountBalance - d;
                    getAccountBlanceTostring();
                }
            }else {
                if(checkBookingDate(records.getRecordBookingDate())){
                    double d=toDouble(records.getRecordAmount());
                    accountBalance = accountBalance - d;
                    getAccountBlanceTostring();
                }
            }

        }

    }

    public void getAccountrecordsAmountUpdateBalance(){
        accountBalance=0;
        if(accountsRecords.size()!=0){
            for (int i=0; i<accountsRecords.size();i++){
                FinanceRecords records=accountsRecords.get(i);
                if(records.isIncome()){
                    if(checkBookingDate(records.getRecordBookingDate())){
                        accountBalance = accountBalance + toDouble(records.getRecordAmount());
                        getAccountBlanceTostring();
                    }

                }else {
                    if(records.getRecordNAme().equals("Grocery list")){
                        if(records.isSecured()){
                            double d=toDouble(records.getRecordAmount());
                            accountBalance = accountBalance - d;
                            getAccountBlanceTostring();
                        }
                    }else {
                        if(checkBookingDate(records.getRecordBookingDate())){
                            double d=toDouble(records.getRecordAmount());
                            accountBalance = accountBalance - d;
                            getAccountBlanceTostring();
                        }
                    }

                }
            }
        }
    }

    private boolean checkBookingDate(String bookingDate){
        Calendar c=new GregorianCalendar();
        Date dat=c.getTime();
        String date = (String) android.text.format.DateFormat.format("dd-MM-yyyy", dat);
        String[] dateArray=date.split("-");

        String[] datePartnow = date.split("-");
        if(bookingDate==null || bookingDate.isEmpty()){
            return true;
        }
        String[] datePartlast = bookingDate.split("-");

        if(datePartnow[1].equals(datePartlast[1])){
            int daynow=Integer.parseInt(datePartnow[0]);
            int dayrecord=Integer.parseInt(datePartlast[0]);

            if(daynow>=dayrecord){
                return true;
            }else {
                return false;
            }

        }else {
            int monthnow=Integer.parseInt(datePartnow[1]);
            int monthrecord=Integer.parseInt(datePartlast[1]);
            if(monthnow >= monthrecord){
                return true;
            }else {
                return false;
            }
        }
    }

    public double toDouble(String value){
        double dValue=0;
        DecimalFormat df = new DecimalFormat("0.00");
        df.setMaximumFractionDigits(2);

        try {
            Number nm=df.parse(value.replace(",","."));

            dValue=Double.parseDouble(value.replace(",","."));
            //dValue=nm.doubleValue();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dValue;
    }
    public void removeRecord(FinanceRecords records){
        accountsRecords.remove(records);
        getAccountRecordsString();
    }

    public boolean updateAccount(FinanceAccount account){
        if(accountUniqueId.equals(account.getAccountUniqueId())){
            accountName=account.getAccountName();
            accountBalance=account.getAccountBalance();
            accountBalance=account.getAccountBalance();
            accountsRecords=account.getAccountsRecord();
            accountOwners=account.getAccountOwners();
            return true;
        }
       return false;
    }

    public boolean removeAccount(FinanceAccount account){
        if(myAccounts.contains(account)){
            myAccounts.remove(account);

            return true;
        }
        return false;
    }

    public boolean createAccount(FinanceAccount account){
        if(!myAccounts.contains(account)){
            myAccounts.add(account);

            return true;
        }
        return false;
    }
    public boolean initAccount(Context context,FinanceAccount account){
        if(myAccounts.contains(account)){
            FinanceAccount financeAccount=new FinanceAccount(context);
            financeAccount.setAccountName(account.getAccountName());
            financeAccount.setAccountUniqueId(account.getAccountUniqueId());
            financeAccount.setAccountBalance(0);

            myAccounts.remove(account);



            return true;
        }
        return false;
    }


    public String getAccountRecordsString() {
        String accountRecordsString="";

        if(accountsRecords.size()!=0){
            try {

                JSONObject json = new JSONObject();

                JSONArray jsonArray = new JSONArray();
                for (int i=0; i < accountsRecords.size(); i++) {

                    jsonArray.put(accountsRecords.get(i).getJSONObjectFINANCErecords());
                }
                json.put("accountRecordsString", jsonArray);

                accountRecordsString=json.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return accountRecordsString;
    }

    public ArrayList<FinanceRecords> getRecords(){
        ArrayList<FinanceRecords> items=new ArrayList<>();

        if(accountRecordsString!=null && !accountRecordsString.isEmpty()){
            JSONObject json = null;
            try {
                json = new JSONObject(accountRecordsString);
                JSONArray array = json.getJSONArray("accountRecordsString");
                for(int i =0; i <array.length();i++){
                    items.add( new FinanceRecords().getFinanceRecordsFromJSONObject(array.getJSONObject(i)));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        setAccountsRecord(items);
        return items;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(accountName);
        parcel.writeString(accountUniqueId);
        parcel.writeDouble(accountBalance);
        parcel.writeTypedList(accountsRecords);
        parcel.writeStringList(accountOwners);
        parcel.writeTypedList(myAccounts);
        parcel.writeString(lastchangeToAccount);
        parcel.writeString(accountRecordsString);
        parcel.writeString(accountOwnersToString);
    }
}
