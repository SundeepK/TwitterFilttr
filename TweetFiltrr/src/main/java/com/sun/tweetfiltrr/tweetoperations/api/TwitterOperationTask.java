package com.sun.tweetfiltrr.tweetoperations.api;

import android.view.View;

/**
 * Created by Sundeep on 29/01/14.
 */
public class TwitterOperationTask implements ITwitterOperationTask<ITwitterOperation> {

    private final View _viewToUpdate;
    private final ISubmittable<ITwitterOperation> _submittable;

    public TwitterOperationTask(View viewToUpdate_, ISubmittable<ITwitterOperation> submittable_){
        _viewToUpdate = viewToUpdate_;
        _submittable = submittable_;
    }

    @Override
    public View getView() {
        return _viewToUpdate;
    }

    @Override
    public ISubmittable<ITwitterOperation> getSubmittableTask() {
        return _submittable;
    }

    @Override
    public void performTask(ITwitterOperation[] objectsToSubmit_) {

    }

    @Override
    public boolean submitNewTask(ITwitterOperation callableToSubmit_) {
        return _submittable.submitNewTask(callableToSubmit_);
    }

    @Override
    public boolean isComplete() {
        return _submittable.isComplete();
    }
}