package com.sun.tweetfiltrr.tweetoperations;

import android.content.ContentResolver;
import android.content.Context;
import android.util.Log;

import com.sun.tweetfiltrr.asyncretriever.api.IKeywordUpdateRetriever;
import com.sun.tweetfiltrr.asyncretriever.api.TweetRetrieverWrapper;
import com.sun.tweetfiltrr.daoflyweigth.impl.DaoFlyWeightFactory;
import com.sun.tweetfiltrr.database.DBUtils;
import com.sun.tweetfiltrr.database.dao.IDBDao;
import com.sun.tweetfiltrr.database.dbupdater.api.IDBUpdater;
import com.sun.tweetfiltrr.database.dbupdater.impl.SimpleDBUpdater;
import com.sun.tweetfiltrr.database.tables.FriendTable;
import com.sun.tweetfiltrr.database.tables.TimelineTable;
import com.sun.tweetfiltrr.parcelable.ParcelableTimeLineEntry;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.parcelable.ParcelableUserToKeywords;
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

import twitter4j.RateLimitStatus;
import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Created by Sundeep on 24/12/13.
 */
public class KeywordTweetUpdateRetriever implements IKeywordUpdateRetriever {

    private static final String TAG = KeywordTweetUpdateRetriever.class.getName();
    private IDBDao<ParcelableUserToKeywords> _keywordFriendDao;
    private static final String SEARCH_RESOURCE_KEY = "/users/search";
    private TweetRetrieverWrapper _tweetRetriever;
    private ExecutorService _taskExecutor;
    private IDBUpdater<ParcelableUser> _dbUserUpdater;
    private IDBUpdater<ParcelableTimeLineEntry> _dbTimelineUpdater;
    private ContentResolver _resolver;
    private IDBDao<ParcelableUser> _friendDao;
    IDBDao<ParcelableTimeLineEntry> _timelineDao;
    public KeywordTweetUpdateRetriever(ExecutorService taskExecutor_, ContentResolver resolver_) {
        _taskExecutor = taskExecutor_;
        _resolver = resolver_;

        DaoFlyWeightFactory daoFlyWeightFactory = DaoFlyWeightFactory.getInstance(_resolver);

        _keywordFriendDao = (IDBDao<ParcelableUserToKeywords>)
                daoFlyWeightFactory.getDao(DaoFlyWeightFactory.DaoFactory.FRIEND_KEYWORD_DAO, null);

        _timelineDao = (IDBDao<ParcelableTimeLineEntry>)
                daoFlyWeightFactory.getDao(DaoFlyWeightFactory.DaoFactory.TIMELINE_DAO, null);

        _friendDao = (IDBDao<ParcelableUser>)
                daoFlyWeightFactory.getDao(DaoFlyWeightFactory.DaoFactory.FRIEND_DAO, null);

        _dbTimelineUpdater = new SimpleDBUpdater<ParcelableTimeLineEntry>();
        _dbUserUpdater = new SimpleDBUpdater<ParcelableUser>();

        for(String s :DBUtils.getprojections(TimelineTable.TimelineColumn.values()) ){
            Log.v(TAG, "DB columns for timeline: " +s );

        }



        ThreadLocal<SimpleDateFormat> simpleDateFormatThreadLocal = TwitterUtil.getInstance().getSimpleDateFormatThreadLocal();
        _tweetRetriever = new TweetRetrieverWrapper(_taskExecutor, simpleDateFormatThreadLocal);

    }


    public void searchForKeywordTweetUpdates(Context context_) {

        if (TwitterUtil.hasInternetConnection(context_)) {

            Log.v(TAG,
                    "Attempting to update user's friends and timelines associated with freinds");

            Twitter twitter = TwitterUtil.getInstance().getTwitter();
            int remainingSearchLimit = getSearchRateLimitCount(twitter);

            Collection<ParcelableUserToKeywords> friendsWithKeywords = getUsersWithKeywordGroup(remainingSearchLimit);

            for(ParcelableUserToKeywords user : friendsWithKeywords){
                Log.v(TAG, "User for keyword srearch is: " + user.getFriend().toString() );
            }

            Collection<Callable<ParcelableUser>> tasks = lookForNewKeywordTweets(friendsWithKeywords);
            executeTasks(tasks);

            Log.v(TAG, "DONE running tasks for search");
        } else {
            Log.v(TAG,
                    "No internet access or user not logged in, so will not update twitter data");
        }

    }


    private void executeTasks(Collection<Callable<ParcelableUser>> tasks_){
        try {
            List<Future<ParcelableUser>> updatedUserFutures =  _taskExecutor.invokeAll(tasks_, 3, TimeUnit.MINUTES);
            flushDBEntries(updatedUserFutures);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Collection<ParcelableUserToKeywords> getUsersWithKeywordGroup(int remainingSearchLimit_){
        return _keywordFriendDao.getEntries(null,null,
                        FriendTable.FriendColumn.COLUMN_MAXID.p() + " DESC, " +
                                FriendTable.FriendColumn.COLUMN_LAST_DATETIME_SYNC.p() + " ASC " +
                                " LIMIT " + remainingSearchLimit_ );
    }

    private void flushDBEntries(List<Future<ParcelableUser>> updatedFutures_) throws ExecutionException, InterruptedException {
        Collection<ParcelableUser> users = new ArrayList<ParcelableUser>();
        Collection<ParcelableTimeLineEntry> timeLines = new ArrayList<ParcelableTimeLineEntry>();
        Collection<IDBDao<ParcelableUser>> userDao = new ArrayList<IDBDao<ParcelableUser>>();
        Collection<IDBDao<ParcelableTimeLineEntry>> timelineDao = new ArrayList<IDBDao<ParcelableTimeLineEntry>>();

        userDao.add(_friendDao);
        for(Future<ParcelableUser> futureUser : updatedFutures_){
            ParcelableUser user = futureUser.get();
            if(user != null){
                users.add(user);
                timeLines.addAll(user.getUserTimeLine());
            }
        }
        //Update DB
        _dbTimelineUpdater.flushToDB(timelineDao, timeLines);
        _dbUserUpdater.flushToDB(userDao, users);
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
//                Log.v(TAG, "Key for limi: " + limit.getKey());
//                Log.v(TAG, "Value for limi: " + limit.getValue());
//
//            }

        } catch (TwitterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return maxRemaining;
    }



    private Collection<Callable<ParcelableUser>> lookForNewKeywordTweets(Collection<ParcelableUserToKeywords> friends_){
        return  _tweetRetriever.getCallableRetrieverList(friends_, false, false);
    }


}
