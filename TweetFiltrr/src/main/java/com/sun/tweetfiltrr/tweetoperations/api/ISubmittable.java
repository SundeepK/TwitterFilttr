package com.sun.tweetfiltrr.tweetoperations.api;

/**
 * Created by Sundeep.Kahlon on 29/01/14.
 */
public interface ISubmittable<V> {

    public boolean submitNewTask(V callableToSubmit_);
    public boolean isComplete();

}