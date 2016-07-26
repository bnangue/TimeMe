package com.example.bricenangue.timeme;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.InputStream;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

public class AddItemToListActivity extends AppCompatActivity implements View.OnFocusChangeListener, SearchView.OnQueryTextListener, View.OnClickListener,ListViewAdapter.ShoppingItemSetListener {
    private  SearchView search;
    private Button createnewitem;
    private ListView addItemtolistListview;
    private TextView textViewNoDatainDB, textViewAmontToPay;
    private  ArrayList<String> itemNamee=new ArrayList<>();
    private ArrayList<String> itemPrice=new ArrayList<>();
    private ArrayList<ShoppingItem> itemsDB=new ArrayList<>();
    private ArrayList<ShoppingItem> itemstoShopList=new ArrayList<>();
    private ShoppingItem []itemstoShoparray;
    private  Menu menu;
    private String listName="";
    private boolean areItemsAdded=false;
    private UserLocalStore userLocalStore;
    private SQLiteShoppingList sqLiteShoppingList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item_to_list);
        userLocalStore=new UserLocalStore(this);
        sqLiteShoppingList=new SQLiteShoppingList(this);
        createnewitem=(Button) findViewById(R.id.grocery_activity_add_itemtoList_button);
        addItemtolistListview=(ListView)findViewById(R.id.shoppinglistViewaddItemToListactivity);
        textViewNoDatainDB=(TextView) findViewById(R.id.textView_add_item_to_list_List_empty);
        textViewAmontToPay=(TextView)findViewById(R.id.grocery_fragment_balance_amount_activity_add_item_to_list);

        Bundle extras=getIntent().getExtras();
        if(extras.containsKey("listName")){
            listName=extras.getString("listName");
        }
         search=(SearchView)findViewById(R.id.actionbarsearch_add_item_to_list);
        search.setQueryHint("Start typing to search...");
        if(search.hasFocus()){
            search.setBackgroundColor(getResources().getColor(R.color.white));
        }
        search.setOnQueryTextFocusChangeListener(this);
        search.setOnQueryTextListener(this);
        createnewitem.setOnClickListener(this);
       new LoadItemDBAsyncTask().execute();


    }

    void populateListview(){
        itemsDB=getItemFromDB(itemNamee,itemPrice);
        if(itemsDB.size()==0){
            textViewNoDatainDB.setText(R.string.text_add_itme_to_list_DataBase_empty);
        }else{

            textViewNoDatainDB.setText(R.string.text_add_itme_to_list_DataBase_not_empty);
        }
        ListViewAdapter listViewAdapter=new ListViewAdapter(this,itemsDB,this);
        addItemtolistListview.setVisibility(View.VISIBLE);
        addItemtolistListview.setAdapter(listViewAdapter);
    }

    private ArrayList<ShoppingItem> getItems(ShoppingItem[] itemstoShoparray) {
        ArrayList<ShoppingItem> itemstoShopList=new ArrayList<>();

        for(ShoppingItem s:itemstoShoparray){
            if(s!=null){
                itemstoShopList.add(s);
            }
        }

        return itemstoShopList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu=menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_item_to_list_done, menu);

        return true;
    }

    private void hideOption(int id){
        MenuItem item=menu.findItem(id);
        item.setVisible(false);
    }
    private void showOption(int id){
        MenuItem item=menu.findItem(id);
        item.setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_items_added_done) {
            ArrayList<ShoppingItem> list=new ArrayList<>();
            for(int i=0;i<itemstoShoparray.length;i++){
                if(itemstoShoparray[i]!=null){

                   list.add(itemstoShoparray[i]);
                }

            }

            if(list.size()!=0){
                Calendar c=new GregorianCalendar();
                Date dat=c.getTime();
                //String day= String.valueOf(dat.getDay());
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                String date = (String) android.text.format.DateFormat.format("dd-MM-yyyy", dat);
                GroceryList groceryList=new GroceryList();
                groceryList.setItemsOftheList(list);
                int hashid=(userLocalStore.getUserfullname() + format.format(dat)).hashCode();
                groceryList.setList_unique_id(String.valueOf(hashid));
                groceryList.setCreatorName(userLocalStore.getUserfullname());
                if(!listName.isEmpty()){
                    groceryList.setDatum(listName);
                }

               if(sqLiteShoppingList.addShoppingList(groceryList)!= -1) {
                   startActivity(new Intent(this, CreateNewShoppingListActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtra("GroceryshoppingList",groceryList));
                   finish();
               }else{
                   Toast.makeText(getApplicationContext(),"could not save the shopping list",Toast.LENGTH_SHORT).show();
               }

            }else {

                Toast.makeText(getApplicationContext(),"No items added",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, CreateNewShoppingListActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public ArrayList<ShoppingItem> getItemFromDB(ArrayList<String> itemsname, ArrayList<String> itemsprice){

        ArrayList<ShoppingItem> itemsdb=new ArrayList<>();
        for(int i=0;i<itemsname.size()-1;i++){

            ShoppingItem item=new ShoppingItem();
                item.setPrice(itemsprice.get(i));
                item.setItemName(itemsname.get(i));
            int hashid=(itemsname.get(i) + itemsprice.get(i)).hashCode();
            item.setUnique_item_id((String.valueOf(hashid)));
            item.setDetailstoItem("");
            item.setItemSpecification("");
            item.setNumberofItemsetForList(0);
            itemsdb.add(item);

        }
        itemstoShoparray=new ShoppingItem[itemsdb.size()];
        return itemsdb;
    }
    @Override
    public void onFocusChange(View v, boolean hasFocus) {

    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText.length() > 1) {

            Toast.makeText(getApplicationContext(),"isSeraching",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(),"nothing on search",Toast.LENGTH_SHORT).show();                }


        return false;
    }

    @Override
    public void onClick(View v) {
        //start create an item activity
        startActivity(new Intent(this,CreatANewItemActivity.class));
    }


    public void readXlsFile(){
        try{
            AssetManager manager=getAssets();
            InputStream in=manager.open("household_sept.xlsx");
            Workbook wb=Workbook.getWorkbook(in);
            Sheet sheet=wb.getSheet(0);
            int row=sheet.getRows();
            int col=sheet.getColumns();
            String str="";
            for(int i=0; i<row;i++){
                for (int j =0;j<col;j ++){
                    Cell cell=sheet.getCell(j,i);
                    str = str +cell.getContents();
                }
                str=str+"\n";
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public  void readXLSXFile()
    {


        XSSFWorkbook  wb = null;
        try {
            AssetManager manager=getAssets();
            InputStream in=manager.open("book_shopping_item.xlsx");
            wb = new XSSFWorkbook(in);
            XSSFWorkbook test = new XSSFWorkbook();

            XSSFSheet sheet = wb.getSheetAt(1);
            XSSFRow row;
            XSSFCell cell;

            Iterator rows = sheet.rowIterator();

            while (rows.hasNext())
            {
                row=(XSSFRow) rows.next();
                Iterator cells = row.cellIterator();
                while (cells.hasNext())
                {
                    cell=(XSSFCell) cells.next();

                    if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING)
                    {
                        System.out.print(cell.getStringCellValue()+" ");
                        itemNamee.add(cell.getStringCellValue());
                    }
                    else if(cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC)
                    {
                        System.out.print(cell.getNumericCellValue()+" ");
                        itemPrice.add(String.valueOf(cell.getNumericCellValue()));
                    }
                    else
                    {
                        //U Can Handel Boolean, Formula, Errors
                    }
                }
                System.out.println();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static void writeXLSXFile() throws IOException {

        String excelFileName = "C:/Test.xlsx";//name of excel file

        String sheetName = "Sheet1";//name of sheet

        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet(sheetName) ;

        //iterating r number of rows
        for (int r=0;r < 5; r++ )
        {
            XSSFRow row = sheet.createRow(r);

            //iterating c number of columns
            for (int c=0;c < 5; c++ )
            {
                XSSFCell cell = row.createCell(c);

                cell.setCellValue("Cell "+r+" "+c);
            }
        }

        FileOutputStream fileOut = new FileOutputStream(excelFileName);

        //write this workbook to an Outputstream.
        wb.write(fileOut);
        fileOut.flush();
        fileOut.close();
    }


    @Override
    public void onShoppingOtemSet(ShoppingItem item,int position) {

       itemstoShoparray[position]=item;
         double totalPrice=0;
        for (int i=0;i<itemstoShoparray.length;i++){
            if(itemstoShoparray[i]!=null){
                int numb=itemstoShoparray[i].getNumberofItemsetForList();
                totalPrice= totalPrice+Double.parseDouble(itemstoShoparray[i].getPrice())*numb;
            }
        }
        DecimalFormat df = new DecimalFormat("0.00");
        df.setMaximumFractionDigits(2);
        String priceStr = df.format(totalPrice);
        if(priceStr.equals("0.00")){
            textViewAmontToPay.setText(priceStr+"€");
            textViewAmontToPay.setTextColor(getResources().getColor(R.color.grey));
        }else {
            textViewAmontToPay.setText("- "+priceStr+"€");
            textViewAmontToPay.setTextColor(getResources().getColor(R.color.warning_color));
        }

    }


    class LoadItemDBAsyncTask extends AsyncTask<Void ,Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //start progressBar
        }

        @Override
        protected Void doInBackground(Void... params) {

            readXLSXFile();

            return null;

        }

        @Override
        protected void onPostExecute(Void params) {
            //end progressBar

            populateListview();
        }
    }

}
