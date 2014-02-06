package com.sun.tweetfiltrr.twitter.tweetoperations.api;

import twitter4j.TwitterException;

/**
 * Created by Sundeep on 01/02/14.
 */
public interface IOnTweetOperationFail {

    public void onTweetOperationFail(TwitterException exception_, ITweetOperation tweetOperation_);

}
