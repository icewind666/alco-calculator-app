package simo.com.alco.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedList;

import simo.com.alco.AppActivities;
import simo.com.alco.IcewindUtils;
import simo.com.alco.MainActivity;
import simo.com.alco.R;
import simo.com.alco.api.ApiCallback;
import simo.com.alco.api.ApiDataManager;
import simo.com.alco.api.ApiUser;
import simo.com.alco.fragments.events.OnListFragmentItemSelection;

/**
 * This fragment is shown when we detect that user is not logged in
 * or registered.
 */
public class RegistrationFragment extends FragmentWithInit {

    private View mView;
    private Button mRegBtn;
    private Button mCancelBtn;
    private ProgressBar mProgressBar;
    private boolean privacyAccepted = false;


    public RegistrationFragment() {
    }

    @Override
    public void initFragment(Object... args) {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registration, container, false);
        mView = view;
        mProgressBar = view.findViewById(R.id.progress_bar);
        mRegBtn = view.findViewById(R.id.registrationBtn);
        mCancelBtn = view.findViewById(R.id.cancelBtn);

        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Object> args = new ArrayList<>();
                MainActivity activity = ((MainActivity) getActivity());
                activity.showFragment(EnterShopFragment.class, args);
            }
        });

        mRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!privacyAccepted) {
                    return;
                }

                final EditText loginField = mView.findViewById(R.id.regEmail);
                loginField.setEnabled(false);
                final EditText passwordField = mView.findViewById(R.id.regPassword);
                passwordField.setEnabled(false);
                final Switch optIn = mView.findViewById(R.id.option_in);

                if(loginField.getText().length() == 0) {
                    Toast.makeText(getContext(), "Не указана почта! Пожалуйста, введите адрес действующей почты!", Toast.LENGTH_SHORT).show();
                    mProgressBar.setVisibility(View.INVISIBLE);
                    loginField.setEnabled(true);
                    passwordField.setEnabled(true);
                    return;
                }
                if(passwordField.getText().length() == 0) {
                    Toast.makeText(getContext(), "Не заполнен пароль! Пожалуйста, введите желаемый пароль!", Toast.LENGTH_SHORT).show();
                    mProgressBar.setVisibility(View.INVISIBLE);
                    loginField.setEnabled(true);
                    passwordField.setEnabled(true);
                    return;
                }
                if(!IcewindUtils.isValidEmail(loginField.getText())) {
                    Toast.makeText(getContext(), "Некорректный формат email!", Toast.LENGTH_SHORT).show();
                    mProgressBar.setVisibility(View.INVISIBLE);
                    loginField.setEnabled(true);
                    passwordField.setEnabled(true);
                    return;
                }
                if(passwordField.getText().length() < 6) {
                    Toast.makeText(getContext(), "Некорректный формат пароля! Пароль должен состоять минимум из 6 символов.", Toast.LENGTH_SHORT).show();
                    mProgressBar.setVisibility(View.INVISIBLE);
                    loginField.setEnabled(true);
                    passwordField.setEnabled(true);
                    return;
                }

                //!IcewindUtils.isValidEmail(loginField.getText())

                String userlogin = loginField.getText().toString();
                final String password = passwordField.getText().toString();

                ApiCallback callback = new ApiCallback() {
                    @Override
                    public void onEvent(Object data) {
                        boolean result = (boolean) data;
                        mProgressBar.setVisibility(View.INVISIBLE);

                        if(result) {
                            MainActivity activity = ((MainActivity) getActivity());
                            activity.switchTo(AppActivities.PROCESS_LIST);
                        }
                        else {
                            Log.d("API", "Registration FAILED");
                            Toast.makeText(getContext(), "Ошибка регистрации", Toast.LENGTH_SHORT).show();
                            loginField.setEnabled(true);
                            passwordField.setEnabled(true);
                        }
                    }
                };
                mProgressBar.setVisibility(View.VISIBLE);
                ApiDataManager.registerUser(new ApiUser(userlogin, password, optIn.isChecked()), callback);

            }
        });


        if(!privacyAccepted) {
            // show privacy fragment
            AcceptPrivacyDialog pdialog = new AcceptPrivacyDialog();

            pdialog.listener = new AcceptPrivacyDialog.AcceptPrivacyDialogListener() {
                @Override
                public void onPositive(DialogFragment dialog) {
                    privacyAccepted = true;
                }

                @Override
                public void onNegative(DialogFragment dialog) {
                    privacyAccepted = false;
                    ((MainActivity)getActivity()).switchTo(AppActivities.ENTER);
                }
            };
            pdialog.show(getActivity().getSupportFragmentManager(), "PrivacyDialog");
        }
        return view;
    }

    @Override
    public void onClick(View v) {

    }
}
