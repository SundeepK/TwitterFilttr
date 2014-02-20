package com.sun.tweetfiltrr.twitter.twitterretrievers.api;

import android.content.SharedPreferences;

import java.util.Collection;

import twitter4j.TwitterException;

/**
 * Created by Sundeep on 13/02/14.
 */
public interface IAccessTokenRetrieverFromPref {

    public Collection<UserBundle> retrieveAccessTokenFromSharedPref(SharedPreferences sharedPreferences_) throws TwitterException;

}
