package com.sun.tweetfiltrr.customviews.webview;

import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.view.KeyEvent;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by Sundeep on 19/02/14.
 */
public class TwitterAuthWebViewClient extends WebViewClient {

    private String _callback;
    private OnValidVerifer _veriferCallback;
    public TwitterAuthWebViewClient(String callback_, OnValidVerifer veriferCallback_){
        _callback = callback_;
        _veriferCallback = veriferCallback_;
    }

    public interface OnValidVerifer{
        public void onValidVerifer(String verifier_);
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        handler.proceed();
        super.onReceivedSslError(view, handler, error);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        boolean shouldProceed;
        if(url.startsWith(_callback)){
            shouldProceed = true;
            final Uri uri = Uri.parse(url);
            final String verifier = uri.getQueryParameter("oauth_verifier");
            _veriferCallback.onValidVerifer(verifier);
        }else{
            shouldProceed = false;
        }
        return shouldProceed;
    }

    @Override
    public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
        return super.shouldOverrideKeyEvent(view, event);
    }
}
