package com.example.bricenangue.timeme;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class ListViewAdapter extends BaseAdapter {

    private int numberoftiemeverused=0;

    public interface ShoppingItemSetListener{
        void onShoppingOtemSet(ShoppingItem item,int position);
    }


    private ShoppingItemSetListener shoppingItemSetListener;
    private Context context;
    private int counterItemSelected=0;


    private static LayoutInflater inflater=null;
    private ArrayList<ShoppingItem> shoppingItemsDBList;

    //Array mit allen UnterschriftenObjekten
//    Unterschrift [] unterschriftenArray;


    private int numberOfSelectedItems,positionmemo=0;

    //wird von SignatureActivity übergeben
    private int [] numberSelectedOnrow;
    private boolean bol = false;


    public ListViewAdapter(Context oldContext, ArrayList<ShoppingItem> shoppingItemsDBList,ShoppingItemSetListener shoppingItemSetListener)
    {
        context = oldContext;
        this.shoppingItemsDBList = shoppingItemsDBList;
        if(null != shoppingItemsDBList) {
            numberSelectedOnrow=new int[shoppingItemsDBList.size()];
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.shoppingItemSetListener=shoppingItemSetListener;
        }

    }

    public void setSelectedItems(int [] itemsArray,int numberOfSelectedItem){
        numberSelectedOnrow = itemsArray;

        numberOfSelectedItems=numberOfSelectedItem;
        notifyDataSetChanged();
    }

    @Override
    //Anzahl der Unterschriften
    public int getCount() {
        int i = 0;
        if(shoppingItemsDBList != null)
            i = shoppingItemsDBList.size();

        return i;
    }


    @Override
    public Object getItem(int position) {
        return shoppingItemsDBList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        final holder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item, null);
            viewHolder = new holder();


            viewHolder.itemName = (TextView) convertView.findViewById(R.id.TextView_list_item_title_item);
            viewHolder.itemPrice = (TextView) convertView.findViewById(R.id.TextView_list_item_price_item);
            viewHolder.nummberitemSelected = (TextView) convertView.findViewById(R.id.TextView_list_item_number_of_item_selected);
            viewHolder.totalPrice = (TextView) convertView.findViewById(R.id.TextView_list_item_total_item_price);
            viewHolder.addItem = (ImageButton) convertView.findViewById(R.id.imageButton_add_one_item_to_list);
            viewHolder.removeItem = (ImageButton) convertView.findViewById(R.id.imageButton_remove_one_item_from_list);


            convertView.setTag(viewHolder);

        } else {
            viewHolder = (holder) convertView.getTag();
        }
        viewHolder.nummberitemSelected.setTextColor(context.getResources().getColor(R.color.black));
        // fill Data
        viewHolder.itemName.setText(shoppingItemsDBList.get(position).getItemName());
        viewHolder.itemPrice.setText(shoppingItemsDBList.get(position).getPrice()+"€");

        int numb=shoppingItemsDBList.get(position).getNumberofItemsetForList();
        double price= Double.parseDouble(shoppingItemsDBList.get(position).getPrice())*numb;
        DecimalFormat df = new DecimalFormat("0.00");
        df.setMaximumFractionDigits(2);
        String priceStr = df.format(price);

        if(numb>0){
            viewHolder.nummberitemSelected.setTextColor(context.getResources().getColor(R.color.warning_color));
            viewHolder.removeItem.setEnabled(true);
        }else {
            viewHolder.removeItem.setEnabled(false);
        }
        viewHolder.nummberitemSelected.setText(String.valueOf(numb));
        viewHolder.totalPrice.setText(priceStr+"€");


        viewHolder.addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                viewHolder.removeItem.setEnabled(true);
                viewHolder.nummberitemSelected.setTextColor(context.getResources().getColor(R.color.warning_color));

                if(positionmemo==0 && counterItemSelected==0){
                    positionmemo=position;
                }
                if(positionmemo==position){
                    viewHolder.removeItem.setEnabled(true);
                    numberoftiemeverused=shoppingItemsDBList.get(position).getNumberoftimeAddedAnyToList();
                    counterItemSelected=shoppingItemsDBList.get(position).getNumberofItemsetForList();
                    counterItemSelected++;

                    numberoftiemeverused++;
                    numberSelectedOnrow[position]=counterItemSelected;
                    shoppingItemsDBList.get(position).setNumberofItemsetForList(counterItemSelected);
                    shoppingItemsDBList.get(position).setNumberoftimeAddedAnyToList(numberoftiemeverused);

                    viewHolder.nummberitemSelected.setText(String.valueOf(counterItemSelected));
                    double price=0.00;
                    price=Double.parseDouble(shoppingItemsDBList.get(position).getPrice())*counterItemSelected;
                    DecimalFormat df = new DecimalFormat("0.00");
                    df.setMaximumFractionDigits(2);
                    String priceStr = df.format(price);
                    if(priceStr.length()==5){
                        viewHolder.totalPrice.setTextSize(17f);

                    }else if(priceStr.length()>5){
                        viewHolder.totalPrice.setTextSize(15f);

                    }else {
                        viewHolder.totalPrice.setTextSize(18f);
                    }
                    viewHolder.totalPrice.setText(priceStr+"€");

                }else{
                    viewHolder.removeItem.setEnabled(true);
                    numberoftiemeverused=shoppingItemsDBList.get(position).getNumberoftimeAddedAnyToList();
                    counterItemSelected=shoppingItemsDBList.get(position).getNumberofItemsetForList();
                    counterItemSelected++;
                    numberoftiemeverused++;
                    numberSelectedOnrow[position]=counterItemSelected;
                    shoppingItemsDBList.get(position).setNumberofItemsetForList(counterItemSelected);

                    shoppingItemsDBList.get(position).setNumberoftimeAddedAnyToList(numberoftiemeverused);

                    viewHolder.nummberitemSelected.setText(String.valueOf(counterItemSelected));
                    double price=0.00;
                    price=Double.parseDouble(shoppingItemsDBList.get(position).getPrice())*counterItemSelected;
                    DecimalFormat df = new DecimalFormat("0.00");
                    df.setMaximumFractionDigits(2);
                    String priceStr = df.format(price);
                    if(priceStr.length()==5){
                        viewHolder.totalPrice.setTextSize(17f);

                    }else if(priceStr.length()>5){
                        viewHolder.totalPrice.setTextSize(15f);

                    }else {
                        viewHolder.totalPrice.setTextSize(18f);
                    }
                    viewHolder.totalPrice.setText(priceStr+"€");
                    positionmemo=position;

                }
                if(shoppingItemSetListener!=null){
                    shoppingItemSetListener.onShoppingOtemSet(shoppingItemsDBList.get(position),position);
                }

            }
        });
        viewHolder.removeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(positionmemo==position){

                    counterItemSelected=shoppingItemsDBList.get(position).getNumberofItemsetForList();
                    numberoftiemeverused=shoppingItemsDBList.get(position).getNumberoftimeAddedAnyToList();
                    if(counterItemSelected>0){
                        viewHolder.nummberitemSelected.setTextColor(context.getResources().getColor(R.color.warning_color));
                        viewHolder.removeItem.setEnabled(true);
                        counterItemSelected--;
                        numberoftiemeverused--;
                        numberSelectedOnrow[position]=counterItemSelected;
                        viewHolder.nummberitemSelected.setText(String.valueOf(counterItemSelected));
                        shoppingItemsDBList.get(position).setNumberofItemsetForList(counterItemSelected);
                        shoppingItemsDBList.get(position).setNumberoftimeAddedAnyToList(numberoftiemeverused);

                        double price= Double.parseDouble(shoppingItemsDBList.get(position).getPrice())*counterItemSelected;

                        DecimalFormat df = new DecimalFormat("0.00");
                        df.setMaximumFractionDigits(2);
                        String priceStr = df.format(price);

                        if(priceStr.length()==5){
                            viewHolder.totalPrice.setTextSize(17f);

                        }else if(priceStr.length()>5){
                            viewHolder.totalPrice.setTextSize(15f);

                        }else {
                            viewHolder.totalPrice.setTextSize(18f);
                        }
                        if(counterItemSelected==0){
                            viewHolder.nummberitemSelected.setTextColor(context.getResources().getColor(R.color.black));
                            viewHolder.totalPrice.setText(R.string.null_item_total_price);
                            viewHolder.removeItem.setEnabled(false);
                            numberSelectedOnrow[position]=counterItemSelected;
                            shoppingItemsDBList.get(position).setNumberofItemsetForList(counterItemSelected);

                        }else {
                            viewHolder.totalPrice.setText(priceStr+"€");
                        }


                    }else {
                        viewHolder.nummberitemSelected.setTextColor(context.getResources().getColor(R.color.black));
                        viewHolder.removeItem.setEnabled(false);
                        viewHolder.nummberitemSelected.setText(R.string.null_item_selected);
                        viewHolder.totalPrice.setText(R.string.null_item_total_price);

                        numberSelectedOnrow[position]=counterItemSelected;
                        shoppingItemsDBList.get(position).setNumberofItemsetForList(counterItemSelected);
                    }

                }else {

                    viewHolder.removeItem.setEnabled(true);
                   counterItemSelected=shoppingItemsDBList.get(position).getNumberofItemsetForList();
                    numberoftiemeverused=shoppingItemsDBList.get(position).getNumberoftimeAddedAnyToList();

                    if(counterItemSelected>0){
                        viewHolder.nummberitemSelected.setTextColor(context.getResources().getColor(R.color.warning_color));
                        viewHolder.removeItem.setEnabled(true);
                        counterItemSelected--;
                        numberoftiemeverused--;
                        numberSelectedOnrow[position]=counterItemSelected;
                        viewHolder.nummberitemSelected.setText(String.valueOf(counterItemSelected));
                        shoppingItemsDBList.get(position).setNumberofItemsetForList(counterItemSelected);

                        shoppingItemsDBList.get(position).setNumberoftimeAddedAnyToList(numberoftiemeverused);
                        double price= Double.parseDouble(shoppingItemsDBList.get(position).getPrice())*counterItemSelected;

                        DecimalFormat df = new DecimalFormat("0.00");
                        df.setMaximumFractionDigits(2);
                        String priceStr = df.format(price);

                        if(priceStr.length()==5){
                            viewHolder.totalPrice.setTextSize(17f);

                        }else if(priceStr.length()>5){
                            viewHolder.totalPrice.setTextSize(15f);

                        }else {
                            viewHolder.totalPrice.setTextSize(18f);
                        }
                        if(counterItemSelected==0){
                            viewHolder.nummberitemSelected.setTextColor(context.getResources().getColor(R.color.black));
                            viewHolder.totalPrice.setText(R.string.null_item_total_price);
                            viewHolder.removeItem.setEnabled(false);
                            numberSelectedOnrow[position]=counterItemSelected;
                            shoppingItemsDBList.get(position).setNumberofItemsetForList(counterItemSelected);

                        }else {
                            viewHolder.totalPrice.setText(priceStr+"€");
                        }



                    }else {
                        viewHolder.nummberitemSelected.setTextColor(context.getResources().getColor(R.color.black));
                        viewHolder.removeItem.setEnabled(false);
                        viewHolder.nummberitemSelected.setText(R.string.null_item_selected);
                        viewHolder.totalPrice.setText(R.string.null_item_total_price);

                        numberSelectedOnrow[position]=counterItemSelected;
                        shoppingItemsDBList.get(position).setNumberofItemsetForList(counterItemSelected);
                    }
                    positionmemo=position;

                }


                if(shoppingItemSetListener!=null){
                    shoppingItemSetListener.onShoppingOtemSet(shoppingItemsDBList.get(position),position);
                }

            }
        });

        viewHolder.removeItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                counterItemSelected=shoppingItemsDBList.get(position).getNumberofItemsetForList();
                numberoftiemeverused=shoppingItemsDBList.get(position).getNumberoftimeAddedAnyToList();
                numberoftiemeverused=numberoftiemeverused-counterItemSelected;
                counterItemSelected=0;
                shoppingItemsDBList.get(position).setNumberoftimeAddedAnyToList(numberoftiemeverused);
                shoppingItemsDBList.get(position).setNumberofItemsetForList(counterItemSelected);

                viewHolder.nummberitemSelected.setTextColor(context.getResources().getColor(R.color.black));
                viewHolder.removeItem.setEnabled(false);
                viewHolder.nummberitemSelected.setText(R.string.null_item_selected);
                viewHolder.totalPrice.setText(R.string.null_item_total_price);

                numberSelectedOnrow[position]=counterItemSelected;

                if(shoppingItemSetListener!=null){
                    shoppingItemSetListener.onShoppingOtemSet(shoppingItemsDBList.get(position),position);
                }

                return false;
            }
        });







        return convertView;
    }



    static class holder {
        public TextView itemName;
        public TextView itemPrice;
        public TextView nummberitemSelected;
        public TextView totalPrice;
        public ImageButton addItem;
        public ImageButton removeItem;

    }
}
