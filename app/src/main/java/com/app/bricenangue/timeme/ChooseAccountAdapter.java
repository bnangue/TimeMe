package com.app.bricenangue.timeme;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by bricenangue on 18/08/16.
 */
public class ChooseAccountAdapter extends BaseAdapter {
    private boolean isAllBought=false;

    public interface OnAccountChooseListener{
        void onAccountChoosed(FinanceAccount financeAccount,boolean[] selected);
    }


    private OnAccountChooseListener onAccountChooseListener;
    private Context context;


    private static LayoutInflater inflater=null;
    private ArrayList<FinanceAccount> accountsNames;

    //wird von SignatureActivity übergeben
    private boolean[] numberSelectedOnrow;
    private boolean bol = false;

    public ChooseAccountAdapter(Context oldContext, ArrayList<FinanceAccount> accountsNames, OnAccountChooseListener onAccountChooseListener)
    {
        context = oldContext;
        this.accountsNames = accountsNames;
        if(null != accountsNames) {
            numberSelectedOnrow=new boolean[accountsNames.size()];
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.onAccountChooseListener=onAccountChooseListener;
        }

    }

    public void setNumberSelectedOnrow(boolean [] selectedOnrow){
        this.numberSelectedOnrow=selectedOnrow;
    }
    @Override
    //Anzahl der Unterschriften
    public int getCount() {
        int i = 0;
        if(accountsNames != null)
            i = accountsNames.size();

        return i;
    }


    @Override
    public Object getItem(int position) {
        return accountsNames.get(position);
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

        viewHolder.sortName.setText(accountsNames.get(position).getAccountName());

        viewHolder.buttonisChecked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                for(int i=0;i<numberSelectedOnrow.length;i++){
                    numberSelectedOnrow[i]=false;
                }
                numberSelectedOnrow[position]=true;
                if(onAccountChooseListener!=null){
                    onAccountChooseListener.onAccountChoosed(accountsNames.get(position),numberSelectedOnrow);
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
