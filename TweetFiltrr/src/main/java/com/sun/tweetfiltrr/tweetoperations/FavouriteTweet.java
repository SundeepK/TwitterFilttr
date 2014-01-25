package com.sun.tweetfiltrr.tweetoperations;

import android.util.Log;

import com.sun.tweetfiltrr.database.dao.IDBDao;
import com.sun.tweetfiltrr.parcelable.ParcelableTimeLineEntry;
import com.sun.tweetfiltrr.smoothprogressbarwrapper.SmoothProgressBarWrapper;
import com.sun.tweetfiltrr.tweetoperations.api.ITwitterOperation;
import com.sun.tweetfiltrr.utils.TwitterUtil;

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
    public void performTwitterOperation(ParcelableTimeLineEntry tweets_) {
        Twitter twitter = TwitterUtil.getInstance().getTwitter();
        try {
            twitter4j.Status status = twitter.createFavorite(tweets_.getTweetID());
            tweets_.setIsFavourite(status.isFavorited());
        } catch (TwitterException e) {
            e.printStackTrace();
            Log.v(TAG, "Error occured whilst trying to fav tweet");
        }
    }

}
