package com.example.bricenangue.timeme;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by bricenangue on 04/08/16.
 */
public class DetailsFinanceAccountAdapter extends BaseAdapter {


    private Context context;

    private static LayoutInflater inflater=null;
    private ArrayList<FinanceRecords> recordsArrayList;


    public DetailsFinanceAccountAdapter(Context oldContext, ArrayList<FinanceRecords> recordsArrayList)
    {
        context = oldContext;
        this.recordsArrayList = recordsArrayList;
        if(null != recordsArrayList) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

    }

    @Override
    //Anzahl der Unterschriften
    public int getCount() {
        int i = 0;
        if(recordsArrayList != null)
            i = recordsArrayList.size();

        return i;
    }


    @Override
    public Object getItem(int position) {
        return recordsArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        final holder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.finance_record_details_item, null);
            viewHolder = new holder();


            viewHolder.recordName = (TextView) convertView.findViewById(R.id.TextView_finance_recored_details_item_record_name);
            viewHolder.recordDate = (TextView) convertView.findViewById(R.id.TextView_finance_recored_details_item_record_creation_date);
            viewHolder.recordAmount = (TextView) convertView.findViewById(R.id.TextView_finance_recored_details_item_record_amount);
            viewHolder.recordAmountpendinttext=(TextView)convertView.findViewById(R.id.TextView_finance_recored_details_item_record_amount_pending) ;



            convertView.setTag(viewHolder);

        } else {
            viewHolder = (holder) convertView.getTag();
        }

        //set Views here
        viewHolder.recordName.setText(recordsArrayList.get(position).getRecordNAme());
        viewHolder.recordDate.setText(recordsArrayList.get(position).getRecordBookingDate());


        if(recordsArrayList.get(position).isIncome()){
            String p= recordsArrayList.get(position).getRecordAmount()+" €";
            if(!checkBookingDate(recordsArrayList.get(position).getRecordBookingDate()) ){
                viewHolder.recordAmount.setTextColor(context.getResources().getColor(R.color.color_account_balance_positive));
                viewHolder.recordAmount.setText(p);
                viewHolder.recordAmountpendinttext.setVisibility(View.VISIBLE);
                viewHolder.recordAmountpendinttext.setText(context.getString(R.string.account_finance_card_pending_expenses));
            }else {
                viewHolder.recordAmountpendinttext.setVisibility(View.GONE);
                viewHolder.recordAmount.setTextColor(context.getResources().getColor(R.color.color_account_balance_positive));
                viewHolder.recordAmount.setText(p);

            }

        }else {
            String p= recordsArrayList.get(position).getRecordAmount()+" €";
            if(recordsArrayList.get(position).getRecordNAme().equals("Grocery list")){
                // grocery list done all item bought substract from account balance

                if(recordsArrayList.get(position).isSecured()){
                    viewHolder.recordAmountpendinttext.setVisibility(View.GONE);

                    viewHolder.recordAmount.setTextColor(context.getResources().getColor(R.color.warning_color));
                    viewHolder.recordAmount.setText(p);
                }else {
                    //grocrey list not done yet  do not substract from account balance amount
                    viewHolder.recordAmountpendinttext.setVisibility(View.VISIBLE);
                    viewHolder.recordAmountpendinttext.setText(context.getString(R.string.account_finance_card_pending_expenses));
                    viewHolder.recordAmount.setTextColor(context.getResources().getColor(R.color.grey));
                    viewHolder.recordAmount.setText(p);
                }

            }else {
                if(!checkBookingDate(recordsArrayList.get(position).getRecordBookingDate())  ){
                    viewHolder.recordAmountpendinttext.setVisibility(View.VISIBLE);
                    viewHolder.recordAmount.setTextColor(context.getResources().getColor(R.color.warning_color));
                    viewHolder.recordAmountpendinttext.setText(context.getString(R.string.account_finance_card_pending_expenses));
                    viewHolder.recordAmount.setText(p);
                }else {
                    viewHolder.recordAmountpendinttext.setVisibility(View.GONE);
                    viewHolder.recordAmount.setTextColor(context.getResources().getColor(R.color.warning_color));
                    viewHolder.recordAmount.setText(p);
                }

            }
        }
        return convertView;
    }


    private boolean checkBookingDate(String bookingDate){
        Calendar c=new GregorianCalendar();
        Date dat=c.getTime();
        String date = (String) android.text.format.DateFormat.format("dd-MM-yyyy", dat);
        String[] dateArray=date.split("-");

        String[] datePartnow = date.split("-");
        if(bookingDate==null || bookingDate.isEmpty()){
            return true;
        }
        String[] datePartlast = bookingDate.split("-");

        if(datePartnow[1].equals(datePartlast[1])){
            int daynow=Integer.parseInt(datePartnow[0]);
            int dayrecord=Integer.parseInt(datePartlast[0]);

            if(daynow>=dayrecord){
                return true;
            }else {
                return false;
            }

        }else {
            int monthnow=Integer.parseInt(datePartnow[1]);
            int monthrecord=Integer.parseInt(datePartlast[1]);
            if(monthnow >= monthrecord){
                return true;
            }else {
                return false;
            }
        }
    }

    static class holder {
        public TextView recordName;
        public TextView recordDate;
        public TextView recordAmount;
        public TextView recordAmountpendinttext;

    }
}
