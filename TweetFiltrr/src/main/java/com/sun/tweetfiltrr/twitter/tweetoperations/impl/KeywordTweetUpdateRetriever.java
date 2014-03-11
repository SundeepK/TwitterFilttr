package com.sun.tweetfiltrr.twitter.tweetoperations.impl;

import android.content.Context;
import android.util.Log;

import com.sun.tweetfiltrr.database.dao.impl.FriendDao;
import com.sun.tweetfiltrr.database.dao.impl.FriendKeywordDao;
import com.sun.tweetfiltrr.database.dao.impl.TimelineDao;
import com.sun.tweetfiltrr.database.dbupdater.api.IDatabaseUpdater;
import com.sun.tweetfiltrr.database.dbupdater.impl.DatabaseUpdater;
import com.sun.tweetfiltrr.database.dbupdater.impl.TimelineDatabaseUpdater;
import com.sun.tweetfiltrr.database.tables.FriendTable;
import com.sun.tweetfiltrr.database.tables.TimelineTable;
import com.sun.tweetfiltrr.database.utils.DBUtils;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.twitter.api.ITwitterAPICall;
import com.sun.tweetfiltrr.twitter.api.ITwitterAPICallStatus;
import com.sun.tweetfiltrr.twitter.twitterretrievers.api.IKeywordUpdateRetriever;
import com.sun.tweetfiltrr.twitter.twitterretrievers.api.TweetRetrieverFactory;
import com.sun.tweetfiltrr.utils.DateUtils;
import com.sun.tweetfiltrr.utils.TwitterUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.ObjectGraph;
import twitter4j.RateLimitStatus;
import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Created by Sundeep on 24/12/13.
 */
public class KeywordTweetUpdateRetriever implements IKeywordUpdateRetriever, ITwitterAPICallStatus {

    private static final String TAG = KeywordTweetUpdateRetriever.class.getName();
    private static final String SEARCH_RESOURCE_KEY = "/users/search";


    private ExecutorService _taskExecutor;
    private Collection<IDatabaseUpdater> _userDaoUpdaters;
    private static final int MAX_WAIT_TIME = 5;//in mins
    @Inject FriendDao _friendDao;
    @Inject TimelineDao _timelineDao;
    @Inject FriendKeywordDao _keywordFriendDao;
    @Inject TweetRetrieverFactory _tweetRetriever;

    public KeywordTweetUpdateRetriever(ExecutorService taskExecutor_, ObjectGraph objectGraph_) {
        _taskExecutor = taskExecutor_;
        objectGraph_.inject(this);

        _userDaoUpdaters = new ArrayList<IDatabaseUpdater>();
        _userDaoUpdaters.add(new TimelineDatabaseUpdater(_timelineDao));
        String[] cols = new String[]{FriendTable.FriendColumn.FRIEND_ID.s(),
                FriendTable.FriendColumn.TWEET_COUNT.s(), FriendTable.FriendColumn.SINCEID_FOR_KEYWORDS.s(),
                FriendTable.FriendColumn.MAXID_FOR_KEYWORDS.s(),  FriendTable.FriendColumn.FOLLOWER_COUNT.s()
                , FriendTable.FriendColumn.FRIEND_NAME.s(), FriendTable.FriendColumn.FRIEND_SCREENNAME.s(), FriendTable.FriendColumn.PROFILE_IMAGE_URL.s(),
                FriendTable.FriendColumn.BACKGROUND_PROFILE_IMAGE_URL.s(), FriendTable.FriendColumn.BANNER_PROFILE_IMAE_URL.s(), FriendTable.FriendColumn.DESCRIPTION.s(),
                FriendTable.FriendColumn.COLUMN_LAST_DATETIME_SYNC.s()};
        _userDaoUpdaters.add(new DatabaseUpdater(_friendDao,cols ));
        for(String s :DBUtils.getprojections(TimelineTable.TimelineColumn.values()) ){
            Log.v(TAG, "DB columns for timeline: " +s );

        }

    }


    public int searchForKeywordTweetUpdates(Context context_) {
        int totalNewTweetsFound = 0;
        if (TwitterUtil.hasInternetConnection(context_)) {
            Log.v(TAG,
                    "Attempting to update user's friends and timelines associated with freinds");
            Twitter twitter = TwitterUtil.getInstance().getTwitter();
            int remainingSearchLimit = getSearchRateLimitCount(twitter);
            Collection<ParcelableUser> friendsWithKeywords = getUsersWithKeywordGroup(remainingSearchLimit);
            for(ParcelableUser user : friendsWithKeywords){
                Log.v(TAG, "User for keyword srearch is: " + user.toString() );
            }
            Collection<Callable<Collection<ParcelableUser>>> tasks = lookForNewKeywordTweets(friendsWithKeywords);
            totalNewTweetsFound =  executeTasks(tasks);
            Log.v(TAG, "DONE running tasks for search");
        } else {
            Log.v(TAG,
                    "No internet access or user not logged in, so will not update keyword tweets");
        }
        return totalNewTweetsFound;
    }

    private int executeTasks(Collection<Callable<Collection<ParcelableUser>>> tasks_){
        List<Future<Collection<ParcelableUser>>> updatedUserFutures = new ArrayList<Future<Collection<ParcelableUser>>>();
        try {
            updatedUserFutures.addAll(_taskExecutor.invokeAll(tasks_, MAX_WAIT_TIME, TimeUnit.MINUTES));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return waitForTweets(updatedUserFutures);
    }

    private Collection<ParcelableUser> getUsersWithKeywordGroup(int remainingSearchLimit_){
        return _keywordFriendDao.getEntries(null,null,
                        FriendTable.FriendColumn.MAXID_FOR_KEYWORDS.p() + " DESC, " +
                                FriendTable.FriendColumn.COLUMN_LAST_DATETIME_SYNC.p() + " ASC " +
                                " LIMIT " + remainingSearchLimit_ );
    }

    private int waitForTweets(List<Future<Collection<ParcelableUser>>> updatedFutures_)  {
        final SimpleDateFormat dateFormat = TwitterUtil.getInstance().getSimpleDateFormatThreadLocal().get();
        final Collection<ParcelableUser> users = new ArrayList<ParcelableUser>();
        int totalTweets = 0;
        for(Future<Collection<ParcelableUser>> futureUser : updatedFutures_){
            Collection<ParcelableUser> user = null;
            try {
                user = futureUser.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            if(user != null){
                users.addAll(user);
            }
        }
        final String lastUpdateTime = dateFormat.format(DateUtils.getCurrentDate());
        for(ParcelableUser user : users){
            user.setLastUpadateDate(lastUpdateTime);
            totalTweets+=user.getUserTimeLine().size();
        }

        for(IDatabaseUpdater updater : _userDaoUpdaters){
            updater.updateUsersToDB(users);
        }
        return totalTweets;
    }


    private int getSearchRateLimitCount(Twitter twitter_){
        Map<String, RateLimitStatus> limits;
        int maxRemaining = 0;
        try {
            limits = twitter_.getRateLimitStatus();
            RateLimitStatus rateLimit = limits.get(SEARCH_RESOURCE_KEY);
            maxRemaining = rateLimit.getRemaining();
            Log.v(TAG, "Twitter search limit remaining:" + maxRemaining);
//            for (Map.Entry<String, RateLimitStatus> limit : limits
//                    .entrySet()) {
//                Log.v(TAG, "Key for limi: " + limit.getTag());
//                Log.v(TAG, "Value for limi: " + limit.getValue());
//
//            }
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        return maxRemaining;
    }



    private Collection<Callable<Collection<ParcelableUser>>> lookForNewKeywordTweets(Collection<ParcelableUser> friends_){
        return  _tweetRetriever.getKeywordRetriever(friends_, true, false, this);
    }


    @Override
    public void onTwitterApiCallSuccess(ParcelableUser user_, ITwitterAPICall apiCallType_) {

    }

    @Override
    public void onTwitterApiCallFail(ParcelableUser failedTweet_, TwitterException exception_, ITwitterAPICall apiCallType_) {

    }
}
