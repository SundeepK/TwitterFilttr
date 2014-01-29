package com.sun.tweetfiltrr.tweetoperations;

import android.util.Log;

import com.sun.tweetfiltrr.parcelable.ParcelableTweet;
import com.sun.tweetfiltrr.tweetoperations.api.ITwitterOperation;
import com.sun.tweetfiltrr.utils.TwitterUtil;

import java.text.SimpleDateFormat;

import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Created by Sundeep on 01/01/14.
 */
public  class FavouriteTweet implements ITwitterOperation {

    private static final String TAG = FavouriteTweet.class.getName() ;

    public FavouriteTweet(){
    }

    @Override
    public ParcelableTweet performTwitterOperation(ParcelableTweet tweets_) {
        Twitter twitter = TwitterUtil.getInstance().getTwitter();
        ParcelableTweet newTweet = null;
        SimpleDateFormat format = TwitterUtil.getInstance().getSimpleDateFormatThreadLocal().get();
        try {
            twitter4j.Status status = twitter.createFavorite(tweets_.getTweetID());
            newTweet = new ParcelableTweet(status, format.format(status.getCreatedAt()), status.getUser().getId());
        } catch (TwitterException e) {
            e.printStackTrace();
            Log.v(TAG, "Error occured whilst trying to fav tweet");
        }

        return newTweet;

    }

}
