package com.sun.tweetfiltrr.concurrent;

import android.os.AsyncTask;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by Sundeep on 06/01/14.
 */
public class AsyncFutureTaskWrapper<T,V> extends AsyncTask<Future<T>,V,T>{
    private final static String TAG = AsyncFutureTaskWrapper.class.getName();

    private final long _timeout;
    private final TimeUnit _timeUnit;

    /**
     * {@link android.os.AsyncTask} to wait for {@link java.util.concurrent.Future} tasks to wait and retrieve the  
     * {@link com.sun.tweetfiltrr.parcelable.parcelable.api.IParcelableTwitter} object and return the {@link java.util.Collection}
     *
     * @param timeout_
     * @param timeUnit_
     */
    public AsyncFutureTaskWrapper(long timeout_,TimeUnit timeUnit_){
        _timeout = timeout_;
        _timeUnit = timeUnit_;
    }

    @Override
    protected T doInBackground(Future<T>... params) {
      T futureResults = null;

        //TODO add exception handling
        for(Future<T> future : params){
            try {
                futureResults =  future.get(_timeout,_timeUnit );
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
        }

        return futureResults;
    }

}
