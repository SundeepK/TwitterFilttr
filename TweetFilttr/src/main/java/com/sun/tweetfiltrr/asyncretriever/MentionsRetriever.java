package com.sun.tweetfiltrr.asyncretriever;

import android.util.Log;

import com.sun.tweetfiltrr.asyncretriever.api.ATweetRetiever;
import com.sun.tweetfiltrr.parcelable.ParcelableTimeLineEntry;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.tweetprocessor.api.ITweetProcessor;
import com.sun.tweetfiltrr.utils.DateUtils;
import com.sun.tweetfiltrr.utils.TwitterUtil;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Created by Sundeep on 12/01/14.
 */
public class MentionsRetriever extends ATweetRetiever<ParcelableUser> {


    private static final String TAG = MentionsRetriever.class.getName();
    protected ParcelableUser _friend;
    private boolean _shouldLookForOldTweetsOnly;

    /**
     *
     * Implementation of a {@link com.sun.tweetfiltrr.asyncretriever.api.ATweetRetiever} to get the authenticating user's
     * mentions
     *
     * @param tweetProcessor_
     */
    protected MentionsRetriever(ParcelableUser friend_, ITweetProcessor tweetProcessor_, boolean shouldLookForOldTweetsOnly_) {
        super(tweetProcessor_);
		_friend = friend_;
        _shouldLookForOldTweetsOnly = shouldLookForOldTweetsOnly_;
    }

    @Override
    public ParcelableUser call() throws Exception {
        _friend.getUserTimeLine().clear();
		long friendId = _friend.getUserId();
		long maxID =  _friend.getMaxIdForMentions();
		long sinceID = _friend.getSinceIdForMentions();

        //TODO we need the last page number because searching using maxID will include the tweet and so will return
        //a
        int lastPageNumber =  _friend.getLastTimelinePageNumber() <= 0  ? 1 :  _friend.getLastTimelinePageNumber();


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

		Twitter twitter = TwitterUtil.getInstance().getTwitter();
		ResponseList<Status> mentions = null;

		Date previousDate = DateUtils.getPreviousDate();

		Log.v(TAG, "Current date minus 1 day :" + previousDate.toString());

		do {
			try {

                mentions = twitter.getMentionsTimeline(page);
			} catch (TwitterException e) {
				e.printStackTrace();
				Log.e(TAG, "Error occured while attempting to retrieve user mentions, maybe becuase query limit has been execeeded.");
				Log.w(TAG, "Setting maxID: " + maxID + " and returing");
				setFriendSinceMaxId(_friend);
				return _friend;
 			}

			Iterator<Status> it = mentions.iterator();

            if(!processTimeLine(it, _friend, null, true)){
                break;
            }

			
		} while (mentions.size() > 0);
		Log.v(TAG, "reached end of timeline task, with pagenumber: " + maxID);
        _friend.setLastTimelinePageNumber(lastPageNumber);
        setFriendSinceMaxId(_friend);
	return _friend;

    }

    protected void setFriendSinceMaxId(ParcelableUser friend_){
        if (!friend_.getUserTimeLine().isEmpty()) {
            List<ParcelableTimeLineEntry> timelines = friend_.getUserTimeLine();
            ParcelableTimeLineEntry timelineFirst = timelines.get(timelines.size() - 1);
            ParcelableTimeLineEntry timelineLast = timelines.get(0);
            Log.v(TAG, "Setting new sinceID " + timelineLast.getTweetID());
            Log.v(TAG, "Setting new maxID " + timelineFirst.getTweetID());
            friend_.setSinceIdForMentions(timelineLast.getTweetID());
            friend_.setMaxIdForMentions( timelineFirst.getTweetID());
        }
    }



}
