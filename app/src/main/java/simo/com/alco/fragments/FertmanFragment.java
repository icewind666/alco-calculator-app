package simo.com.alco.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import simo.com.alco.R;
import simo.com.alco.fragments.events.OnFragmentInteractionListener;

/**
 * Fertman view fragment.
 * Transfers all events to parent MainActivity to handle
 * Created by icewind on 27.09.2017.
 */
public class FertmanFragment extends FragmentWithInit {
    private OnFragmentInteractionListener mListener;

    int[] initialProofRows;
    int[] desiredProofColumns;

    List<Button> buttons = new ArrayList<>();

    /**
     * Need this constructor to conform with android fragment api
     * to create empty fragment
     */
    public FertmanFragment() {

    }

    public void initFragment(Object... args) {
        ArrayList<int[]> argList = (ArrayList<int[]>) args[0];

        int[] initialRows = argList.get(0);
        int[] desiredColumns = argList.get(1);

        initialProofRows = initialRows;
        desiredProofColumns = desiredColumns;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fertman_fragment, container, false);
        // init fragment buttons
        initButtonsInFragment(v);
        return v;
    }

    /**
     * Sets event listener to all buttons in view
     */
    private void initButtonsInFragment(View v) {
        buttons.clear();
        buttons.add((Button) v.findViewById(R.id.fbutton_a));
        buttons.add((Button) v.findViewById(R.id.fbutton_b));
        buttons.add((Button) v.findViewById(R.id.fbutton_c));
        buttons.add((Button) v.findViewById(R.id.fbutton_d));
        buttons.add((Button) v.findViewById(R.id.fbutton_e));
        buttons.add((Button) v.findViewById(R.id.fbutton_f));
        buttons.add((Button) v.findViewById(R.id.fbutton_g));
        buttons.add((Button) v.findViewById(R.id.fbutton_h));
        buttons.add((Button) v.findViewById(R.id.fbutton_i));
        buttons.add((Button) v.findViewById(R.id.fbutton_j));
        buttons.add((Button) v.findViewById(R.id.fbutton_k));
        buttons.add((Button) v.findViewById(R.id.fbutton_l));
        buttons.add((Button) v.findViewById(R.id.fbutton_m));

        for (Button btn: buttons) {
            btn.setOnClickListener(this);
        }

        v.findViewById(R.id.buttonClearFertman).setOnClickListener(this);
        v.findViewById(R.id.buttonNextFertman).setOnClickListener(this);

        setInitialProofButtonTitles();
    }


    public void onButtonPressed(View sourceView) {
        if (mListener != null) {
            mListener.onFragmentFertmanClick(sourceView, this);
        }
    }

    @Override
    public void onAttach(Context context) {
        // this works on api > v4
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
        // this works in api v4
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

    /**
     * Set titles for buttons
     */
    public void setInitialProofButtonTitles() {
        int j = 12;
        for (Button i: buttons) {
            i.setText(String.valueOf(initialProofRows[j]));
            j--;
        }
    }

    /**
     * Set titles for buttons
     */
    public void setDesiredProofButtonTitles() {
        int j = 12;
        for (Button i: buttons) {
            i.setText(String.valueOf(desiredProofColumns[j]));
            j--;
        }
    }
}
