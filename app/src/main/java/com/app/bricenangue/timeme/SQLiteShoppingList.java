package com.app.bricenangue.timeme;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by bricenangue on 22/02/16.
 */
public class SQLiteShoppingList extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "ShoppingListCreated";


    public SQLiteShoppingList(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_INCOMING_TABLE = "CREATE TABLE ShoppingLists ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, listname VARCHAR NOT NULL , creator VARCHAR NOT NULL, contain TEXT NOT NULL, status INTEGER NOT NULL," +
                " uniqueId TEXT NOT NULL, isShareStatus INTEGER NOT NULL, accountid VARCHAR NOT NULL)";

        // create books table
        db.execSQL(CREATE_INCOMING_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS ShoppingLists");

        // create fresh books table
        onCreate(db);
    }

    // Books table name
    private static final String SHOPPING_LIST_TABLE = "ShoppingLists";

    // Books Table Columns names
    private static final String KEY_ID = "id";
    private static final String LIST_LISTNAME = "listname";
    private static final String LIST_CREATOR = "creator";
    private static final String LIST_STATUS = "status";
    private static final String LIST_ID = "uniqueId";
    private static final String LIST_CONTAIN = "contain";
    private static final String LIST_IS_SHARE_STATUS = "isShareStatus";
    private static final String LIST_ACCOUNT_ID = "accountid";




    public int addShoppingList(GroceryList groceryList){

        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(LIST_LISTNAME, groceryList.getDatum());
        values.put(LIST_CREATOR, groceryList.getCreatorName());
        values.put(LIST_CONTAIN, groceryList.getListcontain().replace("\\",""));
        values.put(LIST_STATUS, groceryList.isListdone() ? 1 : 0);
        values.put(LIST_ID, groceryList.getList_unique_id());
        values.put(LIST_IS_SHARE_STATUS, groceryList.isToListshare() ? 1 : 0);
        values.put(LIST_ACCOUNT_ID, groceryList.getAccountid());

        // 3. insert
        int i= (int) db.insert(SHOPPING_LIST_TABLE, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
        return i;
    }

    public ArrayList[] getAllShoppingList() {
        ArrayList[] arraysLists=new ArrayList[2];
        ArrayList<GroceryList> groceryLists = new ArrayList<>();
        ArrayList<GroceryList> groceryListsdone = new ArrayList<>();

        // 1. build the query
        String query = "SELECT  * FROM " + SHOPPING_LIST_TABLE;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build book and add it to list
        GroceryList groceryList = null;
        if (cursor.moveToFirst()) {
            do {
                groceryList = new GroceryList();

                groceryList.setDatum(cursor.getString(1));
                groceryList.setCreatorName(cursor.getString(2));
                groceryList.setListcontain(cursor.getString(3));
                groceryList.setListdone((cursor.getInt(4) == 1));
                groceryList.setList_unique_id(cursor.getString(5));
                groceryList.setToListshare((cursor.getInt(6) == 1));
                groceryList.setItemsOftheList(groceryList.getListItems());
                groceryList.setAccountid(cursor.getString(7));

               groceryList.getListItems();
                // Add grocery list to arraylist
                if(groceryList.isListdone()){
                    groceryListsdone.add(groceryList);
                }else{
                    groceryLists.add(groceryList);
                }

            } while (cursor.moveToNext());
        }

        arraysLists[0]=groceryLists;
        arraysLists[1]=groceryListsdone;


        // return books
        return arraysLists;
    }

    public int updateShoppingList(GroceryList groceryList) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(LIST_LISTNAME, groceryList.getDatum());
        values.put(LIST_CONTAIN, groceryList.getListcontain());
        values.put(LIST_STATUS, groceryList.isListdone() ? 1 : 0);
        values.put(LIST_IS_SHARE_STATUS, groceryList.isToListshare() ? 1 : 0);



        // 3. updating row
        int i = db.update(SHOPPING_LIST_TABLE, //table
                values, // column/value
                "uniqueId = ?", // selections
                new String[] { groceryList.getList_unique_id() }); //selection args

        // 4. close
        db.close();

        return i;

    }



    public int deleteShoppingList(String list_id) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
       int i= db.delete(SHOPPING_LIST_TABLE, //table name
                LIST_ID+" = ?",  // selections
                new String[] { list_id }); //selections args

        // 3. close
        db.close();

        return i;
    }
    public void reInitializeShoppingListSqliteTable(){
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(SHOPPING_LIST_TABLE, //table name
                null,  // selections
                null); //selections args

        // 3. close
        db.close();
    }
    public void reInitializeShoppinListSqliteTable(){
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(SHOPPING_LIST_TABLE, //table name
               "1",  // selections
                null); //selections args

        // 3. close
        db.close();
    }


}
