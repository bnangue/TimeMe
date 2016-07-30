package com.example.bricenangue.timeme;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import android.content.Context;
import android.util.Log;


public class AddItemToListActivity extends AppCompatActivity implements View.OnFocusChangeListener, SearchView.OnQueryTextListener,
        View.OnClickListener,ListViewAdapter.ShoppingItemSetListener,DialogFragmentDatePicker.OnDateGet {
    private  SearchView search;
    private Button createnewitem,setDateButtuon;
    private ListView addItemtolistListview;
    private TextView textViewNoDatainDB, textViewAmontToPay;
    private  ArrayList<String> itemNamee=new ArrayList<>();
    private ArrayList<String> itemPrice=new ArrayList<>();
    private ArrayList<String> itemUsageFrequency=new ArrayList<>();
    private ArrayList<ShoppingItem> itemsDB=new ArrayList<>();
    private ArrayList<ShoppingItem> itemstoShopList=new ArrayList<>();
    private ShoppingItem []itemstoShoparray;
    private  Menu menu;
    private String listName="";
    private boolean areItemsAdded=false;
    private UserLocalStore userLocalStore;
    private SQLiteShoppingList sqLiteShoppingList;
    private int[] status ;
    private Spinner spinner;
    private String [] userAccArray={"standard","most used","price ascending","price descending","selected first","selected last"};

    private FileHelper fileHelper;


    public static boolean newItem=false;

    //This arraylist will have data as pulled from server. This will keep cumulating.
   private ArrayList<ShoppingItem> productResults = new ArrayList<ShoppingItem>();
    //Based on the search string, only filtered products will be moved here from productResults
    private ArrayList<ShoppingItem> filteredProductResults = new ArrayList<ShoppingItem>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item_to_list);
        fileHelper=new FileHelper(this);
        userLocalStore=new UserLocalStore(this);
        sqLiteShoppingList=new SQLiteShoppingList(this);
        setDateButtuon=(Button)findViewById(R.id.dateaddeventstart_creatList_add_item_to_list);
        createnewitem=(Button) findViewById(R.id.grocery_activity_add_itemtoList_button);
        addItemtolistListview=(ListView)findViewById(R.id.shoppinglistViewaddItemToListactivity);
        textViewNoDatainDB=(TextView) findViewById(R.id.textView_add_item_to_list_List_empty);
        textViewAmontToPay=(TextView)findViewById(R.id.grocery_fragment_balance_amount_activity_add_item_to_list);
        setDateButtuon.setOnClickListener(this);

        spinner=(Spinner)findViewById(R.id.spinner_activity_add_item_to_list);
        SpinnerAdapter adap = new ArrayAdapter<>(this, R.layout.spinnerlayout, userAccArray);
        spinner.setAdapter(adap);

        spinner.setSelection(0);

        Bundle extras=getIntent().getExtras();

        if(extras!=null){
            if(extras.containsKey("itemDB")){
                itemsDB=extras.getParcelableArrayList("itemDB");
                assert itemsDB != null;
                itemstoShoparray=new ShoppingItem[itemsDB.size()];
                initArray();

            }
        }


         search=(SearchView)findViewById(R.id.actionbarsearch_add_item_to_list);
        search.setQueryHint("Start typing to search...");
        if(search.hasFocus()){
            search.setBackgroundColor(getResources().getColor(R.color.white));
        }
        search.setOnQueryTextFocusChangeListener(this);
        search.setOnQueryTextListener(this);
        createnewitem.setOnClickListener(this);

        initializeDatePicker();
        listName=setDateButtuon.getText().toString();
        if(savedInstanceState==null){
             if(itemsDB.size()==0 || itemsDB==null){
                 new LoadItemDBAsyncTask().execute();
             }else {
                 populateListview();
             }

        }else {
            itemsDB=savedInstanceState.getParcelableArrayList("itemDB");
            populateListview();
        }



        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(itemsDB.size()!=0){
                    sort(spinner.getSelectedItem().toString());
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        sort(spinner.getSelectedItem().toString());


    }

    private void initArray() {
        for (int i=0;i<itemsDB.size();i++){
            if(itemsDB.get(i).getNumberofItemsetForList()!=0){
                itemstoShoparray[i]=itemsDB.get(i);
            }
        }
        initTotalPricetextView();
    }

    void sort(String sortname){
        switch (sortname){
            case "standard":
                Collections.sort(itemsDB, new ComparatorShoppingItemName());
                break;
            case "price ascending":
                Collections.sort(itemsDB, new ComparatorShoppingItemPrice());
                break;
            case "price descending":
                Comparator<ShoppingItem> comparator_type = Collections.reverseOrder(new ComparatorShoppingItemPrice());
                Collections.sort(itemsDB, comparator_type);
                break;
            case "most used":
                Comparator<ShoppingItem> comparator_type2 = Collections.reverseOrder(new ComparatorShoppingItemMostUsed());
                Collections.sort(itemsDB, comparator_type2);

                break;
            case "selected first":
                Comparator<ShoppingItem> comparator_type1 = Collections.reverseOrder(new CompareAddItemAlreadyadded());
                Collections.sort(itemsDB, comparator_type1);
                break;
            case "selected last":
                Collections.sort(itemsDB, new CompareAddItemAlreadyadded());
                break;


        }

        populateListview();


    }
    void populateListview(){


        if(itemsDB.size()==0){
            textViewNoDatainDB.setText(R.string.text_add_itme_to_list_DataBase_empty);
        }else{

            textViewNoDatainDB.setText(R.string.text_add_itme_to_list_DataBase_not_empty);
        }
        if(productResults.size()!=0){
            merge();
        }
        ListViewAdapter listViewAdapter=new ListViewAdapter(this,itemsDB,this);
        addItemtolistListview.setVisibility(View.VISIBLE);
        addItemtolistListview.setAdapter(listViewAdapter);
    }

    private void merge() {
        for (int i=0;i<productResults.size();i++){
            for(int j=0;j<itemsDB.size();j++){
                if(productResults.get(i).getUnique_item_id().equals(itemsDB.get(j).getUnique_item_id())&&
                        productResults.get(i).getNumberofItemsetForList()!=itemsDB.get(j).getNumberofItemsetForList()){

                    itemsDB.set(j,productResults.get(i));
                }
            }

        }

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
                updateExcelFile(this,list);
                listName=setDateButtuon.getText().toString();
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
        }else if (id == R.id.action_item_list_refresh){
            new LoadItemDBAsyncTask().execute();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private  boolean updateExcelFile(Context context, ArrayList<ShoppingItem> items) {
        boolean success= false;

        FileHelper fileHelper=new FileHelper(context);
        // Creating Input Stream

        // Create a path where we will place our List of objects on external storage
        File file=null;
        FileOutputStream os = null;

        try {

            // Creating Input Stream
            file = new File(fileHelper.getExcelfile("book_shopping_item"));


            FileInputStream myInput = new FileInputStream(file);


            XSSFWorkbook wb = new  XSSFWorkbook(myInput);
            XSSFSheet sheet = wb.getSheetAt(0);
            XSSFRow row;
            XSSFCell cell;

            Iterator rows = sheet.rowIterator();

            while (rows.hasNext())
            {
                row=(XSSFRow) rows.next();

                cell=row.getCell(0);

                for(int i=0;i<items.size();i++){
                    if(cell.getStringCellValue().equals(items.get(i).getItemName())){

                        row.getCell(2).setCellValue(items.get(i).getNumberoftimeAddedAnyToList());
                        System.out.print(cell.getStringCellValue()+" "+row.getCell(2).getNumericCellValue());
                    }
                }

                System.out.println();
            }



            os = new FileOutputStream(file);
            wb.write(os);
            Log.w("FileUtils", "Writing file" + file);
            success = true;
        } catch (IOException e) {
            Log.w("FileUtils", "Error writing " + file, e);
        } catch (Exception e) {
            Log.w("FileUtils", "Failed to save file", e);
        } finally {
            try {
                if (null != os)
                    os.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return success;
    }




    public ArrayList<ShoppingItem> getItemFromDB(ArrayList<String> itemsname, ArrayList<String> itemsprice, ArrayList<String> itemUsageFrequency){

        ArrayList<ShoppingItem> itemsdb=new ArrayList<>();
        for(int i=0;i<itemsname.size();i++){

            ShoppingItem item=new ShoppingItem();
            item.setPrice(itemsprice.get(i));
            item.setItemName(itemsname.get(i));
            int hashid=(itemsname.get(i) + itemsprice.get(i)).hashCode();
            item.setUnique_item_id((String.valueOf(hashid)));
            item.setDetailstoItem("");
            item.setItemSpecification("");
            item.setNumberofItemsetForList(0);
            DecimalFormat df = new DecimalFormat("0.00");
            df.setMaximumFractionDigits(2);
            try {
                Number number=df.parse(itemUsageFrequency.get(i));
                item.setNumberoftimeAddedAnyToList(number.intValue());
            } catch (Exception e) {
                e.printStackTrace();
            }

            itemsdb.add(item);

        }
        itemstoShoparray=new ShoppingItem[itemsdb.size()];
        return itemsdb;
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
            super.onSaveInstanceState(state);

            state.putParcelableArrayList("itemDB", itemsDB);

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
        if (newText.length() > 0) {

            new SearchAsyncTask().execute(newText);
        } else {
           populateListview();
        }


        return false;
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.grocery_activity_add_itemtoList_button:
                startActivity(new Intent(this,CreatANewItemActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .putExtra("itemDB",itemsDB));
                break;
            case R.id.dateaddeventstart_creatList_add_item_to_list:
                onDatePickercliced(true);
                break;
        }

    }

    @Override
    public void dateSet(String date, boolean isstart) {
        setDateButtuon.setText(date);
    }

    private void initializeDatePicker(){
        final SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        formatter.setLenient(false);

        Date curDate = new Date();
        String curTime = formatter.format(curDate);
        setDateButtuon.setText(curTime);
    }
    public void onDatePickercliced(boolean bol){
        android.support.v4.app.FragmentManager manager=getSupportFragmentManager();
        DialogFragment fragmentDatePicker=DialogFragmentDatePicker.newInstance(bol);

        fragmentDatePicker.show(manager,"datePickerfr");
    }


    private  boolean saveExcelXLSXFileFirstInit(Context context) {
        boolean success= false;

        FileHelper fileHelper=new FileHelper(context);
        XSSFWorkbook  wb = null;
        String workbooksFolderpath= fileHelper.getWorkbooksFolder();
        String filesDirectorypath= fileHelper.getFilesDirectory();
        File filedirectory=new File(filesDirectorypath);

        if (!filedirectory.exists()){
            filedirectory.mkdirs();
        }
        File workbookfolder=new File(workbooksFolderpath);
        if(!workbookfolder.exists()){
            workbookfolder.mkdir();
        }
        // Create a path where we will place our List of objects on external storage
        File file=null;
        FileOutputStream os = null;

        try {
            AssetManager manager=getAssets();
            InputStream in=manager.open("book_shopping_item.xlsx");
            wb = new XSSFWorkbook(in);

             file = new File(fileHelper.getExcelfile("book_shopping_item"));
            if(!file.exists()){
                file.createNewFile();

            }

            os = new FileOutputStream(file);
            wb.write(os);
            Log.w("FileUtils", "Writing file" + file);
            success = true;
        } catch (IOException e) {
            Log.w("FileUtils", "Error writing " + file, e);
        } catch (Exception e) {
            Log.w("FileUtils", "Failed to save file", e);
        } finally {
            try {
                if (null != os)
                    os.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return success;
    }


    private void readExcelXLSXFile(Context context) {

        FileHelper fileHelper=new FileHelper(context);

        File file=null;
        try{
            // Creating Input Stream
            file = new File(fileHelper.getExcelfile("book_shopping_item"));


            FileInputStream myInput = new FileInputStream(file);


            XSSFWorkbook wb = new  XSSFWorkbook(myInput);
            XSSFSheet sheet = wb.getSheetAt(0);
            XSSFRow row;
            XSSFCell cell;

            Iterator rows = sheet.rowIterator();

            while (rows.hasNext())
            {
                row=(XSSFRow) rows.next();

                cell=row.getCell(0);

                if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING)
                {
                    System.out.print(cell.getStringCellValue()+" ");
                    itemNamee.add(cell.getStringCellValue());
                }
                cell=row.getCell(1);
               if(cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC)
                {
                    System.out.print(cell.getNumericCellValue()+" ");
                    itemPrice.add(String.valueOf(cell.getNumericCellValue()));
                }
                cell=row.getCell(2);
                if(cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC)
                {
                    System.out.print(cell.getNumericCellValue()+" ");
                    itemUsageFrequency.add(String.valueOf(cell.getNumericCellValue()));
                }

                System.out.println();
            }

        }catch (Exception e){e.printStackTrace(); }


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


    private static boolean saveExcelFile(Context context, String fileName) {

        boolean success = false;

        //New Workbook
        Workbook wb = new HSSFWorkbook();

        Cell c = null;

        //Cell style for header row
        CellStyle cs = wb.createCellStyle();
        cs.setFillForegroundColor(HSSFColor.LIME.index);
        cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        //New Sheet
        Sheet sheet1 = null;
        sheet1 = wb.createSheet("myOrder");

        // Generate column headings
        Row row = sheet1.createRow(0);

        c = row.createCell(0);
        c.setCellValue("Item Number");
        c.setCellStyle(cs);

        c = row.createCell(1);
        c.setCellValue("Quantity");
        c.setCellStyle(cs);

        c = row.createCell(2);
        c.setCellValue("Price");
        c.setCellStyle(cs);

        sheet1.setColumnWidth(0, (15 * 500));
        sheet1.setColumnWidth(1, (15 * 500));
        sheet1.setColumnWidth(2, (15 * 500));

        // Create a path where we will place our List of objects on external storage
        File file = new File(context.getExternalFilesDir(null), fileName);
        FileOutputStream os = null;

        try {
            os = new FileOutputStream(file);
            wb.write(os);
            Log.w("FileUtils", "Writing file" + file);
            success = true;
        } catch (IOException e) {
            Log.w("FileUtils", "Error writing " + file, e);
        } catch (Exception e) {
            Log.w("FileUtils", "Failed to save file", e);
        } finally {
            try {
                if (null != os)
                    os.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return success;
    }

    private static void readExcelFile(Context context, String filename) {

        try{
            // Creating Input Stream
            File file = new File(context.getExternalFilesDir(null), filename);
            FileInputStream myInput = new FileInputStream(file);

            // Create a POIFSFileSystem object
            POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);

            // Create a workbook using the File System
            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);

            // Get the first sheet from workbook
            HSSFSheet mySheet = myWorkBook.getSheetAt(0);

            /** We now need something to iterate through the cells.**/
            Iterator<Row> rowIter = mySheet.rowIterator();

            while(rowIter.hasNext()){
                HSSFRow myRow = (HSSFRow) rowIter.next();
                Iterator<Cell> cellIter = myRow.cellIterator();
                while(cellIter.hasNext()){
                    HSSFCell myCell = (HSSFCell) cellIter.next();
                    Log.w("FileUtils", "Cell Value: " +  myCell.toString());
                    Toast.makeText(context, "cell Value: " + myCell.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }catch (Exception e){e.printStackTrace(); }

        return;
    }

    @Override
    public void onShoppingOtemSet(ShoppingItem item,int position) {

        itemstoShoparray[position]=item;
        initTotalPricetextView();

    }

    private void initTotalPricetextView(){
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
        if((priceStr.replace(",",".")).equals("0.00")){
            textViewAmontToPay.setText(priceStr+"€");
            textViewAmontToPay.setTextColor(getResources().getColor(R.color.grey));
        }else {
            textViewAmontToPay.setText("- "+priceStr+"€");
            textViewAmontToPay.setTextColor(getResources().getColor(R.color.warning_color));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected void onPause() {
        super.onPause();
        newItem=false;
    }

    //in this myAsyncTask, we are fetching data from server for the search string entered by user.
    class SearchAsyncTask extends AsyncTask<String, Void, String> {
        ArrayList<ShoppingItem>  productList;
        String textSearch;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            productList =new ArrayList<>();
        }

        @Override
        protected String doInBackground(String... sText) {

            // url="http://lawgo.in/lawgo/products/user/1/search/"+sText[0];
            String returnResult = getProductList();
            this.textSearch = sText[0];
            return returnResult;

        }

        public String getProductList() {

            ShoppingItem object;
            String matchFound = "N";


            try {

                productList=itemsDB;
                //parse date for dateList
                for (int i = 0; i < productList.size(); i++) {





                    object=productList.get(i);


                    //check if this product is already there in productResults, if yes, then don't add it again.
                    matchFound = "N";

                    for (int j = 0; j < productResults.size(); j++) {

                        if (productResults.get(j).getItemName().equals(object.getItemName())) {
                            matchFound = "Y";
                        }
                    }

                    if (matchFound == "N") {
                        productResults.add(object);
                    }

                }


                return ("OK");

            } catch (Exception e) {
                e.printStackTrace();
                return ("Exception Caught");
            }




        }

        @Override
        protected void onPostExecute (String result){

            super.onPostExecute(result);

            if (result.equalsIgnoreCase("Exception Caught")) {
                Toast.makeText(getApplicationContext(), "Unable to connect to server,please try later", Toast.LENGTH_LONG).show();

            } else {
                //calling this method to filter the search results from productResults and move them to
                //filteredProductResults
                filterProductArray(textSearch);

                prepareListviewall(filteredProductResults);

            }
        }


    }

    void prepareListviewall(ArrayList<ShoppingItem> shoppingItems){

        if(shoppingItems.size()==0){
            textViewNoDatainDB.setText(R.string.text_search_itme_to_list_DataBase_empty);
        }else{

            textViewNoDatainDB.setText(R.string.text_search_itme_to_list_DataBase_not_empty);
        }
        ListViewAdapter listViewAdapter=new ListViewAdapter(this,shoppingItems,this);
        addItemtolistListview.setVisibility(View.VISIBLE);
        addItemtolistListview.setAdapter(listViewAdapter);
    }

    public void filterProductArray(String newText) {

        String pName;

        filteredProductResults.clear();
        for (int i = 0; i < productResults.size(); i++) {
            pName = productResults.get(i).getItemName().toLowerCase();
            if (pName.contains(newText.toLowerCase())) {
                filteredProductResults.add(productResults.get(i));



            }
        }

    }


    class LoadItemDBAsyncTask extends AsyncTask<Void ,Void, Void> {

        private FragmentProgressBarLoading progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //start progressBar
            progressDialog = new FragmentProgressBarLoading();
            progressDialog.setCancelable(false);
            progressDialog.show(getSupportFragmentManager(), "task_progress");
        }

        @Override
        protected Void doInBackground(Void... params) {

            //readXLSXFile();

            readExcelXLSXFile(getApplicationContext());
            return null;

        }

        @Override
        protected void onPostExecute(Void params) {
            //end progressBar
            progressDialog.dismiss(getSupportFragmentManager());

            if(itemsDB.size()==0 || itemsDB==null ){
                itemsDB=getItemFromDB(itemNamee,itemPrice,itemUsageFrequency);
            }
            populateListview();
        }
    }

    private void mergeItemsDB(ArrayList<ShoppingItem> items) {
        for (int i=0;i<items.size();i++){
            for(int j=0;j<itemsDB.size();j++){
                if(items.get(i).getUnique_item_id().equals(itemsDB.get(j).getUnique_item_id())&&
                        items.get(i).getNumberofItemsetForList()!=itemsDB.get(j).getNumberofItemsetForList()){

                    itemsDB.set(j,items.get(i));
                }
            }

        }
    }

}
