package com.app.bricenangue.timeme;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * Created by bricenangue on 25/09/16.
 */

public class FinanceRecordsForFireBase {
    private String recordNAme;
    private String recordValueDate;
    private String recordNote;
    private String recordAmount;
    private String recordUniquesId;
    private String recordCategorie;
    private String recordBookingDate;
    private String recordCreator;
    private int recordUpdateVersion;
    private String recordDataPath;
    private boolean isSecured=false;
    private boolean isIncome=false;

    public FinanceRecordsForFireBase() {
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

    public int getRecordUpdateVersion() {
        return recordUpdateVersion;
    }

    public void setRecordUpdateVersion(int recordUpdateVersion) {
        this.recordUpdateVersion = recordUpdateVersion;
    }

    public String getRecordDataPath() {
        return recordDataPath;
    }

    public void setRecordDataPath(String recordDataPath) {
        this.recordDataPath = recordDataPath;
    }

    public boolean isSecured() {
        return isSecured;
    }

    public void setSecured(boolean secured) {
        isSecured = secured;
    }

    public boolean isIncome() {
        return isIncome;
    }

    public void setIncome(boolean income) {
        isIncome = income;
    }
}
