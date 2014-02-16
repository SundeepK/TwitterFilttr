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

import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Created by Sundeep on 16/02/14.
 */
public class BulkFriendRetriever implements Runnable,ITwitterAPICallStatus {

    private static final String TAG = BulkFriendRetriever.class.getName();
    private ParcelableUser _currentUser;
    private Twitter _twitter= TwitterUtil.getInstance().getTwitter();
    private UsersFriendRetriever _userRetriver;
    private Collection<ParcelableUser> _friends;
    private ExecutorService _executorService;
    private Collection<IDatabaseUpdater> _dbUpdaters;

    public BulkFriendRetriever(ParcelableUser currentUser_, Collection<IDatabaseUpdater> dbUpdaters_){
        _currentUser = currentUser_;
        _userRetriver = new UsersFriendRetriever(true);
        _executorService =  Executors.newFixedThreadPool(4);
        _friends = new ArrayList<ParcelableUser>();
        _dbUpdaters = dbUpdaters_;
    }

    @Override
    public void run() {
        Log.v(TAG, "call started for user " + _currentUser.getScreenName() + "with current friend count" + _currentUser.getCurrentFriendCount());
        ICachedUser cachedFriendUser = new CachedFriendDetails(_currentUser);
        long[] friendIDs = cachedFriendUser.getUserIds();
        //if the user we want to bulk search friends for doesn't have any cached userID's then run once before
        //we fire concurrent threads to load more
        if(friendIDs == null){
            try {
                _friends.addAll(searchForFriends(cachedFriendUser));
                friendIDs = cachedFriendUser.getUserIds();
            } catch (TwitterException e) {
                e.printStackTrace();
            }
        }
        int friendIDCount = friendIDs.length;
//        if(friendIDCount <= 0){
//            _friends.addAll(searchForFriends(cachedFriendUser));
//        }

        Log.v(TAG, "passed initail load with last array index @ " + cachedFriendUser.getLastArrayIndex());

        //we already processed all friends
//        if(_friends.size() >= friendIDCount){
//            Log.v(TAG, "already processed user " + _currentUser.getScreenName() + " so returnining");
//            return null;
//        }

        Collection<Future<Collection<ParcelableUser>>> futures = new ArrayList<Future<Collection<ParcelableUser>>>();
        int currentIndex = cachedFriendUser.getLastArrayIndex();
        while(currentIndex < cachedFriendUser.getUser().getTotalFriendCount()){
            //make 4 threads to run in batched rather than submit many threads at once
           Collection<Callable<Collection<ParcelableUser>>> callables
                   = new ArrayList<Callable<Collection<ParcelableUser>>>();
            for(int i =0 ; i < 4; i++){
                //make copy of user but set the ID's and last array index so it can pick up at correct index
                currentIndex =  currentIndex >= 5000 ? currentIndex : currentIndex+100 ;
                int arrayIndex = currentIndex+100;
                if(arrayIndex > 5000){
                    currentIndex = 5000 - arrayIndex;
                }
                if(currentIndex >= 5000 || currentIndex >cachedFriendUser.getUser().getTotalFriendCount()){
                    break;
                }
                ParcelableUser cachedUser = new ParcelableUser(_currentUser);
                cachedUser.setLastFriendIndex(currentIndex);
                cachedUser.setFriendIDs(friendIDs);
                Log.v(TAG, "adding callable for user" + cachedUser.getScreenName() + " with last index" + cachedUser.getLastFriendIndex());
                callables.add(new FriendsRetriever(cachedUser,_userRetriver, this));
            }

           if(currentIndex >= 5000 && currentIndex  < _currentUser.getTotalFriendCount()){
               try {
                   _friends.addAll(searchForFriends(cachedFriendUser));
               } catch (TwitterException e) {
                   e.printStackTrace();
               }
               currentIndex = cachedFriendUser.getLastArrayIndex();
           }else{
               for(Callable<Collection<ParcelableUser>> callable : callables){
                   futures.add(_executorService.submit(callable));
               }

               if(waitForFriendTasks(futures)){
                   //all tasks finished fine so update DB
                   Log.v(TAG, "finished waiting for 4 tasks with total size: " + _friends.size());
                   for(IDatabaseUpdater updater : _dbUpdaters){
                       //  updater.updateUsersToDB(_friends);
                       _friends.clear();
                   }

               }else{
                   break;
               }
           }


        }


        Log.v(TAG, "finished with currentcount" + cachedFriendUser.getUser().getCurrentFriendCount() + "with total friend count" + cachedFriendUser.getUser().getTotalFriendCount());

        return ;
    }

    /**
     * this will wait for tasks but will stop if there are any errors, we need to look at keeping error tasks and retryinh depdening on
     * situation
     * @param futures
     * @return
     */
    private boolean waitForFriendTasks(Collection<Future<Collection<ParcelableUser>>> futures){
       Iterator<Future<Collection<ParcelableUser>>> iterator = futures.iterator();
       while (iterator.hasNext()){
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
       return  _userRetriver.retrieveTwitterData(user_, this);
    }

    @Override
    public void onTwitterApiCallSuccess(ParcelableUser user_) {
            Log.v(TAG, "SUCCESS");
    }

    @Override
    public void onTwitterApiCallFail(ParcelableUser failedTweet_, TwitterException exception_, ITwitterAPICall apiCallType_) {
        Log.v(TAG, "fail");

    }


}
