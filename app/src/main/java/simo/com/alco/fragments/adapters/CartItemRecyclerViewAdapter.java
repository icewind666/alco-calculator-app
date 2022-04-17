package simo.com.alco.fragments.adapters;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import simo.com.alco.R;
import simo.com.alco.api.AlcoAppApi;
import simo.com.alco.api.ApiCartItem;
import simo.com.alco.fragments.events.OnCartOrderAction;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 *
 */
public class CartItemRecyclerViewAdapter extends RecyclerView.Adapter<CartItemRecyclerViewAdapter.ViewHolder> {

    private final List<ApiCartItem> mValues;
    private final OnCartOrderAction mListener;

    public CartItemRecyclerViewAdapter(List<ApiCartItem> items, OnCartOrderAction listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public CartItemRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_cartitem, parent, false);
        return new CartItemRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CartItemRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mContentView.setText(mValues.get(position).title);

        try {
            Bitmap bm = AlcoAppApi.GetImageBitmap(mValues.get(position).imagePaths[0]);
            holder.mImageView.setImageBitmap(bm);
        }
        catch (Exception e) {
            Log.e("API", "Cant get image from server");
        }

        double totalItemPrice = Double.parseDouble(mValues.get(position).price) * holder.mItem.quantityInCart;

        BigDecimal bdAmount = new BigDecimal(totalItemPrice).setScale(2,
                RoundingMode.HALF_UP);

        holder.mPrice.setText(String.format("%s руб.", bdAmount));
        holder.mQuantity.setText(String.format("%s шт.", mValues.get(position).quantityInCart));
        holder.itemView.findViewById(R.id.changeBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.OnChangeCartItem(holder.mItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mImageView;
        public final TextView mContentView;
        public final TextView mQuantity;
        public final TextView mPrice;
        public ApiCartItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = view.findViewById(R.id.item_image);
            mContentView = view.findViewById(R.id.item_content);
            mQuantity = view.findViewById(R.id.item_instock);
            mPrice = view.findViewById(R.id.item_price_txt_value);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
