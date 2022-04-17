package simo.com.alco.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import simo.com.alco.MainActivity;
import simo.com.alco.R;
import simo.com.alco.api.ApiBaseHandler;
import simo.com.alco.api.ApiCartItem;
import simo.com.alco.api.ApiDataManager;
import simo.com.alco.api.ApiItem;
import simo.com.alco.api.ApiOrder;
import simo.com.alco.fragments.events.OnListFragmentItemSelection;
import simo.com.alco.fragments.events.OnPreloadDoneListener;

import static com.vk.sdk.VKUIHelper.getApplicationContext;

public class ShopItemDetailsFragment extends FragmentWithInit {
    ApiCartItem mItem;
    OnListFragmentItemSelection mListener;
    CarouselView carouselView;
    TextView priceView;
    TextView priceTotalView;
    TextView descView;
    TextView titleView;
    Button mButtonView;
    NumberPicker mNumberPicker;
    View mView;
    ProgressBar mProgressBar;
    ImageListener imageListener;


    public ShopItemDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void initFragment(Object... args) {
        ArrayList<Object> arr = (ArrayList<Object>) args[0];
        ApiItem apiItem = (ApiItem) arr.get(0);
        mItem = new ApiCartItem(apiItem);
        mItem.quantityInCart = 1;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_shop_item_details, container, false);
        mView = v;

        mButtonView = v.findViewById(R.id.orderBtn);

        mButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiBaseHandler pHandlerAddItem = new ApiBaseHandler() {
                    @Override
                    public void onResponse(JSONObject arg) {
                        ((MainActivity)getActivity()).updateCartCountBadge(-1);
                    }
                };
                ApiDataManager.addItemToUserCart(mItem, pHandlerAddItem);
                showToast("Товар добавлен в корзину");
            }
        });

        carouselView = v.findViewById(R.id.carouselView);
        if(carouselView != null) {
            carouselView.setPageCount(0);
            carouselView.setImageListener(null);
        }
        else {
            Log.e("API", "carousel is null!!");
        }


        priceView = v.findViewById(R.id.item_price);
        String priceStr = String.format("%s руб.", mItem.price);
        priceView.setText(priceStr);

        priceTotalView = v.findViewById(R.id.item_price_total);
        String priceTotalStr = String.format("%s руб.", mItem.price() * mItem.quantityInCart);
        priceTotalView.setText(priceTotalStr);

        titleView = v.findViewById(R.id.item_title);
        titleView.setText(mItem.title);

        descView = v.findViewById(R.id.item_description);
        descView.setText(mItem.desc);

        mNumberPicker = mView.findViewById(R.id.numberPicker);
        mNumberPicker.setMinValue(1);
        mNumberPicker.setMaxValue(mItem.inStock);
        mNumberPicker.setWrapSelectorWheel(false);
        mNumberPicker.setValue(1);

        mNumberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                mItem.quantityInCart = i1;
                double totalPrice = mItem.price() * mItem.quantityInCart;
                BigDecimal bdAmount = new BigDecimal(totalPrice).setScale(2,
                        RoundingMode.HALF_UP);


                String priceTotalStr = String.format("%s руб.", bdAmount);
                priceTotalView.setText(priceTotalStr);
            }
        });

        mProgressBar = v.findViewById(R.id.progress_bar);
        initImagesLoading();
        return v;
    }


    public void showToast(String txt) {
        Toast toast = Toast.makeText(getApplicationContext(),
                txt,
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public void loadingDone() {
        try {
            imageListener = new ImageListener() {
                @Override
                public void setImageForPosition(int position, ImageView imageView) {
                    try {
                        imageView.setImageBitmap(mItem.imageBitmaps[position]);
                    }
                    catch (Exception e) {
                        Log.d("API", "Cant set image bitmap");
                    }
                }
            };
            carouselView.setImageListener(imageListener);
            if (mItem.imageBitmaps != null && mItem.imageBitmaps.length>0)
                carouselView.setPageCount(mItem.imagePaths.length);
            else
                carouselView.setPageCount(0);
        }
        catch (Exception e) {
            Log.e("API", "loading images error", e);
        }
        finally {
            mProgressBar.setVisibility(View.INVISIBLE);
        }

    }


    public void initImagesLoading() {
        try {
            new AsyncBitmapLoadTask(new OnPreloadDoneListener() {
                @Override
                public void preloadDone() {

                }

                @Override
                public void preloadCartOrderDone(ApiOrder order) {

                }

                @Override
                public void preloadImagesDone(Bitmap[] bitmaps) {
                    mItem.imageBitmaps = bitmaps;
                    if(mItem.imageBitmaps == null) {
                        Log.d("API", "images is null");
                    }
                    loadingDone();
                }
            }).execute(mItem.imagePaths);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View view) {

    }


    /**
     *
     */
    private class AsyncBitmapLoadTask extends AsyncTask<String[], Integer, Bitmap[]> {
        OnPreloadDoneListener handler;

        AsyncBitmapLoadTask(OnPreloadDoneListener postExecuteHandler) {
            handler = postExecuteHandler;
        }


        @Override
        protected void onPostExecute(Bitmap[] bitmaps) {
            super.onPostExecute(bitmaps);
            handler.preloadImagesDone(bitmaps);
        }

        @Override
        protected Bitmap[] doInBackground(String[]... strings) {

            List<Bitmap> bitmaps = new ArrayList<>();

            for (String url: strings[0]) {
                try {
                    Bitmap bm;
                    URL aURL = new URL(url);
                    URLConnection conn = aURL.openConnection();
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    BufferedInputStream bis = new BufferedInputStream(is);
                    bm = BitmapFactory.decodeStream(bis);
                    bitmaps.add(bm);
                    bis.close();
                    is.close();
                } catch (IOException e) {
                    Log.e("API", "Error getting bitmap", e);
                }
            }
            return bitmaps.toArray(new Bitmap[0]);
        }


    }


}
