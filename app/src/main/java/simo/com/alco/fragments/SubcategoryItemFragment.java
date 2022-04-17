package simo.com.alco.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import simo.com.alco.R;
import simo.com.alco.api.ApiCategory;
import simo.com.alco.api.ApiDataManager;
import simo.com.alco.api.ApiSubcategory;
import simo.com.alco.fragments.adapters.SubcategoryItemRecyclerViewAdapter;
import simo.com.alco.fragments.events.OnListFragmentItemSelection;

public class SubcategoryItemFragment extends FragmentWithInit {

    private ApiCategory mCategory;
    private OnListFragmentItemSelection mListener;

    public SubcategoryItemFragment() {
        // Required empty public constructor
    }

    @Override
    public void initFragment(Object... args) {
        ArrayList<Object> arr = (ArrayList<Object>) args[0];
        mCategory = (ApiCategory) arr.get(0);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subcategoryitem_list, container, false);

        if (view instanceof CoordinatorLayout) {
            List<ApiSubcategory> subs = ApiDataManager.subcategoriesByCategory(mCategory);
            //final SubcategoryItemFragment this_ref = this;

            RecyclerView recyclerView = view.findViewById(R.id.list);
            Context context = recyclerView.getContext();
            LinearLayoutManager mgr = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(mgr);
            DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                    mgr.getOrientation());

            recyclerView.addItemDecoration(mDividerItemDecoration);

            recyclerView.setAdapter(new SubcategoryItemRecyclerViewAdapter(ApiDataManager.subcategoriesByCategory(mCategory), mListener));
            TextView mMinPriceView = view.findViewById(R.id.min_price);
            TextView mFreeShippingView = view.findViewById(R.id.free_shipping);
            String minPriceTxt = String.format("%s р.", ApiDataManager.MIN_PRICE);
            String freeShippingTxt = String.format("%s р.", ApiDataManager.FREE_DELIVERY_FROM);
            mMinPriceView.setText(minPriceTxt);
            mFreeShippingView.setText(freeShippingTxt);


        }

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentItemSelection) {
            mListener = (OnListFragmentItemSelection) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
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

}
