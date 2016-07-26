package com.example.bricenangue.timeme;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by bricenangue on 25/07/16.
 */
public class GroceryListAdapter extends BaseAdapter {


    private Context context;
    private int counterItemSelected=0;


    private static LayoutInflater inflater=null;
    private ArrayList<GroceryList> shoppingListItems;


    public GroceryListAdapter(Context oldContext, ArrayList<GroceryList> shoppingListItems)
    {
        context = oldContext;
        this.shoppingListItems = shoppingListItems;
        if(null != shoppingListItems) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

    }



    @Override
    //Anzahl der Unterschriften
    public int getCount() {
        int i = 0;
        if(shoppingListItems != null)
            i = shoppingListItems.size();

        return i;
    }


    @Override
    public Object getItem(int position) {
        return shoppingListItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        final holder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.overview_lists_created_item, null);
            viewHolder = new holder();


            viewHolder.listname = (TextView) convertView.findViewById(R.id.TextView_list_name_item);
            viewHolder.listSatuts = (TextView) convertView.findViewById(R.id.TextView_list_status_item);
            viewHolder.listCreator = (TextView) convertView.findViewById(R.id.TextView_list_creator_item);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (holder) convertView.getTag();
        }
        // fill Data
        viewHolder.listSatuts.setText(shoppingListItems.get(position).isListdone() ? R.string.grocery_list_status_done_text : R.string.grocery_list_status__not_done_text );
        viewHolder.listCreator.setText(shoppingListItems.get(position).getCreatorName());
        viewHolder.listname.setText(shoppingListItems.get(position).getDatum());




        return convertView;
    }



    static class holder {
        public TextView listname;
        public TextView listCreator;
        public TextView listSatuts;


    }
}
