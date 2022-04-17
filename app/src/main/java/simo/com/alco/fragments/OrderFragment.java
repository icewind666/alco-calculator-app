package simo.com.alco.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.api.Api;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashSet;
import java.util.List;

import ru.yandex.money.android.sdk.Amount;
import ru.yandex.money.android.sdk.Checkout;
import ru.yandex.money.android.sdk.PaymentMethodType;
import ru.yandex.money.android.sdk.ShopParameters;
import simo.com.alco.IcewindUtils;
import simo.com.alco.MainActivity;
import simo.com.alco.R;
import simo.com.alco.api.AlcoAppApi;
import simo.com.alco.api.ApiBaseHandler;
import simo.com.alco.api.ApiCartItem;
import simo.com.alco.api.ApiDataManager;
import simo.com.alco.api.ApiKnownUrls;
import simo.com.alco.api.ApiOrder;
import simo.com.alco.api.ApiUser;
import simo.com.alco.fragments.events.OnCartOrderAction;
import simo.com.alco.fragments.events.OnPreloadDoneListener;
import simo.com.alco.postal.DadataCity;
import simo.com.alco.postal.DadataCityAdapter;
import simo.com.alco.postal.DadataHouse;
import simo.com.alco.postal.DadataHouseAdapter;
import simo.com.alco.postal.DadataRegion;
import simo.com.alco.postal.DadataRegionAdapter;
import simo.com.alco.postal.DadataStreet;
import simo.com.alco.postal.DadataStreetAdapter;
import simo.com.alco.postal.RussianPostApi;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.INPUT_METHOD_SERVICE;
import static com.vk.sdk.VKUIHelper.getApplicationContext;


public class OrderFragment extends FragmentWithInit {

    private View mView;
    private ProgressBar mProgressBar;

    protected DadataCityAdapter cityAdapter;
    protected DadataRegionAdapter regionAdapter;
    protected DadataStreetAdapter streetAdapter;
    protected DadataHouseAdapter houseAdapter;

    private String clientApplicationKey = "live_NTQ5MDUxxgq2D0XgemdNKljs9Y0q5aPNedDnkP_biW8";
    private String yandexAppID = "0f63955b11534f6ea203c470b287cc9a";
    private ApiOrder mOrder;

    public OrderFragment() {
        // Required empty public constructor
    }


    private boolean validateAllFields() {
        String city = ((AutoCompleteTextView) mView.findViewById(R.id.citySelector)).getText().toString();
        if (city.isEmpty())
            return false;

        String street = ((AutoCompleteTextView) mView.findViewById(R.id.streetSelector)).getText().toString();
        if (street.isEmpty())
            return false;

        String postIndex = ((EditText) mView.findViewById(R.id.postalCodeSelector)).getText().toString();
        if (postIndex.isEmpty())
            return false;

        String lastname = ((EditText) mView.findViewById(R.id.lastnameEditText)).getText().toString();
        if (lastname.isEmpty())
            return false;

        String firstname = ((EditText) mView.findViewById(R.id.firstnameEditText)).getText().toString();
        if (firstname.isEmpty())
            return false;

        String phone = ((EditText) mView.findViewById(R.id.phoneEditText)).getText().toString();
        if (phone.isEmpty() || !IcewindUtils.isValidPhoneNumber(phone))
            return false;

        String email = ((EditText) mView.findViewById(R.id.emailEditText)).getText().toString();

        return !email.isEmpty() || !IcewindUtils.isValidEmail(email);
    }

    /**
     * Send request to get amount of postal fee to add
     * to order total amount.
     * <p>
     * After request is done - we start yandex tokenization process
     *
     * @return
     */
    private void getPostFee(String indexTo, final double weight) {
        new RussianPostApi(new ApiBaseHandler() {
            @Override
            public void onResponse(JSONObject arg) {
                super.onResponse(arg);

                try {
                    double rate = 200; // default postal fee if request will fail
                    double amount = mOrder.getPrice();


                    if(amount >= ApiDataManager.FREE_DELIVERY_FROM) {
                        rate = 0;
                    }
                    else {

                        if (arg != null && arg.has("ground-rate")) {
                            JSONObject ground_rate = arg.getJSONObject("ground-rate");
                            double vat = (double) ground_rate.getInt("vat") / 100;
                            rate = (double) ground_rate.getInt("rate") / 100 + vat;

                            // weight correction
                            double weightPriceAddon = Math.ceil(weight / (double) 500) * 50;
                            rate += weightPriceAddon;
                        }
                    }
                    // fuck you yandex
                    if (ApiUser.isAdmin) {
                        // discount time
                        amount = 1.0;
                        rate = 1.0;
                    }

                    mOrder.postalFee = rate;
                    placeOrderOnBackend();


                    double totalAmount = amount + rate;

                    BigDecimal bdAmount = new BigDecimal(totalAmount).setScale(2,
                            RoundingMode.HALF_UP);

                    String product = mOrder.toString();
                    String overallDescription = String.format("Заказ: %s руб.\nДоставка: %s руб.",
                            amount, rate);

                    Amount yandexAmount = new Amount(bdAmount, Currency.getInstance("RUB"));

                    // Define payment methods
                    HashSet<PaymentMethodType> methods = new HashSet<>();
                    methods.add(PaymentMethodType.BANK_CARD);

                    // Start MSDK tokenization
                    Context theContext = OrderFragment.this.getActivity();

                    ShopParameters shopParameters = new ShopParameters(
                            product,
                            overallDescription,
                            clientApplicationKey,
                            methods,
                            true,
                            yandexAppID,
                            null,
                            true
                    );

                    Checkout.tokenize(theContext, yandexAmount, shopParameters);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("API", "Yandex tokenize failed!");
                    Log.e("API", e.toString());
                }
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        }).getFee(indexTo, weight);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_order, container, false);
        mView = view;
        mProgressBar = mView.findViewById(R.id.progress_bar);
        Button mPayButton = view.findViewById(R.id.payButton);
        mPayButton.setOnClickListener(getPayBtnListener());

        initAutoCompleteFields(view);
        Checkout.attach(getActivity().getSupportFragmentManager());

        Checkout.setResultCallback(new Checkout.ResultCallback() {
            @Override
            public void onResult(@NotNull String s, @NotNull PaymentMethodType paymentMethodType) {
                new AlcoAppApi(getCheckoutHandler()).createPayment(s, paymentMethodType, mOrder);
            }

            @NonNull
            private ApiBaseHandler getCheckoutHandler() {
                return new ApiBaseHandler() {
                    @Override
                    public void onResponse(JSONObject arg) {
                        super.onResponse(arg);
                        try {
                            if (arg.has("data")) {
                                if (arg.getJSONObject("data").has("confirmation_url")) {
                                    try {
                                        timeToStart3DS(arg.getJSONObject("data").getString("confirmation_url"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    ((MainActivity)getActivity()).showFragment(ShopMainFragment.class,
                                            new ArrayList<Object>() {});
                                }
                            }
                            else {
                                showToast(getString(R.string.error_in_3ds));
                                Log.d("API", "Something wrong with 3ds: no data object in response");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("API", "Failed to start 3ds intent or createPayment() failed");
                        }
                    }
                };
            }
        });


        mView.findViewById(R.id.main_order).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                return false;
            }
        });

        return view;
    }

    private void initAutoCompleteFields(View view) {
        // adapters to auto complete fields
        regionAdapter = new DadataRegionAdapter(getActivity(), android.R.layout.simple_dropdown_item_1line);
        cityAdapter = new DadataCityAdapter(getActivity(), android.R.layout.simple_dropdown_item_1line);
        streetAdapter = new DadataStreetAdapter(getActivity(), android.R.layout.simple_dropdown_item_1line);
        houseAdapter = new DadataHouseAdapter(getActivity(), android.R.layout.simple_dropdown_item_1line);

        AutoCompleteTextView mRegionSelector = view.findViewById(R.id.regionSelector);
        AutoCompleteTextView mCitySelector = view.findViewById(R.id.citySelector);
        AutoCompleteTextView mStreetSelector = view.findViewById(R.id.streetSelector);
        AutoCompleteTextView mHouseSelector = view.findViewById(R.id.houseSelector);

        mCitySelector.setAdapter(cityAdapter);
        mRegionSelector.setAdapter(regionAdapter);
        mStreetSelector.setAdapter(streetAdapter);
        mHouseSelector.setAdapter(houseAdapter);

        // set listeners to auto complete fields
        mRegionSelector.setOnItemClickListener(getRegionAdapter());
        mCitySelector.setOnItemClickListener(getCityAdapter());
        mStreetSelector.setOnItemClickListener(getStreetAdapter());
        mHouseSelector.setOnItemClickListener(getHouseAdapter());
    }

    private void placeOrderOnBackend() {
        // prepare order information for transfer
        mOrder.city = ((AutoCompleteTextView) mView.findViewById(R.id.citySelector)).getText().toString();
        mOrder.street = ((AutoCompleteTextView) mView.findViewById(R.id.streetSelector)).getText().toString();
        mOrder.post_index = ((EditText) mView.findViewById(R.id.postalCodeSelector)).getText().toString();
        mOrder.lastname = ((EditText) mView.findViewById(R.id.lastnameEditText)).getText().toString();
        mOrder.name = ((EditText) mView.findViewById(R.id.firstnameEditText)).getText().toString();
        mOrder.phone = ((EditText) mView.findViewById(R.id.phoneEditText)).getText().toString();
        mOrder.email = ((EditText) mView.findViewById(R.id.emailEditText)).getText().toString();
        mOrder.flat = ((EditText) mView.findViewById(R.id.flatSelector)).getText().toString();
        mOrder.region = ((EditText) mView.findViewById(R.id.regionSelector)).getText().toString();
        mOrder.house = ((EditText) mView.findViewById(R.id.houseSelector)).getText().toString();
        mOrder.building = "";
        mOrder.comment = "";

        new AlcoAppApi(new ApiBaseHandler() {
            @Override
            public void onResponse(JSONObject arg) {
                super.onResponse(arg);
                Log.d("API", "Order is updated");
            }
        }).createOrder(mOrder);
    }


    @NonNull
    private View.OnClickListener getPayBtnListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressBar.setVisibility(View.VISIBLE);
                boolean fieldsAreValid = validateAllFields();

                if (!fieldsAreValid) {
                    showToast("Обязательные поля не заполнены!");
                    mProgressBar.setVisibility(View.INVISIBLE);
                    return;
                }

                Log.d("API", "Sending request to post api");
                EditText postIndexTxt = mView.findViewById(R.id.postalCodeSelector);
                String postIndexTo = postIndexTxt.getText().toString();
                getPostFee(postIndexTo, mOrder.getWeight());
                mProgressBar.setVisibility(View.INVISIBLE);

            }
        };
    }

    @NonNull
    private AdapterView.OnItemClickListener getRegionAdapter() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DadataRegion item = (DadataRegion) parent.getItemAtPosition(position);

                if (item != null) {
                    try {
                        if (item.region_fias_id() != null)
                            cityAdapter.currentRegion = item.region_fias_id();
                        else
                            cityAdapter.currentArea = item.area_fias_id();
                    } catch (JSONException e) {
                        Log.e("API", "There was json parsing exception in dadataRegion", e);
                        e.printStackTrace();
                    }
                } else {
                    Log.e("API", "Item is not DadataRegion!!");
                }

            }
        };
    }

    @NonNull
    private AdapterView.OnItemClickListener getCityAdapter() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DadataCity item = (DadataCity) parent.getItemAtPosition(position);

                if (item != null) {
                    try {
                        streetAdapter.currentCity = item.city_fias_id();
                    } catch (JSONException e) {
                        Log.e("API", "There was json parsing exception in dadataCity", e);
                        e.printStackTrace();
                    }
                } else {
                    Log.e("API", "Item is not DadataCity!!");
                }

            }
        };
    }

    @NonNull
    private AdapterView.OnItemClickListener getStreetAdapter() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DadataStreet item = (DadataStreet) parent.getItemAtPosition(position);

                if (item != null) {
                    try {
                        houseAdapter.currentStreet = item.street_fias_id();
                    } catch (JSONException e) {
                        Log.e("API", "There was json parsing exception in dadataStreet", e);
                        e.printStackTrace();
                    }
                } else {
                    Log.e("API", "Item is not DadataHouse!!");
                }

            }
        };
    }

    @NonNull
    private AdapterView.OnItemClickListener getHouseAdapter() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DadataHouse item = (DadataHouse) parent.getItemAtPosition(position);

                if (item != null) {
                    try {
                        String postalCodeStr = item.postalCode();
                        EditText postalCodeView = mView.findViewById(R.id.postalCodeSelector);
                        postalCodeView.setText(postalCodeStr);

                    } catch (JSONException e) {
                        Log.e("API", "There was json parsing exception in dadataHouse", e);
                        e.printStackTrace();
                    }
                } else {
                    Log.e("API", "Item is not DadataHouse!!");
                }

            }
        };
    }


    @Override
    public void onDetach() {
        // Detach MSDK from supportFragmentManager
        Checkout.detach();
        // Detach result callback
        Checkout.setResultCallback(null);
        super.onDetach();
    }

    @Override
    public void onClick(View v) {

    }

    public void showToast(String txt) {
        Toast toast = Toast.makeText(getApplicationContext(),
                txt,
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }


    /**
     * Shows 3DS confirmation view.
     *
     * @param urlFor3ds
     */
    void timeToStart3DS(String urlFor3ds) {
        Intent intent = null;
        try {
            intent = Checkout.create3dsIntent(
                    getActivity().getApplicationContext(),
                    new URL(urlFor3ds),
                    new URL(ApiKnownUrls.RETURN_URL)
            );
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("API", "Failed to start 3DS intent");
        }
        startActivityForResult(intent, 1);
    }

    private void sendOrderStatusPayed() {
        new AlcoAppApi(new ApiBaseHandler() {
            @Override
            public void onResponse(JSONObject arg) {
                super.onResponse(arg);
                //TODO: redirect to shop fragment
                ((MainActivity)getActivity()).showFragment(ShopMainFragment.class,
                        new ArrayList<Object>() {});

            }
        }).setOrderIsPayed(mOrder.order_id);
    }

    private void sendOrderStatusCancelled() {
        new AlcoAppApi(new ApiBaseHandler() {
            @Override
            public void onResponse(JSONObject arg) {
                super.onResponse(arg);
                ((MainActivity)getActivity()).showFragment(ShopMainFragment.class,
                        new ArrayList<Object>() {});
            }
        }).setOrderIsCancelled(mOrder.order_id);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            switch (resultCode) {
                case RESULT_OK:
                    // 3ds прошел
                    showToast("Оплата прошла успешно");
                    // update order status and redirect to shop
                    sendOrderStatusPayed();
                    break;
                case RESULT_CANCELED:
                    // экран 3ds был закрыт
                    showToast("Оплата была отменена");
                    sendOrderStatusCancelled();
                    break;
                case Checkout.RESULT_ERROR:
                    showToast("При оплате произошла ошибка");
                    Log.e("API", data.getStringExtra(Checkout.EXTRA_ERROR_DESCRIPTION));
                    // во время 3ds произошла какая-то ошибка (нет соединения или что-то еще)
                    // более подробную информацию можно посмотреть в data
                    // data.getIntExtra(Checkout.EXTRA_ERROR_CODE) - код ошибки из WebViewClient.ERROR_* или Checkout.ERROR_NOT_HTTPS_URL
                    // data.getStringExtra(Checkout.EXTRA_ERROR_DESCRIPTION) - описание ошибки (может отсутствовать)
                    // data.getStringExtra(Checkout.EXTRA_ERROR_FAILING_URL) - url по которому произошла ошибка (может отсутствовать)
                    break;
            }
        }
    }

    public void reInitUIFields() {
        if(!mOrder.city.equals("null"))
            ((AutoCompleteTextView) mView.findViewById(R.id.citySelector)).setText(mOrder.city);
        if(!mOrder.street.equals("null"))
            ((AutoCompleteTextView) mView.findViewById(R.id.streetSelector)).setText(mOrder.street);
        if(!mOrder.post_index.equals("null"))
            ((EditText) mView.findViewById(R.id.postalCodeSelector)).setText(mOrder.post_index);
        if(!mOrder.lastname.equals("null"))
            ((EditText) mView.findViewById(R.id.lastnameEditText)).setText(mOrder.lastname);
        if(!mOrder.name.equals("null"))
            ((EditText) mView.findViewById(R.id.firstnameEditText)).setText(mOrder.name);
        if(!mOrder.phone.equals("null"))
            ((EditText) mView.findViewById(R.id.phoneEditText)).setText(mOrder.phone);
        if(!mOrder.email.equals("null"))
            ((EditText) mView.findViewById(R.id.emailEditText)).setText(mOrder.email);
        if(!mOrder.flat.equals("null"))
            ((EditText) mView.findViewById(R.id.flatSelector)).setText(mOrder.flat);
        if(!mOrder.region.equals("null"))
            ((EditText) mView.findViewById(R.id.regionSelector)).setText(mOrder.region);
        if(!mOrder.house.equals("null"))
            ((EditText) mView.findViewById(R.id.houseSelector)).setText(mOrder.house);
        mOrder.building = "";
        mOrder.comment = "";
    }

    @Override
    public void initFragment(Object... args) {
        ArrayList<Object> arr = (ArrayList<Object>) args[0];

        if (arr == null || arr.size() < 1) {
            Log.e("API", "Order is not set!!!!");
            throw new IllegalArgumentException("Order cannot be null in OrderFragment");
        }

        mOrder = (ApiOrder) arr.get(0);

        // check if order is not loaded
        if (mOrder.phone == null || mOrder.phone.equals("")) {
            ApiDataManager.preloadDoneCallback = new OnPreloadDoneListener() {
                @Override
                public void preloadDone() {
                    // not used here
                }

                @Override
                public void preloadCartOrderDone(ApiOrder order) {
                    mOrder = order;
                    reInitUIFields();
                }

                @Override
                public void preloadImagesDone(Bitmap[] bitmaps) {
                    // not used here
                }
            };
            ApiDataManager.orderInfo(mOrder.order_id);
        }
    }
}
