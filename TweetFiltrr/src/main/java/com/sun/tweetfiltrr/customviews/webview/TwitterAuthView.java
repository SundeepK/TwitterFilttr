package com.sun.tweetfiltrr.customviews.webview;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.sun.tweetfiltrr.activity.activities.ITwitterAuthCallback;
import com.sun.tweetfiltrr.twitter.twitterretrievers.api.ITwitterAccessTokenRetriever;
import com.sun.tweetfiltrr.twitter.twitterretrievers.api.TwitterAccessTokenRetriever;
import com.sun.tweetfiltrr.twitter.twitterretrievers.api.UserBundle;

import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.RequestToken;

/**
 * Created by Sundeep on 19/02/14.
 */
public class TwitterAuthView extends WebView implements TwitterAuthWebViewClient.OnOAuthStarted, TwitterAuthWebViewClient.OnPageLoadCallBack {
    private static final String TAG = TwitterAuthView.class.getName();

    private ITwitterAuthCallback lis;
    private ExecutorService _executorService;
    private RequestToken _tempRequestToken;
    private Twitter _twitter;
    private ProgressDialog _progressDialog;

    public void setExecutorService(ExecutorService executorService_){
        _executorService =  executorService_;
    }

    public void setSuccessLis(ITwitterAuthCallback lis_){
        lis = lis_;
    }

    public TwitterAuthView(Context context) {
        super(context);
        init(context, null);

    }

    public TwitterAuthView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);

    }

    public TwitterAuthView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

    }

    private void init(Context context_, AttributeSet attrs)
        {

            _progressDialog =  new ProgressDialog(context_);
            _progressDialog.setMessage("Loading");
            WebSettings settings = getSettings();
            settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
            settings.setJavaScriptEnabled(true);
            settings.setBuiltInZoomControls(true);
            setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);
            requestFocus(View.FOCUS_DOWN);
            _executorService = Executors.newFixedThreadPool(2);
        }


    @Override
    public void onPageReceivedVerifier(WebView view_, String url_, String verifier_) {
        if(verifier_ == null){
            loadUrl("about:blank");
        }else if(TextUtils.isEmpty(verifier_)){
            loadUrl("about:blank");
        }
        Log.v(TAG, "verifier now recived, so looking for accesstoken" + verifier_);
            _executorService.submit(new AccessTokenCallable(_tempRequestToken,
                    new TwitterAccessTokenRetriever(), _twitter, verifier_));

    }

    @Override
    public void onPageReceivedError(WebView view_, int errorCode_, String description_, String failingUrl_) {

    }

    public void startTwitterAuthentication(AuthenticationDetails authDetails_){
        if(isEmpty(authDetails_.getConsumerKey()) || isEmpty(authDetails_.getConsumerSecrect()) || isEmpty(authDetails_.getCallback())){
            throw new IllegalArgumentException("AuthenticationDetails must contain valid consumer key, secrect and callback");
        }

        TwitterAuthWebViewClient client = new TwitterAuthWebViewClient(authDetails_.getCallback(), this);
        client.setOnPageListener(this);
        setWebViewClient(client);
        _twitter = new TwitterFactory().getInstance();
        _twitter.setOAuthConsumer(authDetails_.getConsumerKey(), authDetails_.getConsumerSecrect());
        final Future<RequestToken> requestTokenFuture =
                _executorService.submit(new RequestTokenCallable(authDetails_));
    }

    private boolean isEmpty(String value_){
        return TextUtils.isEmpty(value_);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        if(!url.startsWith("about:blank")){
            _progressDialog.show();
        }
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        _progressDialog.hide();

    }

    private class RequestTokenCallable implements Callable<RequestToken>{
        private final AuthenticationDetails _authDetails;
        public RequestTokenCallable(AuthenticationDetails authDetails_){
            _authDetails = authDetails_;
        }

        @Override
        public RequestToken call() throws Exception {
            final Twitter twitter = _authDetails.getTwitter();
            try {
                TwitterAuthView.this._tempRequestToken = twitter.getOAuthRequestToken();
                TwitterAuthView.this.post(new Runnable() {
                    @Override
                    public void run() {
                        TwitterAuthView.this.loadUrl(_tempRequestToken.getAuthenticationURL());
                    }
                });
                return _tempRequestToken;
            } catch (TwitterException e) {
                throw new TwitterException(e);
            }
        }
    }

    private class AccessTokenCallable implements Callable<UserBundle>{

        private final RequestToken _requestToken;
        private final ITwitterAccessTokenRetriever _accessTokenRetriever;
        private final Twitter _twitter;
        private final String _verifier;
        public AccessTokenCallable(RequestToken requestToken_, ITwitterAccessTokenRetriever accessTokenRetriever_,
                                   Twitter twitter_, String verifier_){
            _requestToken = requestToken_;
            _accessTokenRetriever =accessTokenRetriever_;
            _twitter = twitter_;
            _verifier = verifier_;
        }

        @Override
        public UserBundle call() throws Exception {
            try{
            final Collection<UserBundle> userBundles =
                    _accessTokenRetriever.retrieverAccessTokenFromTwitter(_requestToken,_verifier, _twitter );
            if(!userBundles.isEmpty()){
                final UserBundle userBundle = userBundles.iterator().next();
                TwitterAuthView.this.post(new Runnable() {
                    @Override
                    public void run() {
                        TwitterAuthView.this.lis.onSuccessTwitterOAuth(userBundle);
                    }
                });
                return userBundle;
            }else{
                TwitterAuthView.this.post(new Runnable() {
                    @Override
                    public void run() {
                        TwitterAuthView.this.lis.onFailTwitterOAuth(null);
                    }
                });
                return null;
            }

            }catch (Exception e){
                e.printStackTrace();
                throw new Exception(e);
            }
        }
    }

}
