package com.app.bricenangue.timeme;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CreatANewItemActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    private EditText editTextItemname,editTextItemPrice,editTextDescription;
    private String itemname,itemprice,itemdescription, itemSpecification ="";
    private RadioGroup radioGroup;
    private RadioButton rbByKg,rbByNumb,rbNone;
    private Button buttoncreateItem,buttoncanclecreation;
    private final  String SPECIFICATIONNONE="NONE";
    private final  String SPECIFICATIONNUMBER="NUMBER";
    private final  String SPECIFICATIONKG="KG";
    private Spinner spinnerItemCategory,spinnerItemMarket;
    private Snackbar snackbar;
    private CoordinatorLayout coordinatorLayout;



    private ArrayList<ShoppingItem> list=new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creat_anew_item);
        buttoncanclecreation=(Button)findViewById(R.id.button_create_item_cancle);
        buttoncreateItem=(Button)findViewById(R.id.button_create_item_create);

        Bundle bundle=getIntent().getExtras();
        if(bundle!=null){
            list=bundle.getParcelableArrayList("itemDB");
        }

        String [] catergories={getString(R.string.household),getString(R.string.fruit),getString(R.string.vegetables),
                getString(R.string.grain_products),getString(R.string.technology),getString(R.string.drinks),
                getString(R.string.fats_and_oils),getString(R.string.milk_products),getString(R.string.spices),
                getString(R.string.drugstore),getString(R.string.others),getString(R.string.meat),getString(R.string.sweets)};
        String [] markets={getString(R.string.rewe_Market),getString(R.string.dm_Market),getString(R.string.norma_Market)};
        coordinatorLayout=(CoordinatorLayout)findViewById(R.id.coordinateLayoutcreateItem);
        editTextDescription=(EditText)findViewById(R.id.editText_create_new_item_description);
        editTextItemPrice=(EditText)findViewById(R.id.editText_create_new_item_price);
        editTextItemname=(EditText)findViewById(R.id.editText_create_new_item_name);
        radioGroup=(RadioGroup)findViewById(R.id.radiogroupcreate_item);
        rbNone=(RadioButton)findViewById(R.id.create_new_item_readiobtn_by_none);
        rbByNumb=(RadioButton)findViewById(R.id.create_new_item_readiobtn_by_number);
        rbByKg=(RadioButton)findViewById(R.id.create_new_item_readiobtn_by_kg);
        spinnerItemCategory=(Spinner)findViewById(R.id.spinner_item_category_activity_create_anew_item);
        spinnerItemMarket=(Spinner)findViewById(R.id.spinner_item_market_activity_create_anew_item);



        SpinnerAdapter adapter1 = new ArrayAdapter<>(this, R.layout.spinnerlayout, catergories);
        spinnerItemCategory.setAdapter(adapter1);

        spinnerItemCategory.setSelection(0);

        SpinnerAdapter adapter2 = new ArrayAdapter<>(this, R.layout.spinnerlayout, markets);
        spinnerItemMarket.setAdapter(adapter2);

        spinnerItemMarket.setSelection(0);

        buttoncanclecreation.setOnClickListener(this);
        buttoncreateItem.setOnClickListener(this);
        radioGroup.setOnCheckedChangeListener(this);


    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.button_create_item_cancle:
                //cancle creation
                break;
            case R.id.button_create_item_create:
                //check if fields are empty
                // Reset errors.
                editTextDescription.setError(null);
                editTextItemPrice.setError(null);
                editTextItemname.setError(null);

                // Store values at the time of the login attempt.
                itemname=editTextItemname.getText().toString();
                itemprice=editTextItemPrice.getText().toString();
                itemdescription=editTextDescription.getText().toString();

                boolean cancel = false;
                View focusView = null;

                // Check for a valid password, if the user entered one.
                if (TextUtils.isEmpty(itemname)) {
                    editTextItemname.setError(getString(R.string.error_field_required_name));
                    focusView = editTextItemname;
                    cancel = true;
                }

                // Check for a valid email address.
                if (TextUtils.isEmpty(itemprice)) {
                    editTextItemPrice.setError(getString(R.string.error_field_required_price));
                    focusView = editTextItemPrice;
                    cancel = true;
                }

                if (cancel) {
                    // There was an error; don't attempt login and focus the first
                    // form field with an error.
                    focusView.requestFocus();
                } else {
                    createAndSaveItem();
                }

                break;
        }
    }

    void clearEditTextContain(){
        editTextItemname.setText("");
        editTextItemPrice.setText("");
        editTextDescription.setText("");
        editTextItemname.requestFocus();
        spinnerItemCategory.setSelection(0);

        spinnerItemMarket.setSelection(0);
    }
    private  boolean saveShoppingItmeInExcelXLSXFile(Context context, ShoppingItem item) {
        boolean success= false;

        FileHelper fileHelper=new FileHelper(context);
        // Creating Input Stream

        // Create a path where we will place our List of objects on external storage
        File file=null;
        FileOutputStream os = null;

        try {

            file = new File(fileHelper.getExcelfile("shopping_list_items"));


            FileInputStream myInput = new FileInputStream(file);

            XSSFWorkbook wb = new  XSSFWorkbook(myInput);
            XSSFSheet sheet = wb.getSheetAt(0);
            XSSFRow row;

            Cell c = null;
            int lastrow = sheet.getLastRowNum();

             row = sheet.createRow(lastrow +1);

            c = row.createCell(0);
            c.setCellValue(item.getItemName());


            c = row.createCell(1);
            DecimalFormat df = new DecimalFormat("0.00");
            df.setMaximumFractionDigits(2);
            Number nm=df.parse(item.getPrice());

            c.setCellValue(Double.parseDouble(item.getPrice().replace(",",".")));


            c = row.createCell(2);
            c.setCellValue(0);

            c = row.createCell(3);
            c.setCellValue(item.getItemmarket());

            c = row.createCell(4);
            c.setCellValue(item.getItemcategory());

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

    private void createAndSaveItem() {
        ShoppingItem item=new ShoppingItem();

        String itemcatgory=spinnerItemCategory.getSelectedItem().toString();
        String itemmarket=spinnerItemMarket.getSelectedItem().toString();

      int id= radioGroup.getCheckedRadioButtonId();

        switch (id){
            case R.id.create_new_item_readiobtn_by_none:
                itemSpecification =SPECIFICATIONNONE;
                break;
            case R.id.create_new_item_readiobtn_by_number:
                itemSpecification =SPECIFICATIONNUMBER;
                break;
            case R.id.create_new_item_readiobtn_by_kg:
                itemSpecification =SPECIFICATIONKG;
                break;

        }

        if((itemprice.replace(",",".").equals("0.00"))||
                (itemprice.replace(",",".").equals("0.0"))  || (itemprice.replace(",",".").equals("0"))){
            Toast.makeText(getApplicationContext(),"The price cannot be 0,00€",Toast.LENGTH_SHORT).show();
        }else {
            item.setDetailstoItem(itemdescription);
            item.setItemName(itemname);
            item.setPrice(itemprice);
            item.setItemcategory(itemcatgory);
            item.setItemmarket(itemmarket);
            item.setItemSpecification(itemSpecification);
            item.setItemIsBought(false);
            final SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy, HH:mm");
            formatter.setLenient(false);

            Date curDate = new Date();
            String curTime = formatter.format(curDate);
            int itemId= (itemSpecification+itemname+itemprice+itemSpecification+curTime).hashCode();
            item.setUnique_item_id(String.valueOf(itemId));
            new CreateItemToDBAsyncTask(this,item).execute();
        }
       // saveShoppingItmeInExcelXLSXFile(this,item);
       // saveItem(item);
    }

    private void saveItem(final ShoppingItem item) {
        //save item to DB
        ServerRequests serverRequests=new ServerRequests(this);
        serverRequests.saveItemInBackgroung(item, new GetEventsCallbacks() {
            @Override
            public void done(ArrayList<CalendarCollection> returnedeventobject) {

            }

            @Override
            public void itemslis(ArrayList<ShoppingItem> returnedShoppingItem) {

            }

            @Override
            public void updated(String reponse) {

                if(reponse.contains("Item added successfully")){
                    Toast.makeText(getApplicationContext(),"item created",Toast.LENGTH_SHORT).show();
                   clearEditTextContain();
                }else {
                    //error
                    showSnackBar(item);
                }
            }
        });
    }

    public void showSnackBar(final ShoppingItem item){
        snackbar = Snackbar
                .make(coordinatorLayout, " An error occured during creation ", Snackbar.LENGTH_INDEFINITE)
                .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveItem(item);
                    }
                });
        snackbar.setActionTextColor(Color.WHITE);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(getResources().getColor(R.color.colorSnackbar));
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.create_new_item_readiobtn_by_none:
                itemSpecification =SPECIFICATIONNONE;
                break;
            case R.id.create_new_item_readiobtn_by_number:
                itemSpecification =SPECIFICATIONNUMBER;
                break;
            case R.id.create_new_item_readiobtn_by_kg:
                itemSpecification =SPECIFICATIONKG;
                break;

        }
    }


    class CreateItemToDBAsyncTask extends AsyncTask<Void ,Void, Boolean> {

        private FragmentProgressBarLoading progressDialog;
        private ShoppingItem item;
        private Context context;
        private boolean success=false;
        public CreateItemToDBAsyncTask(Context context, ShoppingItem item){
            this.item=item;
            this.context=context;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //start progressBar
            progressDialog = new FragmentProgressBarLoading();
            progressDialog.setCancelable(false);
            progressDialog.show(getSupportFragmentManager(), "task_progress");
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            if (saveShoppingItmeInExcelXLSXFile(context,item)){

                success=true;

            }
            return success;

        }

        @Override
        protected void onPostExecute(Boolean params) {
            //end progressBar
            progressDialog.dismiss(getSupportFragmentManager());
            if(params){
                list.add(item);

                Toast.makeText(getApplicationContext(),"Shopping item "+ item.getItemName()+ " created",Toast.LENGTH_SHORT).show();

                AddItemToListActivity.newItem=true;
                clearEditTextContain();
            }else{
                Toast.makeText(getApplicationContext(),"error",Toast.LENGTH_SHORT).show();
            }

        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
            startActivity(new Intent(this, AddItemToListActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).
                    putExtra("itemDB",list));


    }
}
