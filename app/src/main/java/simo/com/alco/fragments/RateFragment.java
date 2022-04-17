package simo.com.alco.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import simo.com.alco.R;
import simo.com.alco.fragments.events.OnSettingsFragmentEvent;

/**
 * Rate view fragment.
 * Shows rate_fragment buttons in app interface.
 *
 * Created by icewind on 30.09.2017.
 */

public class RateFragment extends FragmentWithInit implements View.OnClickListener {
    public OnSettingsFragmentEvent mSettingsListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.rate_fragment, container, false);
        Button btn1 = (Button) v.findViewById(R.id.btnNoThanks);
        btn1.setOnClickListener(this);
        Button btn2 = (Button) v.findViewById(R.id.btnLater);
        btn2.setOnClickListener(this);
        Button btn3 = (Button) v.findViewById(R.id.btnRate);
        btn3.setOnClickListener(this);
        return v;
    }


    @Override
    public void onClick(View v) {
        if (mSettingsListener != null) {
            switch (v.getId()) {
                case R.id.btnNoThanks:
                    // user clicked no thanks
                    mSettingsListener.rate_noThanks();
                    break;
                case R.id.btnRate:
                    // User clicked  rate_fragment
                    mSettingsListener.rate_ok();
                    break;

                case R.id.btnLater:
                    // user clicked rate_fragment later
                    mSettingsListener.rate_later();
                    break;
            }
        }

        Button btn1 = (Button) v;
        btn1.setOnClickListener(null);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnSettingsFragmentEvent) {
            mSettingsListener = (OnSettingsFragmentEvent) context;
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
    
}
