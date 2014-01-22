package com.sun.tweetfiltrr.concurrent;

import android.util.Log;

import com.sun.tweetfiltrr.concurrent.api.OnAsyncTaskPostExecute;
import com.sun.tweetfiltrr.database.dao.IDBDao;
import com.sun.tweetfiltrr.database.dbupdater.api.IDBUpdater;
import com.sun.tweetfiltrr.parcelable.ParcelableTimeLineEntry;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

    /**
     * Created by Sundeep on 06/01/14.
     */
 public  class AsyncFutureTimelineDBUpdatetask<V> extends AsyncFutureTaskWrapper<Collection<ParcelableUser>, V> {

        private final static String TAG = AsyncFutureTimelineDBUpdatetask.class.getName();
        private IDBUpdater<ParcelableTimeLineEntry> _databaseUpdater;
        private Collection<IDBDao<ParcelableTimeLineEntry>> _daosToUpdate;
        private OnAsyncTaskPostExecute _postExecuteLis;
        /**
         *
         * {@link android.os.AsyncTask} which wraps around a {@link AsyncFutureTaskWrapper} and waits for the computation to complete.
         * The fecthed results are then updated to the DB.
         *
         * @param timeout_
         * @param timeUnit_
         * @param databaseUpdater_
         */
        public AsyncFutureTimelineDBUpdatetask(long timeout_, TimeUnit timeUnit_,
                                               Collection<IDBDao<ParcelableTimeLineEntry>> daosToUpdate_,
                                               IDBUpdater<ParcelableTimeLineEntry> databaseUpdater_,
                                               OnAsyncTaskPostExecute postExecuteLis_) {
            super(timeout_, timeUnit_);
            _databaseUpdater  = databaseUpdater_;
            _daosToUpdate = daosToUpdate_;
            _postExecuteLis = postExecuteLis_;
        }

        @Override
        protected void onPreExecute() {
            _postExecuteLis.onPreExecute();
        }

        @Override
        protected Collection<ParcelableUser> doInBackground(Future[] params) {
            Collection<ParcelableUser> futureResults = super.doInBackground(params);

           Collection<ParcelableTimeLineEntry> timeline = new ArrayList<ParcelableTimeLineEntry>();

            for(ParcelableUser user : futureResults){
                timeline.addAll(user.getUserTimeLine());
            }


            Log.v(TAG, "trying to update tiomeline size" + timeline.size() + " for user + " + futureResults.toString());

            try {
                _databaseUpdater.flushToDB(_daosToUpdate, timeline);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return futureResults;
        }

        @Override
        protected void onPostExecute(Collection<ParcelableUser> parcelableUser) {
            _postExecuteLis.onPostExecute(parcelableUser);
        }
}

