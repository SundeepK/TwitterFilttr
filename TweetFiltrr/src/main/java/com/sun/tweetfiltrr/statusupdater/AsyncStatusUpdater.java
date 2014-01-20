package com.sun.tweetfiltrr.statusupdater;


import com.sun.tweetfiltrr.parcelable.ParcelableTimeLineEntry;
import com.sun.tweetfiltrr.utils.TwitterUtil;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class AsyncStatusUpdater implements Runnable {

	private ParcelableTimeLineEntry _tweetToUpdate;
	private OnStatusUpdateFailCallback _exceptionCallBack;
	
	public AsyncStatusUpdater(ParcelableTimeLineEntry tweetToUpdate_, OnStatusUpdateFailCallback exceptionCallBack_){
		_tweetToUpdate = tweetToUpdate_;
		_exceptionCallBack = exceptionCallBack_;
	}
	
	public interface OnStatusUpdateFailCallback{
		public void onUpdateFail(TwitterException exception_);
	}
	
	@Override
	public void run() {
		Twitter twitter = TwitterUtil.getInstance().getTwitter();
		StatusUpdate tweetUpdate = new StatusUpdate(_tweetToUpdate.getTweetText());
		tweetUpdate.setInReplyToStatusId(_tweetToUpdate.getInReplyToTweetId());
		try {
			twitter.updateStatus(tweetUpdate);
			//RelatedResults r = twitter.getRelatedResults(_tweetToUpdate.getInReplyToTweetId());
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if(_exceptionCallBack != null){
				_exceptionCallBack.onUpdateFail(e);
			}
		}
		
	}

}
