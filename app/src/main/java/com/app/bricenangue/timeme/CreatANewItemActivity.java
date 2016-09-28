package com.app.bricenangue.timeme;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
import java.util.Collections;
import java.util.Comparator;
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
    private ProgressDialog progressBar;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private ArrayList<ShoppingItem> itemDB=new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creat_anew_item);
        buttoncanclecreation=(Button)findViewById(R.id.button_create_item_cancle);
        buttoncreateItem=(Button)findViewById(R.id.button_create_item_create);

        auth=FirebaseAuth.getInstance();

        Bundle extras=getIntent().getExtras();
        if(extras!=null){
            itemDB= extras.getParcelableArrayList("itemDB");
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

    private void createShoppingitem(final ShoppingItem item) {

        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(false);
        progressBar.setTitle("Loading items");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.show();

        final ArrayList<ShoppingItem> list=new ArrayList<>();
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(Config.FIREBASE_APP_URL_SHOPPING_ITEMS_XSL)
                .child(Config.FIREBASE_APP_URL_SHOPPING_ITEMS_XSL_ITEMS_NODE);

        ref.child(item.getUnique_item_id()).setValue(new ShoppingItem().getitemForFirebase(item))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                       if(task.isSuccessful()){
                           Toast.makeText(getApplicationContext(),"Shopping item "+ item.getItemName()+ " created",Toast.LENGTH_SHORT).show();
                           clearEditTextContain();
                           if (progressBar != null) {
                               progressBar.dismiss();
                           }

                       }else {
                           Toast.makeText(getApplicationContext(),
                                   task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                       }
                    }
                });
        if (!new ServerRequests(this).haveNetworkConnection()) {

            if (progressBar != null) {
                progressBar.dismiss();
            }
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
            Toast.makeText(getApplicationContext(),"The price cannot be 0,00 €",Toast.LENGTH_SHORT).show();
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

            String  sortName=item.getItemcategory();

            if(sortName.equals(getString(R.string.household))){
                item.setItemcategory("Household");

            }else if(sortName.equals(getString(R.string.fruit))){
                item.setItemcategory("Fruit");

            }else if(sortName.equals(getString(R.string.vegetables))){
                item.setItemcategory("Vegetables");

            }else if(sortName.equals(getString(R.string.grain_products))){
                item.setItemcategory("Grain products");

            }else if(sortName.equals(getString(R.string.technology))){
                item.setItemcategory("Technology");

            }else if(sortName.equals(getString(R.string.drinks))){
                item.setItemcategory("Drinks");

            }else if(sortName.equals(getString(R.string.fats_and_oils))){
                item.setItemcategory("Fats and oils");

            }else if(sortName.equals(getString(R.string.milk_products))){
                item.setItemcategory("Milk products");

            }else if(sortName.equals(getString(R.string.spices))){
                item.setItemcategory("Spices");

            }else if(sortName.equals(getString(R.string.drugstore))){
                item.setItemcategory("Drugstore");

            }else if(sortName.equals(getString(R.string.others))){
                item.setItemcategory("Others");

            }else if(sortName.equals(getString(R.string.meat))){
                item.setItemcategory("Meat");

            }else if(sortName.equals(getString(R.string.sweets))){
                item.setItemcategory("Sweets");

            }
            createShoppingitem(item);
        }

    }

    public void showSnackBar(final ShoppingItem item){
        snackbar = Snackbar
                .make(coordinatorLayout, " An error occured during creation ", Snackbar.LENGTH_INDEFINITE)
                .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

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

    @Override
    public void onBackPressed() {

        startActivity(new Intent(this,AddItemToListActivity.class).putExtra("itemstoShoparray",itemDB).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }
}
