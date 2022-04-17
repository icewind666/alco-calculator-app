package simo.com.alco.fragments;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.math.RoundingMode;

import simo.com.alco.MainActivity;
import simo.com.alco.R;
import simo.com.alco.api.ApiCartItem;
import simo.com.alco.api.ApiDataManager;
import simo.com.alco.api.ApiOrder;
import simo.com.alco.fragments.adapters.CartItemRecyclerViewAdapter;
import simo.com.alco.fragments.events.OnCartOrderAction;
import simo.com.alco.fragments.events.OnListFragmentItemSelection;
import simo.com.alco.fragments.events.OnPreloadDoneListener;

import static com.vk.sdk.VKUIHelper.getApplicationContext;

/**
 * User cart {@link Fragment} .
 */
public class CartFragment extends FragmentWithInit implements OnPreloadDoneListener,
        OnCartOrderAction {
    private OnCartOrderAction mListener;
    private View mView;
    private TextView mCartTotalValue;

    ProgressBar mProgressBar;
    Button mOrderBtn;
    public ApiOrder mOrder;


    public CartFragment() {
        // Required empty public constructor
    }

    public void openQuantityChangeDialog(ApiCartItem item) {
        QuantityChangeDialogFragment dialog = new QuantityChangeDialogFragment();
        dialog.setItem(item);
        dialog.parentFragment = this;
        dialog.show(getActivity().getSupportFragmentManager(), "QuantityChangeDialogFragment");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_cart, container, false);
        mProgressBar = mView.findViewById(R.id.progress_bar);

        loadCartItemsList();

        mCartTotalValue = mView.findViewById(R.id.cart_total_value);

        mOrderBtn = mView.findViewById(R.id.cartOrderBtn);
        mOrderBtn.setEnabled(false);

        mOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {

                    if (mOrder.getPrice() < ApiDataManager.MIN_PRICE) {
                        showToast(String.format("Минимальная цена заказа: %s р.",
                                ApiDataManager.MIN_PRICE));
                    }
                    else {
                        mListener.OnCartOrder(mOrder);
                    }
                }
            }
        });

        ((MainActivity) getActivity()).updateCartCountBadge(-1);
        return mView;
    }


    public void showToast(String txt) {
        Toast toast = Toast.makeText(getApplicationContext(),
                txt,
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public void loadCartItemsList() {
        mProgressBar.setVisibility(View.VISIBLE);
        ApiDataManager.preloadDoneCallback = this;
        ApiDataManager.cartItems();
    }


    public void initRecycler() {
        Log.d("API", "Re-init recycler");

        if (mView instanceof CoordinatorLayout) {
            RecyclerView recyclerView = mView.findViewById(R.id.list);
            Context context = recyclerView.getContext();
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new CartItemRecyclerViewAdapter(mOrder.items, this));
        }

    }

    @Override
    public void onClick(View v) {
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCartOrderAction) {
            mListener = (OnCartOrderAction) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnCartOrderAction");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void preloadDone() {
    }

    @Override
    public void preloadCartOrderDone(ApiOrder order) {
        mProgressBar.setVisibility(View.INVISIBLE);
        mOrder = order;
        if(order == null || order.items.size() < 1) {
            // disable order button if no items in cart
            initRecycler();
            mCartTotalValue.setText("0 руб.");
            mOrderBtn.setEnabled(false);
        }
        else {
            BigDecimal bdAmount = new BigDecimal(mOrder.getPrice()).setScale(2,
                    RoundingMode.HALF_UP);

            mCartTotalValue.setText(String.format("%s руб.", String.valueOf(bdAmount)));
            initRecycler();
            mOrderBtn.setEnabled(true);
        }

    }

    @Override
    public void preloadImagesDone(Bitmap[] bitmaps) {
        // nothing to do yet
    }

    @Override
    public void OnCartOrder(ApiOrder theCartOrder) {

    }

    @Override
    public void OnChangeCartItem(ApiCartItem itemToChange) {
        openQuantityChangeDialog(itemToChange);
    }

}
