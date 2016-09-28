package com.app.bricenangue.timeme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class AllChartsActivity extends AppCompatActivity {
    private FinanceAccount financeAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_charts);
        Bundle extras=getIntent().getExtras();
        if(extras!=null){
            financeAccount=extras.getParcelable("financeAccount");
        }
    }

    public void OnBarChartClicked(View view){
        startActivity(new Intent(this,FinanceChartActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra("financeAccount",financeAccount));
    }


    public void OnPieChartClicked(View view){
        startActivity(new Intent(this,PieChartActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .putExtra("financeAccount",financeAccount
                ));
    }
}

