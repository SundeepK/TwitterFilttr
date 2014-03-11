package com.sun.tweetfiltrr.twitter.twitterretrievers.impl;

import android.util.Log;

import com.sun.tweetfiltrr.parcelable.ParcelableTweet;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.parcelable.parcelable.api.ICachedUser;
import com.sun.tweetfiltrr.twitter.api.ITwitterAPICall;
import com.sun.tweetfiltrr.twitter.api.ITwitterAPICallStatus;
import com.sun.tweetfiltrr.utils.TwitterUtil;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import javax.inject.Inject;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

public class ConversationRetriever implements ITwitterAPICall<Collection<ParcelableUser>>{

	private static final String TAG = ConversationRetriever.class.getName();

    @Inject
	public ConversationRetriever() {
	}

    @Override
    public TwitterAPICallType getTweetOperationType() {
        return TwitterAPICallType.GET_CONVO;
    }

    @Override
    public Collection<ParcelableUser> retrieveTwitterData(ICachedUser user_, ITwitterAPICallStatus failLis_) {
        final LinkedList <ParcelableUser> convo = getConversation(user_.getUser(), failLis_ );
        Collections.reverse(convo);
        return convo;
    }

    private LinkedList <ParcelableUser> getConversation(ParcelableUser tweetFirsInConvo_, ITwitterAPICallStatus failLis_){
        final LinkedList <ParcelableUser>	convo = new LinkedList <ParcelableUser>();
        final Twitter twitter = TwitterUtil.getInstance().getTwitter();
        final ThreadLocal<SimpleDateFormat> dateFormat = TwitterUtil.getInstance().getSimpleDateFormatThreadLocal();
        final SimpleDateFormat simpleDateFormat = dateFormat.get();
        getConversationFromNetwork(tweetFirsInConvo_, convo, twitter, simpleDateFormat);
        return convo;
    }

    private ParcelableUser getReplyTweet(long tweetReplyId_, ParcelableTweet tweet, Twitter twitter_, SimpleDateFormat dateFormat_, boolean isSwapped) throws TwitterException {
        final Status replyTweet = twitter_.showStatus(tweetReplyId_);
        final User user = replyTweet.getUser();
        final ParcelableUser parcelableUser = new ParcelableUser(user);
        final ParcelableTweet parcelableTimeline = new ParcelableTweet(
                replyTweet, dateFormat_.format(replyTweet
                .getCreatedAt()), user.getId(), isSwapped);
        if(tweet.isFavourite()){
            parcelableTimeline.setIsFavourite(true);
        }
        if(tweet.isMention()){
            parcelableTimeline.setIsMention(true);
        }
        if(tweet.isRetweeted()){
            parcelableTimeline.setIsRetweeted(true);
        }
        if(tweet.isKeyWordSearchedTweet()){
            parcelableTimeline.setIsKeyWordSearedTweet(true);
        }
        parcelableUser.addTimeLineEntry(parcelableTimeline);
        return parcelableUser;
    }

    private void getConversationFromNetwork(
            ParcelableUser twitterUser_,
            LinkedList <ParcelableUser> conversation_, Twitter twitter_, SimpleDateFormat dateFormat_) {
        ParcelableUser parcelableUser = null;

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
                    parcelableUser =  getReplyTweet(tweet.getInReplyToTweetId(), tweet, twitter_, dateFormat_, false);
                } catch (TwitterException e) {
                    Log.v(TAG, "Cant retrieve reply tweet, so retrying with user ID");

                    try {
                        parcelableUser =  getReplyTweet(tweet.getInReplyToUserId(), tweet, twitter_, dateFormat_, true);
                    } catch (TwitterException e1) {
                        Log.v(TAG, "Cant retrieve reply tweet using User ID either");
                        e1.printStackTrace();
                    }
                    e.printStackTrace();
                }
                getConversationFromNetwork(parcelableUser, conversation_,
                        twitter_, dateFormat_);
            }
        }
    }

}
