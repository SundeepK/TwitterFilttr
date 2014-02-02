package com.sun.tweetfiltrr.twitter.retrievers;

import android.os.Handler;
import android.util.Log;

import com.sun.tweetfiltrr.database.dao.IDBDao;
import com.sun.tweetfiltrr.database.dao.TimelineDao;
import com.sun.tweetfiltrr.parcelable.ParcelableTweet;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.utils.TwitterConstants;
import com.sun.tweetfiltrr.utils.TwitterUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

import static com.sun.tweetfiltrr.database.tables.TimelineTable.TimelineColumn;

public class ConversationRetriever implements Runnable{

	private static final String TAG = ConversationRetriever.class.getName();
	private ParcelableTweet _tweetFirsInConvo;
	private ParcelableUser _friend;
	private IDBDao<ParcelableTweet> _timelineDao;
	private Date _today;
	private SimpleDateFormat _dateFormat;
	private OnConvoLoadListener _onFinishLis;
	private Handler _currentHandler;
	
	public ConversationRetriever(
            ParcelableUser friend_, IDBDao<ParcelableTweet> timelineDao_,
            OnConvoLoadListener onFinishLis_, Handler currentHandler_) {
	
		_friend = friend_;
		_timelineDao = timelineDao_;
		Calendar calender = Calendar.getInstance();
		calender.add(Calendar.DATE, -1);
		_today = calender.getTime();
		_dateFormat = new SimpleDateFormat(
				TwitterConstants.SIMPLE_DATE_FORMATE);
		_onFinishLis = onFinishLis_;
		_currentHandler = currentHandler_;
	}
	
//	public ConversationRetriever(
//			ParcelableTweet tweetFirsInConvo_, TimelineDao timelineDao_, OnConversationLoadFinish onFinishLis_ ) {
//	
//		_tweetFirsInConvo = tweetFirsInConvo_;
//		_timelineDao = timelineDao_;
//		Calendar calender = Calendar.getInstance();
//		calender.add(Calendar.DATE, -1);
//		_today = calender.getTime();
//		_dateFormat = new SimpleDateFormat(
//				TwitterConstants.SIMPLE_DATE_FORMATE);
//		_onFinishLis = onFinishLis_;
//	}
	
//	protected ConversationRetriever(Handler timelineHandler_, int flag_,
//			ParcelableTweet tweetFirsInConvo_, TimelineDao timelineDao_, OnConversationLoadFinish onFinishLis_ ) {
//		super(timelineHandler_, flag_);
//		_tweetFirsInConvo = tweetFirsInConvo_;
//		_timelineDao = timelineDao_;
//		Calendar calender = Calendar.getInstance();
//		calender.add(Calendar.DATE, -1);
//		_today = calender.getTime();
//		_dateFormat = new SimpleDateFormat(
//				TwitterConstants.SIMPLE_DATE_FORMATE);
//		_onFinishLis = onFinishLis_;
//	}
	
	public interface OnConvoLoadListener{
		
		public void onLoadFinish(LinkedList<ParcelableUser> conversation_);
		
	}

	@Override
	public void run() {
		Twitter twitter = TwitterUtil.getInstance().getTwitter();

		final LinkedList <ParcelableUser> convo = getConversation(_friend, _timelineDao, twitter);
		//final LinkedList <ParcelableUser> convo = getSimilarTweets(twitter, _friend);
		Collections.reverse(convo);
		
		_currentHandler.post(new Runnable() {
			
			@Override
			public void run() {
				_onFinishLis.onLoadFinish(convo);	
			}
		});
		
		
	}
	
	protected LinkedList<ParcelableUser> processTimeline(Iterator<Status> iterator_){
		LinkedList<ParcelableUser> users = new LinkedList<ParcelableUser>();
		while (iterator_.hasNext()) {
			Status tweet = iterator_.next();

			Log.v(TAG, "------------------------------------------------");
			Log.v(TAG, tweet.getText());		
			Log.v(TAG, "" + tweet.getInReplyToScreenName());		
			Log.v(TAG, "Tweet date: " + tweet.getCreatedAt().toString());
			
			User user = tweet.getUser();
			ParcelableUser parcelUser = new ParcelableUser(user);
			
			ParcelableTweet timeLineEntry =
					new ParcelableTweet(tweet,_dateFormat.format(tweet.getCreatedAt()), parcelUser.getUserId());
			parcelUser.addTimeLineEntry(timeLineEntry);
			users.add(parcelUser);
		}
		return users;
	}
	
	
//	private LinkedList <ParcelableUser> getSimilarTweets(Twitter twitter_, ParcelableUser tweetFirsInConvo_){
//		LinkedList<ParcelableUser>  users =  null;
//		try {
//
//			for(ParcelableTweet tweet : tweetFirsInConvo_.getUserTimeLine()){
//				RelatedResults results =twitter_.getRelatedResults(tweet.getTweetID());
//				ResponseList<Status> convos =	results.getTweetsWithReply();
//				users = processTimeline(convos.iterator());
//				break;
//			}
//		} catch (TwitterException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		return users;
//	}
	
	private LinkedList <ParcelableUser> getConversation(ParcelableUser tweetFirsInConvo_,  IDBDao<ParcelableTweet> timelineDao_, Twitter twitter_){
		LinkedList <ParcelableUser>	convo = new LinkedList <ParcelableUser>();

		
		for(ParcelableTweet tweetFirsInConvo : tweetFirsInConvo_.getUserTimeLine()){

//			Collection<ParcelableTweet> tweets =timelineDao_.getEntries(TimelineColumn.TWEET_ID.a() + "=?",
//				new String[]{tweetFirsInConvo.getInReplyToTweetId() + ""}, null);
		
//		if(!tweets.isEmpty() && tweets.size() > 0){
//			getConversationFromDB(tweetFirsInConvo_, convo, timelineDao_);
//			return convo;
//		}
		getConversationFromNetwork(tweetFirsInConvo_, convo, twitter_);
		
//			for (ParcelableUser user : convo) {
//				timelineDao_.insertOrUpdate(user.getUserTimeLine(),
//						TimelineColumn.values());
//			}
			break;
		}
		return convo;
	}


    private ParcelableUser getReplyTweet(long tweetReplyId_, Twitter twitter_) throws TwitterException {
        Status replyTweet = twitter_.showStatus(tweetReplyId_);
        User user = replyTweet.getUser();
        ParcelableUser parcelableUser = new ParcelableUser(user);
        ParcelableTweet parcelableTimeline = new ParcelableTweet(
                replyTweet, _dateFormat.format(replyTweet
                .getCreatedAt()), user.getId());
        parcelableUser.addTimeLineEntry(parcelableTimeline);
        return parcelableUser;
    }

    private void getConversationFromNetwork(
            ParcelableUser twitterUser_,
            LinkedList <ParcelableUser> conversation_, Twitter twitter_) {
        ParcelableUser parcelaberUser = null;

        if(twitterUser_ == null){
            return;
        }

        conversation_.add(twitterUser_);
        //We only really expect on tweet here, but we loop any way
        for (ParcelableTweet tweet : twitterUser_.getUserTimeLine()) {
            Log.v(TAG, twitterUser_.toString());
            Log.v(TAG, tweet.toString());
            if (tweet.getInReplyToTweetId() > 0	&& tweet.getInReplyToUserId() > 0) {
                //this maybe a potentail bug with twitter4j so follow up
                //but we need to try both the reply tweetID and userID, since twitter4j mixes up it's getters
                //for these values TODO
                try {
                    parcelaberUser =  getReplyTweet(tweet.getInReplyToTweetId(), twitter_);
                } catch (TwitterException e) {
                    Log.v(TAG, "Cant retrieve reply tweet, so retrying with user ID");

                    try {
                        parcelaberUser =  getReplyTweet(tweet.getInReplyToUserId(), twitter_);
                    } catch (TwitterException e1) {
                        Log.v(TAG, "Cant retrieve reply tweet using User ID either");
                        e1.printStackTrace();
                    }
                    e.printStackTrace();
                }
                getConversationFromNetwork(parcelaberUser, conversation_,
                        twitter_);
            }
        }
    }

	private void  getConversationFromDB(ParcelableTweet tweetFirsInConvo_,
			Collection<ParcelableTweet> conversation_, TimelineDao timelineDao_){
		
		Collection<ParcelableTweet> convo =_timelineDao.getEntries(TimelineColumn.TWEET_ID.s() + "=?",
				new String[]{tweetFirsInConvo_.getInReplyToTweetId() + ""}, null);
		conversation_.addAll(convo);
		if(!convo.isEmpty() && convo.size() > 0){
			for(ParcelableTweet friend : convo){
				if(friend.getInReplyToTweetId() != 0){
					getConversationFromDB(friend, conversation_, timelineDao_);
				}
			}
		}

	}

}
