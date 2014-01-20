package com.sun.tweetfiltrr.asyncretriever.retrievers;

import android.util.Log;

import com.sun.tweetfiltrr.database.dao.IDBDao;
import com.sun.tweetfiltrr.parcelable.ParcelableTimeLineEntry;
import com.sun.tweetfiltrr.smoothprogressbarwrapper.SmoothProgressBarWrapper;
import com.sun.tweetfiltrr.tweetoperations.SimpleTweetOperation;

import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Created by Sundeep on 01/01/14.
 */
public  class FavouriteTweet extends SimpleTweetOperation {

    private static final String TAG = FavouriteTweet.class.getName() ;


    public FavouriteTweet(SmoothProgressBarWrapper progressBar_, IDBDao<ParcelableTimeLineEntry> timelineDao_){
        super(progressBar_, timelineDao_);
    }

    @Override
    protected void performSimpleOperation(ParcelableTimeLineEntry tweets_, Twitter twitter_) {
        try {
            twitter4j.Status status = twitter_.createFavorite(tweets_.getTweetID());
            tweets_.setIsFavourite(status.isFavorited());
        } catch (TwitterException e) {
            e.printStackTrace();
            Log.v(TAG, "Error occured whilst trying to fav tweet");
        }
    }

}
