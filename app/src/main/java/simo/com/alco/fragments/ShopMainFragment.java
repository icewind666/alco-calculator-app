package simo.com.alco.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import simo.com.alco.MainActivity;
import simo.com.alco.R;
import simo.com.alco.api.ApiBaseHandler;
import simo.com.alco.api.ApiCategory;
import simo.com.alco.api.ApiItem;
import simo.com.alco.api.ApiKnownUrls;
import simo.com.alco.api.ApiOrder;
import simo.com.alco.api.ApiSubcategory;
import simo.com.alco.fragments.adapters.ShopItemRecyclerViewAdapter;
import simo.com.alco.api.ApiDataManager;
import simo.com.alco.fragments.events.OnListFragmentItemSelection;
import simo.com.alco.fragments.events.OnPreloadDoneListener;

import static simo.com.alco.api.ApiDataManager.getCategoryById;
import static simo.com.alco.api.ApiDataManager.getSubcategoryById;


public class ShopMainFragment extends FragmentWithInit implements OnPreloadDoneListener {
    private OnListFragmentItemSelection mListener;
    private View mView;
    private ProgressBar mProgressBar;
    private ApiSubcategory mSubcategory;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ShopMainFragment() {
    }

    @Override
    public void initFragment(Object... args) {
        ArrayList<Object> arr = (ArrayList<Object>) args[0];
        mSubcategory = (ApiSubcategory) arr.get(0);

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //ApiDataManager.preloadDoneCallback = this;
        //preloadDone();
        //List<ApiItem> result = ApiDataManager.itemsBySubcategory(mSubcategory);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shopitem_list, container, false);
        mView = view;
        mProgressBar = view.findViewById(R.id.progress_bar);
        mProgressBar.setVisibility(View.VISIBLE);
        preloadDone();

        return view;
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
        // Set the adapter
        if (mView instanceof CoordinatorLayout) {
            final RecyclerView recyclerView = mView.findViewById(R.id.list);
            Context context = recyclerView.getContext();
            LinearLayoutManager mgr = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(mgr);
            DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                    mgr.getOrientation());

            recyclerView.addItemDecoration(mDividerItemDecoration);


            ApiBaseHandler handler = new ApiBaseHandler() {
                @Override
                public void onResponse(JSONObject arg) {

                    try {
                        JSONArray jsonData = arg.getJSONArray("data");
                        ApiDataManager.ITEMS.clear();
                        if(jsonData.length() < 1) {
                            // show dialog toast
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setMessage("Извините, в данный момент идет наполнение ассортимента. Уже очень скоро мы добавим товары в данном разделе! Предлагаем ознакомиться с наличием в других разделах.")
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
                        else {

                            for (int i = 0; i < jsonData.length(); i++) {

                                JSONObject currentItem = jsonData.getJSONObject(i);
                                int categoryId = currentItem.getInt("category_id");
                                int processId = currentItem.getInt("process_id");
                                int subcategoryId = currentItem.getInt("subcategory_id");
                                int itemId = currentItem.getInt("id");
                                String itemName = currentItem.getString("title");


                                ApiItem x = new ApiItem();
                                x.id = itemId;
                                x.process = ApiDataManager.getProcessById(processId);
                                x.category = getCategoryById(categoryId);
                                x.subcategory = getSubcategoryById(subcategoryId);
                                x.title = itemName;
                                x.price = currentItem.getString("price");
                                x.inStock = currentItem.getInt("in_stock");
                                x.desc = currentItem.getString("desc");

                                JSONArray imgs = currentItem.getJSONArray("images_paths");
                                List<String> images = new ArrayList<>();

                                for (int j = 0; j < imgs.length(); j++) {
                                    String currentImg = ApiKnownUrls.BASE_URL + "/" +
                                            imgs.getString(j);
                                    images.add(currentImg);

                                }
                                x.imagePaths = images.toArray(new String[0]);

                                ApiDataManager.ITEMS.add(x);
                            }

                            recyclerView.setAdapter(new ShopItemRecyclerViewAdapter(ApiDataManager.ITEMS, mListener));

                        }
                        mProgressBar.setVisibility(View.INVISIBLE);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            ApiDataManager.itemsBySubcategory(mSubcategory, handler);




            TextView mMinPriceView = mView.findViewById(R.id.min_price);
            TextView mFreeShippingView = mView.findViewById(R.id.free_shipping);
            String minPriceTxt = String.format("%s р.", ApiDataManager.MIN_PRICE);
            String freeShippingTxt = String.format("%s р.", ApiDataManager.FREE_DELIVERY_FROM);
            mMinPriceView.setText(minPriceTxt);
            mFreeShippingView.setText(freeShippingTxt);


        }


    }

    @Override
    public void preloadCartOrderDone(ApiOrder order) {

    }

    @Override
    public void preloadImagesDone(Bitmap[] bitmaps) {

    }
}
