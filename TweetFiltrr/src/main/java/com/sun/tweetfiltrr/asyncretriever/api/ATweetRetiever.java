package com.sun.tweetfiltrr.asyncretriever.api;

import android.util.Log;

import com.sun.tweetfiltrr.parcelable.ParcelableTimeLineEntry;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.tweetprocessor.api.ITweetProcessor;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;


public abstract class ATweetRetiever<V> implements Callable<V> {
	
	
	private static final String TAG = ATweetRetiever.class.getName();
    private ITweetProcessor _tweetProcessor;


    /**
     * Base class for runnable's which need to retrieve tweets from twitter. It provides default functionality to convert
     * tweets to {@link ParcelableTimeLineEntry} and add them to the current {@link ParcelableUser} so that they can later
     * be updated in the database.
     */
	protected ATweetRetiever(ITweetProcessor tweetProcessor_) {
        _tweetProcessor = tweetProcessor_;
	}



	protected boolean processTimeLine(Iterator<twitter4j.Status> iterator_, ParcelableUser friend_, Date today_, boolean shouldRunOnce_){
        return _tweetProcessor.processTimeLine(iterator_,friend_, today_, shouldRunOnce_);
	}
	

	protected void setFriendSinceMaxId(ParcelableUser friend_){
		if (!friend_.getUserTimeLine().isEmpty()) {
			List<ParcelableTimeLineEntry> timelines = friend_.getUserTimeLine();
			ParcelableTimeLineEntry timelineFirst = timelines.get(timelines.size() - 1);
			ParcelableTimeLineEntry timelineLast = timelines.get(0);
			Log.v(TAG, "Setting new maxID " + timelineLast.getTweetID());
			friend_.setSinceId(timelineLast.getTweetID());
			friend_.setMaxId( timelineFirst.getTweetID());
		}
	}



}
