package simo.com.alco.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.methods.VKApiGroups;

import org.w3c.dom.Text;

import java.util.ArrayList;

import simo.com.alco.AppActivities;
import simo.com.alco.MainActivity;
import simo.com.alco.R;
import simo.com.alco.api.AlcoAppApi;
import simo.com.alco.api.ApiUser;
import simo.com.alco.fragments.events.OnSettingsFragmentEvent;

/**
 * Settings Fragment
 * Created by icewind on 29.09.2017.
 */

public class SettingsFragment extends FragmentWithInit implements View.OnClickListener {
    public OnSettingsFragmentEvent mSettingsListener;
    private int currentHeadsPercent = 8; // default
    private ApiUser currentUser;

    /**
     * Scope is set of required permissions for application
     *
     * @see <a href="https://vk.com/dev/permissions">vk.com api permissions documentation</a>
     */
    private static final String[] sMyScope = new String[]{
            VKScope.GROUPS
    };

    public void initFragment(Object... args) {


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.settings_fragment, container, false);
        this.currentHeadsPercent = ((MainActivity) getActivity()).getHeadsPercent();
        Button btnMailChoice = v.findViewById(R.id.sendEmail_settings);
        btnMailChoice.setOnClickListener(this);

        Button btnRateApp = v.findViewById(R.id.rateApp_settings);
        btnRateApp.setOnClickListener(this);

        Button btnSocial = v.findViewById(R.id.socialDialog_settings);
        btnSocial.setOnClickListener(this);

        Button btnPartner = v.findViewById(R.id.partnerBtn);
        btnPartner.setOnClickListener(this);

        Button privacyAgrBtn = v.findViewById(R.id.privacyAgreement_settings);
        Button privacyPolicyBtn = v.findViewById(R.id.privacyPolicy_settings);

        privacyAgrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).switchTo(AppActivities.PRIVACY_AGREEMENT);
            }
        });
        privacyPolicyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).switchTo(AppActivities.PRIVACY_POLICY);
            }
        });

        final Button logoutBtn = v.findViewById(R.id.auth_settings);

        Button btnMyOrders = v.findViewById(R.id.myorders_settings);
        btnMyOrders.setOnClickListener(this);
        final TextView usernameView = v.findViewById(R.id.txtAuth);

        if(AlcoAppApi.user == null) {
            // make log out btn login btn
            logoutBtn.setText("ВОЙТИ");
            logoutBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MainActivity) getActivity()).switchTo(AppActivities.ENTER);
                }
            });
        }
        else {
            logoutBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlcoAppApi.AUTH_TICKET = "";
                    ApiUser.TOKEN = "";
                    AlcoAppApi.user = new ApiUser("", "", false, -1, false);
                    SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor ed = sharedPref.edit();
                    ed.remove(getString(R.string.usertoken));
                    ed.remove("username");
                    ed.remove("is_admin");
                    ed.apply();

                    ((MainActivity)getActivity()).updateCartCountBadge(0);

                    Toast.makeText(getContext(), "Вы успешно вышли из аккаунта", Toast.LENGTH_SHORT).show();
                    logoutBtn.setText("ВОЙТИ");
                    usernameView.setText(String.format("ВЫ НЕ АВТОРИЗОВАНЫ"));

                    logoutBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ((MainActivity) getActivity()).switchTo(AppActivities.ENTER);
                        }
                    });


                }
            });
        }

        ((TextView)v.findViewById(R.id.headsPercentTxt)).setText(String.format("%1$s%%",
                currentHeadsPercent));

        SeekBar seekBar = v.findViewById(R.id.headsPercent);
        seekBar.setIndeterminate(false);
        seekBar.setMax(20);
        seekBar.setProgress(currentHeadsPercent-5);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    //seekBar.setProgress(i);
                int casted = i + 5;
                ((MainActivity)getActivity()).setHeadsPercent(casted);

                ((TextView)v.findViewById(R.id.headsPercentTxt)).setText(String.format("%1$s%%",
                        casted));
                currentHeadsPercent = casted;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        if (AlcoAppApi.user != null) {
            usernameView.setText(String.format("АВТОРИЗАЦИЯ: %s", AlcoAppApi.user.username));
            logoutBtn.setText("ВЫЙТИ");
        }


        return v;
    }

    @Override
    public void onClick(View v) {
        if (mSettingsListener != null) {
            switch (v.getId()) {
                case R.id.rateApp_settings:
                    // show rate_fragment app dialog
                    mSettingsListener.rateApp(this);
                    break;

                case R.id.sendEmail_settings:
                    // send email dialog
                    mSettingsListener.sendEmailDialog(this);
                    break;

                case R.id.socialDialog_settings:
                    // show social dialog
                    mSettingsListener.showSocialDialog(this);
                    break;
                case R.id.myorders_settings:
                    mSettingsListener.showMyOrdersFragment();
                    break;

                case R.id.partnerBtn:
                    // show social dialog
                    mSettingsListener.sendPartnershipRequestEmailDialog(this);
            }
        }

    }

    /**
     * Calls VK SDK activity to join the group
     */
    public void shareVkDialog() {
        VKSdk.login(getActivity(), sMyScope);
        VKRequest vkRequest = new VKApiGroups().join(VKParameters.from("group_id", "139572213"));
        vkRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                Toast.makeText(getActivity().getApplicationContext(), R.string.vkSuccess, Toast.LENGTH_SHORT)
                            .show();

            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnSettingsFragmentEvent) {
            mSettingsListener = (OnSettingsFragmentEvent) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof OnSettingsFragmentEvent) {
            mSettingsListener = (OnSettingsFragmentEvent) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mSettingsListener = null;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                // success
            }
            @Override
            public void onError(VKError error) {
                // error occured
            }

        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
