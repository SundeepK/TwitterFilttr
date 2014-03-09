package com.sun.tweetfiltrr.twitter.twitterretrievers.impl;

import com.sun.tweetfiltrr.database.dao.impl.FriendDao;
import com.sun.tweetfiltrr.database.dao.impl.TimelineDao;
import com.sun.tweetfiltrr.database.tables.FriendTable;
import com.sun.tweetfiltrr.database.tables.TimelineTable;
import com.sun.tweetfiltrr.parcelable.ParcelableTweet;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;

import java.util.Collection;
import java.util.LinkedList;

import javax.inject.Inject;

/**
 * Created by Sundeep on 09/03/14.
 */
public class ConversationRetrieverFromDB {


    @Inject TimelineDao _timelineDao;
    @Inject FriendDao _friendDao;

    public ConversationRetrieverFromDB (){}

    public boolean hasConversationInDB(ParcelableTweet tweetsToLookUp_){
        final long replyTweetId = tweetsToLookUp_.getInReplyToTweetId();
        final long replyUserId = tweetsToLookUp_.getInReplyToUserId();
        boolean hasConvoInDB = false;
        if(replyTweetId < 0 ||  replyUserId < 0){
            hasConvoInDB = false;
        }else {
            Collection<ParcelableTweet> replyTweets = getTweetFromDB(replyTweetId);
                    hasConvoInDB = !replyTweets.isEmpty();
        }
        return hasConvoInDB;
    }

    private Collection<ParcelableTweet> getTweetFromDB(long replyTweetId_){
      return _timelineDao.getEntries(TimelineTable.TimelineColumn.TWEET_ID.s() + " = ?",
                new String[]{Long.toString(replyTweetId_)}, null);
    }

    private Collection<ParcelableUser> getUserFromDB(long userID_){
        return _friendDao.getEntries(FriendTable.FriendColumn.FRIEND_ID.s() + " = ?",
                new String[]{Long.toString(userID_)}, null);
    }

    public Collection<ParcelableUser> getConversationFromDB(ParcelableTweet tweetsToLookUp_){
        Collection<ParcelableUser> users = new LinkedList<ParcelableUser>();
        if(hasConversationInDB(tweetsToLookUp_)){
            getConvo(users, tweetsToLookUp_);
        }
        return users;
    }

    /**
     * Recurse through the DB finding tweets and users, whilst adding them the collection passed in.
     * @param users_
     * @param tweetToLookUp_
     * @return
     */
    private void getConvo(Collection<ParcelableUser> users_, ParcelableTweet tweetToLookUp_){
        final long replyTweetId = tweetToLookUp_.getInReplyToTweetId();
        final long replyUserId = tweetToLookUp_.getInReplyToUserId();

        Collection<ParcelableTweet> tweets = getTweetFromDB(replyTweetId);
        if(!tweets.isEmpty()){
            Collection<ParcelableUser> users = getUserFromDB(replyUserId);
            if(!users.isEmpty()){
                //we will only have one user since userID's are unique
                ParcelableUser user = users.iterator().next();
                user.getUserTimeLine().addAll(tweets);
                users_.addAll(users);
                //we will only have one tweet as well since tweetid are unique
                ParcelableTweet tweet = tweets.iterator().next();
                getConvo(users_, tweet);
            }
        }
    }



}
