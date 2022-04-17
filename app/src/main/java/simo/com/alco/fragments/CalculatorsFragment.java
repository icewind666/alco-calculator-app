package simo.com.alco.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import simo.com.alco.AppActivities;
import simo.com.alco.MainActivity;
import simo.com.alco.R;
import simo.com.alco.fragments.events.OnCalculatorSelectListener;

/**
 * Calculators view fragment.
 * Shows all available calculation variants.
 *
 * Transfers all events to parent MainActivity to handle
 * Created by icewind on 27.09.2017.
 */
public class CalculatorsFragment extends FragmentWithInit {
    private OnCalculatorSelectListener mListener;

    /**
     * Need this constructor to conform with android fragment api
     * to create empty fragment
     */
    public CalculatorsFragment() {
    }

    /**
     * Factory method.
     * Useful when we will have arguments for creating fragment
     * @return fragment instance
     */
    public void initFragment(Object... args) {
        // pass
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // inflate content block with layout of current fragment
        View v = inflater.inflate(R.layout.calculators_fragment, container, false);
        Button btnFormula = v.findViewById(R.id.calc_formula_btn);
        btnFormula.setOnClickListener(this);

        Button btnFertman = v.findViewById(R.id.calc_fertman_btn);
        btnFertman.setOnClickListener(this);

        Button btnFertman1 = v.findViewById(R.id.calc_fertman1_btn);
        btnFertman1.setOnClickListener(this);

        Button btnHeads = v.findViewById(R.id.calc_heads_btn);
        btnHeads.setOnClickListener(this);

        Button btnSugarLitre = v.findViewById(R.id.calc_sugar_litre_btn);
        btnSugarLitre.setOnClickListener(this);

        ((MainActivity) getActivity()).updateCartCountBadge(0);
        ((MainActivity) getActivity()).updateCartCountBadge(-1);
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnCalculatorSelectListener) {
            mListener = (OnCalculatorSelectListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof OnCalculatorSelectListener) {
            mListener = (OnCalculatorSelectListener) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        if (mListener != null) {
            switch (v.getId()) {
                case R.id.calc_formula_btn:
                    mListener.switchTo(AppActivities.FORMULA);
                    break;
                case R.id.calc_fertman_btn:
                    mListener.switchTo(AppActivities.FERTMAN1);
                    break;
                case R.id.calc_fertman1_btn:
                    mListener.switchTo(AppActivities.FERTMAN2);
                    break;
                case R.id.calc_heads_btn:
                    mListener.switchTo(AppActivities.HEADS);
                    break;
                case R.id.calc_sugar_litre_btn:
                    mListener.switchTo(AppActivities.SUGAR_LITRE);
                    break;
            }
        }

    }


}


