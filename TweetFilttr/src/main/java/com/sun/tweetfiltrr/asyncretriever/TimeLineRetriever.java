package com.sun.tweetfiltrr.asyncretriever;

import android.util.Log;

import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.tweetprocessor.api.ITweetProcessor;
import com.sun.tweetfiltrr.asyncretriever.api.ATweetRetiever;
import com.sun.tweetfiltrr.utils.DateUtils;
import com.sun.tweetfiltrr.utils.TwitterUtil;

import java.util.Date;
import java.util.Iterator;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class TimeLineRetriever extends ATweetRetiever<ParcelableUser> {

	private static final String TAG = TimeLineRetriever.class
			.getName();
    protected ParcelableUser _friend;
    private boolean _shouldLookForOldTweetsOnly;
    private boolean _shouldRunOnce;


    public TimeLineRetriever(ParcelableUser friend_,
                             ITweetProcessor tweetProcessor_,
                             boolean shouldLookForOldTweetsOnly_, boolean shouldRunOnce_) {
        super( tweetProcessor_);
        _friend = friend_;
        _shouldLookForOldTweetsOnly = shouldLookForOldTweetsOnly_;
        _shouldRunOnce = shouldRunOnce_;
    }

	                             

	@Override
	public ParcelableUser call() {
		long friendId = _friend.getUserId();
		long maxID =  _friend.getMaxId() ;
		long sinceID = _friend.getSinceId();
        _friend.getUserTimeLine().clear();

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
		ResponseList<twitter4j.Status> timeLine = null;

		Date previousDate = DateUtils.getPreviousDate();

		Log.v(TAG, "Current date minus 1 day :" + previousDate.toString());

		do {
			try {
		
				timeLine = twitter.getUserTimeline(friendId, page);
			} catch (TwitterException e) {
				e.printStackTrace();
				Log.e(TAG, "Error occured while attempting to retrieve user timeline, maybe becuase query limit has been execeeded.");
				Log.w(TAG, "Setting page count to: " + maxID + " and inserting into DB");
				
				setFriendSinceMaxId(_friend);
				return null;
 			}

			Iterator<twitter4j.Status> it = timeLine.iterator();

            if(!processTimeLine(it, _friend, null, _shouldRunOnce)){
                break;
            }

			
		} while (timeLine.size() > 0);
		Log.v(TAG, "reached end of timeline task, with pagenumber: " + maxID);
        setFriendSinceMaxId(_friend);
        _friend.setTotalTweetCount(_friend.getTotalTweetCount()+_friend.getUserTimeLine().size());

	return _friend;
    }




}
