package simo.com.alco.fragments.events;


import android.view.View;

import simo.com.alco.fragments.FertmanFragment;

/**
 * This interface must be implemented by activities that contain this
 * fragment to allow an interaction in this fragment to be communicated
 * to the activity and potentially other fragments contained in that
 * activity.
 * <p/>
 * See the Android Training lesson <a href=
 * "http://developer.android.com/training/basics/fragments/communicating.html"
 * >Communicating with Other Fragments</a> for more information.
 */
public interface OnFragmentInteractionListener {
    void onFragmentFormulaClick(View sourceView);
    void onFragmentFertmanClick(View sourceView, FertmanFragment sourceFragment);
}
