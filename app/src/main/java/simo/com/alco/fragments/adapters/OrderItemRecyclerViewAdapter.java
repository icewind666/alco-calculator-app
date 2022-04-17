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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import simo.com.alco.MainActivity;
import simo.com.alco.R;
import simo.com.alco.api.AlcoAppApi;
import simo.com.alco.api.ApiOrder;
import simo.com.alco.api.ApiSingleOrderItem;
import simo.com.alco.fragments.events.OnListFragmentItemSelection;

public class OrderItemRecyclerViewAdapter extends RecyclerView.Adapter<OrderItemRecyclerViewAdapter.OrderItemViewHolder> {

    private final List<ApiOrder> mValues;
    private final OnListFragmentItemSelection mListener;

    public OrderItemRecyclerViewAdapter(List<ApiOrder> items, OnListFragmentItemSelection listener) {
        mValues = items;
        mListener = listener;
    }

    @NonNull
    @Override
    public OrderItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_my_order_item, parent, false);
        return new OrderItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final OrderItemViewHolder holder, int position) {
        final ApiOrder currentOrder = mValues.get(position);
        holder.mItem = currentOrder;
        holder.mContentView.setText(currentOrder.toString());

        String quantityStr = String.format("%s", currentOrder.items.size());
        holder.mQuantity.setText(quantityStr);

        String statusStr = String.format("Статус заказа: %s", currentOrder.statusTitle);
        holder.mStatus.setText(statusStr);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // вызываем фрагмент заказа для дооформления
                mListener.onMyOrderItemClick(currentOrder);
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

    public class OrderItemViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        final ImageView mImageView;
        final TextView mContentView;
        final TextView mQuantity;
        final TextView mStatus;
        ApiOrder mItem;

        OrderItemViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = view.findViewById(R.id.item_image);
            mContentView = view.findViewById(R.id.item_content);
            mStatus = view.findViewById(R.id.order_status);
            mQuantity = view.findViewById(R.id.order_quantity);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
