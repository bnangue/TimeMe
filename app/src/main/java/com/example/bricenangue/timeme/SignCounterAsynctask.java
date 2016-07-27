package com.example.bricenangue.timeme;

import android.app.Activity;
import android.os.AsyncTask;

import java.util.ArrayList;


/**
 * Created by bricenangue on 07/07/16.
 * start and stop Progressbar
 *
 */
public class SignCounterAsynctask extends AsyncTask<Void, Integer, Void>
{
    private NewCalendarActivty activity;
    private boolean b;
    private int cont,numberOfselectedItems;
    private ArrayList<Integer> selectedItemsIndex=new ArrayList<>();

    public SignCounterAsynctask(NewCalendarActivty signatureActivity){
        this.activity=signatureActivity;
        this.numberOfselectedItems=numberOfselectedItems;
        this.cont=cont;
    }

    private FragmentProgressBarLoading progressDialog;

    @Override protected Void doInBackground(Void... params)
    {

        //activity.signOrCancelOrder(selectedItemsIndex, b);
        return null ;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progressDialog = new FragmentProgressBarLoading();
        progressDialog.setCancelable(false);
        progressDialog.show(activity.getSupportFragmentManager(), "task_progress");


    }

    @Override protected void onProgressUpdate(Integer... values)
    {
        super.onProgressUpdate(values);

        // update the progress
        progressDialog.setProgress(activity.getSupportFragmentManager(), values[0]);

    }

    @Override protected void onPostExecute(Void result)
    {
        super.onPostExecute(result);


        if (activity != null)
        {
            // use the new activity reference just incase it was re-attached
            progressDialog.dismiss(activity.getSupportFragmentManager());

            // reference UI updates using activity
           // activity.updateUI(b,numberOfselectedItems);

        }

        // remove the reference
        SignCounterAsynctaskHelper.getInstance().removeTask("task");
    }

    /**
     * Attaches an activity to the task
     * @param a The activity to attach
     */
    public void attach(Activity a)
    {
        this.activity = (NewCalendarActivty) a;
    }

    /**
     * Removes the activity from the task
     */
    public void detach()
    {
        this.activity = null;
    }

}
