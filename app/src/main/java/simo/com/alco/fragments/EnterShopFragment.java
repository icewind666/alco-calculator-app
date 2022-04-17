package simo.com.alco.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

import simo.com.alco.MainActivity;
import simo.com.alco.R;
import simo.com.alco.fragments.events.OnListFragmentItemSelection;

/**
 * This fragment is shown when we detect that user is not logged in
 * or registered.
 */
public class EnterShopFragment extends FragmentWithInit {


    public EnterShopFragment() {
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
        View view = inflater.inflate(R.layout.fragment_entershop, container, false);
        Button mToLoginBtn = view.findViewById(R.id.to_login_btn);
        Button mRegBtn = view.findViewById(R.id.registrationBtn);

        mToLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Object> args = new ArrayList<>();
                ((MainActivity)getActivity()).showFragment(LoginFragment.class, args);

            }
        });

        mRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Object> args = new ArrayList<>();
                ((MainActivity)getActivity()).showFragment(RegistrationFragment.class, args);

            }
        });
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentItemSelection) {
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnProcessListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View v) {

    }
}
