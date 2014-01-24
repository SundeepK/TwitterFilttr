package com.sun.tweetfiltrr.tweetoperations;

import android.os.AsyncTask;

import com.sun.tweetfiltrr.smoothprogressbarwrapper.SmoothProgressBarWrapper;


/**
 * Created by Sundeep on 01/01/14.
 */
public abstract class AsyncSmoothProgressBarTask<V, T, E>  extends AsyncTask<V, T, E> {

    private SmoothProgressBarWrapper _progressBar;
    private static final String TAG = FavouriteTweet.class.getName() ;

    public  AsyncSmoothProgressBarTask(SmoothProgressBarWrapper progressBar_){
        _progressBar = progressBar_;
    }

    @Override
    protected void onPostExecute(E status) {
        _progressBar.stop();
        super.onPostExecute(status);

    }

    @Override
    protected void onPreExecute() {
        _progressBar.start();
        super.onPreExecute();

    }

}
