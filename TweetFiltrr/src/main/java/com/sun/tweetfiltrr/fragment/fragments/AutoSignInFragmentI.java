package com.sun.tweetfiltrr.fragment.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sun.tweetfiltrr.R;
import com.sun.tweetfiltrr.activity.activities.ITwitterAuthCallback;
import com.sun.tweetfiltrr.customviews.webview.TwitterAuthView;
import com.sun.tweetfiltrr.database.dao.FriendDao;
import com.sun.tweetfiltrr.fragment.api.ASignInFragment;
import com.sun.tweetfiltrr.twitter.twitterretrievers.api.IAccessTokenRetrieverFromPref;
import com.sun.tweetfiltrr.twitter.twitterretrievers.api.UserBundle;
import com.sun.tweetfiltrr.twitter.twitterretrievers.impl.AccessTokenRetrieverFromPref;

import java.util.Collection;

import javax.inject.Inject;

/**
 * Created by Sundeep.Kahlon on 20/02/14.
 */
public class AutoSignInFragmentI extends ASignInFragment {
    private TwitterAuthView _authWebView;
    @Inject
    FriendDao _friendDao;

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
        IAccessTokenRetrieverFromPref tokenRetrieverFromPref = new AccessTokenRetrieverFromPref(_friendDao);
        UserBundle bundle = null;
        try {
            Collection<UserBundle> userBundles = tokenRetrieverFromPref.retrieveAccessTokenFromSharedPref(sharedPreferences_);
            bundle = userBundles.iterator().next();
        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }


}
