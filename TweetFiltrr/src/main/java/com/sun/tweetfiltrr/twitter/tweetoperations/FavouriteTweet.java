package com.sun.tweetfiltrr.twitter.tweetoperations;

import android.util.Log;

import com.sun.tweetfiltrr.parcelable.ParcelableTweet;
import com.sun.tweetfiltrr.twitter.tweetoperations.api.IOnTweetOperationFail;
import com.sun.tweetfiltrr.twitter.tweetoperations.api.ITweetOperation;
import com.sun.tweetfiltrr.utils.TwitterUtil;

import java.text.SimpleDateFormat;

import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Created by Sundeep on 01/01/14.
 */
public  class FavouriteTweet implements ITweetOperation {

    private static final String TAG = FavouriteTweet.class.getName() ;

    public FavouriteTweet(){
    }

    @Override
    public ParcelableTweet performTwitterOperation(ParcelableTweet tweets_, IOnTweetOperationFail failLister_) {
        Twitter twitter = TwitterUtil.getInstance().getTwitter();
        ParcelableTweet newTweet = null;
        SimpleDateFormat format = TwitterUtil.getInstance().getSimpleDateFormatThreadLocal().get();
        try {
            twitter4j.Status status = twitter.createFavorite(tweets_.getTweetID());
            newTweet = new ParcelableTweet(status, format.format(status.getCreatedAt()), status.getUser().getId());

            if(tweets_.isKeyWordSearchedTweet()){
                newTweet.setIsKeyWordSearedTweet(true);
            }

            if(tweets_.isMention()){
                newTweet.setIsMention(true);
            }


        } catch (TwitterException e) {
            e.printStackTrace();
            Log.v(TAG, "Error occured whilst trying to fav tweet");
            failLister_.onTweetOperationFail(e);
        }

        return newTweet;

    }


}
