package com.app.bricenangue.timeme;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by bricenangue on 02/08/16.
 */
public class SQLFinanceAccount  extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "MY_FINANCE_ACCOUNT";

    private Context context;

    public SQLFinanceAccount(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_INCOMING_TABLE = "CREATE TABLE Accounts ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, accountName VARCHAR NOT NULL , accountOwner VARCHAR NOT NULL, accountRecords TEXT NULL," +
                " accountLastChange VARCHAR NOT NULL, accountBalance VARCHAR NOT NULL, accountUniqueId TEXT NOT NULL)";

        // create books table
        db.execSQL(CREATE_INCOMING_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Accounts");

        // create fresh books table
        onCreate(db);
    }

    // Books table name
    private static final String ACCOUNT_TABLE = "Accounts";

    // Books Table Columns names
    private static final String KEY_ID = "id";
    private static final String ACCOUNT_NAME = "accountName";
    private static final String ACCOUNT_OWNERS = "accountOwner";
    private static final String ACCOUNT_RECORDS = "accountRecords";
    private static final String ACCOUNT_LASTCHANGE = "accountLastChange";
    private static final String ACCOUNT_BALANCE = "accountBalance";
    private static final String ACCOUNT_UNIQUE_ID = "accountUniqueId";




    public int addFINANCEACCOUNT(FinanceAccount financeAccount){

        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(ACCOUNT_NAME, financeAccount.getAccountName());
        values.put(ACCOUNT_OWNERS, financeAccount.getAccountOwnersToString());
        values.put(ACCOUNT_RECORDS, financeAccount.getAccountRecordsString());
        values.put(ACCOUNT_LASTCHANGE, financeAccount.getLastChangeDateToAccount());
        values.put(ACCOUNT_UNIQUE_ID, financeAccount.getAccountUniqueId());
        values.put(ACCOUNT_BALANCE, financeAccount.getAccountBalance());

        // 3. insert
        int i= (int) db.insert(ACCOUNT_TABLE, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
        return i;
    }

    public ArrayList<FinanceAccount> getAllFinanceAccount() {

        ArrayList<FinanceAccount> financeAccounts = new ArrayList<>();

        // 1. build the query
        String query = "SELECT  * FROM " + ACCOUNT_TABLE;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build book and add it to list
        FinanceAccount financeAccount = null;
        if (cursor.moveToFirst()) {
            do {
                financeAccount = new FinanceAccount(context);

                financeAccount.setAccountName(cursor.getString(1));
                financeAccount.setAccountOwnersToString(cursor.getString(2));
                financeAccount.setAccountRecordsString(cursor.getString(3));
                financeAccount.setLastchangeToAccount((cursor.getString(4)));
                financeAccount.setAccountBalanceToString(cursor.getString(5));
                financeAccount.setAccountUniqueId(cursor.getString(6));

                financeAccounts.add(financeAccount);
                // Add grocery list to arraylist
             /**   if(groceryList.isListdone()){
                    groceryListsdone.add(groceryList);
                }else{
                    groceryLists.add(groceryList);
                }
            **/
            } while (cursor.moveToNext());
        }




        // return finance accounts
        return financeAccounts;
    }


    public int updateFinanceAccount(FinanceAccount financeAccount) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ACCOUNT_RECORDS, financeAccount.getAccountRecordsString());
        values.put(ACCOUNT_BALANCE, financeAccount.getAccountBlanceTostring());
        values.put(ACCOUNT_LASTCHANGE, financeAccount.getLastChangeDateToAccount());
        values.put(ACCOUNT_OWNERS, financeAccount.getAccountOwnersToString());




        // 3. updating row
        int i = db.update(ACCOUNT_TABLE, //table
                values, // column/value
                "accountUniqueId = ?", // selections
                new String[] { financeAccount.getAccountUniqueId() }); //selection args

        // 4. close
        db.close();

        return i;

    }



    public int deleteFinanceAccount(String list_id) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        int i= db.delete(ACCOUNT_TABLE, //table name
                ACCOUNT_UNIQUE_ID+" = ?",  // selections
                new String[] { list_id }); //selections args

        // 3. close
        db.close();

        return i;
    }
    public void reInitializeFinanceSqliteTable(){
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(ACCOUNT_TABLE, //table name
                null,  // selections
                null); //selections args

        // 3. close
        db.close();
    }
    public void reInitializeFinanceAccountSqliteTable(){
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(ACCOUNT_TABLE, //table name
                "1",  // selections
                null); //selections args

        // 3. close
        db.close();
    }


}
