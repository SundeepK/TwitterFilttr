package com.sun.tweetfiltrr.twitter.twitterretrievers.api;

import android.content.SharedPreferences;

import com.sun.tweetfiltrr.twitter.twitterretrievers.impl.AccessTokenRetriever;

import twitter4j.TwitterException;

/**
 * Created by Sundeep on 13/02/14.
 */
public interface IAccessTokenRetriever {

    public AccessTokenRetriever.UserBundle retrieverAccessToken(SharedPreferences sharedPreferences_, String verifier_) throws TwitterException;

}
