package com.sun.tweetfiltrr.concurrent;

import android.util.Log;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutorService;

/**
 * Created by Sundeep on 07/12/13.
 */
public class RetryingCallable<V> implements Callable<V>{

    private final ExecutorService _executorService;
    private final Callable<V> _callableToTry;
    private int _maxRetries;
    private static final String TAG = RetryingCallable.class.getName();


    public RetryingCallable(ExecutorService executorService_, Callable<V> callableToTry_, int maxRetries_) {
        _executorService = executorService_;
        _callableToTry = callableToTry_;
        _maxRetries = maxRetries_;
    }


    private void retry(){
        Log.v(TAG, "retrying again");
        _executorService.submit(this);
    }

    @Override
    public V call() throws Exception {
        V result = null;
        try {
            result = _callableToTry.call();
        } catch (InterruptedException interuptedE) {
            Log.v(TAG, "Error occured with interuption");
            throw new InterruptedException(interuptedE.getMessage());
        } catch (CancellationException cancelledE) {
            Log.v(TAG, "Error occured with cancellation");
            throw new CancellationException(cancelledE.getMessage());
        } catch (Exception e) {
            if (_maxRetries <= 0) {
                Log.v(TAG, "Error occured with Exception");
                e.printStackTrace();
                throw new RetryException("Task failed to retry");
            }
            _maxRetries--;
            retry();
        }
        return result;
    }
}
