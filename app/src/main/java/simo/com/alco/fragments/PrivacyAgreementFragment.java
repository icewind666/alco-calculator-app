package simo.com.alco.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import simo.com.alco.R;


public class PrivacyAgreementFragment extends FragmentWithInit {
    WebView mWebView;
    View mView;

    public PrivacyAgreementFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_privacy, container, false);
        mWebView  = mView.findViewById(R.id.webView);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(false);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);

        final ProgressBar pgBar = mView.findViewById(R.id.progress_bar);
        pgBar.setVisibility(View.VISIBLE);

        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                pgBar.setVisibility(View.INVISIBLE);
            }
        });

        mWebView .loadUrl("https://samogon-calculator.ru/agreement");

        return mView;
    }

    @Override
    public void onClick(View view) {

    }
}
