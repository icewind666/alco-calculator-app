package simo.com.alco.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import simo.com.alco.AppActivities;
import simo.com.alco.MainActivity;
import simo.com.alco.R;
import simo.com.alco.api.AlcoAppApi;
import simo.com.alco.api.ApiBaseHandler;
import simo.com.alco.api.ApiCartItem;
import simo.com.alco.api.ApiCategory;
import simo.com.alco.api.ApiItem;
import simo.com.alco.api.ApiKnownUrls;
import simo.com.alco.api.ApiOrder;
import simo.com.alco.api.ApiProcess;
import simo.com.alco.api.ApiSingleOrderItem;
import simo.com.alco.api.ApiSubcategory;
import simo.com.alco.fragments.adapters.OrderItemRecyclerViewAdapter;
import simo.com.alco.fragments.events.OnListFragmentItemSelection;

/**
 * Users orders view fragment.
 */
public class MyOrdersFragment extends FragmentWithInit {
    private View mView;
    private ProgressBar mProgressBar;
    private List<ApiOrder> groupedOrders = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private OnListFragmentItemSelection eventListener = new OnListFragmentItemSelection() {
        @Override
        public void onSubcategoryListFragmentInteraction(ApiSubcategory item) {

        }

        @Override
        public void onCategoryListFragmentInteraction(ApiCategory item) {

        }

        @Override
        public void onProcessListFragmentInteraction(ApiProcess item) {

        }

        @Override
        public void onShopItemsListFragmentInteraction(ApiItem item) {

        }

        @Override
        public void onMyOrderItemClick(ApiOrder order) {
            List<Object> args = new ArrayList<>();
            args.add(order);
            ((MainActivity)getActivity()).showFragment(OrderFragment.class, args);
        }
    };

    public MyOrdersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void initFragment(Object... args) {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_my_orders, container, false);
        mRecyclerView = mView.findViewById(R.id.orders_list);
        mProgressBar = mView.findViewById(R.id.progress_bar);

        if(AlcoAppApi.user == null || AlcoAppApi.AUTH_TICKET.isEmpty()) {
            ((MainActivity)getActivity()).switchTo(AppActivities.ENTER);
        }



        mProgressBar.setVisibility(View.VISIBLE);

        ApiBaseHandler pHandler = new ApiBaseHandler() {
            @Override
            public void onResponse(JSONObject arg) {
                try {
                    JSONArray jsonData = arg.getJSONArray("data");

                    if(jsonData.length() >= 1) {
                        groupedOrders = new ArrayList<>();

                        for (int i = 0; i < jsonData.length(); i++) {

                            JSONObject currentItem = jsonData.getJSONObject(i);

                            final ApiCartItem singleOrderItem = new ApiCartItem();
                            singleOrderItem.id = currentItem.getInt("item_id");
                            singleOrderItem.title = currentItem.getString("item_title");
                            singleOrderItem.orderId = currentItem.getInt("order_id");
                            // search if we already have that order in list
                            ApiOrder found = null;
                            for (ApiOrder x: groupedOrders) {
                                if (x.order_id == singleOrderItem.orderId) {
                                    found = x;
                                    found.statusTitle = currentItem.getString("status_title");
                                }
                            }

                            if (found == null) {
                                found = new ApiOrder();
                                found.order_id = singleOrderItem.orderId;
                                found.statusTitle = currentItem.getString("status_title");
                                groupedOrders.add(found);
                            }

                            found.items.add(singleOrderItem);
                            singleOrderItem.quantityInCart = currentItem.getInt("quantity");

                            JSONArray imgs = currentItem.getJSONArray("image_paths");

                            List<String> images = new ArrayList<>();

                            for (int j = 0; j < imgs.length(); j++) {
                                String currentImg = ApiKnownUrls.BASE_URL + "/" +
                                        imgs.getString(j);

                                images.add(currentImg);
                            }
                            singleOrderItem.imagePaths = images.toArray(new String[0]);
                        }
                        setAdapter();
                    }
                    else {
                        // show toast
                        // show dialog toast
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("В данный момент у Вас еще нет ни одного заказа!")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //do things
                                        getActivity().onBackPressed();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();

                    }
                    mProgressBar.setVisibility(View.INVISIBLE);

                } catch (JSONException e) {
                    Log.e("API", e.toString());
                    e.printStackTrace();
                }
            }
        };
        // Make an api call to receive orders

        if (AlcoAppApi.user != null) {
            new AlcoAppApi(pHandler).orders();
        }
        else {
            ((MainActivity)getActivity()).switchTo(AppActivities.ENTER);
        }

        return mView;
    }

    private void setAdapter() {
        if (mView instanceof CoordinatorLayout) {
            Context context = mRecyclerView.getContext();
            mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            OrderItemRecyclerViewAdapter adapter = new OrderItemRecyclerViewAdapter(this.groupedOrders, eventListener);
            mRecyclerView.setAdapter(adapter);
        }
        else {
            Log.e("API", "mView is not instanceof CoordinatorLayout");
        }


    }

}
