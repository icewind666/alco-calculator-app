package simo.com.alco.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kotlin.NotImplementedError;
import simo.com.alco.IcewindUtils;
import simo.com.alco.MainActivity;
import simo.com.alco.R;
import simo.com.alco.api.ApiBaseHandler;
import simo.com.alco.api.ApiCallback;
import simo.com.alco.api.ApiDataManager;
import simo.com.alco.api.ApiUser;
import simo.com.alco.fragments.events.OnListFragmentItemSelection;

import static com.vk.sdk.VKUIHelper.getApplicationContext;

/**
 * This fragment is shown when we detect that user is not logged in
 * or registered.
 *
 * After successfully login redirects to fragment depending on MainActivity.currentFragment
 * value.
 *
 */
public class LoginFragment extends FragmentWithInit {
    private View mView;
    private ProgressBar mProgressBar;

    public LoginFragment() {
    }

    @Override
    public void initFragment(Object... args) {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_login, container, false);
        mProgressBar = mView.findViewById(R.id.progress_bar);
        Button mLoginBtn = mView.findViewById(R.id.loginBtn);
        mLoginBtn.setEnabled(true);
        EditText login = mView.findViewById(R.id.loginEditField);
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        imm.showSoftInput(login, InputMethodManager.SHOW_IMPLICIT);
        login.requestFocus();
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText loginField = mView.findViewById(R.id.loginEditField);
                final EditText passwordField = mView.findViewById(R.id.passwordEditField);

                String userlogin = loginField.getText().toString();
                final String password = passwordField.getText().toString();

                if(userlogin.isEmpty()) {
                    showToast("Пожалуйства, введите адрес почты!");
                    return;
                }
                if(!IcewindUtils.isValidEmail(userlogin)) {
                    showToast("Некорректный формат адреса почты!");
                    return;
                }
                if(password.isEmpty()) {
                    showToast("Пожалуйства, введите пароль!");
                    return;
                }


                loginField.setEnabled(false);
                passwordField.setEnabled(false);

                ApiCallback callback = getApiCallback(loginField, passwordField);

                ApiDataManager.loginUser(new ApiUser(userlogin, password), callback);
                mProgressBar.setVisibility(View.VISIBLE);
            }
        });

        return mView;
    }

    @NonNull
    private ApiCallback getApiCallback(final EditText loginField, final EditText passwordField) {
        return new ApiCallback() {
            @Override
            public void onEvent(Object data) {
                boolean result = (boolean) data;
                mProgressBar.setVisibility(View.INVISIBLE);

                if (result) {
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
                                ((MainActivity) getActivity()).showFragment(ProcessListItemFragment.class,
                                        new ArrayList<>());
                            } catch (JSONException e) {
                                ((MainActivity) getActivity()).showFragment(ProcessListItemFragment.class,
                                        new ArrayList<>());
                                e.printStackTrace();
                            }

                        }
                    });



                } else {
                    //login failed
                    showToast(getString(R.string.login_failed_err_txt));
                    loginField.setEnabled(true);
                    passwordField.setEnabled(true);
                }
            }
        };
    }

    private void showToast(String txt) {
        Toast toast = Toast.makeText(getApplicationContext(),
                txt,
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    @Override
    public void onClick(View view) {
        throw new UnsupportedOperationException();
    }
}
