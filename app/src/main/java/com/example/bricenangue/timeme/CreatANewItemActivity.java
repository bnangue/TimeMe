package com.example.bricenangue.timeme;

import android.graphics.Color;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

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

    private Snackbar snackbar;
    private CoordinatorLayout coordinatorLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creat_anew_item);
        buttoncanclecreation=(Button)findViewById(R.id.button_create_item_cancle);
        buttoncreateItem=(Button)findViewById(R.id.button_create_item_create);

        coordinatorLayout=(CoordinatorLayout)findViewById(R.id.coordinateLayoutcreateItem);
        editTextDescription=(EditText)findViewById(R.id.editText_create_new_item_description);
        editTextItemPrice=(EditText)findViewById(R.id.editText_create_new_item_price);
        editTextItemname=(EditText)findViewById(R.id.editText_create_new_item_name);
        radioGroup=(RadioGroup)findViewById(R.id.radiogroupcreate_item);
        rbNone=(RadioButton)findViewById(R.id.create_new_item_readiobtn_by_none);
        rbByNumb=(RadioButton)findViewById(R.id.create_new_item_readiobtn_by_number);
        rbByKg=(RadioButton)findViewById(R.id.create_new_item_readiobtn_by_kg);

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

    private void createAndSaveItem() {
        ShoppingItem item=new ShoppingItem();

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
        item.setDetailstoItem(itemdescription);
        item.setItemName(itemname);
        item.setPrice(itemprice);
        item.setItemSpecification(itemSpecification);
        item.setItemIsBought(false);
        final SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy, HH:mm");
        formatter.setLenient(false);

        Date curDate = new Date();
        String curTime = formatter.format(curDate);
        int itemId= (itemSpecification+itemname+itemprice+itemSpecification+curTime).hashCode();
        item.setUnique_item_id(String.valueOf(itemId));
        saveItem(item);
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
                    editTextDescription.setText("");
                    editTextItemname.setText("");
                    editTextItemPrice.setText("");
                    editTextItemname.requestFocus();
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
}
