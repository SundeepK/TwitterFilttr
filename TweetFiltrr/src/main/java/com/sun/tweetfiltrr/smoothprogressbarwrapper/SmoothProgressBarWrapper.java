package com.sun.tweetfiltrr.smoothprogressbarwrapper;

import android.view.View;

import com.sun.tweetfiltrr.fragment.pulltorefresh.IProgress;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

/**
 * Created by Sundeep on 01/01/14.
 */
public class SmoothProgressBarWrapper implements IProgress {

    private SmoothProgressBar _progressBar;
    private int registeredCount = 0 ;
    private final static String  TAG = SmoothProgressBarWrapper.class.getName();

    public SmoothProgressBarWrapper(SmoothProgressBar progressBar_){
        _progressBar = progressBar_;
    }

    private synchronized void stop(){

        if(registeredCount < 1){
            throw new IllegalStateException("Cannot stop progressbar because start has not been called");
        }

        if(registeredCount == 1){
            registeredCount--;
            _progressBar.setVisibility(View.INVISIBLE);

        }else{
            registeredCount--;
        }
    }

    private synchronized void start(){
        if(registeredCount <=0){
            _progressBar.setVisibility(View.VISIBLE);

            registeredCount++;
        }else{
            registeredCount++;
        }
    }

    @Override
    public void startRefreshAnimation() {
        start();
    }

    @Override
    public void setRefreshAnimationFinish() {
        stop();
    }
}
