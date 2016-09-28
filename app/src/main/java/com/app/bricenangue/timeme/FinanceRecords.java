package com.app.bricenangue.timeme;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by bricenangue on 01/08/16.
 */
public class FinanceRecords implements Parcelable {
    private  Context context;
    private String recordNAme, recordValueDate,recordNote,recordAmount,recordUniquesId,recordCategorie,recordBookingDate,recordCreator,recordDataPath;
    private int recordUpdateVersion;
    private Bitmap recordData;
    private boolean isSecured=false, isIncome;


    public FinanceRecords(Context context){
        this.context=context;
    }


    public FinanceRecords(Context context,String recordNAme,String recordValueDate,String recordNote,String recordAmount,String recordUniquesId,String recordCategorie,
                          String recordBookingDate,String recordCreator,int recordUpdateVersion,boolean isSecured,boolean isIncome){

        this.recordNAme=recordNAme;
        this.recordValueDate=recordValueDate;
        this.recordNote=recordNote;
        this.recordAmount=recordAmount;
        this.recordUniquesId=recordUniquesId;
        this.recordCategorie=recordCategorie;
        this.recordBookingDate=recordBookingDate;
        this.recordCreator=recordCreator;
        this.recordUpdateVersion=recordUpdateVersion;
        this.isSecured=isSecured;
        this.isIncome=isIncome;
        this.context=context;
    }



    public JSONObject getJSONObjectFINANCErecords() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("recordNAme", recordNAme);
            obj.put("recordValueDate", recordValueDate);
            if(!recordNote.isEmpty() && recordNote!=null){
                obj.put("recordNote", recordNote);
            }else{
                obj.put("recordNote", "no description aviable");
            }

            obj.put("recordUniquesId", recordUniquesId);
            obj.put("recordAmount", recordAmount);
            obj.put("recordCategorie", recordCategorie);
            obj.put("recordUpdateVersion", recordUpdateVersion);
            obj.put("isSecured", isSecured);
            obj.put("recordBookingDate", recordBookingDate);
            obj.put("recordCreator", recordCreator);
            obj.put("isIncome",isIncome);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public FinanceRecords getFinanceRecordsFromJSONObject(JSONObject obj) {
        FinanceRecords item= new FinanceRecords(context);
        try {

            if(obj.getString("recordNAme").equals("Grocery list")||
                    obj.getString("recordNAme").equals("Einkaufsliste") ){
                item.setRecordNAme(context.getString(R.string.textInitialize_create_account_grocery_note));
            }else {
                item.setRecordNAme(obj.getString("recordNAme"));
            }
            item.setRecordValueDate(obj.getString("recordValueDate"));
            item.setRecordNote(obj.getString("recordNote"));
            item.setRecordUniquesId(obj.getString("recordUniquesId"));
            item.setRecordAmount(obj.getString("recordAmount"));

            item.setRecordCategorie( obj.optString("recordCategorie"));
            item.setRecordUpdateVersion( obj.optInt("recordUpdateVersion"));
            item.setSecured(obj.getBoolean("isSecured"));
            item.setRecordBookingDate(obj.getString("recordBookingDate"));
            item.setRecordCreator(obj.getString("recordCreator"));
            item.setIncome(obj.getBoolean("isIncome"));


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return item;
    }

    protected FinanceRecords(Parcel in) {
        recordNAme = in.readString();
        recordValueDate = in.readString();
        recordNote = in.readString();
        recordAmount = in.readString();
        recordUniquesId = in.readString();
        recordCategorie = in.readString();
        recordUpdateVersion = in.readInt();
        recordData = in.readParcelable(Bitmap.class.getClassLoader());
        isSecured = in.readByte() != 0;
        recordBookingDate=in.readString();
        isIncome = in.readByte() != 0;
        recordCreator = in.readString();
    }

    public static final Creator<FinanceRecords> CREATOR = new Creator<FinanceRecords>() {
        @Override
        public FinanceRecords createFromParcel(Parcel in) {
            return new FinanceRecords(in);
        }

        @Override
        public FinanceRecords[] newArray(int size) {
            return new FinanceRecords[size];
        }
    };

    public String getRecordDataPath() {
        return recordDataPath;
    }

    public void setRecordDataPath(String recordDataPath) {
        this.recordDataPath = recordDataPath;
    }

    public String getRecordNAme() {
        return recordNAme;
    }

    public void setRecordNAme(String recordNAme) {
        this.recordNAme = recordNAme;
    }

    public String getRecordValueDate() {
        return recordValueDate;
    }

    public void setRecordValueDate(String recordValueDate) {
        this.recordValueDate = recordValueDate;
    }

    public String getRecordNote() {
        return recordNote;
    }

    public void setRecordNote(String recordNote) {
        this.recordNote = recordNote;
    }

    public String getRecordAmount() {
        return recordAmount;
    }

    public void setRecordAmount(String recordAmount) {
        this.recordAmount = recordAmount;
    }

    public String getRecordUniquesId() {
        return recordUniquesId;
    }

    public void setRecordUniquesId(String recordUniquesId) {
        this.recordUniquesId = recordUniquesId;
    }

    public String getRecordCategorie() {
        return recordCategorie;
    }

    public void setRecordCategorie(String recordCategorie) {
        this.recordCategorie = recordCategorie;
    }

    public int getRecordUpdateVersion() {
        return recordUpdateVersion;
    }

    public void setRecordUpdateVersion(int recordUpdateVersion) {
        this.recordUpdateVersion = recordUpdateVersion;
    }

    public Bitmap getRecordData() {
        return recordData;
    }

    public void setRecordData(Bitmap recordData) {
        this.recordData = recordData;
    }

    public boolean isSecured() {
        return isSecured;
    }

    public void setSecured(boolean secured) {
        isSecured = secured;
    }

    public String getRecordBookingDate() {
        return recordBookingDate;
    }

    public void setRecordBookingDate(String recordBookingDate) {
        this.recordBookingDate = recordBookingDate;
    }

    public String getRecordCreator() {
        return recordCreator;
    }

    public void setRecordCreator(String recordCreator) {
        this.recordCreator = recordCreator;
    }

    public boolean isIncome() {
        return isIncome;
    }

    public void setIncome(boolean income) {
        isIncome = income;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(recordNAme);
        parcel.writeString(recordValueDate);
        parcel.writeString(recordNote);
        parcel.writeString(recordAmount);
        parcel.writeString(recordUniquesId);
        parcel.writeString(recordCategorie);
        parcel.writeInt(recordUpdateVersion);
        parcel.writeParcelable(recordData, i);
        parcel.writeByte((byte) (isSecured ? 1 : 0));
        parcel.writeString(recordBookingDate);
        parcel.writeByte((byte) (isIncome ? 1 : 0));
        parcel.writeString(recordCreator);
    }


    public FinanceRecordsForFireBase getRecordsForFirebase(FinanceRecords financeRecords){
        FinanceRecordsForFireBase financeRecordsForFireBase= new FinanceRecordsForFireBase();

        financeRecordsForFireBase.setRecordNAme(financeRecords.getRecordNAme());
        financeRecordsForFireBase.setRecordValueDate(financeRecords.getRecordValueDate());
        financeRecordsForFireBase.setRecordNote(financeRecords.getRecordNote());
        financeRecordsForFireBase.setRecordAmount(financeRecords.getRecordAmount());
        financeRecordsForFireBase.setRecordUniquesId(financeRecords.getRecordUniquesId());
        financeRecordsForFireBase.setRecordCategorie(financeRecords.getRecordCategorie());
        financeRecordsForFireBase.setRecordUpdateVersion(financeRecords.getRecordUpdateVersion());
        financeRecordsForFireBase.setRecordDataPath(financeRecords.getRecordDataPath());
        financeRecordsForFireBase.setIncome(financeRecords.isIncome());
        financeRecordsForFireBase.setSecured(financeRecords.isSecured());
        financeRecordsForFireBase.setRecordBookingDate(financeRecords.getRecordBookingDate());
        financeRecordsForFireBase.setRecordCreator(financeRecords.getRecordCreator());


        return financeRecordsForFireBase;
    }

    public FinanceRecords getRecordsFromFirebase(FinanceRecordsForFireBase financeRecords){

        FinanceRecords financeRecordsForFireBase= new FinanceRecords(context);

        financeRecordsForFireBase.setRecordNAme(financeRecords.getRecordNAme());
        financeRecordsForFireBase.setRecordValueDate(financeRecords.getRecordValueDate());
        financeRecordsForFireBase.setRecordNote(financeRecords.getRecordNote());
        financeRecordsForFireBase.setRecordAmount(financeRecords.getRecordAmount());
        financeRecordsForFireBase.setRecordUniquesId(financeRecords.getRecordUniquesId());
        financeRecordsForFireBase.setRecordCategorie(financeRecords.getRecordCategorie());
        financeRecordsForFireBase.setRecordUpdateVersion(financeRecords.getRecordUpdateVersion());
        financeRecordsForFireBase.setRecordDataPath(financeRecords.getRecordDataPath());
        financeRecordsForFireBase.setIncome(financeRecords.isIncome());
        financeRecordsForFireBase.setSecured(financeRecords.isSecured());
        financeRecordsForFireBase.setRecordBookingDate(financeRecords.getRecordBookingDate());
        financeRecordsForFireBase.setRecordCreator(financeRecords.getRecordCreator());


        return financeRecordsForFireBase;
    }

}
