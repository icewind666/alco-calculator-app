package simo.com.alco.fragments;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

/**
 * Base class for our fragments.
 *
 * Created by icewind on 28.09.2017.
 */

public abstract class FragmentWithInit extends Fragment implements View.OnClickListener {
    /**
     * Initiates fragments with arguments if needed.
     * Should override in subclasses
     *
     * @param args
     */
    public void initFragment(Object... args) {

    }
}
