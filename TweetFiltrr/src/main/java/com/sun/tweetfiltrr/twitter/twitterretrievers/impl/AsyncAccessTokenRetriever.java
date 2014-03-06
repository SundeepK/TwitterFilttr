package com.sun.tweetfiltrr.twitter.twitterretrievers.impl;

import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.sun.tweetfiltrr.customviews.webview.api.ITwitterAuthCallback;
import com.sun.tweetfiltrr.database.dao.FriendDao;
import com.sun.tweetfiltrr.twitter.twitterretrievers.api.IAccessTokenRetrieverFromPref;
import com.sun.tweetfiltrr.twitter.twitterretrievers.api.UserBundle;

import java.util.Collection;

import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;

public class AsyncAccessTokenRetriever extends AsyncTask<AccessToken, Void, UserBundle> {

	private static final String TAG = AsyncAccessTokenRetriever.class.getName();

    private ITwitterAuthCallback _callback;
    private  IAccessTokenRetrieverFromPref _tokenRetrieverFromPref;
    private SharedPreferences _preferences;
    public AsyncAccessTokenRetriever(FriendDao friendDao_, ITwitterAuthCallback callback_,
                                     SharedPreferences preferences_){
        _callback = callback_;
        _tokenRetrieverFromPref = new AccessTokenRetrieverFromPref(friendDao_);
        _preferences = preferences_;

    }

    @Override
    protected void onPostExecute(UserBundle parcelableUser) {
        super.onPostExecute(parcelableUser);
        if(parcelableUser!=null){
            _callback.onSuccessTwitterOAuth(parcelableUser);
        }
    }


	
	@Override
    protected UserBundle doInBackground(AccessToken... params) {
        try {
            Collection<UserBundle> bundles =
                    _tokenRetrieverFromPref.retrieveAccessTokenFromSharedPref(_preferences);
            return bundles.iterator().next();
        } catch (TwitterException e) {
            e.printStackTrace();
            _callback.onFailTwitterOAuth(e);
        }
        return null;
    }



}
