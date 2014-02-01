package com.sun.tweetfiltrr.tweetoperations.api;

import twitter4j.TwitterException;

/**
 * Created by Sundeep on 01/02/14.
 */
public interface IOnTweetOperationFail {

    public void onTweetOperationFail(TwitterException exception_);

}
