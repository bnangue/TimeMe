package com.app.bricenangue.timeme;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by bricenangue on 25/07/16.
 */
public class ListAdapterCreateShopList extends BaseAdapter {
    private boolean isAllBought=false;

    public interface ShoppingItemBoughtListener{
        void onShoppingItemBought(ShoppingItem item,boolean[] positions,int position);
    }


    private ShoppingItemBoughtListener shoppingItemSetListener;
    private Context context;
    private int counterItemSelected=0;


    private static LayoutInflater inflater=null;
    private ArrayList<ShoppingItem> shoppingItemsList;

    //Array mit allen UnterschriftenObjekten
//    Unterschrift [] unterschriftenArray;


    private int numberOfSelectedItems,positionmemo=0;

    //wird von SignatureActivity übergeben
    private boolean[] numberSelectedOnrow;
    private boolean bol = false;

    public ListAdapterCreateShopList(Context oldContext, ArrayList<ShoppingItem> shoppingItemsDBList,ShoppingItemBoughtListener shoppingItemSetListener,boolean isAllBought)
    {
        this.isAllBought=isAllBought;
        context = oldContext;
        this.shoppingItemsList = shoppingItemsDBList;
        if(null != shoppingItemsDBList) {
            numberSelectedOnrow=new boolean[shoppingItemsDBList.size()];
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.shoppingItemSetListener=shoppingItemSetListener;
        }

    }

    public void setSelectedItems(boolean [] itemsArray,int numberOfSelectedItem){
        numberSelectedOnrow = itemsArray;

        numberOfSelectedItems=numberOfSelectedItem;
        notifyDataSetChanged();
    }

    @Override
    //Anzahl der Unterschriften
    public int getCount() {
        int i = 0;
        if(shoppingItemsList != null)
            i = shoppingItemsList.size();

        return i;
    }


    @Override
    public Object getItem(int position) {
        return shoppingItemsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        final holder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.shopping_list_items, null);
            viewHolder = new holder();


            viewHolder.itemName = (TextView) convertView.findViewById(R.id.TextView_list_item_title_itemcreate_shopping_list);
            viewHolder.itemPrice = (TextView) convertView.findViewById(R.id.TextView_list_item_price_itemcreate_shopping_list);

            viewHolder.itemCategory = (TextView) convertView.findViewById(R.id.TextView_shopping_list_item_categorycreate_shopping_list);
            viewHolder.itemMArket = (TextView) convertView.findViewById(R.id.TextView_shopping_list_item_marketcreate_shopping_list);
            viewHolder.nummberitemSelected = (TextView) convertView.findViewById(R.id.TextView_list_item_number_of_item_selectedcreate_shopping_list);
            viewHolder.totalPrice = (TextView) convertView.findViewById(R.id.TextView_list_item_total_item_pricecreate_shopping_list);
            viewHolder.buttonisbought = (ImageButton) convertView.findViewById(R.id.checkerImageViewcreate_shopping_list);


            convertView.setTag(viewHolder);

        } else {
            viewHolder = (holder) convertView.getTag();
        }
        viewHolder.nummberitemSelected.setTextColor(context.getResources().getColor(R.color.black));
        // fill Data
        viewHolder.itemName.setText(shoppingItemsList.get(position).getItemName());
        viewHolder.itemPrice.setText(shoppingItemsList.get(position).getPrice()+" €");

        String itemCategory="("+shoppingItemsList.get(position).getItemcategory()+")";
        viewHolder.itemCategory.setText(itemCategory);
        viewHolder.itemMArket.setText(shoppingItemsList.get(position).getItemmarket());


        int numb=shoppingItemsList.get(position).getNumberofItemsetForList();
        double price= Double.parseDouble(shoppingItemsList.get(position).getPrice())*numb;
        DecimalFormat df = new DecimalFormat("0.00");
        df.setMaximumFractionDigits(2);
        String priceStr = df.format(price);


        viewHolder.nummberitemSelected.setText(String.valueOf(numb));
        if(priceStr.length()==5){
            viewHolder.totalPrice.setTextSize(17f);
            viewHolder.totalPrice.setText(priceStr+" €");
        }else if(priceStr.length()>5){
            viewHolder.totalPrice.setTextSize(14f);
            viewHolder.totalPrice.setText(priceStr+ " €");
        }else {
            viewHolder.totalPrice.setText(priceStr+" €");
        }

        if(isAllBought){
            viewHolder.buttonisbought.setClickable(false);
            viewHolder.buttonisbought.setEnabled(false);
        }

        viewHolder.buttonisbought.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(shoppingItemsList.get(position).isItemIsBought()){
                    shoppingItemsList.get(position).setItemIsBought(false);
                    numberSelectedOnrow[position]=false;
                }else {
                    shoppingItemsList.get(position).setItemIsBought(true);
                    numberSelectedOnrow[position]=true;
                }

                if(shoppingItemSetListener!=null){
                    shoppingItemSetListener.onShoppingItemBought(shoppingItemsList.get(position),numberSelectedOnrow,position);
                }
            }
        });

        if (shoppingItemsList.get(position).isItemIsBought()) {
            viewHolder.buttonisbought.setImageResource(R.drawable.checked);

        }else{
            viewHolder.buttonisbought.setImageResource(R.drawable.unchecked);

        }


        return convertView;
    }



    static class holder {
        public TextView itemName;
        public TextView itemPrice;
        public TextView itemCategory;
        public TextView itemMArket;
        public TextView nummberitemSelected;
        public TextView totalPrice;
        public ImageButton buttonisbought;

    }

}
