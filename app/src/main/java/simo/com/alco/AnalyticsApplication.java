package simo.com.alco;

import android.app.Application;
//import android.support.multidex.MultiDexApplication;
//import com.google.android.gms.analytics.GoogleAnalytics;
//import com.google.android.gms.analytics.Tracker;
import com.vk.sdk.VKSdk;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import simo.com.alco.api.ApiKnownUrls;


/**
 * Subclassing {@link Application}
 * for wrap our application with google analytics
 */
public class AnalyticsApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        VKSdk.initialize(this);
    }
}
