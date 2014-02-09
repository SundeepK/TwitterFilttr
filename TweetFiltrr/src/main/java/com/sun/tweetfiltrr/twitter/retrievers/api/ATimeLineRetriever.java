package com.sun.tweetfiltrr.twitter.retrievers.api;

import android.util.Log;

import com.sun.tweetfiltrr.parcelable.ParcelableTweet;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.parcelable.parcelable.api.ICachedUser;
import com.sun.tweetfiltrr.twitter.tweetprocessor.api.ITweetProcessor;
import com.sun.tweetfiltrr.utils.DateUtils;
import com.sun.tweetfiltrr.utils.TwitterUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import twitter4j.Twitter;
import twitter4j.TwitterException;

public abstract class ATimeLineRetriever<T> implements ITwitterRetriever<Collection<ParcelableUser>> {

	private static final String TAG = ATimeLineRetriever.class
			.getName();
    private boolean _shouldLookForOldTweetsOnly;
    private boolean _shouldRunOnce = true;
    private ITweetProcessor _tweetProcessor;
    private ITwitterParameter<T> _twitterParameter;

    public ATimeLineRetriever(ITweetProcessor tweetProcessor_, ITwitterParameter<T> twitterParameter_,
                              boolean shouldRunOnce_, boolean shouldLookForOldTweetsOnly_) {
        _shouldRunOnce = shouldRunOnce_;
        _shouldLookForOldTweetsOnly = shouldLookForOldTweetsOnly_;
        _tweetProcessor = tweetProcessor_;
        _twitterParameter = twitterParameter_;
    }


    @Override
    public Collection<ParcelableUser> retrieveTwitterData(ICachedUser user_) {
        return getUpdatedUserWithTimeline(user_);
    }

    /**
     * Get the timeline and associate it to the user
     *
     * @param user_
     * @return
     */
    private Collection<ParcelableUser> getUpdatedUserWithTimeline(ICachedUser user_){
        final ParcelableUser currentUser = user_.getUser();
        final T page = _twitterParameter.getTwitterParameter(user_,_shouldLookForOldTweetsOnly );
        final Twitter twitter = TwitterUtil.getInstance().getTwitter();
        ITwitterResponse<T> timeLine = null;
        final Date previousDate = DateUtils.getPreviousDate();
        final long friendId = currentUser.getUserId();
        Collection<ParcelableUser> usersWithTweets = new ArrayList<ParcelableUser>();
        Log.v(TAG, "Current date minus 1 day :" + previousDate.toString());

        do {
            try {

                if(timeLine == null){
                    timeLine =  getTweets(twitter, friendId, page);
                }else{
                    timeLine =  getTweets(twitter, friendId, timeLine.getNextTwitterParameter());
                }


            } catch (TwitterException e) {
                e.printStackTrace();
                Log.e(TAG, "Error occured while attempting to retrieve user timeline, maybe becuase query limit has been execeeded.");
                Log.w(TAG, "Setting page count to: " + currentUser.getMaxId() + " and inserting into DB");
                setUpdateUserDetails(user_, currentUser.getUserTimeLine());
                return new ArrayList<ParcelableUser>();
            }

            usersWithTweets.addAll(_tweetProcessor.processTimeLine(timeLine.getTwitterResult(), user_, previousDate));

            if(_shouldRunOnce){
                break;
            }

        } while (timeLine.hasNext());
        Log.v(TAG, "reached end of timeline task, with maxID: " + currentUser.getMaxId());

        //below is now handled by tweet processior because it's much easier there
       // setUpdateUserDetails(user_, currentUser.getUserTimeLine());

        return usersWithTweets;
    }

    protected abstract ITwitterResponse<T> getTweets(Twitter twitter_, long friendID_, T page_) throws  TwitterException;

    /**
     * Update the MaxID, SinceID and total tweet count for current user so we can pick up where we left off. This prevents us from
     * processing the same request more than once.
     *
     * @param friend_
     * @param timeLineEntries_
     */
    protected void setUpdateUserDetails(final ICachedUser friend_, final List<ParcelableTweet> timeLineEntries_){
        if (!timeLineEntries_.isEmpty()) {
            ParcelableTweet timelineFirst = timeLineEntries_.get(timeLineEntries_.size() - 1);
            ParcelableTweet timelineLast = timeLineEntries_.get(0);
            Log.v(TAG, "Setting new maxID " + timelineLast.getTweetID());
            friend_.setSinceId(timelineLast.getTweetID());
            friend_.setMaxId( timelineFirst.getTweetID());
        }
        //update the total tweets recieved
        friend_.setTotalCount(friend_.getTotalCount()+timeLineEntries_.size());
    }


}
