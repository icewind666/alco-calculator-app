package simo.com.alco.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import simo.com.alco.R;
import simo.com.alco.api.ApiCartItem;

public class QuantityChangeDialogFragment extends DialogFragment {

    public interface QuantityChangeDialogListener {
        void onPositiveClick(DialogFragment dialog);
        void onNegativeClick(DialogFragment dialog);
        void onRemoveFromOrderClick(DialogFragment dialog);
    }

    public CartFragment parentFragment;

    QuantityChangeDialogListener mListener;

    View mView;
    NumberPicker mNumberPicker;
    ApiCartItem currentItem;

    public void setItem(ApiCartItem theItem) {
        currentItem = theItem;
    }

    public ApiCartItem getItem() {
        return currentItem;
    }

    // Override the Fragment.onAttach() method to instantiate the QuantityChangeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the QuantityChangeDialogListener so we can send events to the host
            mListener = (QuantityChangeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement QuantityChangeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final DialogFragment this_ref = this;

        mView = inflater.inflate(R.layout.cart_item_change_dialog, null);
        mNumberPicker = mView.findViewById(R.id.numberPicker);
        mNumberPicker.setMinValue(1);

        int realStock = 0;
        for (ApiCartItem item: parentFragment.mOrder.items) {
            if(item.id == currentItem.id) {
                realStock = currentItem.inStock - item.quantityInCart;
                break;
            }

        }
        mNumberPicker.setMaxValue(realStock);
        mNumberPicker.setWrapSelectorWheel(false);

        mNumberPicker.setValue(getItem().quantityInCart);

        builder.setView(mView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // user is done
                        mListener.onPositiveClick(this_ref);
                    }
                })
                .setNegativeButton("ОТМЕНА", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        mListener.onNegativeClick(this_ref);
                    }
                })
                .setNeutralButton("Удалить из заказа", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User removed the items
                        mListener.onRemoveFromOrderClick(this_ref);
                    }

                });

        // Create the AlertDialog object and return it
        return builder.create();
    }

    public int getCurrentQuantity() {
        return mNumberPicker.getValue();
    }

}