package simo.com.alco.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import java.util.ArrayList;

import simo.com.alco.AppActivities;
import simo.com.alco.MainActivity;
import simo.com.alco.R;
import simo.com.alco.api.ApiCartItem;

public class AcceptPrivacyDialog extends DialogFragment {

    public interface AcceptPrivacyDialogListener {
        void onPositive(DialogFragment dialog);
        void onNegative(DialogFragment dialog);
    }

    View mView;
    AcceptPrivacyDialogListener listener;

    public AcceptPrivacyDialog() {

    }



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final DialogFragment this_ref = this;

        mView = inflater.inflate(R.layout.accept_privacy_dialog, null);

        builder.setView(mView)
                .setPositiveButton("Продолжить регистрацию", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // user is done
                        listener.onPositive(this_ref);
                    }
                })
                .setNegativeButton("ОТМЕНА", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        listener.onNegative(this_ref);
                    }
                })
                .setNeutralButton("Пользовательское соглашение", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User removed the items
                        ((MainActivity)getActivity()).switchTo(AppActivities.PRIVACY);
                    }

                });

        // Create the AlertDialog object and return it
        return builder.create();
    }

}