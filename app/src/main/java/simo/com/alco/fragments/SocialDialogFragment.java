package simo.com.alco.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import simo.com.alco.AppActivities;
import simo.com.alco.MainActivity;
import simo.com.alco.R;

/**
 * Choosing social dialog.
 * Used in joining vk and OK groups
 * Created by icewind on 01.10.2017.
 */

public class SocialDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.joinSocial)
                .setItems(R.array.social_titles, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected items
                        switch (which) {
                            case 0:
                                ((MainActivity) getActivity()).socialChoiceDialogVK();
                                break;

                            case 1:
                                ((MainActivity) getActivity()).socialChoiceDialogOK();
                                break;
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // nothing to do? closes automatically
                        //((MainActivity) getActivity()).defaultChoiceDialogCancel();
                    }
                });
        return builder.create();
    }

}
