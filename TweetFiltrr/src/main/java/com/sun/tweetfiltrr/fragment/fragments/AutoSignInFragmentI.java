package com.sun.tweetfiltrr.fragment.fragments;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.sun.tweetfiltrr.activity.activities.ITwitterAuthCallback;
import com.sun.tweetfiltrr.customviews.webview.TwitterAuthView;
import com.sun.tweetfiltrr.database.dao.FriendDao;
import com.sun.tweetfiltrr.fragment.api.ASignInFragment;
import com.sun.tweetfiltrr.twitter.twitterretrievers.impl.AsyncAccessTokenRetriever;

import javax.inject.Inject;

/**
 * Created by Sundeep.Kahlon on 20/02/14.
 */
public class AutoSignInFragmentI extends ASignInFragment {
    private static final String TAG = AutoSignInFragmentI.class.getName();
    private TwitterAuthView _authWebView;
    @Inject
    FriendDao _friendDao;


    @Override
    protected void authenticateUser(ITwitterAuthCallback callback_) {
        Log.v(TAG, "starting auto auth");
        SharedPreferences sharedPreferences =  PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        AsyncAccessTokenRetriever task  =new AsyncAccessTokenRetriever(_friendDao, callback_,sharedPreferences);
        task.execute();
    }



}
