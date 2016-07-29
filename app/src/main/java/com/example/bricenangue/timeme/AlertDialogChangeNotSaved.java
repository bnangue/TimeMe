package com.example.bricenangue.timeme;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by bricenangue on 04/03/16.
 */
public class AlertDialogChangeNotSaved extends DialogFragment {
    public interface OnChangesCancel{
        void changescanceled(boolean canceled);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof OnChangesCancel)) {
            throw new ClassCastException(activity.toString() + " must implement OnChangesCancel");
        }
    }

    public static AlertDialogChangeNotSaved newInstance(String title,String message,String buttonoktext,String buttoncanceltext) {
        AlertDialogChangeNotSaved frag = new AlertDialogChangeNotSaved();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("message", message);
        args.putString("buttonoktext", buttonoktext);
        args.putString("buttoncanceltext", buttoncanceltext);
        frag.setArguments(args);
        return frag;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog dialoglog = new AlertDialog.Builder(getContext()).create();


        final String titletext =getArguments().getString("title");
        final String messagetext =getArguments().getString("message");

        final String buttonoktext =getArguments().getString("buttonoktext");
        final String buttoncanceltext =getArguments().getString("buttoncanceltext");

        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vw = inflater.inflate(R.layout.layout_error_warning, null);

        dialoglog.setView(vw);
        final TextView title=(TextView)vw.findViewById(R.id.titelwarningerror);
        final TextView message=(TextView)vw.findViewById(R.id.messagewarningerror);

        Button btnOK=(Button)vw.findViewById(R.id.buttonchangeerrorOk);
        Button btnCancl=(Button)vw.findViewById(R.id.buttonchangeerroCancel);
        title.setText(titletext);
        message.setText(messagetext);
        btnCancl.setText(buttoncanceltext);
        btnOK.setText(buttonoktext);

        btnCancl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((OnChangesCancel) getActivity()).changescanceled(false);
                dialoglog.dismiss();
            }
        });

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((OnChangesCancel) getActivity()).changescanceled(true);
                dialoglog.dismiss();
            }
        });

        dialoglog.setCancelable(false);
        return dialoglog;
    }
}
