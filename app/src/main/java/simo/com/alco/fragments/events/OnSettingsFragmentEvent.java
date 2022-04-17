package simo.com.alco.fragments.events;

import simo.com.alco.fragments.SettingsFragment;

/**
 * Interface for calling main activity methods from
 * settings fragment.
 *
 * Created by icewind on 29.09.2017.
 */

public interface OnSettingsFragmentEvent {

    void sendPartnershipRequestEmailDialog(SettingsFragment fragment);
    /**
     * Send feedback dialog
     * @param fragment
     */
    void sendEmailDialog(SettingsFragment fragment);

    /**
     * Shows rate_fragment application dialog
     * @param fragment
     */
    void rateApp(SettingsFragment fragment);
    void rate_noThanks();
    void rate_ok();
    void rate_later();
    void socialChoiceDialogVK();
    void socialChoiceDialogOK();
    void showSocialDialog(SettingsFragment settingsFragment);
    void showMyOrdersFragment();
}
