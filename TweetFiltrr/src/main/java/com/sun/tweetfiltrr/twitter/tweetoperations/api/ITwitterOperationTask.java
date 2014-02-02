package com.sun.tweetfiltrr.twitter.tweetoperations.api;

import android.view.View;

/**
 * Created by Sundeep on 29/01/14.
 */
public interface ITwitterOperationTask<V> extends ISubmittable<V>{

    public View getView();
    public ISubmittable<V> getSubmittableTask();
}
