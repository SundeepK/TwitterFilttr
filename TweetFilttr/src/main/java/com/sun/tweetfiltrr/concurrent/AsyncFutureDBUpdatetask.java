package com.sun.tweetfiltrr.concurrent;

import android.os.AsyncTask;
import android.util.Log;

import com.sun.tweetfiltrr.database.dao.IDBDao;
import com.sun.tweetfiltrr.database.dbupdater.api.IDBUpdater;
import com.sun.tweetfiltrr.parcelable.parcelable.api.IParcelableTwitter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by Sundeep on 06/01/14.
 */
public class AsyncFutureDBUpdatetask<T extends IParcelableTwitter, V> extends AsyncTask<Future<Collection<T>>,V,Collection<T>> {

    private final static String TAG = AsyncFutureDBUpdatetask.class.getName();
    private IDBUpdater<T> _databaseUpdater;
    private Collection<IDBDao<T>> _daosToUpdate;
    private final long _timeout;
    private final TimeUnit _timeUnit;

    /**
     *
     * {@link android.os.AsyncTask} which wraps around a {@link AsyncFutureTaskWrapper} and waits for the computation to complete.
     * The fecthed results are then updated to the DB.
     *
     * @param timeout_
     * @param timeUnit_
     * @param databaseUpdater_
     */
    public AsyncFutureDBUpdatetask(long timeout_, TimeUnit timeUnit_, Collection<IDBDao<T>> daosToUpdate_, IDBUpdater<T> databaseUpdater_) {
        _databaseUpdater  = databaseUpdater_;
        _daosToUpdate = daosToUpdate_;
        _timeout = timeout_;
        _timeUnit = timeUnit_;
    }

    @Override
    protected Collection<T> doInBackground(Future<Collection<T>>[] params) {
        //TODO add exception handling

        Collection<T> results = new ArrayList<T>();

        for(Future<Collection<T>> future : params){
            try {
                results.addAll(future.get(_timeout,_timeUnit ));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
        }


      if(results.isEmpty()){
          Log.v(TAG, "future tasks are empty");
            return results;
      }

        Log.v(TAG, "future result size: " + results);


        try {
            _databaseUpdater.flushToDB(_daosToUpdate, results);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return results;
    }
}
