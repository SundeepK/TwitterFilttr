package com.sun.tweetfiltrr.customviews.webview.impl;

import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by Sundeep on 19/02/14.
 */
public class TwitterAuthWebViewClient extends WebViewClient {


    private static final String TAG = TwitterAuthWebViewClient.class.getName();
    private String _callback;
    private OnOAuthStarted _pageLoadListener;
    private OnPageLoadCallBack _onPageListener;
    public TwitterAuthWebViewClient(String callback_, OnOAuthStarted pageLoadListener_){
        _callback = callback_;
        _pageLoadListener = pageLoadListener_;
    }


    public interface OnOAuthStarted {
        public void onPageReceivedVerifier(WebView view_, String url_, String verifier_);
        public void onPageReceivedError(WebView view_, int errorCode_, String description_, String failingUrl_);
    }

    public interface OnPageLoadCallBack {
        public void onPageStarted(WebView view, String url, Bitmap favicon);
        public void onPageFinished(WebView view, String url);
    }

    public void setOnPageListener(OnPageLoadCallBack pageListener){
        _onPageListener = pageListener;
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        handler.proceed();
        super.onReceivedSslError(view, handler, error);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        if(_onPageListener != null){
            _onPageListener.onPageStarted(view, url, favicon);
        }
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        Log.v(TAG, "failed error " + failingUrl);
        _pageLoadListener.onPageReceivedError(view, errorCode, description, failingUrl);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        if(_onPageListener != null){
            _onPageListener.onPageFinished(view, url);
        }
        super.onPageFinished(view, url);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if(url.startsWith(_callback)){
            final Uri uri = Uri.parse(url);
            String verifier = uri.getQueryParameter("oauth_verifier");
            Log.v(TAG, "verifier recieved is " + verifier);

            _pageLoadListener.onPageReceivedVerifier(view, url, verifier);
        }else{
            view.loadUrl(url);
        }
        return true;
    }

    @Override
    public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
        return super.shouldOverrideKeyEvent(view, event);
    }

}
