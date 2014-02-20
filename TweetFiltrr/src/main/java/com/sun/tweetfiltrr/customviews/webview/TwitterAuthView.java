package com.sun.tweetfiltrr.customviews.webview;

import android.content.Context;
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
public class TwitterAuthView extends WebView implements TwitterAuthWebViewClient.OnPageLoad {
    private static final String TAG = TwitterAuthView.class.getName();

    private ITwitterAuthCallback lis;
    private ExecutorService _executorService;
    private RequestToken _tempRequestToken;
    private Twitter _twitter;

    public void setExecutorService(ExecutorService executorService_){
        _executorService =  executorService_;
    }

    public void setSuccessLis(ITwitterAuthCallback lis_){
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
            _executorService = Executors.newFixedThreadPool(2);
            _twitter = new TwitterFactory().getInstance();
        }


    @Override
    public void onPageReceivedVerifier(WebView view_, String url_, String verifier_) {
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

        setWebViewClient(new TwitterAuthWebViewClient(authDetails_.getCallback(), this));
        _twitter.setOAuthConsumer(authDetails_.getConsumerKey(), authDetails_.getConsumerSecrect());
        final Future<RequestToken> requestTokenFuture =
                _executorService.submit(new RequestTokenCallable(authDetails_));
    }

    private boolean isEmpty(String value_){
        return TextUtils.isEmpty(value_);
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
            final UserBundle UserBundle = userBundles.iterator().next();
            TwitterAuthView.this.post(new Runnable() {
                @Override
                public void run() {
                    TwitterAuthView.this.lis.onSuccessTwitterOAuth(UserBundle);
                }
            });
            return UserBundle;
            }catch (Exception e){
                e.printStackTrace();
                throw new Exception(e);
            }
        }
    }

//        private class AsyncTwitterAuthTask extends AsyncTask<AuthenticationDetails, Void, AccessToken>{
//        RequestToken _requestToken;
//        @Override
//        protected AccessToken doInBackground(AuthenticationDetails... params) {
//            if(params.length > 1){
//                throw new IllegalArgumentException("Can only process one twitter authentication at a time");
//            }
//            final AuthenticationDetails auth = params[0];
//
//            publishProgress();
//            while(called.get())
//            {
//            }
//            Log.v(TAG, "passed publish " );
//
//            AccessToken access =  getAccessToken(twitter,_requestToken);
//            try {
//                ParcelableUser  parcelableUser = new ParcelableUser(twitter.showUser(access
//                         .getUserId()));
//                Log.v(TAG, "twitter user after oauth " + parcelableUser);
//                TwitterUtil.getInstance().setCurrentUser(parcelableUser);
//            } catch (TwitterException e) {
//                e.printStackTrace();
//            }
//
//            return access;
//        }
//
//        @Override
//        protected void onProgressUpdate(Void... values) {
//            //super.onProgressUpdate(values);
//            String uri = _requestToken.getAuthenticationURL();
//            TwitterAuthView.this.loadUrl(uri);
//
//
//        }
//
//        @Override
//        protected void onCancelled(AccessToken accessToken) {
//            super.onCancelled(accessToken);
//        }
//
//        @Override
//        protected void onCancelled() {
//            super.onCancelled();
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//        @Override
//        protected void onPostExecute(AccessToken accessToken) {
//            super.onPostExecute(accessToken);
//            lis.onSuccessTwitterOAuth(accessToken);
//
//        }
//
//        private AccessToken getAccessToken(Twitter twitter, RequestToken requestToken)
//        {
//            try
//            {
//                // Get an access token. This triggers network access.
//
//                return token;
//            }
//            catch (TwitterException e)
//            {
//                // Failed to get an access token.
//                e.printStackTrace();
//                Log.e(TAG, "Failed to get an access token.", e);
//
//                // No access token.
//                return null;
//            }
//        }
//    }



}
