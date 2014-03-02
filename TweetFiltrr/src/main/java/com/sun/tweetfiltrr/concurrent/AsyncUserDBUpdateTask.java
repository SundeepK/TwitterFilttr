package com.sun.tweetfiltrr.concurrent;

import android.os.AsyncTask;
import android.util.Log;

import com.sun.tweetfiltrr.concurrent.api.OnAsyncTaskPostExecute;
import com.sun.tweetfiltrr.database.dbupdater.api.IDBUpdater;
import com.sun.tweetfiltrr.database.dbupdater.api.IDatabaseUpdater;
import com.sun.tweetfiltrr.parcelable.ParcelableTweet;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
     * Created by Sundeep on 06/01/14.
     */

 public  class AsyncUserDBUpdateTask<V> extends AsyncTask<Future<Collection<ParcelableUser>>, V, Collection<ParcelableUser>> {

        private final static String TAG = AsyncUserDBUpdateTask.class.getName();
        private IDBUpdater<ParcelableTweet> _databaseUpdater;
        private Collection<IDatabaseUpdater> _userUpdaters;
        private OnAsyncTaskPostExecute _postExecuteLis;
          private final long _timeout;
         private final TimeUnit _timeUnit;
        /**
         *
         * {@link android.os.AsyncTask} which wraps around a {@link AsyncFutureTaskWrapper} and waits for the computation to complete.
         * The fecthed results are then updated to the DB.
         *
         * @param timeout_
         * @param timeUnit_
         */
        public AsyncUserDBUpdateTask(long timeout_, TimeUnit timeUnit_,
                                     Collection<IDatabaseUpdater> daosToUpdate_,
                                     OnAsyncTaskPostExecute postExecuteLis_) {
            _timeout = timeout_;
            _timeUnit = timeUnit_;
            _userUpdaters = daosToUpdate_;
            _postExecuteLis = postExecuteLis_;
        }

        @Override
        protected void onPreExecute() {
            _postExecuteLis.onPreExecute();
        }

        @Override
        protected Collection<ParcelableUser> doInBackground(Future<Collection<ParcelableUser>>... params) {
            Collection<ParcelableUser> futureResults  = new ArrayList<ParcelableUser>();

            if(params.length <= 0){
                return futureResults;
            }

            //TODO add exception handling
            for(Future<Collection<ParcelableUser>> future : params){
                try {
                    Collection<ParcelableUser> user = future.get(_timeout,_timeUnit);

                    if(user != null){
                        futureResults.addAll(user);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }
            }

            Log.v(TAG, "trying to update tiomeline size" + futureResults.size() + " for user + " + futureResults.toString());


            if(!futureResults.isEmpty() && futureResults.size() > 0){
                for(IDatabaseUpdater updater : _userUpdaters){
                    updater.updateUsersToDB(futureResults);
                }
            }

            return futureResults;

        }

        @Override
        protected void onPostExecute(Collection<ParcelableUser> parcelableUser) {
            _postExecuteLis.onPostExecute(parcelableUser);
        }
}

