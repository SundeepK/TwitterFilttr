package com.sun.tweetfiltrr.twitter.tweetoperations.impl;

import android.util.Log;

import com.sun.tweetfiltrr.parcelable.ParcelableTweet;
import com.sun.tweetfiltrr.parcelable.parcelable.api.ICachedUser;
import com.sun.tweetfiltrr.twitter.api.ITwitterAPICall;
import com.sun.tweetfiltrr.twitter.api.ITwitterAPICallStatus;
import com.sun.tweetfiltrr.utils.TwitterUtil;

import java.text.SimpleDateFormat;

import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Created by Sundeep on 01/01/14.
 */
public  class FavouriteTweet implements ITwitterAPICall<ParcelableTweet> {

    private static final String TAG = FavouriteTweet.class.getName() ;

    public FavouriteTweet(){
    }

    @Override
    public ParcelableTweet retrieveTwitterData(ICachedUser user_, ITwitterAPICallStatus failLister_) {
        Twitter twitter = TwitterUtil.getInstance().getTwitter();
        ParcelableTweet newTweet = null;
        ParcelableTweet tweetToProcess =  user_.getUser().getUserTimeLine().iterator().next();
        SimpleDateFormat format = TwitterUtil.getInstance().getSimpleDateFormatThreadLocal().get();

        Log.v(TAG, "fav before " + tweetToProcess.toString());

        try {
            twitter4j.Status status = null;
            if(!tweetToProcess.isFavourite()){
                status = twitter.createFavorite(tweetToProcess.getTweetID());
                tweetToProcess.setIsFavourite(true);
            }else{
                status  = twitter.destroyFavorite(tweetToProcess.getTweetID());
                tweetToProcess.setIsFavourite(false);
            }
            newTweet = new ParcelableTweet(status, format.format(status.getCreatedAt()), status.getUser().getId());

            if(tweetToProcess.isKeyWordSearchedTweet()){
                newTweet.setIsKeyWordSearedTweet(true);
            }

            if(tweetToProcess.isMention()){
                newTweet.setIsMention(true);
            }
            Log.v(TAG, "fav after " + newTweet.toString());


        } catch (TwitterException e) {
            e.printStackTrace();
            Log.v(TAG, "Error occured whilst trying to fav tweet");
            failLister_.onTwitterApiCallFail(user_.getUser(),e, this);
        }

        return newTweet;

    }

    @Override
    public ITwitterAPICall.TwitterAPICallType getTweetOperationType() {
        return TwitterAPICallType.POST_FAVOURITE;
    }



}
