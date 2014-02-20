package com.sun.tweetfiltrr.twitter.twitterretrievers.api;

import java.util.Collection;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.RequestToken;

/**
 * Created by Sundeep on 20/02/14.
 */
public interface ITwitterAccessTokenRetriever {
    public Collection<UserBundle> retrieverAccessTokenFromTwitter(RequestToken requestToken_, String verifier_, Twitter twitter_) throws TwitterException;

}
