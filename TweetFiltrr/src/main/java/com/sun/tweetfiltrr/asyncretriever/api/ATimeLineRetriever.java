package com.sun.tweetfiltrr.asyncretriever.api;

import android.util.Log;

import com.sun.tweetfiltrr.parcelable.ParcelableTimeLineEntry;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.parcelable.parcelable.api.ICachedUser;
import com.sun.tweetfiltrr.tweetprocessor.api.ITweetProcessor;
import com.sun.tweetfiltrr.utils.DateUtils;
import com.sun.tweetfiltrr.utils.TwitterUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public abstract class ATimeLineRetriever implements ITwitterRetriever<Collection<ParcelableUser>> {

	private static final String TAG = ATimeLineRetriever.class
			.getName();
    private boolean _shouldLookForOldTweetsOnly;
    private boolean _shouldRunOnce = true;
    private ITweetProcessor _tweetProcessor;


    public ATimeLineRetriever(ITweetProcessor tweetProcessor_,
                              boolean shouldLookForOldTweetsOnly_) {
        _shouldLookForOldTweetsOnly = shouldLookForOldTweetsOnly_;
        _tweetProcessor = tweetProcessor_;
    }


    @Override
    public Collection<ParcelableUser> retrieveTwitterData(ICachedUser user_) {
        return getUpdatedUserWithTimeline(user_);
    }

    /**
     * Get the {@link Paging} required to search for new tweets
     * @param user_
     * @return
     */
    private Paging getPagingForTimeline(ICachedUser user_){
        final ParcelableUser currentUser =  user_.getUser();
        long friendId = currentUser.getUserId();
        long maxID =  user_.getMaxId() ;
        long sinceID = user_.getSinceId();
        currentUser.getUserTimeLine().clear();

        Paging page = new Paging();
        page.setCount(50);

        if(_shouldLookForOldTweetsOnly){
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

    /**
     * Get the timeline and associate it to the user
     *
     * @param user_
     * @return
     */
    private Collection<ParcelableUser> getUpdatedUserWithTimeline(ICachedUser user_){
        final ParcelableUser currentUser = user_.getUser();
        final Paging page = getPagingForTimeline(user_);
        final Twitter twitter = TwitterUtil.getInstance().getTwitter();
        ResponseList<twitter4j.Status> timeLine = null;
        final Date previousDate = DateUtils.getPreviousDate();
        final long friendId = currentUser.getUserId();
        Collection<ParcelableUser> usersWithTweets;
        Log.v(TAG, "Current date minus 1 day :" + previousDate.toString());

        do {
            try {
                timeLine =  getTweets(twitter, friendId, page);
            } catch (TwitterException e) {
                e.printStackTrace();
                Log.e(TAG, "Error occured while attempting to retrieve user timeline, maybe becuase query limit has been execeeded.");
                Log.w(TAG, "Setting page count to: " + currentUser.getMaxId() + " and inserting into DB");
                setUpdateUserDetails(user_, currentUser.getUserTimeLine());
                return new ArrayList<ParcelableUser>();
            }

            usersWithTweets = _tweetProcessor.processTimeLine( timeLine.iterator(), currentUser, null);

            if(_shouldRunOnce){
                break;
            }

        } while (timeLine.size() > 0);
        Log.v(TAG, "reached end of timeline task, with maxID: " + currentUser.getMaxId());

        //below is now handled by tweet processior because it's much easier there
       // setUpdateUserDetails(user_, currentUser.getUserTimeLine());

        return usersWithTweets;
    }

    protected abstract ResponseList<twitter4j.Status> getTweets(Twitter twitter_, long friendID_, Paging page_) throws  TwitterException;

    /**
     * Update the MaxID, SinceID and total tweet count for current user so we can pick up where we left off. This prevents us from
     * processing the same request more than once.
     *
     * @param friend_
     * @param timeLineEntries_
     */
    protected void setUpdateUserDetails(final ICachedUser friend_, final List<ParcelableTimeLineEntry> timeLineEntries_){
        if (!timeLineEntries_.isEmpty()) {
            ParcelableTimeLineEntry timelineFirst = timeLineEntries_.get(timeLineEntries_.size() - 1);
            ParcelableTimeLineEntry timelineLast = timeLineEntries_.get(0);
            Log.v(TAG, "Setting new maxID " + timelineLast.getTweetID());
            friend_.setSinceId(timelineLast.getTweetID());
            friend_.setMaxId( timelineFirst.getTweetID());
        }
        //update the total tweets recieved
        friend_.setTotalCount(friend_.getTotalCount()+timeLineEntries_.size());
    }


}
