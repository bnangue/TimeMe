package com.app.bricenangue.timeme;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class PieChartActivity extends AppCompatActivity {
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
        mPieChart=new PieChart(this);
        setContentView(mPieChart);


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

        // configure pie chart

        mPieChart.setUsePercentValues(true);
        mPieChart.setDescription("by Categories");

        // enable hole and configure
        mPieChart.setDrawHoleEnabled(true);
        //mPieChart.setH
        mPieChart.setHoleRadius(7);
        mPieChart.setTransparentCircleRadius(10);

        // enable rotation of the chart by touch
        mPieChart.setRotationAngle(0);
        mPieChart.setRotationEnabled(true);

        // set a chart value selected listener
        mPieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {

            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                // display msg when value selected
                if (e == null)
                    return;

                Toast.makeText(PieChartActivity.this,
                        xData[e.getXIndex()] + " = " + e.getVal() + "%", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected() {

            }
        });

        // add data
        addData();

        // customize legends
        Legend l = mPieChart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        l.setXEntrySpace(7);
        l.setYEntrySpace(5);
        mPieChart.animateY(2000);
        mPieChart.animateX(3000);


    }

    private void addData() {
        ArrayList<ArrayList<FinanceRecords>> fi=getRecordsPerCategories();
        for(int q=0;q<fi.size();q++){
            if(fi.get(q)!=null){
                yData[q]=fi.get(q).size();
            }else {
                yData[q]=0;
            }
        }
        ArrayList<Entry> yVals1 = new ArrayList<Entry>();

        for (int i = 0; i < yData.length; i++)

            yVals1.add(new Entry(yData[i], i));

        ArrayList<String> xVals = new ArrayList<String>();

        for (int i = 0; i < xData.length; i++)
            xVals.add(xData[i]);

        // create pie data set
        PieDataSet dataSet = new PieDataSet(yVals1, "Market Share");
        dataSet.setSliceSpace(3);
        dataSet.setSelectionShift(5);

        // add many colors
        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());
        dataSet.setColors(colors);

        // instantiate pie data object now
        PieData data = new PieData(xVals, dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.GRAY);

        mPieChart.setData(data);

        // undo all highlights
        mPieChart.highlightValues(null);

        // update pie chart
        mPieChart.invalidate();
    }


    private ArrayList<ArrayList<FinanceRecords>> getRecordsPerCategories(){

        ArrayList<ArrayList<FinanceRecords>> recordsPermonths1=new ArrayList<>();
        int count=0;
        ArrayList<FinanceRecords>[] months=new ArrayList [4];
        if(financeRecordses!=null && financeRecordses.size()!=0){


            for (int k = 0; k < financeRecordses.size(); k++) {
                ArrayList<FinanceRecords> recordsArrayList=new ArrayList<>();
                FinanceRecords financeRecord=financeRecordses.get(k);

                String month=financeRecord.getRecordCategorie();
                switch (month){
                    case "Grocery":
                        if(months[0]!=null){
                            recordsArrayList=months[0];
                            recordsArrayList.add(financeRecord);
                            months[0]=recordsArrayList;
                        }else {
                            recordsArrayList.add(financeRecord);
                            months[0]=recordsArrayList;
                        }

                        break;
                    case "Leisure":
                        if(months[1]!=null){
                            recordsArrayList=months[1];
                            recordsArrayList.add(financeRecord);
                            months[1]=recordsArrayList;
                        }else {
                            recordsArrayList.add(financeRecord);
                            months[1]=recordsArrayList;
                        }
                        break;
                    case "Traveling":
                        if(months[2]!=null){
                            recordsArrayList=months[2];
                            recordsArrayList.add(financeRecord);
                            months[2]=recordsArrayList;
                        }else {
                            recordsArrayList.add(financeRecord);
                            months[2]=recordsArrayList;
                        }
                        break;
                    case "Personal":
                        if(months[3]!=null){
                            recordsArrayList=months[3];
                            recordsArrayList.add(financeRecord);
                            months[3]=recordsArrayList;
                        }else {
                            recordsArrayList.add(financeRecord);
                            months[3]=recordsArrayList;
                        }
                        break;

                }

            }

            for(int i=0;i<months.length;i++){
                recordsPermonths1.add(months[i]);
            }

        }
        return recordsPermonths1;
    }

}
