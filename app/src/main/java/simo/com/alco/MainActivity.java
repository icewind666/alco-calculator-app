package simo.com.alco;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

//import com.google.android.gms.analytics.HitBuilders;
//import com.google.android.gms.analytics.Tracker;
import com.vk.sdk.VKSdk;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import simo.com.alco.api.AlcoAppApi;
import simo.com.alco.api.ApiBaseHandler;
import simo.com.alco.api.ApiCartItem;
import simo.com.alco.api.ApiCategory;
import simo.com.alco.api.ApiDataManager;
import simo.com.alco.api.ApiItem;
import simo.com.alco.api.ApiOrder;
import simo.com.alco.api.ApiProcess;
import simo.com.alco.api.ApiSubcategory;
import simo.com.alco.api.ApiUser;
import simo.com.alco.fragments.CalculatorsFragment;
import simo.com.alco.fragments.CartFragment;
import simo.com.alco.fragments.CategoryItemFragment;
import simo.com.alco.fragments.EnterShopFragment;
import simo.com.alco.fragments.Fertman2Fragment;
import simo.com.alco.fragments.FertmanFragment;
import simo.com.alco.fragments.FormulaFragment;
import simo.com.alco.fragments.FragmentWithInit;
import simo.com.alco.fragments.HeadsFragment;
import simo.com.alco.fragments.MyOrdersFragment;
import simo.com.alco.fragments.PrivacyAgreementFragment;
import simo.com.alco.fragments.PrivacyPolicyFragment;
import simo.com.alco.fragments.SugarLitreFragment;
import simo.com.alco.fragments.events.OnCalculatorSelectListener;
import simo.com.alco.fragments.events.OnCartOrderAction;
import simo.com.alco.fragments.events.OnFragmentInteractionListener;
import simo.com.alco.fragments.events.OnListFragmentItemSelection;
import simo.com.alco.fragments.events.OnSettingsFragmentEvent;
import simo.com.alco.fragments.OrderFragment;
import simo.com.alco.fragments.ProcessListItemFragment;
import simo.com.alco.fragments.QuantityChangeDialogFragment;
import simo.com.alco.fragments.RateFragment;
import simo.com.alco.fragments.SettingsFragment;
import simo.com.alco.fragments.ShopItemDetailsFragment;
import simo.com.alco.fragments.ShopMainFragment;
import simo.com.alco.fragments.SocialDialogFragment;
import simo.com.alco.fragments.SubcategoryItemFragment;

import static simo.com.alco.R.id.textView;
import static simo.com.alco.R.id.textView_fertman;


/**
 * MainActivity represents main application screen.
 */
public class MainActivity extends AppCompatActivity implements
        OnFragmentInteractionListener, OnSettingsFragmentEvent, OnCalculatorSelectListener,
        OnListFragmentItemSelection, OnCartOrderAction, QuantityChangeDialogFragment.QuantityChangeDialogListener {

    SharedPreferences sharedPref;
    AppActivities currentFragment;
    String oldValueToEdit = "";
    TextView displayText;
    TextView displayText_fertman;
    ArrayList<Integer[]> resultIntersection = new ArrayList<>();
    FragmentWithInit currentOpenedFragment;
    public BottomNavigationView mNavigation;

    double initialProofValue = 0;
    double true_initialProofValue = 0;
    double desiredProofValue = 0;
    double initialVolume = 0;
    double temperatureCorrection = 0;

    boolean isTyping = false;
    boolean done = false;
    boolean useTempCorrection = false;
    boolean totalReset_fertman = false;
    boolean done_fertman = false;
    boolean isValid_fertman = false;
    boolean totalReset = true;

    int[] initialProofRows_fertman = new int[]{95, 90, 85, 80, 75, 70, 65, 60, 55, 50, 45, 40, 35};
    int[] desiredProofColumns_fertman = new int[]{90, 85, 80, 75, 70, 65, 60, 55, 50, 45, 40, 35, 30};

    int initialProofValue_fertman = 0;
    int initialProofIndex_fertman = 0;
    int desiredProofValue_fertman = 0;
    int desiredProofIndex_fertman = 0;
    int waterVolume_fertman = 0;
    int step = 0;
    int step_fertman = 0;
    ObjectAnimator objAnim;
    public ApiBaseHandler minPriceAndDeliveryHandler;



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        /**
         * Called on navigation action
         * @param menuItem
         * @return
         */
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            int id = menuItem.getItemId();
            Class fragmentClass = null;
            ArrayList<Object> args = new ArrayList<>();

            switch (id) {
                case R.id.navigation_calculators:
                    fragmentClass = CalculatorsFragment.class;
                    currentFragment = AppActivities.FORMULA;
                    break;

                case R.id.navigation_settings:
                    currentFragment = null;
                    args.add(getHeadsPercent());
                    fragmentClass = SettingsFragment.class;
                    break;

                case R.id.navigation_shop:
                    if (AlcoAppApi.AUTH_TICKET.isEmpty()) {
                        // if no - open EnterShop fragment.
                        fragmentClass = EnterShopFragment.class;
                        currentFragment = AppActivities.ENTER;
                    } else {
                        // If yes - proceed
                        fragmentClass = ProcessListItemFragment.class;
                        currentFragment = AppActivities.SHOP;
                    }
                    break;

                case R.id.navigation_cart:
                    if (AlcoAppApi.AUTH_TICKET.isEmpty()) {
                        // if no - open EnterShop fragment.
                        fragmentClass = EnterShopFragment.class;
                        currentFragment = AppActivities.ENTER;
                    } else {
                        // If yes - proceed
                        fragmentClass = CartFragment.class;
                        currentFragment = AppActivities.CART;
                    }
                    break;
                default:
                    // raise error?
            }

            showFragment(fragmentClass, args);

            displayText = findViewById(textView);
            displayText_fertman = findViewById(textView_fertman);
            clearAll_fertman(null);
            clearAll();
            return true;
        }
    };

    /**
     * Writes last run date to local storage
     */
    private void setLastRunDate(Date d) {
        try {
            SharedPreferences.Editor ed = sharedPref.edit();
            ed.putLong(getString(R.string.lastRunDate), d.getTime());
            ed.apply();
        } catch (Exception e) {
            Log.d("alco", "failed to parse date from long");
        }

    }

    /**
     * Reads last run setting from local storage
     */
    private Date lastRunDate() {
        try {
            if (sharedPref.contains(getString(R.string.lastRunDate))) {
                return new Date(sharedPref.getLong(getString(R.string.lastRunDate), 0));
            } else {
                return null;
            }
        } catch (Exception e) {
            Log.d("alco", "failed to parse date from long");
            return null;
        }
    }


    /**
     * Writes preferences to local storage
     */
    private void setPrevRunDate(Date d) {
        try {
            SharedPreferences.Editor ed = sharedPref.edit();
            ed.putLong(getString(R.string.prevRunDate), d.getTime());
            ed.apply();
        } catch (Exception e) {
            Log.d("alco", "failed to parse date from long");
        }

    }


    /**
     * Reads settings from local storage
     */
    @Nullable
    private Date prevRunDate() {
        try {
            if (sharedPref.contains(getString(R.string.prevRunDate))) {
                return new Date(sharedPref.getLong(getString(R.string.prevRunDate), 0));
            } else {
                return null;
            }
        } catch (Exception e) {
            Log.d("alco", "failed to parse date from long");
            return null;
        }
    }


    /**
     * Shows the rate_fragment app fragment
     */
    protected void replaceContentWithRateView() {
        try {
            showFragment(RateFragment.class, new ArrayList<>());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Called on create of activity
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main_layout);


        mNavigation = findViewById(R.id.navigation);
        mNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        initMainViewElements();

        ApiDataManager.getFreeDeliveryFrom(new ApiBaseHandler() {
            @Override
            public void onResponse(JSONObject arg) {
                try {
                    JSONObject obj = arg.getJSONObject("data");
                    ApiDataManager.FREE_DELIVERY_FROM = Double.parseDouble(obj.getString("free_shipping_min_price"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        ApiDataManager.getMinOrderPrice(new ApiBaseHandler() {
            @Override
            public void onResponse(JSONObject arg) {
                try {
                    JSONObject obj = arg.getJSONObject("data");
                    ApiDataManager.MIN_PRICE = Double.parseDouble(obj.getString("min_price"));
                    if(minPriceAndDeliveryHandler != null) {
                        minPriceAndDeliveryHandler.onResponse(null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        initFertmanFragmentElements();

        showFragment(CalculatorsFragment.class, new ArrayList<>());

        currentFragment = AppActivities.FORMULA;
        displayText = findViewById(textView);
        displayText_fertman = findViewById(textView_fertman);

        VKSdk.initialize(getApplicationContext());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("API", "Activity result");

        super.onActivityResult(requestCode, resultCode, data);

            switch (resultCode) {
                case RESULT_OK:
                    // successful tokenization
                    Log.d("API", "Activity result");
                    break;
                case RESULT_CANCELED:
                    // user canceled tokenization
                    Log.d("API", "Activity result");
                    break;
            }
    }

        /**
         * Inits fertman fragment
         */
    private void initFertmanFragmentElements() {
        resultIntersection.add(new Integer[]{64, 133, 209, 295, 391, 501, 629, 779, 957, 1174, 1443, 1785, 2239});
        resultIntersection.add(new Integer[]{null, 65, 138, 218, 310, 414, 435, 677, 847, 1052, 1306, 1630, 2061});
        resultIntersection.add(new Integer[]{null, null, 68, 144, 231, 329, 443, 578, 738, 932, 1172, 1478, 1884});
        resultIntersection.add(new Integer[]{null, null, null, 72, 153, 246, 353, 480, 630, 812, 1039, 1327, 1709});
        resultIntersection.add(new Integer[]{null, null, null, null, 76, 163, 264, 382, 523, 694, 906, 1177, 1535});
        resultIntersection.add(new Integer[]{null, null, null, null, null, 81, 175, 285, 417, 577, 774, 1027, 1360});
        resultIntersection.add(new Integer[]{null, null, null, null, null, null, 88, 190, 311, 460, 644, 878, 1189});
        resultIntersection.add(new Integer[]{null, null, null, null, null, null, null, 95, 207, 344, 514, 730, 1017});
        resultIntersection.add(new Integer[]{null, null, null, null, null, null, null, null, 103, 229, 384, 583, 845});
        resultIntersection.add(new Integer[]{null, null, null, null, null, null, null, null, null, 114, 255, 436, 674});
        resultIntersection.add(new Integer[]{null, null, null, null, null, null, null, null, null, null, 127, 290, 505});
        resultIntersection.add(new Integer[]{null, null, null, null, null, null, null, null, null, null, null, 144, 335});
        resultIntersection.add(new Integer[]{null, null, null, null, null, null, null, null, null, null, null, null, 167});
    }

    /**
     * Init button actions
     */
    private void initMainViewElements() {
        sharedPref = getPreferences(Context.MODE_PRIVATE);
        ApiDataManager.contextActivity = this;

        // checking if user already registered
        String userToken = getStoredUserTokenIfAvailable();
        int isAdmin = getStoredUserIsAdminOption();

        if (!userToken.isEmpty()) {
            // fill existing auth token
            AlcoAppApi.AUTH_TICKET = userToken;
            ApiUser.TOKEN = userToken;
            AlcoAppApi.user = new ApiUser();

            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
            AlcoAppApi.user.username = sharedPref.getString("username", "");

            if (isAdmin == 1)
                ApiUser.isAdmin = true;

            Authenticator.setDefault(new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(AlcoAppApi.AUTH_TICKET, new char[]{});
                }
            });

            updateCartCountBadge(-1);
        } else {
            AlcoAppApi.AUTH_TICKET = "";
            ApiUser.TOKEN = "";
            ApiUser.isAdmin = false;
            updateCartCountBadge(0);
        }

        processApplicationRunDates();

        if (!sharedPref.contains(getString(R.string._firstRun))) {
            SharedPreferences.Editor ed = sharedPref.edit();
            ed.putInt(getString(R.string._firstRun), 1);
            ed.apply();
        }

        if (!sharedPref.contains(getString(R.string._firstRun))) {
            SharedPreferences.Editor ed = sharedPref.edit();
            ed.putInt(getString(R.string._firstRun), 1);
            ed.apply();
        }

    }

    public void updateCartCountBadge(int value) {
        BottomNavigationMenuView bottomNavigationMenuView =
                (BottomNavigationMenuView) mNavigation.getChildAt(0);
        View nav_view = bottomNavigationMenuView.getChildAt(2);
        final BottomNavigationItemView itemView = (BottomNavigationItemView) nav_view;

        final View badge = LayoutInflater.from(this)
                .inflate(R.layout.navbar_notification_badge, bottomNavigationMenuView, false);


        ApiBaseHandler pHandler = new ApiBaseHandler() {
            @Override
            public void onResponse(JSONObject arg) {
                try {
                    Log.d("API", "Get user cart items count:");
                    JSONObject responseObj = arg.getJSONObject("data");
                    int itemCount = responseObj.getInt("count");
                    TextView tx = badge.findViewById(R.id.notifications_badge);
                    tx.setText(String.format("%d", itemCount));
                    itemView.addView(badge);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        if(value >= 0 ) {
            TextView tx = badge.findViewById(R.id.notifications_badge);
            tx.setText(String.format("%d", value));
            itemView.addView(badge);
        }
        else {
            ApiDataManager.userCartItemsCount(pHandler);
        }
    }



    /**
     * @return
     */
    private String getStoredUserTokenIfAvailable() {
        String userToken = "";

        if (sharedPref.contains(getString(R.string.usertoken))) {
            userToken = sharedPref.getString(getString(R.string.usertoken), "");
        }

        return userToken;

    }

    /**
     * @return
     */
    private int getStoredUserIsAdminOption() {
        int isAdmin = 0;

        if (sharedPref.contains("is_admin")) {
            isAdmin = sharedPref.getInt("is_admin", 0);
        }

        return isAdmin;

    }

    /**
     * Set current and prev dates of application run times
     */
    private void processApplicationRunDates() {
        Date currentStartDate = new Date();
        Date lastRunDate = lastRunDate();

        if (lastRunDate != null) {
            setPrevRunDate(lastRunDate);
        }

        Date prevRunDate = prevRunDate();

        if (prevRunDate != null) {
            setLastRunDate(currentStartDate);
        } else {
            setPrevRunDate(currentStartDate);
        }

        // checking launch dates
        checkLaunchDates();
    }

    /**
     * Returns string resource by id
     *
     * @param key
     * @return
     */
    public String s(int key) {
        return getString(key);
    }

    /**
     * Checks run dates and set counters if needed
     */
    private void checkLaunchDates() {
        Date from = prevRunDate();
        Date to = lastRunDate();

        // Checks if launch dates exist
        if (from != null && to != null) {
            // launched app 3 times the same day (28800 seconds is a 1/3 of 24hrs)
            if (IcewindUtils.getDateDiff(from, to, TimeUnit.SECONDS) <= 28800) {
                // MARK: - increment key "LaunchCount" if key exists, otherwise create key "LaunchCount" and set its value to 1
                if (sharedPref.contains(s(R.string._24hrsLaunchCount))) {
                    int launchCount = sharedPref.getInt(s(R.string._24hrsLaunchCount), 0) + 1;
                    SharedPreferences.Editor ed = sharedPref.edit();
                    ed.putInt(s(R.string._24hrsLaunchCount), launchCount);
                    ed.apply();
                } else {
                    SharedPreferences.Editor ed = sharedPref.edit();
                    ed.putInt(s(R.string._24hrsLaunchCount), 2);
                    ed.apply();
                }
            } else {
                SharedPreferences.Editor ed = sharedPref.edit();
                ed.remove(s(R.string._24hrsLaunchCount));
                ed.apply();
            }

            if (IcewindUtils.getDateDiff(from, to, TimeUnit.SECONDS) >= 57600 &&
                    IcewindUtils.getDateDiff(from, to, TimeUnit.SECONDS) <= 115200) {
                if (sharedPref.contains(s(R.string._2daysInaRowCount))) {
                    int launchCount = sharedPref.getInt(s(R.string._2daysInaRowCount), 0) + 1;
                    SharedPreferences.Editor ed = sharedPref.edit();
                    ed.putInt(s(R.string._2daysInaRowCount), launchCount);
                    ed.apply();
                } else {
                    SharedPreferences.Editor ed = sharedPref.edit();
                    ed.putInt(s(R.string._2daysInaRowCount), 2);
                    ed.apply();

                }
            } else {
                SharedPreferences.Editor ed = sharedPref.edit();
                ed.remove(s(R.string._2daysInaRowCount));
                ed.apply();
            }

            if (sharedPref.contains(s(R.string.MaybeLaterCountDaysStarted))) {
                // date when we started counting days
                long counterStarted = sharedPref.getLong(s(R.string.MaybeLaterCountDaysStarted), 0);
                // current date
                long currentDate = new Date().getTime();
                long timeDeltaSeconds = IcewindUtils.getMillisDiff(counterStarted, currentDate, TimeUnit.SECONDS);
                long daysCount = timeDeltaSeconds / 86400;
                SharedPreferences.Editor ed = sharedPref.edit();
                ed.putLong(s(R.string.MaybeLaterCountDays), daysCount);
                ed.apply();
            }
        }
    }


    /**
     * Validating initial proof value
     *
     * @param initialProof - value for validating
     */
    private void validateInitialProof(double initialProof) {
        TextView displayText = findViewById(textView);
        // storing value before validating
        // to be able to edit it after error
        oldValueToEdit = String.valueOf(initialProof);
        proofValidate(initialProof, displayText);
    }

    /**
     * Validate entered initial proof
     *  @param initialProof
     * @param displayText
     */
    private boolean proofValidate(double initialProof, TextView displayText) {
        double valueToCheck = initialProof;

        if (useTempCorrection) {
            valueToCheck = true_initialProofValue;
        }

        ValidateResult vResult = Validator.validateInitialProof(valueToCheck);

        switch (vResult) {
            case INVALID_INITIAL_PROOF:
                displayText.setText(getString(R.string.unrealInitialProof, String.valueOf(true_initialProofValue)));
                initialProofValue = 0;
                true_initialProofValue = 0;
                return false;
            case UNREAL_INITIAL_PROOF:
                displayText.setText(getText(R.string.noway));
                initialProofValue = 0;
                true_initialProofValue = 0;
                ImageButton deleteBtn = findViewById(R.id.buttonClear);
                deleteBtn.setImageResource(R.drawable.backspace);
                return false;
            case ZERO_PROOF:
                displayText.setText(getText(R.string.water));
                initialProofValue = 0;
                true_initialProofValue = 0;
                return false;
            default:
                initialProofValue = initialProof;
                oldValueToEdit = ""; // this indicated that there was no error
                return true;
        }
    }

    /**
     * Validating desired proof value
     *
     * @param desiredProof
     */
    private void validateDesiredProof(double desiredProof) {
        TextView displayText = findViewById(textView);

        // storing value before validating
        // to be able to edit it after error
        oldValueToEdit = String.valueOf(desiredProof);
        double valueToCheck = initialProofValue;

        if (useTempCorrection) {
            valueToCheck = true_initialProofValue;
        }

        if (desiredProof >= valueToCheck) {
            if (useTempCorrection) {
                String errorMsg = getString(R.string.desiredIsMoreThanInititalProofTemp,
                        String.valueOf(Math.round(initialProofValue)),
                        String.valueOf(Math.round(temperatureCorrection)),
                        String.valueOf(Math.round(true_initialProofValue)),
                        String.valueOf(Math.round(desiredProof)));
                displayText.setText(errorMsg);
            } else {
                String errorMsg = getString(R.string.desiredIsMoreThanInititalProof,
                        String.valueOf(Math.round(initialProofValue)),
                        String.valueOf(Math.round(desiredProof)));
                displayText.setText(errorMsg);
            }
        } else if (desiredProof == 0) {
            displayText.setText(getText(R.string.nothingToDo));
        } else {
            desiredProofValue = desiredProof;
            oldValueToEdit = ""; // this indicated that there was no error
        }
    }

    /**
     * Validates temperature correction
     *
     * @param temperature
     */
    private void validateTemp(double temperature) {
        displayText = findViewById(textView);
        oldValueToEdit = String.valueOf(temperature);

        ValidateResult validateResult = Validator.validateTemperature(temperature);

        switch (validateResult) {
            case TOO_HIGH_TEMPERATURE:
                displayText.setText(getText(R.string.tempTooHigh));
                break;

            case TOO_LOW_TEMPERATURE:
                displayText.setText(getText(R.string.tempTooLow));
                break;

            default:
                temperatureCorrection = temperature;
                // считаем крепость с поправкой
                true_initialProofValue = Math.round(1.0*initialProofValue + (20-temperature)*0.3);
                useTempCorrection = true;
                true_initialProofValue = (true_initialProofValue > 100) ? 100 : true_initialProofValue;
                oldValueToEdit = "";
        }
    }


    /**
     * Shows fragment.
     * Inits with args if needed
     *
     * @param fragmentClass
     * @param args
     */
    public void showFragment(Class fragmentClass, List<Object> args) {
        FragmentWithInit fragment = null;

        try {
            assert fragmentClass != null;
            fragment = (FragmentWithInit) fragmentClass.newInstance();
            fragment.initFragment(args);
            this.currentOpenedFragment = fragment;
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment, fragmentClass.getName()).addToBackStack(null).commit();
    }

    @Override
    public void onFragmentFormulaClick(View sourceView) {
        if (sourceView.getId() == R.id.buttonClear || sourceView.getId() == R.id.buttonNext) {
            ActionTapListener actionTapListener = new ActionTapListener((TextView) findViewById(textView));
            actionTapListener.onClick(sourceView);
        } else {
            NumberTapListener numberTapListener = new NumberTapListener((TextView) findViewById(textView));
            numberTapListener.onClick(sourceView);
        }
    }

    @Override
    public void onFragmentFertmanClick(View sourceView, FertmanFragment sourceFragment) {
        displayText_fertman = findViewById(textView_fertman);

        switch (sourceView.getId()) {
            case R.id.buttonClearFertman:
                if (totalReset_fertman) {
                    clearAll_fertman(sourceFragment);
                } else {
                    clearLastTypedNumber_fertman(sourceFragment);
                }

                break;

            case R.id.buttonNextFertman:
                switch (step_fertman) {
                    case 0:
                        if (initialProofValue_fertman >= 0) {
                            step_fertman++;
                            Fragment fragmentByTag = getSupportFragmentManager().findFragmentByTag(FertmanFragment.class.getName());
                            if (fragmentByTag instanceof FertmanFragment)
                                ((FertmanFragment) fragmentByTag).setDesiredProofButtonTitles();

                            displayText_fertman.setText(getString(R.string.inputDesiredProof));
                        }
                        break;
                    case 1:
                        if (desiredProofValue_fertman >= 0) {
                            validate_fertman();
                            if (isValid_fertman) {
                                step_fertman++;
                                currentOpenedFragment = sourceFragment;
                                shareButton_fertman();
                                calculate_fertman();

                            }
                        }
                        break;
                    default:
                }

                break;
            default:
                switch (step_fertman) {
                    case 0:
                        int tag1 = Integer.valueOf(sourceView.getTag().toString());
                        initialProofValue_fertman = initialProofRows_fertman[tag1];
                        initialProofIndex_fertman = tag1;
                        displayText_fertman.setText(getString(R.string.initialProofIsFertman1, String.valueOf(Math.round(initialProofValue_fertman))));
                        ImageButton deleteBtn = findViewById(R.id.buttonClearFertman);
                        deleteBtn.setImageResource(R.drawable.backspace);
                        break;
                    case 1:
                        int tag = Integer.valueOf(sourceView.getTag().toString());
                        desiredProofValue_fertman = desiredProofColumns_fertman[tag];
                        desiredProofIndex_fertman = tag;
                        displayText_fertman.setText(getString(R.string.desiredProofIsFertman1, String.valueOf(Math.round(desiredProofValue_fertman))));
                        totalReset_fertman = false;
                        ImageButton deleteBtn1 = findViewById(R.id.buttonClearFertman);
                        deleteBtn1.setImageResource(R.drawable.backspace);
                        validate_fertman();
                        break;

                    default:
                        break;
                }
        }
    }

    /**
     * Handles clear button clicks.
     * Removes the most right number
     */
    private void clearLastTypedNumber_fertman(FertmanFragment sourceFragment) {
        switch (step_fertman) {
            case 0:
                clearAll_fertman(sourceFragment);
                break;

            case 1:
                if (desiredProofValue_fertman > 0) {
                    desiredProofValue_fertman = -1;
                    totalReset_fertman = true;
                    waterVolume_fertman = -1;
                    displayText_fertman.setText(getString(R.string.inputDesiredProof));
                    ImageButton deleteBtn = findViewById(R.id.buttonClearFertman);
                    deleteBtn.setImageResource(R.drawable.backspace);
                } else {
                    step_fertman = 0;
                    clearAll_fertman(sourceFragment);
                }
                break;

            default:
                break;
        }
    }

    /**
     * Clears all in fertman fragment
     */
    private void clearAll_fertman(FertmanFragment sourceFragment) {
        totalReset_fertman = false;
        done_fertman = false;
        initialProofValue_fertman = -1;
        desiredProofValue_fertman = -1;
        waterVolume_fertman = -1;
        step_fertman = 0;

        if (displayText_fertman != null) {
            displayText_fertman.setText(s(R.string.inputInitialProofFertman1));
        }

        // delete button image delete
        ImageButton deleteBtn = findViewById(R.id.buttonClearFertman);

        if (deleteBtn != null) {
            deleteBtn.setImageResource(R.drawable.delete);
        }

        if (sourceFragment != null)
            sourceFragment.setInitialProofButtonTitles();

        if (objAnim != null) {
            objAnim.cancel();
        }

        ImageButton nextBtn = findViewById(R.id.buttonNextFertman);
        if (nextBtn != null) {
            nextBtn.setImageResource(R.drawable.forward);
            nextBtn.setOnClickListener(this.currentOpenedFragment);
        }
    }

    /**
     * Clear all states and progress
     */
    protected void clearAll() {
        initialProofValue = 0;
        isTyping = false;
        step = 0;
        desiredProofValue = 0;
        true_initialProofValue = 0;
        initialVolume = 0;
        done = false;

        if (displayText != null) {
            displayText.setText(getText(R.string.inputInitialProof));
        }

        ImageButton deleteBtn = findViewById(R.id.buttonClear);

        if (deleteBtn != null) {
            deleteBtn.setImageResource(R.drawable.delete);
        }

        totalReset = true;
        useTempCorrection = false;
        temperatureCorrection = 0;
        oldValueToEdit = "";
    }


    /**
     * Calculates result in fertman fragment
     */
    private void calculate_fertman() {
        waterVolume_fertman = resultIntersection.get(initialProofIndex_fertman)[desiredProofIndex_fertman];

        displayText_fertman.setText(getString(R.string.resultTextFertman1, String.valueOf(Math.round(desiredProofValue_fertman)),
                String.valueOf(Math.round(waterVolume_fertman)), String.valueOf(Math.round(initialProofValue_fertman))));

        totalReset_fertman = true;
        done_fertman = true;
        ImageButton deleteBtn = findViewById(R.id.buttonClearFertman);
        deleteBtn.setImageResource(R.drawable.delete);
        currentFragment = AppActivities.FERTMAN1;
        showRateDialogIfNeeded();
    }

    /**
     * Validates fertman fragment values
     */
    private void validate_fertman() {
        if (initialProofValue_fertman > 0 && desiredProofValue_fertman > 0) {
            if (desiredProofValue_fertman > initialProofValue_fertman) {
                displayText_fertman.setText(getString(R.string.desiredIsMoreThanInitialFertman1,
                        String.valueOf(Math.round(desiredProofValue_fertman)),
                        String.valueOf(Math.round(initialProofValue_fertman))
                ));
                isValid_fertman = false;
                ImageButton deleteBtn = findViewById(R.id.buttonClearFertman);
                deleteBtn.setImageResource(R.drawable.delete);
            } else if (desiredProofValue_fertman == initialProofValue_fertman) {
                displayText_fertman.setText(getString(R.string.desiredIsEqualInitialFertman1,
                        String.valueOf(Math.round(desiredProofValue_fertman)),
                        String.valueOf(Math.round(initialProofValue_fertman))
                ));
                ImageButton deleteBtn = findViewById(R.id.buttonClearFertman);
                deleteBtn.setImageResource(R.drawable.delete);
                isValid_fertman = false;
            } else {
                isValid_fertman = true;
            }
        }
    }

    @Override
    public void sendPartnershipRequestEmailDialog(SettingsFragment fragment) {
        String subjectFormatted = getString(R.string.becomePartnerMailHeader);
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        int version = Build.VERSION.SDK_INT;
        int appVersion = BuildConfig.VERSION_CODE;
        String versionRelease = Build.VERSION.RELEASE;

        String device_info = "\nУстройство: " + manufacturer
                + "\nМодель:" + model
                + "\n ОС: " + version
                + "\n Приложение: " + appVersion
                + " (" + versionRelease + ")";

        String bodyFormatted = String.format("\n\n\n\n %s", device_info);
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse(getString(R.string.feedbackMail)));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subjectFormatted);
        emailIntent.putExtra(Intent.EXTRA_TEXT, bodyFormatted);
        startActivity(Intent.createChooser(emailIntent, "Отправить заявку..."));

    }

    @Override
    public void sendEmailDialog(SettingsFragment fragment) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri data = Uri.parse("mailto:spasibomusic@gmail.com");
        intent.setData(data);
        startActivity(intent);
    }

    @Override
    public void rateApp(SettingsFragment fragment) {
        rate_ok();
    }

    @Override
    public void rate_noThanks() {
        sharedPref.edit().putBoolean(s(R.string.DecisionMade), true).apply();

        if (currentFragment != null) {
            switch (currentFragment) {
                case FERTMAN1:
                    ArrayList<Object> args = new ArrayList<>();
                    args.add(initialProofRows_fertman);
                    args.add(desiredProofColumns_fertman);
                    clearAll_fertman(null);
                    showFragment(FertmanFragment.class, args);

                    break;

                case FORMULA:
                    showFragment(FormulaFragment.class, new ArrayList<>());
                    break;

                case FERTMAN2:
                    showFragment(Fertman2Fragment.class, new ArrayList<>());
                    break;

                default:
                    showFragment(FormulaFragment.class, new ArrayList<>());
                    break;
            }
        } else {
            ArrayList<Object> args = new ArrayList<>();
            args.add(getHeadsPercent());
            showFragment(SettingsFragment.class, args);
        }
    }

    @Override
    public void rate_ok() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));

        if (currentFragment != null) {
            switch (currentFragment) {
                case FERTMAN1:
                    ArrayList<Object> args = new ArrayList<>();
                    args.add(initialProofRows_fertman);
                    args.add(desiredProofColumns_fertman);
                    clearAll_fertman(null);
                    showFragment(FertmanFragment.class, args);
                    break;

                case FORMULA:
                    showFragment(FormulaFragment.class, new ArrayList<>());
                    break;

                case FERTMAN2:
                    showFragment(Fertman2Fragment.class, new ArrayList<>());
                    break;
            }
        } else {
            ArrayList<Object> args = new ArrayList<>();
            args.add(getHeadsPercent());

            showFragment(SettingsFragment.class, args);
        }
    }

    @Override
    public void rate_later() {
        track("MaybeLaterPressed");
        int maybeCount = 1;

        if (sharedPref.contains(s(R.string.MaybeLaterCount))) {
            maybeCount = sharedPref.getInt(s(R.string.MaybeLaterCount), 0) + 1;
            sharedPref.edit().putInt(s(R.string.MaybeLaterCount), maybeCount).apply();
        } else {
            sharedPref.edit().putInt(s(R.string.MaybeLaterCount), maybeCount).apply();
        }

        // after increasing MayBeLater counter.
        // we should check - if it reached 2:
        if (maybeCount >= 2) {
            // then we start counting days.
            sharedPref.edit().putInt(s(R.string.MaybeLaterCountDays), 0).apply();
            sharedPref.edit().putLong(s(R.string.MaybeLaterCountDaysStarted), new Date().getTime()).apply();
        }

        done = false;

        if (currentFragment != null) {
            switch (currentFragment) {
                case FERTMAN1:
                    ArrayList<Object> args = new ArrayList<>();
                    args.add(initialProofRows_fertman);
                    args.add(desiredProofColumns_fertman);
                    clearAll_fertman(null);
                    showFragment(FertmanFragment.class, args);
                    break;

                case FORMULA:
                    showFragment(FormulaFragment.class, new ArrayList<>());
                    break;

                case FERTMAN2:
                    showFragment(Fertman2Fragment.class, new ArrayList<>());
                    break;
            }
        } else {
            ArrayList<Object> args = new ArrayList<>();
            args.add(getHeadsPercent());
            showFragment(SettingsFragment.class, args);
        }

    }

    @Override
    public void socialChoiceDialogVK() {
       //track("SetJoinOnSocialVK");
        Fragment fragmentByTag = getSupportFragmentManager().findFragmentByTag(SettingsFragment.class.getName());

        if (fragmentByTag instanceof SettingsFragment)
            ((SettingsFragment) fragmentByTag).shareVkDialog();

    }

    @Override
    public void socialChoiceDialogOK() {
        //track("SetJoinOnSocialOK");
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://ok.ru/group/53313952940188"));
        startActivity(browserIntent);
    }

    @Override
    public void showSocialDialog(SettingsFragment settingsFragment) {
        //track("PressedJoinOnSocial");
        DialogFragment dialog = new SocialDialogFragment();
        dialog.show(getFragmentManager(), "SocialDialogFragment");
    }

    @Override
    public void showMyOrdersFragment() {
        showFragment(MyOrdersFragment.class, new ArrayList<>());
    }

    /**
     * '
     *
     * @return
     */
    public int getHeadsPercent() {
        if(!sharedPref.contains(s(R.id.headsPercent))) {
            sharedPref.edit().putInt(s(R.id.headsPercent), 8).apply();
        }
        return sharedPref.getInt(s(R.id.headsPercent), 8);
    }

    public void setHeadsPercent(int x) {
        putIntValue(R.id.headsPercent, x);
    }

    @Override
    public void switchTo(AppActivities selectedFragment) {
        switch (selectedFragment) {
            case FERTMAN1:
                ArrayList<Object> args = new ArrayList<>();
                args.add(initialProofRows_fertman);
                args.add(desiredProofColumns_fertman);
                clearAll_fertman(null);
                showFragment(FertmanFragment.class, args);
                break;

            case SHOP_ITEM:
                break;

            case FORMULA:
                showFragment(FormulaFragment.class, new ArrayList<>());
                break;

            case FERTMAN2:
                showFragment(Fertman2Fragment.class, new ArrayList<>());
                break;

            case SHOP:
                showFragment(ShopMainFragment.class, new ArrayList<>());
                break;
            case PROCESS_LIST:
                showFragment(ProcessListItemFragment.class, new ArrayList<>());
                break;
            case HEADS:
                showFragment(HeadsFragment.class, new ArrayList<>());
                break;

            case PRIVACY:
                showFragment(PrivacyPolicyFragment.class, new ArrayList<>());
                break;
            case PRIVACY_AGREEMENT:
                showFragment(PrivacyAgreementFragment.class, new ArrayList<>());
                break;
            case PRIVACY_POLICY:
                showFragment(PrivacyPolicyFragment.class, new ArrayList<>());
                break;

            case ENTER:
                showFragment(EnterShopFragment.class, new ArrayList<>());
                break;

            case SUGAR_LITRE:
                showFragment(SugarLitreFragment.class, new ArrayList<>());
                break;


            default:
                showFragment(FormulaFragment.class, new ArrayList<>());
                break;

        }

    }

    @Override
    public void onSubcategoryListFragmentInteraction(ApiSubcategory item) {
        ArrayList<Object> args = new ArrayList<>();
        args.add(item);
        showFragment(ShopMainFragment.class, args);
    }

    @Override
    public void onCategoryListFragmentInteraction(ApiCategory item) {
        ArrayList<Object> args = new ArrayList<>();
        args.add(item);
        showFragment(SubcategoryItemFragment.class, args);

    }

    @Override
    public void onProcessListFragmentInteraction(ApiProcess item) {
        ArrayList<Object> args = new ArrayList<>();
        args.add(item);
        showFragment(CategoryItemFragment.class, args);
    }

    @Override
    public void onShopItemsListFragmentInteraction(ApiItem item) {
        ArrayList<Object> args = new ArrayList<>();
        args.add(item);
        showFragment(ShopItemDetailsFragment.class, args);
    }

    @Override
    public void onMyOrderItemClick(ApiOrder order) {
        // Открыть вьюху с подробным описанием заказа.

    }

    @Override
    public void OnCartOrder(ApiOrder theCartOrder) {
        ArrayList<Object> args = new ArrayList<>();
        args.add(theCartOrder);
        showFragment(OrderFragment.class, args);
    }

    @Override
    public void OnChangeCartItem(ApiCartItem itemToChange) {

    }

    @Override
    public void onPositiveClick(final android.support.v4.app.DialogFragment dialog) {
        // Здесь мы должны взять количество из диалога
        // и установить его через апи.
        final QuantityChangeDialogFragment dialogFragment = (QuantityChangeDialogFragment) dialog;
        int quantity = dialogFragment.getCurrentQuantity();
        ApiCartItem editedItem = dialogFragment.getItem();

        if (editedItem.quantityInCart == quantity) {
            // quantity is not changed - no need to update
            return;
        }

        editedItem.quantityInCart = quantity;

        ApiBaseHandler handler = new ApiBaseHandler() {
            @Override
            public void onResponse(JSONObject arg) {
                dialogFragment.parentFragment.loadCartItemsList();
            }
        };

        ApiDataManager.changeItemQuantity(editedItem, handler);

    }

    @Override
    public void onNegativeClick(android.support.v4.app.DialogFragment dialog) {
        // nothing to update
    }

    @Override
    public void onRemoveFromOrderClick(android.support.v4.app.DialogFragment dialog) {
        final QuantityChangeDialogFragment dialogFragment = (QuantityChangeDialogFragment) dialog;
        ApiCartItem editedItem = dialogFragment.getItem();

        ApiBaseHandler handler = new ApiBaseHandler() {
            @Override
            public void onResponse(JSONObject arg) {
                dialogFragment.parentFragment.loadCartItemsList();
            }
        };

        ApiDataManager.removeItemFromCart(editedItem, handler);

        // refresh items counter
        updateCartCountBadge(-1);
    }


    /**
     * Process taps on number buttons in Formula Fragment
     */
    private class NumberTapListener implements View.OnClickListener {
        TextView displayText;

        NumberTapListener(TextView displayArg) {
            super();
            if (displayArg == null) {
                throw new IllegalArgumentException("displayArg cannot be null");
            }
            displayText = displayArg;
        }

        @Override
        public void onClick(View v) {
            if (done) {
                // we are done. Skip all clicks on number buttons
                totalReset = true;
                oldValueToEdit = "";
                return;
            }

            switch (v.getId()) {
                case R.id.button0:
                    userTappedNumber(0);
                    break;
                case R.id.button1:
                    userTappedNumber(1);
                    break;
                case R.id.button2:
                    userTappedNumber(2);
                    break;
                case R.id.button3:
                    userTappedNumber(3);
                    break;
                case R.id.button4:
                    userTappedNumber(4);
                    break;
                case R.id.button5:
                    userTappedNumber(5);
                    break;
                case R.id.button6:
                    userTappedNumber(6);
                    break;
                case R.id.button7:
                    userTappedNumber(7);
                    break;
                case R.id.button8:
                    userTappedNumber(8);
                    break;
                case R.id.button9:
                    userTappedNumber(9);
                    break;
            }
        }


        /**
         * Adds a digit to the textview (concatenates actually)
         *
         * @param number - int number arg
         */
        @SuppressLint("SetTextI18n")
        private void typeOrAdd(int number) {
            if (isTyping) {
                if (!oldValueToEdit.isEmpty()) {
                    displayText.setText(String.valueOf(number));
                    oldValueToEdit = "";//used. clearing
                } else {
                    ImageButton deleteBtn = findViewById(R.id.buttonClear);
                    deleteBtn.setImageResource(R.drawable.backspace);
                    displayText.setText(displayText.getText() + String.valueOf(number));
                }
            } else {
                if (!oldValueToEdit.isEmpty()) {
                    displayText.setText(String.valueOf(number));
                    oldValueToEdit = "";//used. clearing
                } else {

                    displayText.setText(String.valueOf(number));
                    isTyping = true;

                    //change picture to backspace
                    ImageButton deleteBtn = findViewById(R.id.buttonClear);
                    deleteBtn.setImageResource(R.drawable.backspace);
                    totalReset = false;
                }
            }
        }

        /**
         * Handling user tap on number button.
         * Clears text field in case of error
         *
         * @param number - tapped number
         */
        private void userTappedNumber(int number) {
            // this is weird exception handling.
            try {
                Integer.parseInt(displayText.getText().toString());
            } catch (NumberFormatException ex) {
                // this happens when we show error to user and he start typing further
                displayText.setText("");
            }

            // process typed number
            typeOrAdd(number);
            // validate all after adding taped number
            validateCurrentStep();
        }


        /**
         * Current step values validation
         */
        private void validateCurrentStep() {
            try {
                switch (step) {
                    case 0:
                        validateInitialProof(Double.parseDouble(displayText.getText().toString()));
                        break;
                    case 1:
                        validateTemp(Double.parseDouble(displayText.getText().toString()));
                        break;
                    case 2:
                        initialVolume = Double.parseDouble(displayText.getText().toString());
                        break;
                    case 3:
                        validateDesiredProof(Double.parseDouble(displayText.getText().toString()));
                        break;
                }
            } catch (Exception ex) {
                Toast.makeText(MainActivity.this, "Ошибка! Нажмите сброс и попробуйте еще раз", Toast.LENGTH_SHORT).show();
                clearAll();

            }
        }


        /**
         * Clears all variables
         */
        private void clearAll() {
            initialProofValue = 0;
            isTyping = false;
            step = 0;
            desiredProofValue = 0;
            true_initialProofValue = 0;
            initialVolume = 0;
            done = false;
            displayText.setText(getText(R.string.inputInitialProof));
            ImageButton deleteBtn = findViewById(R.id.buttonClear);
            deleteBtn.setImageResource(R.drawable.delete);
            totalReset = true;
            useTempCorrection = false;
            temperatureCorrection = 0;
            oldValueToEdit = "";
        }
    }

    /**
     * Processes taps on action buttons
     */
    private class ActionTapListener implements View.OnClickListener {
        TextView displayText;

        ActionTapListener(TextView displayArg) {
            super();
            displayText = displayArg;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.buttonClear:
                    if (totalReset) {
                        clearAll();
                    } else {
                        clearLastTypedNumber();
                    }
                    break;

                case R.id.buttonNext:
                    isTyping = false;
                    ImageButton deleteBtn;

                    if (totalReset) { // something is wrong) no "next" if need to reset flag is on
                        return;
                    }

                    switch (step) {
                        case 0:
                            deleteBtn = findViewById(R.id.buttonClear);
                            deleteBtn.setImageResource(R.drawable.delete);
                            displayText.setText(getText(R.string.inputTemp));
                            break;

                        case 1:
                            totalReset = true;
                            if(proofValidate(initialProofValue, displayText)) {
                                displayText.setText(getText(R.string.inputInitialVolume));
                            }
                            deleteBtn = findViewById(R.id.buttonClear);
                            deleteBtn.setImageResource(R.drawable.delete);
                            break;

                        case 2:
                            // Second screen showing
                            totalReset = true;
                            deleteBtn = findViewById(R.id.buttonClear);
                            deleteBtn.setImageResource(R.drawable.delete);
                            displayText.setText(getText(R.string.inputDesiredProof));
                            break;

                        case 3:
                            // Last screen showing.
                            // Should calculate the results
                            try {
                                //track("GotResult");
                                totalReset = true;
                                double x;
                                double n = initialProofValue;
                                double p = initialVolume / 100.00;
                                double m = desiredProofValue;
                                double t = true_initialProofValue;

                                if (!useTempCorrection) {
                                    t = initialProofValue;
                                }

                                // this is the goal of an app
                                if (t > 0) {
                                    n = t;
                                }
                                x = 100 * n * p / m - 100 * p;
                                double x_rounded = Math.round(x * 100.0) / 100.0;

                                String lastResult = getString(R.string.resultText,
                                        String.valueOf(Math.round(n)),
                                        String.valueOf(Math.round(x_rounded)),
                                        String.valueOf(Math.round(desiredProofValue)));

                                displayText.setText(lastResult);
                                totalReset = true;
                                deleteBtn = findViewById(R.id.buttonClear);
                                deleteBtn.setImageResource(R.drawable.delete);
                                done = true;
                                showRateDialogIfNeeded();
                                shareButton(); // show share after successful calc
                            } catch (Exception ex) {
                                Toast.makeText(MainActivity.this, "Ошибка! Нажмите сброс и попробуйте еще раз", Toast.LENGTH_SHORT).show();
                                clearAll();
                            }
                    }
                    step++;
                    break;
            }
        }

        /**
         * Removes last typed symbol in field
         */
        private void clearLastTypedNumber() {
            String currentText;
            if (!oldValueToEdit.isEmpty()) {
                currentText = String.valueOf(Math.round(Float.parseFloat(oldValueToEdit)));
                oldValueToEdit = "";
            } else {
                currentText = displayText.getText().toString();
            }

            // if text in displayText is one of our predefined strings ->
            // then clear button will clear all .
            if (currentText.equals(s(R.string.inputInitialProof)) ||
                    currentText.equals(s(R.string.inputDesiredProof)) ||
                    currentText.equals(s(R.string.inputInitialVolume)) ||
                    currentText.equals(s(R.string.inputTemp))
            ) {
                clearAll();
                return;
            }

            if (currentText.length() == 1) {
                // all cleared.
                //change picture to backspace
                ImageButton deleteBtn = findViewById(R.id.buttonClear);
                deleteBtn.setImageResource(R.drawable.delete);
                oldValueToEdit = "";

                switch (step) {
                    case 0:
                        displayText.setText(R.string.inputInitialProof);
                        break;
                    case 1:
                        displayText.setText(R.string.inputTemp);
                        useTempCorrection = false;
                        break;
                    case 2:
                        displayText.setText(R.string.inputInitialVolume);
                        break;
                    case 3:
                        displayText.setText(R.string.inputDesiredProof);
                        break;
                }

            } else {
                String cutString = currentText.substring(0, currentText.length() - 1);
                ImageButton deleteBtn = findViewById(R.id.buttonClear);
                deleteBtn.setImageResource(R.drawable.backspace);
                displayText.setText(cutString);
                validateCurrentStep();
            }
        }

        /**
         *
         */
        private void validateCurrentStep() {
            try {
                switch (step) {
                    case 0:
                        validateInitialProof(Double.parseDouble(displayText.getText().toString()));
                        break;
                    case 1:
                        // проверяем значение температуры
                        validateTemp(Double.parseDouble(displayText.getText().toString()));
                        // проверяем крепость с поправкой на температуру
                        ValidateResult validateInitialProofResult = Validator.validateInitialProof(true_initialProofValue);
                        switch (validateInitialProofResult) {
                            case INVALID_INITIAL_PROOF:
                                displayText.setText(getString(R.string.unrealInitialProof, String.valueOf(true_initialProofValue)));
                                initialProofValue = 0;
                                return;
                            default:
                                break;
                        }

                        break;
                    case 2:
                        initialVolume = Double.parseDouble(displayText.getText().toString());
                        break;
                    case 3:
                        validateDesiredProof(Double.parseDouble(displayText.getText().toString()));
                        break;
                }
            } catch (Exception ex) {
                Toast.makeText(MainActivity.this, "Ошибка! Нажмите сброс и попробуйте еще раз", Toast.LENGTH_SHORT).show();
                clearAll();
            }
        }

        /**
         * Clears all variables
         */
        private void clearAll() {
            initialProofValue = 0;
            isTyping = false;
            step = 0;
            desiredProofValue = 0;
            true_initialProofValue = 0;
            initialVolume = 0;
            done = false;
            displayText.setText(getText(R.string.inputInitialProof));
            ImageButton deleteBtn = findViewById(R.id.buttonClear);
            deleteBtn.setImageResource(R.drawable.delete);
            totalReset = true;
            useTempCorrection = false;
            temperatureCorrection = 0;
            oldValueToEdit = "";

            if (objAnim != null) {
                objAnim.cancel();
            }

            ImageButton nextBtn = findViewById(R.id.buttonNext);
            nextBtn.setImageResource(R.drawable.forward);
            nextBtn.setOnClickListener(this);
        }

    }

    /**
     * Opens share dialog in formula.
     */
    private void shareButton() {
        ImageButton nextBtn = findViewById(R.id.buttonNext);
        nextBtn.setImageResource(R.drawable.ic_share_black_24dp);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String shareLinkiOS = "https://apple.co/2Mu9gK1";
                String shareLinkAndroid = "http://bit.ly/2Oj4zTP";
                String shareMessageText = "Пользуюсь лучшим калькулятором самогонщика.\nЕсть и для iOS и для Android. Скачай, пригодится!\n\nДля Android: " + shareLinkAndroid + "\n\nДля iOS: " + shareLinkiOS;
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, shareMessageText);
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Рекомендую: Калькулятор Самогонщика");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                totalReset = true;
                totalReset_fertman = true;
            }
        });
        pulseAnimation(nextBtn);
    }

    /**
     * Opens share dialog in formula.
     */
    private void shareButton_fertman() {
        ImageButton nextBtn = findViewById(R.id.buttonNextFertman);
        nextBtn.setImageResource(R.drawable.ic_share_black_24dp);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String shareLinkiOS = "https://apple.co/2MnTtMp";
                String shareLinkAndroid = "http://bit.ly/2MqS1ch";
                String shareMessageText = "Пользуюсь лучшим калькулятором самогонщика.\nЕсть и для iOS и для Android. Скачай, пригодится!\n\nДля Android: " + shareLinkAndroid + "\n\nДля iOS: " + shareLinkiOS;

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, shareMessageText);
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Рекомендую: Калькулятор Самогонщика");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                totalReset = true;
                totalReset_fertman = true;
            }
        });
        pulseAnimation(nextBtn);

    }


    private void pulseAnimation(ImageButton btnObj) {
        objAnim = ObjectAnimator.ofPropertyValuesHolder(btnObj, PropertyValuesHolder.ofFloat("scaleX", 1.2f), PropertyValuesHolder.ofFloat("scaleY", 1.2f));
        objAnim.setDuration(400);
        objAnim.setRepeatCount(ObjectAnimator.INFINITE);
        objAnim.setRepeatMode(ObjectAnimator.REVERSE);
        objAnim.setStartDelay(1000);
        objAnim.start();
    }


    private void showRateDialogIfNeeded() {
        // Store successful count
        if (sharedPref.contains(s(R.string.TotalSuccessfulCalculationsCount))) {
            int launchCount = getSavedIntValue(R.string.TotalSuccessfulCalculationsCount) + 1;
            putIntValue(R.string.TotalSuccessfulCalculationsCount, launchCount);
        } else {
            putIntValue(R.string.TotalSuccessfulCalculationsCount, 1);
        }

        Date from = prevRunDate();
        Date to = lastRunDate();

        if (!sharedPref.contains(s(R.string.DecisionMade)) ||
                !sharedPref.getBoolean(s(R.string.DecisionMade), false)) {

            // Check if launch dates exist
            if (from != null && to != null) {

                if (sharedPref.getInt(s(R.string.TotalSuccessfulCalculationsCount), 0) > 10) {
                    //track("SuccessfulResultsMoreThan10");

                    int hours24 = getSavedIntValue(R.string._24hrsLaunchCount);
                    int days2 = getSavedIntValue(R.string._2daysInaRowCount);

                    if (hours24 % 2 == 0 || days2 % 2 == 0) {
                        int daysGone = getSavedIntValue(R.string.MaybeLaterCountDays);

                        if (daysGone == -1) { // we have NO days counter
                            if (getSavedIntValue(R.string.MaybeLaterCount) <= 2) {
                                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        replaceContentWithRateView();
                                    }
                                }, 3000);
                            }
                        } else {
                            if (daysGone >= 10) {
                                // reset all counters
                                putIntValue(R.string._2daysInaRowCount, 0);
                                putIntValue(R.string._24hrsLaunchCount, 0);
                                putIntValue(R.string.TotalSuccessfulCalculationsCount, 0);
                                putIntValue(R.string.MaybeLaterCount, 0);
                                putIntValue(R.string.MaybeLaterCountDays, -1);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * @param strResId
     * @return
     */
    public int getSavedIntValue(int strResId) {
        try {
            if (sharedPref.contains(s(strResId))) {
                return sharedPref.getInt(s(strResId), 0);
            } else {
                return -1;
            }
        } catch (Exception ex) {
            Log.e("API", "cant read int from shared prefs");
            return -1;
        }
    }


    /**
     * @param strResId
     * @return
     */
    public void putIntValue(int strResId, int value) {
        sharedPref.edit().putInt(s(strResId), value).apply();
    }


    /**
     * @param eventName
     */
    public void track(String eventName) {
    }
}
