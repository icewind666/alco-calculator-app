package simo.com.alco.fragments.adapters;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import simo.com.alco.R;
import simo.com.alco.api.AlcoAppApi;
import simo.com.alco.api.ApiItem;
import simo.com.alco.fragments.events.OnListFragmentItemSelection;
import java.util.List;

public class ShopItemRecyclerViewAdapter extends RecyclerView.Adapter<ShopItemRecyclerViewAdapter.ShopItemViewHolder> {

    private final List<ApiItem> mValues;
    private final OnListFragmentItemSelection mListener;

    public ShopItemRecyclerViewAdapter(List<ApiItem> items, OnListFragmentItemSelection listener) {
        mValues = items;
        mListener = listener;
    }

    @NonNull
    @Override
    public ShopItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_shopitem, parent, false);
        return new ShopItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ShopItemViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mContentView.setText(mValues.get(position).title);
        try {
            Bitmap bm = AlcoAppApi.GetImageBitmap(mValues.get(position).imagePaths[0]);
            holder.mImageView.setImageBitmap(bm);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        String priceStr = String.format("%s руб.", mValues.get(position).price);
        holder.mPrice.setText(priceStr);

//        String inStockStr = String.format("%d шт.", mValues.get(position).inStock);
//        holder.mInStock.setText(inStockStr);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an items has been selected.
                    mListener.onShopItemsListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if(mValues != null)
            return mValues.size();
        else
            return 0;
    }

    public class ShopItemViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mImageView;
        public final TextView mContentView;
        public final TextView mPrice;
        public ApiItem mItem;

        public ShopItemViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = view.findViewById(R.id.item_image);
            mContentView = view.findViewById(R.id.item_content);
            mPrice = view.findViewById(R.id.item_price);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
