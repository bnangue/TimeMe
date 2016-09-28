package com.app.bricenangue.timeme;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.data.Entry;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;

public class FinanceChartActivity extends AppCompatActivity {
    private FinanceAccount financeAccount;
    private ArrayList<FinanceRecords> financeRecordses=new ArrayList<>();
    private int numberofMonthsSinceCreation=0;
    private PieChart mPieChart;
    private FinanceRecords financeRecordsAccountInit;
    // we're going to display pie chart for smartphones martket shares
    private float[] yData =new float[4];
    private String[] xData = { "Grocery", "Leisure", "Traveling", "Personal"};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BarChart barChart=new BarChart(this);
        LineChart lineChart=new LineChart(this);
        mPieChart=new PieChart(this);
        setContentView(barChart);

        Bundle extras=getIntent().getExtras();
        if(extras!=null){
            financeAccount=extras.getParcelable("financeAccount");
        }
        if(financeAccount!=null){
            financeRecordses=financeAccount.getAccountsRecord();
            financeRecordsAccountInit=financeAccount.getAccountInitRecord();
            numberofMonthsSinceCreation=financeAccount.getNumberofMonthsSinceCreation();
            String title=getString(R.string.gaccount_title_text_detail_fiannce_account)
                    +" "+financeAccount.getAccountName();

            setTitle(title);
        }
/**
        ArrayList<Entry>entries = new ArrayList();
                entries.add(new Entry(4f, 0));
                entries.add(new Entry(8f, 1));
                entries.add(new Entry(6f, 2));
                entries.add(new Entry(2f, 3));
                entries.add(new Entry(18f, 4));
                entries.add(new Entry(9f, 5));

        LineDataSet lineDataSet= new LineDataSet(entries,"test");



        ArrayList<String> labels = new ArrayList();
                labels.add("January");
                labels.add("February");
                labels.add("March");
                labels.add("April");
                labels.add("May");
                labels.add("June");

        LineData lineData=new LineData(getXAxisValues(),getLineDataSet());
        lineChart.setData(lineData);
        lineChart.setDescription("description");
        lineDataSet.setDrawFilled(true);
        lineDataSet.setColor(getResources().getColor(R.color.primary));
        lineDataSet.setFillColor(Color.rgb(0, 155, 0));
        **/


        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<String>();
        if (financeRecordses!=null && financeRecordses.size()!=0) {
            for (int i = 0; i < financeRecordses.size(); i++) {

                entries.add(new BarEntry(Float.parseFloat(financeRecordses.get(i).getRecordAmount()), i));

                switch (financeRecordses.get(i).getRecordBookingDate().split("-")[1]) {

                }
                labels.add("label");

                //entries.add(new BarEntry(4f, 0));
            }
        }

                BarDataSet dataset = new BarDataSet(entries, "# of Calls");




        BarData data = new BarData(getXAxisValues(), getBarDataSet());

        barChart.setData(data);

        barChart.setDescription("# of times Alice called Bob");

       // barChart.setMaxVisibleValueCount(12);
        barChart.setVisibleXRangeMaximum(15);
        //move to current month +2
        barChart.moveViewToX(10);
        barChart.animateY(1000);
        barChart.animateX(1000);
        LimitLine line = new LimitLine(400f);
        YAxis yAxis = barChart.getAxisLeft();
        yAxis.addLimitLine(line);




        }

    private ArrayList<ILineDataSet> getLineDataSet() {
        financeRecordses=financeAccount.getAccountsRecord();

        double accountInitBAlance=toDouble(financeRecordsAccountInit.getRecordAmount());

        ArrayList<ILineDataSet> dataSets = null;

        ArrayList<Entry> valueSet1 = new ArrayList<>();

        ArrayList<ArrayList<FinanceRecords>>financesrec=getRecordsPerMonths();

        for(int j=0 ;j<financesrec.size();j++){

            ArrayList<FinanceRecords> financeRecordses1=financesrec.get(j);

            if (financeRecordses1!=null && financeRecordses1.size()!=0) {

                Collections.sort(financeRecordses1, new ComparatorSortFinanceDetailsBookingDate());
                ArrayList<ArrayList<FinanceRecords>> recordsesPerDay=getRecordsPerDays(financeRecordses1);

                for(int d=0; d<recordsesPerDay.size();d++){

                    ArrayList<FinanceRecords> recordsesPerDay1=recordsesPerDay.get(d);

                    if(recordsesPerDay1!=null && recordsesPerDay1.size()!=0){

                        Entry v1e1 = new Entry(Float.parseFloat(getSummeRecordsAmount(accountInitBAlance,recordsesPerDay1)), d);
                        valueSet1.add(v1e1);
                        accountInitBAlance=toDouble(getSummeRecordsAmount(accountInitBAlance,recordsesPerDay1));
                    }else{
                        Entry v1e1 = new Entry(0f, d);
                        valueSet1.add(v1e1);
                    }


                }


            }else {


                /**
                BarEntry v1e1 = new BarEntry(0f, j);
                valueSet1.add(v1e1);

                BarEntry v2e1 = new BarEntry(0f, j);
                valueSet2.add(v2e1);

                **/
            }
        }


        LineDataSet lineDataSet1 = new LineDataSet(valueSet1, "Income");
       // barDataSet1.setColor(Color.rgb(0, 155, 0));
       // BarDataSet barDataSet2 = new BarDataSet(valueSet2, "Expenditure");
        //barDataSet2.setColor(getResources().getColor(R.color.warning_color));

        dataSets = new ArrayList<>();
        dataSets.add(lineDataSet1);
       // dataSets.add(barDataSet2);
        return dataSets;
    }

    private ArrayList<IBarDataSet> getBarDataSet() {
        financeRecordses=financeAccount.getAccountsRecord();

        ArrayList<IBarDataSet> dataSets = null;

        ArrayList<BarEntry> valueSet2 = new ArrayList<>();
        ArrayList<BarEntry> valueSet1 = new ArrayList<>();

        ArrayList<ArrayList<FinanceRecords>>financesrec=getRecordsPerMonths();

        for(int j=0 ;j<financesrec.size();j++){

            ArrayList<FinanceRecords> financeRecordses1=financesrec.get(j);

            if (financeRecordses1!=null && financeRecordses1.size()!=0) {

                BarEntry v1e1 = new BarEntry(Float.parseFloat(getSummeIncome(financeRecordses1)), j);
                valueSet1.add(v1e1);

                BarEntry v2e1 = new BarEntry(Float.parseFloat(getSummeExpenditure(financeRecordses1)), j);
                valueSet2.add(v2e1);

            }else {

                    BarEntry v1e1 = new BarEntry(0f, j);
                    valueSet1.add(v1e1);

                    BarEntry v2e1 = new BarEntry(0f, j);
                    valueSet2.add(v2e1);

            }
        }


        BarDataSet barDataSet1 = new BarDataSet(valueSet1, "Income");
        barDataSet1.setColor(Color.rgb(0, 155, 0));
        BarDataSet barDataSet2 = new BarDataSet(valueSet2, "Expenditure");
        barDataSet2.setColor(getResources().getColor(R.color.warning_color));

        dataSets = new ArrayList<>();
        dataSets.add(barDataSet1);
        dataSets.add(barDataSet2);
        return dataSets;
    }

    private ArrayList<String> getXAxisValues() {
        ArrayList<String> xAxis = new ArrayList<>();
        String[] months=getMonths();
        for (int i = 0; i < months.length; i++) {
            if(months[i]!=null){
                xAxis.add(months[i]);
            }

        }
        return xAxis;
    }

    private String[] getDays() {
        String[] days=new String[31];
        days[0]="01";

        days[1]="02";

        days[2]="03";

        days[3]="04";
        days[4]="05";

        days[5]="06";

        days[6]="07";

        days[7]="08";
        days[8]="09";

        days[9]="10";

        days[10]="11";

        days[11]="12";

        days[12]="13";

        days[13]="14";

        days[14]="15";

        days[15]="16";
        days[16]="17";

        days[17]="18";

        days[18]="19";

        days[19]="20";
        days[20]="21";

        days[21]="22";

        days[22]="23";

        days[23]="24";
        days[24]="25";

        days[25]="26";

        days[26]="27";

        days[27]="28";
        days[28]="29";

        days[29]="30";

        days[30]="31";


        return days;
    }


    private ArrayList<ArrayList<FinanceRecords>> getRecordsPerDays(ArrayList<FinanceRecords> recordsOfaMonth){

        ArrayList<ArrayList<FinanceRecords>> recordsPermonths=new ArrayList<>();
        ArrayList<ArrayList<FinanceRecords>> recordsPermonths1=new ArrayList<>();
        int count=0;
        ArrayList<FinanceRecords>[] months=new ArrayList [31];
        if(recordsOfaMonth!=null && recordsOfaMonth.size()!=0){


            for (int k = 0; k < recordsOfaMonth.size(); k++) {
                ArrayList<FinanceRecords> recordsArrayList=new ArrayList<>();
                FinanceRecords financeRecord=recordsOfaMonth.get(k);
                String[] bookingDate=financeRecord.getRecordBookingDate().split("-");
                String month=bookingDate[0];
                switch (month){
                    case "01":
                        if(months[0]!=null){
                            recordsArrayList=months[0];
                            recordsArrayList.add(financeRecord);
                            months[0]=recordsArrayList;
                        }else {
                            recordsArrayList.add(financeRecord);
                            months[0]=recordsArrayList;
                        }

                        break;
                    case "02":
                        if(months[1]!=null){
                            recordsArrayList=months[1];
                            recordsArrayList.add(financeRecord);
                            months[1]=recordsArrayList;
                        }else {
                            recordsArrayList.add(financeRecord);
                            months[1]=recordsArrayList;
                        }
                        break;
                    case "03":
                        if(months[2]!=null){
                            recordsArrayList=months[2];
                            recordsArrayList.add(financeRecord);
                            months[2]=recordsArrayList;
                        }else {
                            recordsArrayList.add(financeRecord);
                            months[2]=recordsArrayList;
                        }
                        break;
                    case "04":
                        if(months[3]!=null){
                            recordsArrayList=months[3];
                            recordsArrayList.add(financeRecord);
                            months[3]=recordsArrayList;
                        }else {
                            recordsArrayList.add(financeRecord);
                            months[3]=recordsArrayList;
                        }
                        break;
                    case "05":
                        if(months[4]!=null){
                            recordsArrayList=months[4];
                            recordsArrayList.add(financeRecord);
                            months[4]=recordsArrayList;
                        }else {
                            recordsArrayList.add(financeRecord);
                            months[4]=recordsArrayList;
                        }
                        break;
                    case"06":
                        if(months[5]!=null){
                            recordsArrayList=months[5];
                            recordsArrayList.add(financeRecord);
                            months[5]=recordsArrayList;
                        }else {
                            recordsArrayList.add(financeRecord);
                            months[5]=recordsArrayList;
                        }
                        break;
                    case"07":
                        if(months[6]!=null){
                            recordsArrayList=months[6];
                            recordsArrayList.add(financeRecord);
                            months[6]=recordsArrayList;
                        }else {
                            recordsArrayList.add(financeRecord);
                            months[6]=recordsArrayList;
                        }
                        break;
                    case "08":
                        if(months[7]!=null){
                            recordsArrayList=months[7];
                            recordsArrayList.add(financeRecord);
                            months[7]=recordsArrayList;
                        }else {
                            recordsArrayList.add(financeRecord);
                            months[7]=recordsArrayList;
                        }
                        break;
                    case "09":
                        if(months[8]!=null){
                            recordsArrayList=months[8];
                            recordsArrayList.add(financeRecord);
                            months[8]=recordsArrayList;
                        }else {
                            recordsArrayList.add(financeRecord);
                            months[8]=recordsArrayList;
                        }
                        break;

                    case "10":
                        if(months[9]!=null){
                            recordsArrayList=months[9];
                            recordsArrayList.add(financeRecord);
                            months[9]=recordsArrayList;
                        }else {
                            recordsArrayList.add(financeRecord);
                            months[9]=recordsArrayList;
                        }
                        break;

                    case "11":
                        if(months[10]!=null){
                            recordsArrayList=months[10];
                            recordsArrayList.add(financeRecord);
                            months[10]=recordsArrayList;
                        }else {
                            recordsArrayList.add(financeRecord);
                            months[10]=recordsArrayList;
                        }
                        break;

                    case "12":
                        if(months[11]!=null){
                            recordsArrayList=months[11];
                            recordsArrayList.add(financeRecord);
                            months[11]=recordsArrayList;
                        }else {
                            recordsArrayList.add(financeRecord);
                            months[11]=recordsArrayList;
                        }
                        break;
                    case "13":
                        if(months[12]!=null){
                            recordsArrayList=months[12];
                            recordsArrayList.add(financeRecord);
                            months[12]=recordsArrayList;
                        }else {
                            recordsArrayList.add(financeRecord);
                            months[12]=recordsArrayList;
                        }

                        break;
                    case "14":
                        if(months[13]!=null){
                            recordsArrayList=months[13];
                            recordsArrayList.add(financeRecord);
                            months[13]=recordsArrayList;
                        }else {
                            recordsArrayList.add(financeRecord);
                            months[13]=recordsArrayList;
                        }
                        break;
                    case "15":
                        if(months[14]!=null){
                            recordsArrayList=months[14];
                            recordsArrayList.add(financeRecord);
                            months[14]=recordsArrayList;
                        }else {
                            recordsArrayList.add(financeRecord);
                            months[14]=recordsArrayList;
                        }
                        break;
                    case "16":
                        if(months[15]!=null){
                            recordsArrayList=months[15];
                            recordsArrayList.add(financeRecord);
                            months[15]=recordsArrayList;
                        }else {
                            recordsArrayList.add(financeRecord);
                            months[15]=recordsArrayList;
                        }
                        break;
                    case "17":
                        if(months[16]!=null){
                            recordsArrayList=months[16];
                            recordsArrayList.add(financeRecord);
                            months[16]=recordsArrayList;
                        }else {
                            recordsArrayList.add(financeRecord);
                            months[16]=recordsArrayList;
                        }
                        break;
                    case"18":
                        if(months[17]!=null){
                            recordsArrayList=months[17];
                            recordsArrayList.add(financeRecord);
                            months[17]=recordsArrayList;
                        }else {
                            recordsArrayList.add(financeRecord);
                            months[17]=recordsArrayList;
                        }
                        break;
                    case"19":
                        if(months[18]!=null){
                            recordsArrayList=months[18];
                            recordsArrayList.add(financeRecord);
                            months[18]=recordsArrayList;
                        }else {
                            recordsArrayList.add(financeRecord);
                            months[18]=recordsArrayList;
                        }
                        break;
                    case "20":
                        if(months[19]!=null){
                            recordsArrayList=months[19];
                            recordsArrayList.add(financeRecord);
                            months[19]=recordsArrayList;
                        }else {
                            recordsArrayList.add(financeRecord);
                            months[19]=recordsArrayList;
                        }
                        break;
                    case "21":
                        if(months[20]!=null){
                            recordsArrayList=months[20];
                            recordsArrayList.add(financeRecord);
                            months[20]=recordsArrayList;
                        }else {
                            recordsArrayList.add(financeRecord);
                            months[20]=recordsArrayList;
                        }
                        break;

                    case "22":
                        if(months[21]!=null){
                            recordsArrayList=months[21];
                            recordsArrayList.add(financeRecord);
                            months[21]=recordsArrayList;
                        }else {
                            recordsArrayList.add(financeRecord);
                            months[21]=recordsArrayList;
                        }
                        break;

                    case "23":
                        if(months[22]!=null){
                            recordsArrayList=months[22];
                            recordsArrayList.add(financeRecord);
                            months[22]=recordsArrayList;
                        }else {
                            recordsArrayList.add(financeRecord);
                            months[22]=recordsArrayList;
                        }
                        break;
                    case "24":
                        if(months[23]!=null){
                            recordsArrayList=months[23];
                            recordsArrayList.add(financeRecord);
                            months[23]=recordsArrayList;
                        }else {
                            recordsArrayList.add(financeRecord);
                            months[23]=recordsArrayList;
                        }

                        break;
                    case "25":
                        if(months[24]!=null){
                            recordsArrayList=months[24];
                            recordsArrayList.add(financeRecord);
                            months[24]=recordsArrayList;
                        }else {
                            recordsArrayList.add(financeRecord);
                            months[24]=recordsArrayList;
                        }
                        break;
                    case "26":
                        if(months[25]!=null){
                            recordsArrayList=months[25];
                            recordsArrayList.add(financeRecord);
                            months[25]=recordsArrayList;
                        }else {
                            recordsArrayList.add(financeRecord);
                            months[25]=recordsArrayList;
                        }
                        break;
                    case "27":
                        if(months[26]!=null){
                            recordsArrayList=months[26];
                            recordsArrayList.add(financeRecord);
                            months[26]=recordsArrayList;
                        }else {
                            recordsArrayList.add(financeRecord);
                            months[26]=recordsArrayList;
                        }
                        break;
                    case "28":
                        if(months[27]!=null){
                            recordsArrayList=months[27];
                            recordsArrayList.add(financeRecord);
                            months[27]=recordsArrayList;
                        }else {
                            recordsArrayList.add(financeRecord);
                            months[27]=recordsArrayList;
                        }
                        break;
                    case"29":
                        if(months[28]!=null){
                            recordsArrayList=months[28];
                            recordsArrayList.add(financeRecord);
                            months[28]=recordsArrayList;
                        }else {
                            recordsArrayList.add(financeRecord);
                            months[28]=recordsArrayList;
                        }
                        break;
                    case"30":
                        if(months[29]!=null){
                            recordsArrayList=months[29];
                            recordsArrayList.add(financeRecord);
                            months[29]=recordsArrayList;
                        }else {
                            recordsArrayList.add(financeRecord);
                            months[29]=recordsArrayList;
                        }
                        break;
                    case "31":
                        if(months[30]!=null){
                            recordsArrayList=months[30];
                            recordsArrayList.add(financeRecord);
                            months[30]=recordsArrayList;
                        }else {
                            recordsArrayList.add(financeRecord);
                            months[30]=recordsArrayList;
                        }
                        break;

                }

            }


            for(int i=0;i<months.length;i++){
                recordsPermonths1.add(months[i]);
                if (months[i]!=null){
                    recordsPermonths.add(months[i]);
                }
            }

        }
        return recordsPermonths1;

    }

    private String[] getMonths(){
        String[] months=new String[12];
        months[0]=getString(R.string.january);

        months[1]=getString(R.string.february);

        months[2]=getString(R.string.march);

        months[3]=getString(R.string.april);
        months[4]=getString(R.string.may);

        months[5]=getString(R.string.june);

        months[6]=getString(R.string.july);

        months[7]=getString(R.string.august);
        months[8]=getString(R.string.septmeber);

        months[9]=getString(R.string.october);

        months[10]=getString(R.string.november);

        months[11]=getString(R.string.december);

        if(financeRecordses!=null && financeRecordses.size()!=0){

            for (int k = 0; k < financeRecordses.size(); k++) {
                FinanceRecords financeRecord=financeRecordses.get(k);
                String[] bookingDate=financeRecord.getRecordBookingDate().split("-");
                String month=bookingDate[1];

                /**
                 switch (month){
                 case "01":

                 months[0]=getString(R.string.january);


                 break;
                 case "02":

                 months[1]=getString(R.string.february);

                 break;
                 case "03":

                 months[2]=getString(R.string.march);

                 break;
                 case "04":

                 months[3]=getString(R.string.april);

                 break;
                 case "05":
                 months[4]=getString(R.string.may);

                 break;
                 case"06":

                 months[5]=getString(R.string.june);

                 break;
                 case"07":

                 months[6]=getString(R.string.july);

                 break;
                 case "08":

                 months[7]=getString(R.string.august);

                 break;
                 case "09":

                 months[8]=getString(R.string.septmeber);

                 break;

                 case "10":

                 months[9]=getString(R.string.october);

                 break;

                 case "11":

                 months[10]=getString(R.string.november);

                 break;

                 case "12":

                 months[11]=getString(R.string.december);
                 break;


                 }
                **/

            }
        }
        return months;
    }


    private ArrayList<ArrayList<FinanceRecords>> getRecordsPerMonths(){
        ArrayList<ArrayList<FinanceRecords>> recordsPermonths=new ArrayList<>();
        ArrayList<ArrayList<FinanceRecords>> recordsPermonths1=new ArrayList<>();
        int count=0;
        ArrayList<FinanceRecords>[] months=new ArrayList [12];
        if(financeRecordses!=null && financeRecordses.size()!=0){


            for (int k = 0; k < financeRecordses.size(); k++) {
                ArrayList<FinanceRecords> recordsArrayList=new ArrayList<>();
                FinanceRecords financeRecord=financeRecordses.get(k);
                String[] bookingDate=financeRecord.getRecordBookingDate().split("-");
                String month=bookingDate[1];
                switch (month){
                    case "01":
                        if(months[0]!=null){
                            recordsArrayList=months[0];
                            recordsArrayList.add(financeRecord);
                            months[0]=recordsArrayList;
                        }else {
                            recordsArrayList.add(financeRecord);
                            months[0]=recordsArrayList;
                        }

                        break;
                    case "02":
                        if(months[1]!=null){
                            recordsArrayList=months[1];
                            recordsArrayList.add(financeRecord);
                            months[1]=recordsArrayList;
                        }else {
                            recordsArrayList.add(financeRecord);
                            months[1]=recordsArrayList;
                        }
                        break;
                    case "03":
                        if(months[2]!=null){
                            recordsArrayList=months[2];
                            recordsArrayList.add(financeRecord);
                            months[2]=recordsArrayList;
                        }else {
                            recordsArrayList.add(financeRecord);
                            months[2]=recordsArrayList;
                        }
                        break;
                    case "04":
                        if(months[3]!=null){
                            recordsArrayList=months[3];
                            recordsArrayList.add(financeRecord);
                            months[3]=recordsArrayList;
                        }else {
                            recordsArrayList.add(financeRecord);
                            months[3]=recordsArrayList;
                        }
                        break;
                    case "05":
                        if(months[4]!=null){
                            recordsArrayList=months[4];
                            recordsArrayList.add(financeRecord);
                            months[4]=recordsArrayList;
                        }else {
                            recordsArrayList.add(financeRecord);
                            months[4]=recordsArrayList;
                        }
                        break;
                    case"06":
                        if(months[5]!=null){
                            recordsArrayList=months[5];
                            recordsArrayList.add(financeRecord);
                            months[5]=recordsArrayList;
                        }else {
                            recordsArrayList.add(financeRecord);
                            months[5]=recordsArrayList;
                        }
                        break;
                    case"07":
                        if(months[6]!=null){
                            recordsArrayList=months[6];
                            recordsArrayList.add(financeRecord);
                            months[6]=recordsArrayList;
                        }else {
                            recordsArrayList.add(financeRecord);
                            months[6]=recordsArrayList;
                        }
                        break;
                    case "08":
                        if(months[7]!=null){
                            recordsArrayList=months[7];
                            recordsArrayList.add(financeRecord);
                            months[7]=recordsArrayList;
                        }else {
                            recordsArrayList.add(financeRecord);
                            months[7]=recordsArrayList;
                        }
                        break;
                    case "09":
                        if(months[8]!=null){
                            recordsArrayList=months[8];
                            recordsArrayList.add(financeRecord);
                            months[8]=recordsArrayList;
                        }else {
                            recordsArrayList.add(financeRecord);
                            months[8]=recordsArrayList;
                        }
                        break;

                    case "10":
                        if(months[9]!=null){
                            recordsArrayList=months[9];
                            recordsArrayList.add(financeRecord);
                            months[9]=recordsArrayList;
                        }else {
                            recordsArrayList.add(financeRecord);
                            months[9]=recordsArrayList;
                        }
                        break;

                    case "11":
                        if(months[10]!=null){
                            recordsArrayList=months[10];
                            recordsArrayList.add(financeRecord);
                            months[10]=recordsArrayList;
                        }else {
                            recordsArrayList.add(financeRecord);
                            months[10]=recordsArrayList;
                        }
                        break;

                    case "12":
                        if(months[11]!=null){
                            recordsArrayList=months[11];
                            recordsArrayList.add(financeRecord);
                            months[11]=recordsArrayList;
                        }else {
                            recordsArrayList.add(financeRecord);
                            months[11]=recordsArrayList;
                        }
                        break;


                }

            }


            for(int i=0;i<months.length;i++){
                recordsPermonths1.add(months[i]);
                if (months[i]!=null){
                    recordsPermonths.add(months[i]);
                }
            }

        }
        return recordsPermonths1;
    }



    private String getSummeExpenditure(ArrayList<FinanceRecords> financeRecordses){
        double accountBalance=0;
        for(int i=0;i<financeRecordses.size();i++){

            if(!financeRecordses.get(i).isIncome()){
                accountBalance = accountBalance + toDouble(financeRecordses.get(i).getRecordAmount());
            }
        }
        return getAccountBlanceTostring(accountBalance);
    }

    private String getSummeIncome(ArrayList<FinanceRecords> financeRecordses){
        double accountBalance=0;
        for(int i=0;i<financeRecordses.size();i++){

            if(financeRecordses.get(i).isIncome()){
                accountBalance = accountBalance + toDouble(financeRecordses.get(i).getRecordAmount());
            }

        }
        return getAccountBlanceTostring(accountBalance);
    }
    private String getSummeRecordsAmount(double financeRecordsAccountInit, ArrayList<FinanceRecords> financeRecordses){

        double accountBalance=0;

        if(financeRecordses.size()!=0 ){


            for (int i=0; i<financeRecordses.size();i++){

                FinanceRecords records=financeRecordses.get(i);
                if(records.isIncome()){
                    accountBalance = accountBalance + toDouble(records.getRecordAmount());

                }else {

                    double d=toDouble(records.getRecordAmount());
                    accountBalance = accountBalance - d;
                }
            }

        }

        return getAccountBlanceTostring(accountBalance);
    }


    private String getAccountBlanceTostring(double accountBalance){
        DecimalFormat df = new DecimalFormat("0.00");
        df.setMaximumFractionDigits(2);

        String priceStr = df.format(accountBalance);
        return priceStr.replace(",",".");
    }

    private double toDouble(String value){
        double dValue=0;
        DecimalFormat df = new DecimalFormat("0.00");
        df.setMaximumFractionDigits(2);

        try {
            Number nm=df.parse(value.replace(",","."));

            dValue=Double.parseDouble(value.replace(",","."));
            //dValue=nm.doubleValue();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dValue;
    }


}
