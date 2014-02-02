package com.sun.tweetfiltrr.tweetprocessor.impl;

import android.util.Log;

import com.sun.tweetfiltrr.twitter.retrievers.api.ATweetRetiever;
import com.sun.tweetfiltrr.parcelable.ParcelableTweet;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.tweetprocessor.api.ITweetProcessor;
import com.sun.tweetfiltrr.utils.DateUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import twitter4j.Status;
import twitter4j.User;

/**
 * Created by Sundeep on 16/12/13.
 * ThreadSafe
 */
public abstract class ATweetProcessor implements ITweetProcessor {

    private static final String TAG = ATweetRetiever.class.getName();
    private ThreadLocal<SimpleDateFormat> _simpleDateFormatThreadLocal;


    /**
     * Threadsafe calss
     *
     * Base class for runnable's which need to retrieve tweets from twitter. It provides default functionality to convert
     * tweets to {@link com.sun.tweetfiltrr.parcelable.ParcelableTweet} and add them to the current {@link com.sun.tweetfiltrr.parcelable.ParcelableUser} so that they can later
     * be updated in the database.

     * @param dateFormat_
     *          The {@link java.text.SimpleDateFormat} that is used to manipulate {@link java.util.Date}
     */
    public ATweetProcessor(final ThreadLocal<SimpleDateFormat> dateFormat_) {
        _simpleDateFormatThreadLocal = dateFormat_;   //  new SimpleDateFormat(TwitterConstants.SIMPLE_DATE_FORMATE);
    }


    @Override
    public Collection<ParcelableUser> processTimeLine(Iterator<Status> iterator_, ParcelableUser friend_, Date today_){
           final  SimpleDateFormat dateFormat = _simpleDateFormatThreadLocal.get();
           final Map<User, Collection<ParcelableTweet>> userToTimeline = new HashMap<User, Collection<ParcelableTweet>>();

            while (iterator_.hasNext()) {
                Status tweet = iterator_.next();
                if(!processTweet(tweet,today_, dateFormat, userToTimeline)){
                    friend_.setLastUpadateDate(dateFormat.format(DateUtils.getCurrentDate()));
                    break;
                }
            }
        Log.v(TAG, "I'm done with processTimeline with ");
        //might as well the lastUpdateTime while were at it
        friend_.setLastUpadateDate(dateFormat.format(DateUtils.getCurrentDate()));
        return flattenMap(userToTimeline);
    }

    private Collection<ParcelableUser> flattenMap(final Map<User, Collection<ParcelableTweet>> usersKeyToTimline_){
        Set<Map.Entry<User,Collection<ParcelableTweet>>> usersAndTimeLineSet = usersKeyToTimline_.entrySet();
        return getUsersWithTimeLine(usersAndTimeLineSet);
    }

    private Collection<ParcelableUser> getUsersWithTimeLine(final Set<Map.Entry<User,Collection<ParcelableTweet>>> usersWithTimeLineSet_){
         final Iterator<Map.Entry<User,Collection<ParcelableTweet>>> entryIterator =  usersWithTimeLineSet_ .iterator();
         final Collection<ParcelableUser> usersWithTweets = new ArrayList<ParcelableUser>();

         while(entryIterator.hasNext()){
            final Map.Entry<User, Collection<ParcelableTweet>> entry = entryIterator.next();
            final Collection<ParcelableTweet> timeline = entry.getValue();
            final ParcelableUser user = new ParcelableUser(entry.getKey());

             if(timeline != null){
                Log.v(TAG, "Users timeline is empty for: " + user.toString());
             }
            user.addAll(timeline);
            cacheLastIDs(user);
            usersWithTweets.add(user);
         }
        return usersWithTweets;
    }

    public abstract void cacheLastIDs(final ParcelableUser user_);

    private void addToMap(final Map<User, Collection<ParcelableTweet>> usersKeyToTimline_, final User user_, final ParcelableTweet tweet_){
        final Collection<ParcelableTweet> tweets = usersKeyToTimline_.get(user_);
        if(tweets == null){
            final Collection<ParcelableTweet> nonNullTweets = new ArrayList<ParcelableTweet>();
            nonNullTweets.add(tweet_);
            usersKeyToTimline_.put(user_, nonNullTweets);
        }else{
            tweets.add(tweet_);
            usersKeyToTimline_.put(user_, tweets);
        }
    }

    /**
     *
     * Process the actual tweet to convert it to a {@link com.sun.tweetfiltrr.parcelable.ParcelableTweet} and add it
     * to the user's internal {@link java.util.Collection} of {@link com.sun.tweetfiltrr.parcelable.ParcelableTweet} tweets
     * @param tweet_ {@link twitter4j.Status} which contains the tweet info
     * @param today_ {@link java.util.Date} is used to break early if the tweet {@link java.util.Date} does not match the
     *                                     specified date and so we can filter out tweets older than
     *                                     the specified {@link java.util.Date}. Can be null.
     * @param dateFormat_ {@link java.text.SimpleDateFormat} used to format the date to a readable format
     * @return true if the tweet {@link java.util.Date} is newer than the one specified in the parameter
     */
    private boolean processTweet(Status tweet_, Date today_,SimpleDateFormat dateFormat_,  Map<User, Collection<ParcelableTweet>> usersKeyToTimline_){
        final Date tweetCreateAt = tweet_.getCreatedAt();
        final User user = tweet_.getUser();

        if(today_ != null){
            if (tweetCreateAt.before(today_)) // have this configurable, so that user can check however old he wants
                return false;
        }

        Log.v(TAG, "------------------------------------------------");
        Log.v(TAG, tweet_.getText());
        Log.v(TAG, "" + tweet_.getInReplyToScreenName());
        Log.v(TAG, "Tweet date: " + tweetCreateAt.toString());
        final Date now = DateUtils.getCurrentDate();
        final ParcelableTweet parcelableTweet = getTimeLineEntry(tweet_, user, dateFormat_, tweetCreateAt );
        processTweet(parcelableTweet);
        addToMap(usersKeyToTimline_, user, parcelableTweet);
        return true;
    }

    private ParcelableTweet getTimeLineEntry(final Status tweet_,final User user_,
                                                    final SimpleDateFormat dateFormat_, final Date tweetCreateAt_ ){
       return  new ParcelableTweet(tweet_, dateFormat_.format(tweetCreateAt_), tweet_.getUser().getId());
    }


    protected abstract void processTweet(ParcelableTweet tweetToProcess_);


}
