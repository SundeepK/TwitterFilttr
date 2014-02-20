package com.sun.tweetfiltrr.fragment.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sun.tweetfiltrr.R;
import com.sun.tweetfiltrr.activity.activities.ITwitterAuthCallback;
import com.sun.tweetfiltrr.customviews.webview.AuthenticationDetails;
import com.sun.tweetfiltrr.customviews.webview.TwitterAuthView;
import com.sun.tweetfiltrr.fragment.api.ASignInFragment;
import com.sun.tweetfiltrr.utils.TwitterConstants;

/**
 * Created by Sundeep.Kahlon on 20/02/14.
 */
public class OAuthSignInFragmentI extends ASignInFragment {
    private TwitterAuthView _authWebView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.login_screen_web_auth, container, false);
        return rootView;
    }


    protected void initView(View rootView_){
        super.initView(rootView_);
        _authWebView = (TwitterAuthView) rootView_.findViewById(R.id.twitter_auth_web_view);
    }

    @Override
    protected void authenticateUser(ITwitterAuthCallback callback_) {
        _authWebView.setSuccessLis(callback_);
        AuthenticationDetails details = new AuthenticationDetails(TwitterConstants.TWITTER_CONSUMER_KEY,
                TwitterConstants.TWITTER_CONSUMER_SECRET, "https://twitterfiltrr.com");
        _authWebView.setVisibility(View.VISIBLE);
        _authWebView.startTwitterAuthentication(details);
    }




}
