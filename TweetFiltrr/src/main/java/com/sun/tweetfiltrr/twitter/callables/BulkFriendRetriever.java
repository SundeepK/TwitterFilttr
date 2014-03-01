package com.sun.tweetfiltrr.twitter.callables;

import android.util.Log;

import com.sun.tweetfiltrr.database.dbupdater.api.IDatabaseUpdater;
import com.sun.tweetfiltrr.parcelable.CachedFriendDetails;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.parcelable.parcelable.api.ICachedUser;
import com.sun.tweetfiltrr.twitter.api.ITwitterAPICall;
import com.sun.tweetfiltrr.twitter.api.ITwitterAPICallStatus;
import com.sun.tweetfiltrr.twitter.twitterretrievers.api.UsersFriendRetriever;
import com.sun.tweetfiltrr.utils.TwitterUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Created by Sundeep on 16/02/14.
 */
public class BulkFriendRetriever implements Runnable, ITwitterAPICallStatus {

    private static final String TAG = BulkFriendRetriever.class.getName();
    private ParcelableUser _currentUser;
    private Twitter _twitter = TwitterUtil.getInstance().getTwitter();
    private UsersFriendRetriever _userRetriver;
    private Collection<ParcelableUser> _friends;
    private ExecutorService _executorService;
    private Collection<IDatabaseUpdater> _dbUpdaters;
    private AtomicInteger _currentFriendCount = new AtomicInteger();
    private OnFriendLoadFinish _onLoadFinishCallback;
    private static final AtomicBoolean IS_SEARCHING = new AtomicBoolean(false);

    public interface OnFriendLoadFinish{
        public void onBulkFriendLoadFinish(ParcelableUser user_);
    }

    public BulkFriendRetriever(ParcelableUser currentUser_, Collection<IDatabaseUpdater> dbUpdaters_,
                               OnFriendLoadFinish onFinishCallback_ ) {
        _currentUser = currentUser_;
        _userRetriver = new UsersFriendRetriever(true);
        _executorService = Executors.newFixedThreadPool(4);
        _friends = new ArrayList<ParcelableUser>();
        _dbUpdaters = dbUpdaters_;
        _onLoadFinishCallback = onFinishCallback_;
    }

    public static boolean isLoadingFriends(){
       return  IS_SEARCHING.get();
    }

    @Override
    public void run() {

        if (!IS_SEARCHING.get()) {
            IS_SEARCHING.getAndSet(true);
            Log.v(TAG, "call started for user " + _currentUser.getScreenName() + "with current friend count" + _currentUser.getCurrentFriendCount());
            ICachedUser cachedFriendUser = new CachedFriendDetails(_currentUser);
            long[] friendIDs = cachedFriendUser.getUserIds();
            //if the user we want to bulk search friends for doesn't have any cached userID's then run once before
            //we fire concurrent threads to load more
            if (friendIDs == null) {
                try {
                    _friends.addAll(searchForFriends(cachedFriendUser));
                    friendIDs = cachedFriendUser.getUserIds();
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
            }
            int friendIDCount = friendIDs.length;
            Log.v(TAG, "passed initail load with last array index @ " + cachedFriendUser.getLastArrayIndex());

            //we already processed all friends
            if (_friends.size() >= friendIDCount) {
                Log.v(TAG, "already processed user " + _currentUser.getScreenName() + " so returnining");
                return;
            }
            _currentFriendCount.addAndGet(cachedFriendUser.getCurrentUserCount());
            Collection<Future<Collection<ParcelableUser>>> futures = new ArrayList<Future<Collection<ParcelableUser>>>();
            int currentIndex = cachedFriendUser.getLastArrayIndex();
            int totalFriendsSearched = currentIndex;
            boolean isReachMaxFriendNumber = false;
            int totalCachedFriendCount = cachedFriendUser.getUser().getTotalFriendCount();

            while (!isReachMaxFriendNumber) {
                //4 threads to run in batched rather than submit many threads at once
                Collection<Callable<Collection<ParcelableUser>>> callables
                        = new ArrayList<Callable<Collection<ParcelableUser>>>();
                for (int i = 0; i < 4; i++) {
                    int lenght = 100;
                    currentIndex += 100;
                    int diff = totalCachedFriendCount - currentIndex;
                    if (currentIndex <= 5000) {
                        if (diff < 100) {
                            lenght = diff;
                            isReachMaxFriendNumber = true;
                        }
                    } else {
                        break;
                    }

                    Log.v(TAG, "current array index is: " + currentIndex + " with total frnd count " + totalCachedFriendCount + " and length " + lenght);

                    long[] ids = new long[lenght];
                    System.arraycopy(friendIDs, currentIndex, ids, 0, lenght);
                    ParcelableUser cachedUser = new ParcelableUser(_currentUser);
                    cachedUser.setLastFriendIndex(0);
                    cachedUser.setFriendIDs(ids);
                    Log.v(TAG, "adding callable for user" + cachedUser.getScreenName() + " with last index" + cachedUser.getLastFriendIndex());
                    callables.add(new FriendsRetriever(cachedUser, _userRetriver, this));
                    if (isReachMaxFriendNumber) {
                        currentIndex += lenght;
                        break;
                    }
                }
                //add the total search friends so far
                totalFriendsSearched += currentIndex;
                //TODO check below is incorrect, we dont even clear the future tasks and probably needs to be done before the for-loop
                if (currentIndex >= 5000 && currentIndex < _currentUser.getTotalFriendCount()) {
                    try {
                        cachedFriendUser.setLastArrayIndex(currentIndex);
                        cachedFriendUser.setTotalCount(_currentFriendCount.get());
                        _friends.addAll(searchForFriends(cachedFriendUser));
                    } catch (TwitterException e) {
                        e.printStackTrace();
                    }
                    currentIndex = cachedFriendUser.getLastArrayIndex();
                } else if (currentIndex >= 5000 && currentIndex > cachedFriendUser.getUser().getTotalFriendCount()) {
                    break;
                } else {
                    for (Callable<Collection<ParcelableUser>> callable : callables) {
                        futures.add(_executorService.submit(callable));
                    }

                    if (waitForFriendTasks(futures)) {
                        //all tasks finished fine so update DB
                        int friendCount = _currentFriendCount.get();
                        int index = friendCount % 5000;
                        _currentUser.setCurrentFriendTotal(friendCount);
                        _currentUser.setLastFriendIndex(index);
                        _friends.add(_currentUser);
                        Log.v(TAG, "finished waiting for 4 tasks with total size: " + _friends.size());
                        Log.v(TAG, "finished waiting for 4 tasks with total size: " + _friends.size());

                        for (IDatabaseUpdater updater : _dbUpdaters) {
                            updater.updateUsersToDB(_friends);
                        }
                        _friends.clear();

                    } else {
                        Log.v(TAG, "waitForFriendTasks was false");
                        break;
                    }
                }
            }
            _friends.clear();
            _friends.add(_currentUser);
            Log.v(TAG, " final currentcount for user is : " + _currentUser.getScreenName() + " index " + _currentUser.getCurrentFriendCount());

            for (IDatabaseUpdater updater : _dbUpdaters) {
                updater.updateUsersToDB(_friends);
            }
            IS_SEARCHING.getAndSet(false);
        }

        _onLoadFinishCallback.onBulkFriendLoadFinish(_currentUser);
    }

    /**
     * this will wait for tasks but will stop if there are any errors, we need to look at keeping error tasks and retrying depdening on
     * situation
     *
     * @param futures
     * @return
     */
    private boolean waitForFriendTasks(Collection<Future<Collection<ParcelableUser>>> futures) {
        Iterator<Future<Collection<ParcelableUser>>> iterator = futures.iterator();
        while (iterator.hasNext()) {
            Future<Collection<ParcelableUser>> result = iterator.next();
            try {
                _friends.addAll(result.get(5, TimeUnit.MINUTES));
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            } catch (ExecutionException e) {
                e.printStackTrace();
                return false;
            } catch (TimeoutException e) {
                e.printStackTrace();
                return false;
            }
            iterator.remove();
        }

        return true;

    }

    private Collection<ParcelableUser> searchForFriends(ICachedUser user_) throws TwitterException {
        return _userRetriver.retrieveTwitterData(user_, this);
    }

    @Override
    public void onTwitterApiCallSuccess(ParcelableUser user_) {

        _currentFriendCount.addAndGet(user_.getCurrentFriendCount());
        Log.v(TAG, "SUCCESS with current count: " + user_.getCurrentFriendCount() + " current index " + user_.getLastFriendIndex() +
                "current friend current friend count" + _currentFriendCount.get());
    }

    @Override
    public void onTwitterApiCallFail(ParcelableUser failedTweet_, TwitterException exception_, ITwitterAPICall apiCallType_) {
        Log.v(TAG, "fail");

    }


}
