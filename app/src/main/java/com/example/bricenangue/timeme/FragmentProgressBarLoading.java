package com.example.bricenangue.timeme;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

/**
 * Created by praktikumfh on 25/04/16.
 */
public class FragmentProgressBarLoading extends DialogFragment {

    private String dialogTag;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        final android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(getContext()).create();
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vw = inflater.inflate(R.layout.fragment_fragment_loading, null);

        alertDialog.setView(vw);
        ProgressBar p=(ProgressBar)vw.findViewById(R.id.prbar);
        p.setVisibility(View.VISIBLE);
        alertDialog.setCancelable(false);

        return alertDialog;
    }
    public void setProgress(FragmentManager manager, int progress)
    {
        FragmentProgressBarLoading dialog = (FragmentProgressBarLoading)manager.findFragmentByTag(dialogTag);
        if (dialog != null)
        {
            ((ProgressDialog)dialog.getDialog()).setProgress(progress);
        }
    }

    /**
     * Dismisses the dialog from the fragment manager. We need to make sure we get the right dialog reference
     * here which is why we obtain the dialog fragment manually from the fragment manager
     * @param manager
     */
    public void dismiss(FragmentManager manager)
    {
        FragmentProgressBarLoading dialog = (FragmentProgressBarLoading)manager.findFragmentByTag(dialogTag);
        if (dialog != null)
        {
            dialog.dismiss();
        }
    }

    @Override public void show(FragmentManager manager, String tag)
    {
        dialogTag = tag;
        super.show(manager, dialogTag);
    }
}
