package com.app.bricenangue.timeme;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by bricenangue on 16/08/16.
 */
public class SortOptionListAdapter  extends BaseAdapter {
    private boolean isAllBought=false;

    public interface OnSortOptionsListener{
        void onSortOption(String sortOption,boolean[] selected);
    }


    private OnSortOptionsListener onSortOptionsListener;
    private Context context;


    private static LayoutInflater inflater=null;
    private ArrayList<String> sortOptions;

    //Array mit allen UnterschriftenObjekten
//    Unterschrift [] unterschriftenArray;


    private int numberOfSelectedItems,positionmemo=0;

    //wird von SignatureActivity übergeben
    private boolean[] numberSelectedOnrow;
    private boolean bol = false;

    public SortOptionListAdapter(Context oldContext, ArrayList<String> sortOptions, OnSortOptionsListener onSortOptionsListener)
    {
        context = oldContext;
        this.sortOptions = sortOptions;
        if(null != sortOptions) {
            numberSelectedOnrow=new boolean[sortOptions.size()];
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.onSortOptionsListener=onSortOptionsListener;
        }

    }

    public void setNumberSelectedOnrow(boolean [] selectedOnrow){
        this.numberSelectedOnrow=selectedOnrow;
    }
    @Override
    //Anzahl der Unterschriften
    public int getCount() {
        int i = 0;
        if(sortOptions != null)
            i = sortOptions.size();

        return i;
    }


    @Override
    public Object getItem(int position) {
        return sortOptions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        final holder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.custom_sort_options_item, null);
            viewHolder = new holder();


            viewHolder.sortName = (TextView) convertView.findViewById(R.id.textView_sort_options_items);

            viewHolder.buttonisChecked = (RadioButton) convertView.findViewById(R.id.radioButton_sort_options_items);


            convertView.setTag(viewHolder);

        } else {
            viewHolder = (holder) convertView.getTag();
        }

        viewHolder.sortName.setText(sortOptions.get(position));

        viewHolder.buttonisChecked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                for(int i=0;i<numberSelectedOnrow.length;i++){
                            numberSelectedOnrow[i]=false;
                }
                numberSelectedOnrow[position]=true;
                if(onSortOptionsListener!=null){
                    onSortOptionsListener.onSortOption(sortOptions.get(position),numberSelectedOnrow);
                }

            }
        });


        if(numberSelectedOnrow[position]){
            viewHolder.buttonisChecked.setChecked(true);

        }else {
            viewHolder.buttonisChecked.setChecked(false);

        }

        return convertView;
    }



    static class holder {
        public TextView sortName;

        public RadioButton buttonisChecked;

    }

}
