package simo.com.alco.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;

import simo.com.alco.MainActivity;
import simo.com.alco.R;
import simo.com.alco.api.ApiBaseHandler;
import simo.com.alco.api.ApiDataManager;
import simo.com.alco.api.ApiOrder;
import simo.com.alco.api.ApiSubcategory;
import simo.com.alco.fragments.adapters.ProcessListItemRecyclerViewAdapter;
import simo.com.alco.fragments.events.OnListFragmentItemSelection;
import simo.com.alco.fragments.events.OnPreloadDoneListener;

public class ProcessListItemFragment extends FragmentWithInit implements OnPreloadDoneListener {
    private OnListFragmentItemSelection mListener;
    ProgressBar mProgressBar;
    public View mView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ProcessListItemFragment() {
    }

    @Override
    public void initFragment(Object... args) {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_proceslistitem_list, container, false);
        mView = view;
        mProgressBar = view.findViewById(R.id.progress_bar);

        if(ApiDataManager.PROCESSES.size() < 1) {
            mProgressBar.setVisibility(View.VISIBLE);
            ApiDataManager.preloadDoneCallback = this;
            ApiDataManager.preloadShopData();
        }
        else {
            initRecycler();
        }
        try {
            ((MainActivity) getActivity()).updateCartCountBadge(-1);
        }
        catch (NullPointerException e) {
            e.printStackTrace();
        }
        final TextView mMinPriceView = view.findViewById(R.id.min_price);
        final TextView mFreeShippingView = view.findViewById(R.id.free_shipping);

        ((MainActivity)getActivity()).minPriceAndDeliveryHandler = new ApiBaseHandler() {
            @Override
            public void onResponse(JSONObject arg) {
                String minPriceTxt = String.format("%s р.", ApiDataManager.MIN_PRICE);
                String freeShippingTxt = String.format("%s р.", ApiDataManager.FREE_DELIVERY_FROM);
                mMinPriceView.setText(minPriceTxt);
                mFreeShippingView.setText(freeShippingTxt);
            }
        };

        String minPriceTxt = String.format("%s р.", ApiDataManager.MIN_PRICE);
        String freeShippingTxt = String.format("%s р.", ApiDataManager.FREE_DELIVERY_FROM);
        mMinPriceView.setText(minPriceTxt);
        mFreeShippingView.setText(freeShippingTxt);
        return view;
    }

    public void initRecycler() {
        if (mView instanceof CoordinatorLayout) {
            RecyclerView recyclerView = mView.findViewById(R.id.list);
            Context context = recyclerView.getContext();
            LinearLayoutManager mgr = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(mgr);

            DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                    mgr.getOrientation());

            recyclerView.addItemDecoration(mDividerItemDecoration);
            recyclerView.setAdapter(new ProcessListItemRecyclerViewAdapter(ApiDataManager.PROCESSES, mListener));
        }
        mProgressBar.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentItemSelection) {
            mListener = (OnListFragmentItemSelection) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnProcessListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void preloadDone() {
        initRecycler();
    }

    @Override
    public void preloadCartOrderDone(ApiOrder order) {

    }

    @Override
    public void preloadImagesDone(Bitmap[] bitmaps) {

    }
}
