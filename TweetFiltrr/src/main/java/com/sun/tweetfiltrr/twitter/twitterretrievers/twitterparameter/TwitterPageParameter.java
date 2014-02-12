package com.sun.tweetfiltrr.twitter.twitterretrievers.twitterparameter;

import android.util.Log;

import com.sun.tweetfiltrr.twitter.twitterretrievers.api.ITwitterParameter;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.parcelable.parcelable.api.ICachedUser;

import javax.inject.Inject;
import javax.inject.Singleton;

import twitter4j.Paging;

/**
 * Created by Sundeep on 24/01/14.
 */
@Singleton
public class TwitterPageParameter implements ITwitterParameter<Paging> {

    @Inject
    public TwitterPageParameter(){}

    private static final String TAG = TwitterPageParameter.class.getName();

    @Override
    public Paging getTwitterParameter(ICachedUser user_, boolean shouldLookForOldTweets_) {
        final ParcelableUser currentUser =  user_.getUser();
        long friendId = currentUser.getUserId();
        long maxID =  user_.getMaxId() ;
        long sinceID = user_.getSinceId();
        currentUser.getUserTimeLine().clear();

        Paging page = new Paging();
        page.setCount(50);

        if(shouldLookForOldTweets_){
            if(maxID > 1){
                Log.v(TAG, "Setting max ID to: " + maxID);
                page.setMaxId(maxID);
            }
        }else if(sinceID > 1){
            Log.v(TAG, "Setting since ID to: " + sinceID);
            page.setSinceId(sinceID);
        }

        return page;
    }
}
