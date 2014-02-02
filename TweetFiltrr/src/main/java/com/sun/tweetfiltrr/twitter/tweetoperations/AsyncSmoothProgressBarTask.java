package com.sun.tweetfiltrr.twitter.tweetoperations;

import android.os.AsyncTask;

import com.sun.tweetfiltrr.fragment.pulltorefresh.IProgress;


/**
 * Created by Sundeep on 01/01/14.
 */
public abstract class AsyncSmoothProgressBarTask<V, T, E>  extends AsyncTask<V, T, E> {

    private IProgress _progressBar;
    private static final String TAG = FavouriteTweet.class.getName() ;

    public  AsyncSmoothProgressBarTask(IProgress progressBar_){
        _progressBar = progressBar_;
    }

    public  AsyncSmoothProgressBarTask(){
    }

    @Override
    protected void onPostExecute(E status) {
        if(_progressBar != null){
        _progressBar.setRefreshFinish();
        }

        super.onPostExecute(status);

    }

    @Override
    protected void onPreExecute() {
        if(_progressBar != null){
            _progressBar.startRefresh();
        }
        super.onPreExecute();
    }

}
