package simo.com.alco.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import simo.com.alco.R;
import simo.com.alco.fragments.events.OnFragmentInteractionListener;

/**
 * Formula view fragment.
 * Used as default view on start (when there're no user settings stored yet)
 * Transfers all events to parent MainActivity to handle
 * Created by icewind on 27.09.2017.
 */
public class FormulaFragment extends FragmentWithInit {
    private OnFragmentInteractionListener mListener;

    /**
     * Need this constructor to conform with android fragment api
     * to create empty fragment
     */
    public FormulaFragment() {
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
        View v = inflater.inflate(R.layout.formula_fragment, container, false);

        int number_ids[] = {
                R.id.button0,
                R.id.button1,
                R.id.button2,
                R.id.button3,
                R.id.button4,
                R.id.button5,
                R.id.button6,
                R.id.button7,
                R.id.button8,
                R.id.button9
        };

        // set event listeners to number buttons
        for (int id: number_ids) {
            View btnView = v.findViewById(id);
            btnView.setOnClickListener(this);
        }

        int action_ids[] = { R.id.buttonClear, R.id.buttonNext };
        for (int id: action_ids) {
            View btnView = v.findViewById(id);
            btnView.setOnClickListener(this);
        }

        return v;
    }

    /**
     * Delegate button pressed event to delegate in main activity
     */
    public void onButtonPressed(View sourceView) {
        if (mListener != null) {
            mListener.onFragmentFormulaClick(sourceView);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) activity;
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
        // we got click on button event -> transfer it to main activity to handle
        onButtonPressed(v);
    }
}


