package com.sun.tweetfiltrr.customviews.webview;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.utils.TwitterUtil;

import java.util.concurrent.atomic.AtomicBoolean;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

/**
 * Created by Sundeep on 19/02/14.
 */
public class TwitterAuthView extends WebView {
    private static final String TAG = AsyncTwitterAuthTask.class.getName();

    final AtomicBoolean called = new AtomicBoolean(true);
    String _verifier;
    OnSuccessfulTwitterOAuth lis;

    public interface OnSuccessfulTwitterOAuth{
        public void onSuccessTwitterOAuth(AccessToken acccessToken);
    }


    public void setSuccessLis(OnSuccessfulTwitterOAuth lis_){
        lis = lis_;
    }

    public TwitterAuthView(Context context) {
        super(context);
        init();

    }

    public TwitterAuthView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    public TwitterAuthView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init()
        {
            WebSettings settings = getSettings();
            settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
            settings.setJavaScriptEnabled(true);
            settings.setBuiltInZoomControls(true);
            setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);
            requestFocus(View.FOCUS_DOWN);
        }




    public void startTwitterAuthentication(AuthenticationDetails authDetails_){
        if(isEmpty(authDetails_.getConsumerKey()) || isEmpty(authDetails_.getConsumerSecrect()) || isEmpty(authDetails_.getCallback())){
            throw new IllegalArgumentException("AuthenticationDetails must contain valid consumer key, secrect and callback");
        }

        setWebViewClient(new TwitterAuthWebViewClientM(authDetails_.getCallback()));
        new AsyncTwitterAuthTask().execute(authDetails_);


    }

    private boolean isEmpty(String value_){
        return TextUtils.isEmpty(value_);
    }

    private class AsyncTwitterAuthTask extends AsyncTask<AuthenticationDetails, Void, AccessToken>{
        RequestToken _requestToken;
        @Override
        protected AccessToken doInBackground(AuthenticationDetails... params) {
            if(params.length > 1){
                throw new IllegalArgumentException("Can only process one twitter authentication at a time");
            }
            final AuthenticationDetails auth = params[0];
            final Twitter twitter = auth.getTwitter();
            try {
                _requestToken = twitter.getOAuthRequestToken();
            } catch (TwitterException e) {
                e.printStackTrace();
            }
            publishProgress();
            while(called.get())
            {
            }
            Log.v(TAG, "passed publish " );

            AccessToken access =  getAccessToken(twitter,_requestToken);
            try {
                ParcelableUser  parcelableUser = new ParcelableUser(twitter.showUser(access
                         .getUserId()));
                Log.v(TAG, "twitter user after oauth " + parcelableUser);
                TwitterUtil.getInstance().setCurrentUser(parcelableUser);
            } catch (TwitterException e) {
                e.printStackTrace();
            }

            return access;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            //super.onProgressUpdate(values);
            String uri = _requestToken.getAuthenticationURL();
            TwitterAuthView.this.loadUrl(uri);


        }

        @Override
        protected void onCancelled(AccessToken accessToken) {
            super.onCancelled(accessToken);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(AccessToken accessToken) {
            super.onPostExecute(accessToken);
            lis.onSuccessTwitterOAuth(accessToken);

        }

        private AccessToken getAccessToken(Twitter twitter, RequestToken requestToken)
        {
            try
            {
                // Get an access token. This triggers network access.
                AccessToken token = twitter.getOAuthAccessToken(requestToken, _verifier);

                return token;
            }
            catch (TwitterException e)
            {
                // Failed to get an access token.
                e.printStackTrace();
                Log.e(TAG, "Failed to get an access token.", e);

                // No access token.
                return null;
            }
        }
    }

    public class TwitterAuthWebViewClientM extends WebViewClient {

        private String _callback;
        public TwitterAuthWebViewClientM(String callback_){
            _callback = callback_;
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
            called.getAndSet(false);
            Log.v(TAG, "failed error " + failingUrl );
            TwitterAuthView.this.setVisibility(GONE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if(url.startsWith(_callback)){
                final Uri uri = Uri.parse(url);
                TwitterAuthView.this._verifier = uri.getQueryParameter("oauth_verifier");
                Log.v(TAG, "verifier recieved is " + _verifier );
                called.getAndSet(false);
                TwitterAuthView.this.setVisibility(GONE);
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

}
