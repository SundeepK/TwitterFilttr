package com.sun.tweetfiltrr.concurrent;

import android.util.Log;

import com.sun.tweetfiltrr.concurrent.api.OnAsyncTaskPostExecute;
import com.sun.tweetfiltrr.database.dbupdater.api.IDBUpdater;
import com.sun.tweetfiltrr.database.dbupdater.api.IUserUpdater;
import com.sun.tweetfiltrr.parcelable.ParcelableTweet;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

    /**
     * Created by Sundeep on 06/01/14.
     */
 public  class AsyncUserDBUpdateTask<V> extends AsyncFutureTaskWrapper<Collection<ParcelableUser>, V> {

        private final static String TAG = AsyncUserDBUpdateTask.class.getName();
        private IDBUpdater<ParcelableTweet> _databaseUpdater;
        private Collection<IUserUpdater> _userUpdaters;
        private OnAsyncTaskPostExecute _postExecuteLis;
        /**
         *
         * {@link android.os.AsyncTask} which wraps around a {@link AsyncFutureTaskWrapper} and waits for the computation to complete.
         * The fecthed results are then updated to the DB.
         *
         * @param timeout_
         * @param timeUnit_
         */
        public AsyncUserDBUpdateTask(long timeout_, TimeUnit timeUnit_,
                                     Collection<IUserUpdater> daosToUpdate_,
                                     OnAsyncTaskPostExecute postExecuteLis_) {
            super(timeout_, timeUnit_);
            _userUpdaters = daosToUpdate_;
            _postExecuteLis = postExecuteLis_;
        }

        @Override
        protected void onPreExecute() {
            _postExecuteLis.onPreExecute();
        }

        @Override
        protected Collection<ParcelableUser> doInBackground(Future<Collection<ParcelableUser>>... params) {
            Collection<ParcelableUser> futureResults = super.doInBackground(params);

            if(futureResults == null){
                futureResults = new ArrayList<ParcelableUser>();
            }

            Log.v(TAG, "trying to update tiomeline size" + futureResults.size() + " for user + " + futureResults.toString());

            for(IUserUpdater updater : _userUpdaters){
                updater.updateUsersToDB(futureResults);
            }

            return futureResults;

        }

        @Override
        protected void onPostExecute(Collection<ParcelableUser> parcelableUser) {
            _postExecuteLis.onPostExecute(parcelableUser);
        }
}

