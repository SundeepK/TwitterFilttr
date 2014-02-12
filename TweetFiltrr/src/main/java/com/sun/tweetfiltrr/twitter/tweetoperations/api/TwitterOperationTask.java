package com.sun.tweetfiltrr.twitter.tweetoperations.api;

import android.view.View;

import com.sun.tweetfiltrr.twitter.api.ITwitterAPICall;

/**
 * Created by Sundeep on 29/01/14.
 */
public class TwitterOperationTask implements ITwitterOperationTask<ITwitterAPICall> {

    private final View _viewToUpdate;
    private final ISubmittable<ITwitterAPICall> _submittable;

    public TwitterOperationTask(View viewToUpdate_, ISubmittable<ITwitterAPICall> submittable_){
        _viewToUpdate = viewToUpdate_;
        _submittable = submittable_;
    }

    @Override
    public View getView() {
        return _viewToUpdate;
    }

    @Override
    public ISubmittable<ITwitterAPICall> getSubmittableTask() {
        return _submittable;
    }


    @Override
    public boolean isComplete() {
        return _submittable.isComplete();
    }

    @Override
    public boolean isFailed() {
        return _submittable.isFailed();
    }

    @Override
    public boolean isRunning() {
        return _submittable.isRunning();
    }
}
